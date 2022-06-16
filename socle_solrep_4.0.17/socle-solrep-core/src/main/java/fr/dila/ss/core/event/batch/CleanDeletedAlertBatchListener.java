package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.core.event.batch.CleanDeletedDocumentBatchListener;

/**
 * Batch de suppression des alertes à l'état deleted
 *
 */
public class CleanDeletedAlertBatchListener extends CleanDeletedDocumentBatchListener {

    public CleanDeletedAlertBatchListener() {
        super(SSEventConstant.CLEAN_DELETED_ALERT_EVENT, STAlertConstant.ALERT_DOCUMENT_TYPE);
    }
}
