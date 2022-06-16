package fr.dila.st.ui.mapper;

import java.io.Serializable;
import java.util.function.BiFunction;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface MapDoc2BeanProcess {
    default BiFunction<MapField, DocumentModel, Serializable> doc2BeanMapper() {
        return (mfield, doc) -> {
            if (mfield.isSystemprop()) {
                return MapDoc2Bean.getSystemValue(doc, mfield.getNxprop());
            } else {
                return doc.getPropertyValue(mfield.getNxprop().xpath());
            }
        };
    }

    default BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        return (mfield, value) -> ((Serializable) value);
    }
}
