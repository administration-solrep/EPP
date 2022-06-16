package fr.dila.st.ui.mapper;

import java.io.Serializable;
import java.util.function.BiFunction;
import org.apache.commons.lang3.BooleanUtils;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Convert a principal to a field.
 *
 * @author olejacques
 *
 */
public class MapDoc2BeanBooleanStringProcess implements MapDoc2BeanProcess {

    public MapDoc2BeanBooleanStringProcess() {
        // do nothing
    }

    @Override
    public BiFunction<MapField, DocumentModel, Serializable> doc2BeanMapper() {
        return (mfield, doc) -> BooleanUtils.toBooleanObject((String) doc.getPropertyValue(mfield.getNxprop().xpath()));
    }

    @Override
    public BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        return (mfield, value) -> (BooleanUtils.toStringTrueFalse((Boolean) value));
    }
}
