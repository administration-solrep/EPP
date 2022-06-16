package fr.dila.solonepp.core.adapter.jeton;

import fr.dila.solonepp.core.domain.jeton.JetonDocImpl;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier JetonDoc.
 *
 * @author jtremeaux
 */
public class JetonDocAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.JETON_DOCUMENT_SCHEMA);
        return new JetonDocImpl(doc);
    }
}
