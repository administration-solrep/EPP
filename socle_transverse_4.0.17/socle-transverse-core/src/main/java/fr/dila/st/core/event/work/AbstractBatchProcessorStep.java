package fr.dila.st.core.event.work;

import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.BatchResultModel;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.model.Page;
import fr.dila.st.api.service.NotificationsSuiviBatchsService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.work.SolonWork;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Status;
import org.nuxeo.common.utils.ExceptionUtils;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.transaction.TransactionHelper;

public abstract class AbstractBatchProcessorStep<T extends Serializable> extends SolonWork {
    private static final long serialVersionUID = -8978429763076707515L;

    private static final String BATCH_RESULT_STEP_DEFAULT_MESSAGE = "Traitement des éléments";

    private static final String ETAT_TRANSACTION_STR = "Etat de la transaction [active : %s || alive : %s]";
    private static final String BATCH_RESULT_JOB_DEFAULT_MESSAGE = "Lanceur du batch";

    private final Class<?> loggerClass;
    private final StepContext stepContext;

    protected BatchLoggerModel batchLoggerModel;
    protected int errorCount;
    protected TypeBatch batchType;
    protected LinkedList<T> items;

    private transient STLogger log;

    protected AbstractBatchProcessorStep(Class<?> loggerClass, StepContext stepContext, List<T> items) {
        super();
        this.loggerClass = loggerClass;
        this.stepContext = stepContext;
        this.batchType = TypeBatch.TECHNIQUE;
        this.items = new LinkedList<>(items);
    }

    @Override
    public String getTitle() {
        return getEventName();
    }

    protected String getEventName() {
        return String.join(
            "",
            this.stepContext.getParentEventName(),
            "Step",
            String.valueOf(this.stepContext.getStepOneIndex())
        );
    }

    @Override
    protected void doWork() {
        log = STLogFactory.getLog(loggerClass);
        openSystemSession();
        log.info(session, STLogEnumImpl.INIT_BATCH_TEC);
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        try {
            batchLoggerModel = suiviBatchService.createBatchLogger(getEventName());
            if (batchLoggerModel == null) {
                return;
            }

            this.errorCount = 0;
            setProgress(new Progress(0, items.size()));

            for (T item : items) {
                try {
                    processItem(session, item);
                    setProgress(new Progress(getProgress().getCurrent() + 1, items.size()));
                } catch (Exception exc) {
                    if (ExceptionUtils.hasInterruptedCause(exc)) {
                        errorCount += items.size() - getProgress().getCurrent();
                        throw exc;
                    }
                    log.error(session, STLogEnumImpl.FAIL_PROCESS_BATCH_TEC, exc);
                    errorCount++;
                }
            }
        } finally {
            doAfterWork();
        }
    }

