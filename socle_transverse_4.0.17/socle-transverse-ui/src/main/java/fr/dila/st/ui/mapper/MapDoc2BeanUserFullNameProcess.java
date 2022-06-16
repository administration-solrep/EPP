package fr.dila.st.ui.mapper;

import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.function.BiFunction;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Convert a document username property to a fullname property.
 *
 * @author olejacques
 *
 */
public class MapDoc2BeanUserFullNameProcess implements MapDoc2BeanProcess {

    public MapDoc2BeanUserFullNameProcess() {
        // Nothing to do
    }

    @Override
    public BiFunction<MapField, DocumentModel, Serializable> doc2BeanMapper() {
        return (mfield, doc) ->
            STServiceLocator
                .getSTUserService()
                .getUserFullName((String) doc.getPropertyValue(mfield.getNxprop().xpath()));
    }

    @Override
    public BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        throw new UnsupportedOperationException("Mapper unidirectionel doc -> bean");
    }
}
