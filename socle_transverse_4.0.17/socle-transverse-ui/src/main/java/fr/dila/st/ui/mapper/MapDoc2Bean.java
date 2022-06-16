package fr.dila.st.ui.mapper;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.annot.NxProp;
import fr.dila.st.ui.annot.NxProp.Way;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public final class MapDoc2Bean {
    private static final STLogger LOG = STLogFactory.getLog(MapDoc2Bean.class);

    private static final Map<String, Function<DocumentModel, String>> SYSTEM_PROPS = ImmutableMap.of(
        "ecm:uuid",
        DocumentModel::getId,
        "ecm:primaryType",
        DocumentModel::getType,
        "ecm:currentLifeCycleState",
        DocumentModel::getCurrentLifeCycleState,
        "ecm:name",
        DocumentModel::getName
    );
    private static final Map<Class<? extends MapDoc2BeanProcess>, MapDoc2BeanProcess> PROCESS = new ConcurrentHashMap<>();

    private static final String EXCEPTION_MESSAGE_FORMAT = "doc2Bean Failed %s -> %s";
    private static final String BAD_TYPE_MESSAGE_FORMAT =
        "Le champ %s n'est pas annoté du type documentaire associé %s";

    private MapDoc2Bean() {
        // do nothing
    }

    public static <T> T docToBean(DocumentModel doc, Class<T> clazz) {
        T bean;
        try {
            bean = clazz.newInstance();
            return docToBean(doc, bean);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new NuxeoException(String.format(EXCEPTION_MESSAGE_FORMAT, doc.getId(), clazz.getName()), e);
        }
    }

    public static <T> T docToBean(DocumentModel doc, T bean) {
        try {
            for (MapField mfield : MapFieldHelper.getMapFields(bean.getClass())) {
                NxProp nxprop = mfield.getNxprop();
                if (doc.getType().equals(nxprop.docType())) {
                    if (nxprop.way() == Way.TWO_WAY || nxprop.way() == Way.DOC_TO_BEAN) {
                        MapDoc2BeanProcess process = mapDoc2BeanProcessInstance(mfield, doc, bean);
                        Serializable value = process.doc2BeanMapper().apply(mfield, doc);
                        if (process instanceof MapDoc2BeanProcessFormat) {
                            value = ((MapDoc2BeanProcessFormat) process).formatter().apply(value);
                        }
                        mfield.getField().set(bean, value);
                    }
                } else {
                    LOG.info(
                        STLogEnumImpl.DEFAULT,
                        String.format(BAD_TYPE_MESSAGE_FORMAT, mfield.getField().getName(), doc.getType())
                    );
                }
            }
            return bean;
        } catch (IllegalAccessException e) {
            throw new NuxeoException(
                String.format(EXCEPTION_MESSAGE_FORMAT, doc.getId(), bean.getClass().getName()),
                e
            );
        }
    }

    private static MapDoc2BeanProcess mapDoc2BeanProcessInstance(MapField mfield, DocumentModel doc, Object bean) {
        return PROCESS.computeIfAbsent(mfield.getNxprop().process(), c -> newMapDoc2BeanProcessInstance(c, doc, bean));
    }

    private static MapDoc2BeanProcess newMapDoc2BeanProcessInstance(
        Class<? extends MapDoc2BeanProcess> processClass,
        DocumentModel doc,
        Object bean
    ) {
        try {
            return processClass.getDeclaredConstructor().newInstance();
        } catch (
            InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e
        ) {
            throw new NuxeoException(
                String.format(EXCEPTION_MESSAGE_FORMAT, doc.getId(), bean.getClass().getName()),
                e
            );
        }
    }

    public static <T> void beanToDoc(T bean, DocumentModel doc) {
        try {
            for (MapField mfield : MapFieldHelper.getMapFields(bean.getClass())) {
                NxProp nxprop = mfield.getNxprop();
                if (doc.getType().equals(nxprop.docType())) {
                    if (!mfield.isSystemprop() && (nxprop.way() == Way.TWO_WAY || nxprop.way() == Way.BEAN_TO_DOC)) {
                        Object object = mfield.getField().get(bean);
                        Serializable value = mapDoc2BeanProcessInstance(mfield, doc, bean)
                            .bean2DocMapper()
                            .apply(mfield, object);
                        doc.setPropertyValue(nxprop.xpath(), value);
                    }
                } else {
                    LOG.info(
                        STLogEnumImpl.DEFAULT,
                        String.format(BAD_TYPE_MESSAGE_FORMAT, mfield.getField().getName(), doc.getType())
                    );
                }
            }
        } catch (IllegalAccessException e) {
            throw new NuxeoException(
                String.format(EXCEPTION_MESSAGE_FORMAT, doc.getId(), bean.getClass().getName()),
                e
            );
        }
    }

    public static <T> void beanToDoc(T bean, DocumentModel doc, boolean ignoreNull) {
        try {
            for (MapField mfield : MapFieldHelper.getMapFields(bean.getClass())) {
                if (doc.getType().equals(mfield.getNxprop().docType())) {
                    if (
                        !mfield.isSystemprop() &&
                        (mfield.getNxprop().way() == Way.TWO_WAY || mfield.getNxprop().way() == Way.BEAN_TO_DOC)
                    ) {
                        Object object = mfield.getField().get(bean);
                        if (ignoreNull) {
                            if (object != null) {
                                Serializable value = mapDoc2BeanProcessInstance(mfield, doc, bean)
                                    .bean2DocMapper()
                                    .apply(mfield, object);
                                doc.setPropertyValue(mfield.getNxprop().xpath(), value);
                            }
                        } else {
                            doc.setPropertyValue(mfield.getNxprop().xpath(), (Serializable) object);
                        }
                    }
                } else {
                    LOG.info(
                        STLogEnumImpl.DEFAULT,
                        String.format(BAD_TYPE_MESSAGE_FORMAT, mfield.getField().getName(), doc.getType())
                    );
                }
            }
        } catch (IllegalAccessException e) {
            throw new NuxeoException(
                String.format(EXCEPTION_MESSAGE_FORMAT, doc.getId(), bean.getClass().getName()),
                e
            );
        }
    }

    public static String getSystemValue(DocumentModel doc, NxProp nxprop) {
        Function<DocumentModel, String> function = SYSTEM_PROPS.get(nxprop.xpath());
        if (function == null) {
            throw new NuxeoException(String.format("No process for meta %s", nxprop.xpath()));
        } else {
            return function.apply(doc);
        }
    }
}