    private void doAfterWork() {
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();

        if (this.batchLoggerModel == null) {
            return;
        }

        BatchLoggerModel persistedBatchLoggerModel = suiviBatchService.findBatchLogById(batchLoggerModel.getIdLog());
        if (persistedBatchLoggerModel != null) {
            persistedBatchLoggerModel.setType(batchType.name());
            persistedBatchLoggerModel.setErrorCount(persistedBatchLoggerModel.getErrorCount() + errorCount);
            persistedBatchLoggerModel.setEndTime(Calendar.getInstance());

            persistedBatchLoggerModel.setParentId(stepContext.getParentBatchJobId());
            suiviBatchService.flushBatchLogger(persistedBatchLoggerModel);
            log.debug(
                session,
                STLogEnumImpl.END_BATCH_TEC,
                String.format(ETAT_TRANSACTION_STR, TransactionHelper.isTransactionActive(), isTransactionAlive())
            );
        }
        batchLoggerModel = suiviBatchService.findBatchLogById(batchLoggerModel.getIdLog());

        try {
            int from = stepContext.getStartIndex() + 1;
            int to = Math.min(stepContext.getStartIndex() + stepContext.getChunkSize(), stepContext.getNbItemsTotal());

            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                String.join(
                    "",
                    getBatchResultStepMessage(),
                    " [",
                    String.valueOf(from),
                    "-",
                    String.valueOf(to),
                    "] / ",
                    String.valueOf(stepContext.getNbItemsTotal())
                ),
                (long) items.size() - errorCount,
                batchLoggerModel.getExecutionTime()
            );
        } catch (Exception exc) {
            log.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, exc);
        }
    }

    protected abstract void processItem(CoreSession session, T item);

    protected String getBatchResultStepMessage() {
        return BATCH_RESULT_STEP_DEFAULT_MESSAGE;
    }

    protected boolean isTransactionAlive() {
        try {
            return (TransactionHelper.lookupUserTransaction().getStatus() != Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isGroupJoin() {
        return true;
    }

    @Override
    public String getPartitionKey() {
        return String.valueOf(stepContext.getParentBatchJobId());
    }

    @Override
    public void onGroupJoinCompletion() {
        if (batchLoggerModel == null) {
            return;
        }

        try (CloseableCoreSession session = SessionUtil.openSession()) {
            final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();

            Page<BatchLoggerModel> batchModelSteps = suiviBatchService.findBatchLog(
                null,
                null,
                stepContext.getParentBatchJobId(),
                null,
                null
            );
            List<BatchResultModel> batchResultSteps = batchModelSteps
                .getResults()
                .stream()
                .map(suiviBatchService::findBatchResult)
                .flatMap(List::stream)
                .collect(Collectors.toList());

            long errors = batchModelSteps
                .getResults()
                .stream()
                .map(BatchLoggerModel::getErrorCount)
                .mapToLong(Long::longValue)
                .sum();
            long totalProcessed = batchResultSteps
                .stream()
                .map(BatchResultModel::getExecutionResult)
                .mapToLong(Long::longValue)
                .sum();

            if (errors > 0) {
                sendNotificationMails(session);
            }

            Calendar endTime = Calendar.getInstance();
            createJobBatchResult(session, totalProcessed, errors, endTime);

            BatchLoggerModel parentBatchModel = suiviBatchService.findBatchLogById(stepContext.getParentBatchJobId());
            parentBatchModel.setEndTime(endTime);
            parentBatchModel.setErrorCount(errors);
            suiviBatchService.flushBatchLogger(parentBatchModel);
        }
    }

    private void createJobBatchResult(CoreSession session, long totalProcessed, long errors, Calendar endTime) {
        if (batchLoggerModel == null) {
            return;
        }

        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();

        batchLoggerModel.setEndTime(endTime);
        batchLoggerModel.setErrorCount(errors);
        suiviBatchService.flushBatchLogger(batchLoggerModel);

        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                getBatchResultJobMessage(),
                totalProcessed,
                batchLoggerModel.getExecutionTime()
            );
        } catch (Exception e) {
            log.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
    }

    protected String getBatchResultJobMessage() {
        return BATCH_RESULT_JOB_DEFAULT_MESSAGE;
    }

    /**
     * Envoi les mails pour les notifications en cas d'echec de batch
     *
     * @param session
     * @throws ClientException
     */
    private void sendNotificationMails(final CoreSession session) {
        final NotificationsSuiviBatchsService notifService = STServiceLocator.getNotificationsSuiviBatchsService();
        if (!notifService.sontActiveesNotifications(session)) {
            return;
        }

        final STParametreService paramService = STServiceLocator.getSTParametreService();
        final String texte = paramService.getParametreValue(
            session,
            STParametreConstant.TEXTE_MAIL_SUIVI_BATCH_NOTIFICATION
        );
        final String objet = paramService.getParametreValue(
            session,
            STParametreConstant.OBJET_MAIL_SUIVI_BATCH_NOTIFICATION
        );
        final Map<String, Object> params = new HashMap<>();
        params.put("eventName", stepContext.getParentEventName());
        STServiceLocator
            .getSTMailService()
            .sendTemplateHtmlMailToUserList(session, notifService.getAllUsers(session), objet, texte, params);
    }
}
