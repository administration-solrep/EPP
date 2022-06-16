package fr.dila.st.core.event;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.event.batch.BatchLoggerModel;
import fr.dila.st.api.event.batch.STBatch;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.NotificationsSuiviBatchsService;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.api.util.FrameworkHelper;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.transaction.Status;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.runtime.transaction.TransactionHelper;

/**
 * Event listener that allocate a session to process the event
 *
 * optionaly can check before doing anything that the eventName as an exepected
 * value.
 *
 * @author SPL
 *
 */
public abstract class AbstractBatchEventListener extends AbstractEventListener implements STBatch {
    private static final String ETAT_TRANSACTION_STR = "Etat de la transaction [active : %s || alive : %s]";
    private static final String EVENT_EXPECTED_STR = "Expected : %s / Value : %s";

    private final STLogger log;
    protected String eventName;
    protected BatchLoggerModel batchLoggerModel;
    protected long batchLoggerId;
    protected long errorCount;
    protected TypeBatch batchType;

    protected AbstractBatchEventListener(STLogger log, String eventName) {
        super();
        this.log = log;
        this.eventName = eventName;
        this.batchLoggerModel = null;
        this.batchType = TypeBatch.TECHNIQUE;
    }

    @Override
    protected final void doHandleEvent(Event event) {
        if (FrameworkHelper.isDevModeSet()) {
            log.info(STLogEnumImpl.DEFAULT, "L'exécution des batchs est désactivée en dev");
            return;
        }

        if (eventName == null) {
            eventName = event.getName();
        } else if (!eventName.equals(event.getName())) {
            log.warn(
                null,
                STLogEnumImpl.UNEXPECTED_EVT_TEC,
                String.format(EVENT_EXPECTED_STR, eventName, event.getName())
            );
            return;
        }
        log.info(null, STLogEnumImpl.INIT_BATCH_TEC);
        log.debug(
            null,
            STLogEnumImpl.INIT_BATCH_TEC,
            String.format(ETAT_TRANSACTION_STR, TransactionHelper.isTransactionActive(), isTransactionAlive())
        );

        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();

        try (CloseableCoreSession session = SessionUtil.openSession()) {
            if (canBatchBeLaunched(session)) {
                BatchLoggerModel batchLoggerModelFinal = null;
                try {
                    batchLoggerModelFinal = suiviBatchService.createBatchLogger(eventName);
                    this.batchLoggerModel = batchLoggerModelFinal;
                    if (batchLoggerModelFinal != null) {
                        this.batchLoggerId = batchLoggerModelFinal.getIdLog();
                        this.errorCount = 0;

                        processEvent(session, event);

                        // Envoi de mail en cas d'échecs
                        sendNotificationMails(session);
                    }
                } catch (Exception e) {
                    event.markBubbleException();
                    throw e;
                } finally {
                    batchLoggerModelFinal = suiviBatchService.findBatchLogById(batchLoggerId);
                    if (batchLoggerModelFinal != null) {
                        batchLoggerModelFinal.setType(batchType.name());
                        batchLoggerModelFinal.setErrorCount(batchLoggerModelFinal.getErrorCount() + errorCount);
                        batchLoggerModelFinal.setEndTime(Calendar.getInstance());
                        if (event.getContext().hasProperty(STEventConstant.BATCH_EVENT_PROPERTY_PARENT_ID)) {
                            long parentId = (Long) event
                                .getContext()
                                .getProperty(STEventConstant.BATCH_EVENT_PROPERTY_PARENT_ID);
                            batchLoggerModelFinal.setParentId(parentId);
                        }
                        suiviBatchService.flushBatchLogger(batchLoggerModelFinal);
                        log.debug(
                            session,
                            STLogEnumImpl.END_BATCH_TEC,
                            String.format(
                                ETAT_TRANSACTION_STR,
                                TransactionHelper.isTransactionActive(),
                                isTransactionAlive()
                            )
                        );
                    }
                }
            }
        }
        log.info(null, STLogEnumImpl.END_BATCH_TEC);
    }

    protected abstract void processEvent(final CoreSession session, final Event event);

    /**
     * Renvoi vrai si le statut de la transaction est différent de "no transaction"
     *
     * @return
     */
    protected boolean isTransactionAlive() {
        try {
            return !(TransactionHelper.lookupUserTransaction().getStatus() == Status.STATUS_NO_TRANSACTION);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Commit de la transaction et restart. En cas d'echec de du commit, on affiche
     * en warn ou debug les logs suivant le booleen
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

    /**
     * Envoi les mails pour les notifications en cas d'echec de batch
     *
     * @param session
     * @throws ClientException
     */
    private void sendNotificationMails(final CoreSession session) {
        final NotificationsSuiviBatchsService notifService = STServiceLocator.getNotificationsSuiviBatchsService();
        if (errorCount > 0 && notifService.sontActiveesNotifications(session)) {
            final STParametreService paramService = STServiceLocator.getSTParametreService();
            final String texte = paramService.getParametreValue(
                session,
                STParametreConstant.TEXTE_MAIL_SUIVI_BATCH_NOTIFICATION
            );
            final String objet = paramService.getParametreValue(
                session,
                STParametreConstant.OBJET_MAIL_SUIVI_BATCH_NOTIFICATION
            );
            final Map<String, Object> params = new HashMap<String, Object>();
            params.put("eventName", eventName);
            STServiceLocator
                .getSTMailService()
                .sendTemplateHtmlMailToUserList(session, notifService.getAllUsers(session), objet, texte, params);
        }
    }

    protected enum JoursSemaine {
        LUNDI(Calendar.MONDAY),
        MARDI(Calendar.TUESDAY),
        MERCREDI(Calendar.WEDNESDAY),
        JEUDI(Calendar.THURSDAY),
        VENDREDI(Calendar.FRIDAY),
        SAMEDI(Calendar.SATURDAY),
        DIMANCHE(Calendar.SUNDAY);

        private Integer calValue;

        private JoursSemaine(Integer calValue) {
            this.calValue = calValue;
        }

        public Integer getCalValue() {
            return calValue;
        }
    }
}
