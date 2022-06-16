package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.constant.SSParapheurConstants;
import fr.dila.st.core.event.batch.CleanDeletedDocumentBatchListener;

public class CleanDeletedParapheurFichierBatchListener extends CleanDeletedDocumentBatchListener {

    /**
     * Default constructor
     */
    public CleanDeletedParapheurFichierBatchListener() {
        super(SSEventConstant.CLEAN_DELETED_PARAPHEUR_FILE_EVENT, SSParapheurConstants.PARAPHEUR_FILE_DOCUMENT_TYPE);
    }
}
