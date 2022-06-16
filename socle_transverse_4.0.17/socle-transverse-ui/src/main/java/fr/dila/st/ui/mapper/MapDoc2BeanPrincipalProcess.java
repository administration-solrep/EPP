package fr.dila.st.ui.mapper;

import java.io.Serializable;
import java.util.function.BiFunction;
import org.nuxeo.ecm.webengine.WebEngine;

/**
 * Convert a principal to a field.
 *
 * @author olejacques
 *
 */
public class MapDoc2BeanPrincipalProcess implements MapDoc2BeanProcess {

    public MapDoc2BeanPrincipalProcess() {
        // Nothing to do
    }

    @Override
    public BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        return (mfield, value) -> WebEngine.getActiveContext().getCoreSession().getPrincipal().getName();
    }
}
