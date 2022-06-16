package fr.dila.solonepp.core.adapter.message;

import fr.dila.solonepp.core.domain.message.MessageImpl;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier Message.
 *
 * @author jtremeaux
 */
public class MessageAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.CASE_LINK_SCHEMA);
        return new MessageImpl(doc);
    }
}
