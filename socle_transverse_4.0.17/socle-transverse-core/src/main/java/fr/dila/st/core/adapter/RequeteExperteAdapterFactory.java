package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.core.requeteur.RequeteExperteImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author jgomez
 */
public class RequeteExperteAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public RequeteExperteAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STRequeteConstants.SMART_FOLDER_SCHEMA);
        return new RequeteExperteImpl(doc);
    }
}
