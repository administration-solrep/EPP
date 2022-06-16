package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.domain.user.ProfileImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique des adapteurs de DocumentModel vers Profile.
 *
 * @author jtremeaux
 */
public class ProfileAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public ProfileAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.GROUP_SCHEMA);
        return new ProfileImpl(doc);
    }
}
