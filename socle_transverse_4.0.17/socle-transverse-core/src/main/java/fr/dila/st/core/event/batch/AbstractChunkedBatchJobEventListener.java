package fr.dila.st.core.event.batch;

import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractEventListener;
import fr.dila.st.core.event.work.AbstractBatchProcessorStep;
import fr.dila.st.core.event.work.StepContext;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.transaction.Status;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Run a job batch event into chunk steps.
 *
 * @author olejacques
 *
 */
public abstract class AbstractChunkedBatchJobEventListener<T extends Serializable> extends AbstractEventListener {
    private static final String ETAT_TRANSACTION_STR = "Etat de la transaction [active : %s || alive : %s]";
    private static final String EVENT_EXPECTED_STR = "Expected : %s / Value : %s";
    private static final String BATCH_RESULT_JOB_DEFAULT_MESSAGE = "Lanceur du batch";

    private final STLogger log;
    protected String eventName;
    protected BatchLoggerModel batchLoggerModel;
    protected long batchLoggerId;
    protected TypeBatch batchType;
    protected int chunkSize;

    protected AbstractChunkedBatchJobEventListener(STLogger log, String eventName, int chunkSize) {
        super();
        this.log = log;
        this.eventName = eventName;
        this.batchLoggerModel = null;
        this.batchType = TypeBatch.TECHNIQUE;
        this.chunkSize = chunkSize;
    }

    @Override
    protected final void doHandleEvent(Event event) {
        if (eventName == null) {
            eventName = event.getName();
        }
        if (!Objects.equals(eventName, event.getName())) {
            log.warn(STLogEnumImpl.UNEXPECTED_EVT_TEC, String.format(EVENT_EXPECTED_STR, eventName, event.getName()));
            return;
        }
        log.info(STLogEnumImpl.INIT_BATCH_TEC);
        log.debug(
            STLogEnumImpl.INIT_BATCH_TEC,
            String.format(ETAT_TRANSACTION_STR, TransactionHelper.isTransactionActive(), isTransactionAlive())
        );

        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();

        try (CloseableCoreSession session = SessionUtil.openSession()) {
            BatchLoggerModel batchLoggerModelFinal = null;
            try {
                batchLoggerModelFinal = suiviBatchService.createBatchLogger(eventName);
                batchLoggerModel = batchLoggerModelFinal;
                if (batchLoggerModelFinal != null) {
                    batchLoggerId = batchLoggerModelFinal.getIdLog();

                    List<T> items = readItems(session);

                    WorkManager workManager = ServiceUtil.getRequiredService(WorkManager.class);
                    if (items.isEmpty()) {
                        StepContext stepContext = new StepContext(batchLoggerId, eventName, 0, chunkSize, 0);
                        workManager.schedule(newBatchProcessorStep(stepContext, new LinkedList<>()));
                    } else {
                        for (int start = 0; start < items.size(); start += chunkSize) {
                            List<T> chunk = items.subList(start, Math.min(start + chunkSize, items.size()));
                            StepContext stepContext = new StepContext(
                                batchLoggerId,
                                eventName,
                                start,
                                chunkSize,
                                items.size()
                            );
                            workManager.schedule(newBatchProcessorStep(stepContext, chunk));
                        }
                    }
                }
            } finally {
                updateBatchJobModel();
                log.debug(
                    session,
                    STLogEnumImpl.END_BATCH_TEC,
                    String.format(ETAT_TRANSACTION_STR, TransactionHelper.isTransactionActive(), isTransactionAlive())
                );
            }
        }
        log.info(STLogEnumImpl.END_BATCH_TEC);
    }

    private void updateBatchJobModel() {
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        BatchLoggerModel batchLoggerModelFinal = suiviBatchService.findBatchLogById(batchLoggerId);
        if (batchLoggerModelFinal == null) {
            return;
        }

        batchLoggerModelFinal.setType(batchType.name());
        suiviBatchService.flushBatchLogger(batchLoggerModelFinal);
    }

    protected abstract List<T> readItems(CoreSession session);

    protected abstract AbstractBatchProcessorStep<T> newBatchProcessorStep(StepContext stepContext, List<T> items);

    protected String getBatchResultJobMessage() {
        return BATCH_RESULT_JOB_DEFAULT_MESSAGE;
    }

    /**
     * Renvoi vrai si le statut de la transaction est différent de "no
     * transaction"
     *
     * @return
     */
    protected boolean isTransactionAlive() {
        try {
            return (TransactionHelper.lookupUserTransaction().getStatus() != Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Commit de la transaction et restart. En cas d'echec de du commit, on
     * affiche en warn ou debug les logs suivant le booleen
     *
     * @param session
     * @param displayLogs
     */
    protected void commitAndRestartTransaction(CoreSession session, boolean displayLogs) {
        try {
            TransactionHelper.commitOrRollbackTransaction();
        } catch (Exception exc) {
            if (displayLogs) {
                log.warn(session, STLogEnumImpl.FAIL_COMMIT_TRANSACTION_TEC, exc);
            } else {
                log.debug(session, STLogEnumImpl.FAIL_COMMIT_TRANSACTION_TEC, exc);
            }
        } finally {
            if (!isTransactionAlive()) {
                TransactionHelper.startTransaction();
            }
        }
    }
}
