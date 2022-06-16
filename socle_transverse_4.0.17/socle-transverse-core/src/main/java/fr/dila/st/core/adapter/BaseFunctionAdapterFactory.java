package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.user.BaseFunctionImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique des adapteurs de DocumentModel vers BaseFunction.
 *
 * @author jtremeaux
 */
public class BaseFunctionAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public BaseFunctionAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.BASE_FUNCTION_SCHEMA);
        return new BaseFunctionImpl(doc);
    }
}
