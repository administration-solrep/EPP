package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.st.api.constant.STDossierLinkConstant;
import fr.dila.st.core.event.batch.CleanDeletedDocumentBatchListener;

/**
 * Batch de suppression des dossier link à l'état deleted
 *
 */
public class CleanDeletedDossierLinkBatchListener extends CleanDeletedDocumentBatchListener {

    public CleanDeletedDossierLinkBatchListener() {
        super(SSEventConstant.CLEAN_DELETED_DL_EVENT, STDossierLinkConstant.DOSSIER_LINK_DOCUMENT_TYPE);
    }
}
