package fr.dila.solonepp.core.event.batch;

import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.event.batch.AbstractNotificationBatchListener;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Batch de ré-essai d'envoi des notifications aux webservices.
 *
 * @author jtremeaux.
 */
public class NotificationBatchListener extends AbstractNotificationBatchListener {

    public NotificationBatchListener() {
        super();
    }

    @Override
    protected void doNotifications(final CoreSession session) throws Exception {
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        Long nbError = new Long(0L);
        jetonService.retryNotification(session, batchLoggerModel, nbError);
        errorCount += nbError.longValue();
    }
}
