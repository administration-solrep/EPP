package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.jeton.JetonDocImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier JetonDoc.
 *
 * @author arolin
 */
public class JetonDocAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public JetonDocAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA);
        return new JetonDocImpl(doc);
    }
}
