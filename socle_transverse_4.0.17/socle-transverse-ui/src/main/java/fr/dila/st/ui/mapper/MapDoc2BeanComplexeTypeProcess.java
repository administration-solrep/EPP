package fr.dila.st.ui.mapper;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

public class MapDoc2BeanComplexeTypeProcess implements MapDoc2BeanProcess {

    public MapDoc2BeanComplexeTypeProcess() {
        // Nothing to do
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiFunction<MapField, DocumentModel, Serializable> doc2BeanMapper() {
        return (mfield, doc) -> {
            Serializable complexeTypeValue = doc.getPropertyValue(mfield.getNxprop().xpath());

            if (complexeTypeValue == null) {
                return null;
            }

            if (Collection.class.isAssignableFrom(complexeTypeValue.getClass())) {
                return ((Collection<Map<String, Serializable>>) complexeTypeValue).stream()
                    .map(complexeType -> this.toBean(this.getTypeArgument(mfield.getField()), complexeType))
                    .collect(Collectors.toCollection(ArrayList::new));
            }

            return toBean(mfield.getField().getType(), (Map<String, Serializable>) complexeTypeValue);
        };
    }

    private Class<?> getTypeArgument(Field field) {
        return (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    private Serializable toBean(Class<?> clazz, Map<String, Serializable> serializableMap) {
        if (!Serializable.class.isAssignableFrom(clazz)) {
            throw new NuxeoException(clazz + " n'est pas Serializable");
        }

        try {
            Object newInstance = clazz.newInstance();
            for (MapField mfield : MapFieldHelper.getMapFields(clazz)) {
                Serializable value = serializableMap.get(mfield.getNxprop().xpath());
                mfield.getField().set(newInstance, value);
            }
            return (Serializable) newInstance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new NuxeoException(String.format("doc2bean Failed sur %s", clazz), e);
        }
    }

    @Override
    public BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        return (mfield, value) -> {
            if (value == null) {
                return null;
            }

            if (Collection.class.isAssignableFrom(value.getClass())) {
                return ((Collection<?>) value).stream()
                    .map(this::toComplexeType)
                    .collect(Collectors.toCollection(ArrayList::new));
            }

            return toComplexeType(value);
        };
    }

    private Serializable toComplexeType(Object object) {
        HashMap<String, Serializable> serializableMap = new HashMap<>();
        for (MapField mfield : MapFieldHelper.getMapFields(object.getClass())) {
            try {
                Object mfieldValue = mfield.getField().get(object);
                if (!Serializable.class.isAssignableFrom(mfield.getField().getType())) {
                    throw new NuxeoException(
                        String.format(
                            "%s du type %s n'est pas Serializable",
                            mfield.getField().getName(),
                            mfield.getField().getName()
                        )
                    );
                }

                serializableMap.put(mfield.getNxprop().xpath(), (Serializable) mfieldValue);
            } catch (IllegalAccessException e) {
                throw new NuxeoException(String.format("bean2doc Failed sur %s", object.getClass().getName()), e);
            }
        }

        return serializableMap;
    }
}
