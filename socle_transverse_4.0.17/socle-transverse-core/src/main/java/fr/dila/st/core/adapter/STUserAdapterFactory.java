package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.user.STUserImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique des adapteurs de DocumentModel vers STUser.
 *
 * @author jtremeaux
 */
public class STUserAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public STUserAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.USER_SCHEMA);
        return new STUserImpl(doc);
    }
}
