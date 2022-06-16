package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.st.api.constant.STRequeteConstants;
import fr.dila.st.core.event.batch.CleanDeletedDocumentBatchListener;

public class CleanDeletedRequeteBatchListener extends CleanDeletedDocumentBatchListener {

    /**
     * Default constructor
     */
    public CleanDeletedRequeteBatchListener() {
        super(SSEventConstant.CLEAN_DELETED_REQUETE_EVENT, STRequeteConstants.SMART_FOLDER_DOCUMENT_TYPE);
    }
}
