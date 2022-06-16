package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.constant.SSFondDeDossierConstants;
import fr.dila.st.core.event.batch.CleanDeletedDocumentBatchListener;

public class CleanDeletedFddFichierBatchListener extends CleanDeletedDocumentBatchListener {

    /**
     * Default constructor
     */
    public CleanDeletedFddFichierBatchListener() {
        super(
            SSEventConstant.CLEAN_DELETED_FDD_FILE_EVENT,
            SSFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE
        );
    }
}
