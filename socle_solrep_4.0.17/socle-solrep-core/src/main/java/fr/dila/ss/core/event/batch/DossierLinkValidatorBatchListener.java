package fr.dila.ss.core.event.batch;

import fr.dila.ss.api.constant.SSEventConstant;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.core.runner.ValidateDueDossierLinkUnrestricted;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Batch de validation automatique des dossierLink + envoi de mails aux utilisateurs
 *
 */
public class DossierLinkValidatorBatchListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(DossierLinkValidatorBatchListener.class);

    public DossierLinkValidatorBatchListener() {
        super(LOGGER, SSEventConstant.VALIDATE_CASELINK_EVENT);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        LOGGER.info(session, SSLogEnumImpl.PROCESS_B_VALID_STEP_TEC, "Début");
        Long nbError = Long.valueOf(0L);
        try {
            // Recherche et valide les DossierLink
            ValidateDueDossierLinkUnrestricted runner = new ValidateDueDossierLinkUnrestricted(
                session,
                batchLoggerModel,
                nbError
            );
            errorCount += nbError.longValue();
            if (!isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
            try {
                runner.runUnrestricted();
            } finally {
                TransactionHelper.commitOrRollbackTransaction();
                TransactionHelper.startTransaction();
            }
        } catch (Exception exc) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_PROCESS_BATCH_TEC,
                "Echec lors du batch de validation automatique",
                exc
            );
            ++errorCount;
        }
        LOGGER.info(session, SSLogEnumImpl.PROCESS_B_VALID_STEP_TEC, "Fin");
    }
}
