package fr.dila.st.ui.mapper;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.BiFunction;
import org.nuxeo.ecm.core.api.DocumentModel;

public class MapDoc2BeanIntegerStringProcess implements MapDoc2BeanProcess {

    public MapDoc2BeanIntegerStringProcess() {
        // do nothing
    }

    @Override
    public BiFunction<MapField, DocumentModel, Serializable> doc2BeanMapper() {
        return (mfield, doc) -> {
            String value = (String) doc.getPropertyValue(mfield.getNxprop().xpath());
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        };
    }

    @Override
    public BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        return (mfield, value) -> Optional.ofNullable(value).map(i -> Integer.toString((Integer) i)).orElse(null);
    }
}
