package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.jeton.LockJetonMaitreImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

public class LockJetonMaitreAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public LockJetonMaitreAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, Class<?> arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.LOCK_JETON_MAITRE_SCHEMA);
        return new LockJetonMaitreImpl(doc);
    }
}
