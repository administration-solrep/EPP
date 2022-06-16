package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.administration.NotificationsSuiviBatchsImpl;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet NotificationsSuiviBatchs.
 *
 * @author feo
 */
public class NotificationsSuiviBatchsAdapterFactory implements STDocumentAdapterFactory {

    /**
     * Default constructor
     */
    public NotificationsSuiviBatchsAdapterFactory() {
        // do nothing
    }

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentSchemas(doc, STSchemaConstant.NOTIFICATIONS_SUIVI_BATCHS_SCHEMA);
        return new NotificationsSuiviBatchsImpl(doc);
    }
}
