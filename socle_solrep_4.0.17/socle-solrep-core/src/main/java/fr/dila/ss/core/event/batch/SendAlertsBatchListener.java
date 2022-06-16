package fr.dila.ss.core.event.batch;

import fr.dila.st.api.alert.Alert;
import fr.dila.st.api.constant.STAlertConstant;
import fr.dila.st.api.constant.STSuiviBatchsConstants.TypeBatch;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STAlertService;
import fr.dila.st.api.service.SuiviBatchService;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.Calendar;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

public class SendAlertsBatchListener extends AbstractBatchEventListener {
    /**
     * Logger formalisé en surcouche du logger apache/log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(SendAlertsBatchListener.class);

    public SendAlertsBatchListener() {
        super(LOGGER, STAlertConstant.ALERT_EVENT);
    }

    @Override
    protected void processEvent(CoreSession session, Event event) {
        // Traite uniquement les évènements de déclenchement d'alerte
        LOGGER.info(session, STLogEnumImpl.PROCESS_B_SEND_ALERTS_TEC, "Début");
        batchType = TypeBatch.FONCTIONNEL;
        final long startTime = Calendar.getInstance().getTimeInMillis();
        final SuiviBatchService suiviBatchService = STServiceLocator.getSuiviBatchService();
        final STAlertService alertService = STServiceLocator.getAlertService();
        final List<Alert> alerts = alertService.getAlertsToBeRun(session);

        final long endTime = Calendar.getInstance().getTimeInMillis();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Nombre d'alertes qui sont en état d'être exécutées",
                alerts.size(),
                endTime - startTime
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(
            session,
            STLogEnumImpl.PROCESS_B_SEND_ALERTS_TEC,
            "Nombre d'alertes qui sont en état d'être exécutées : " + alerts.size()
        );
        final long startTime2 = Calendar.getInstance().getTimeInMillis();
        int mailSentCount = 0;
        for (Alert alert : alerts) {
            Boolean isSent = alertService.sendMail(session, alert);
            if (isSent) {
                mailSentCount++;
            } else {
                errorCount++;
            }
        }
        final long endTime2 = Calendar.getInstance().getTimeInMillis();
        try {
            suiviBatchService.createBatchResultFor(
                batchLoggerModel,
                "Nombre de mail envoyés",
                mailSentCount,
                endTime2 - startTime2
            );
        } catch (Exception e) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
        }
        LOGGER.info(session, STLogEnumImpl.PROCESS_B_SEND_ALERTS_TEC, "Nombre de mail envoyés  " + mailSentCount);
        LOGGER.info(session, STLogEnumImpl.PROCESS_B_SEND_ALERTS_TEC, "Fin");
    }
}
