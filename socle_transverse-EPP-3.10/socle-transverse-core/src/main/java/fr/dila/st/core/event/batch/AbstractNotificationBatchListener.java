package fr.dila.st.core.event.batch;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;

import fr.dila.st.api.constant.STEventConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.event.AbstractBatchEventListener;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Batch socle de notifications
 * 
 */
public abstract class AbstractNotificationBatchListener extends AbstractBatchEventListener {

	/**
	 * Logger formalisé en surcouche du logger apache/log4j
	 */
	private static final STLogger	LOGGER	= STLogFactory.getLog(AbstractNotificationBatchListener.class);

	protected AbstractNotificationBatchListener() {
		super(LOGGER, STEventConstant.WS_NOTIFICATION_EVENT);
	}

	@Override
	protected void processEvent(CoreSession session, Event event) throws ClientException {
		LOGGER.info(session, STLogEnumImpl.INIT_B_NOTIF_WS_TEC);
		final long startTime = Calendar.getInstance().getTimeInMillis();
		try {
			doNotifications(session);
		} catch (Exception exc) {
			LOGGER.error(session, STLogEnumImpl.FAIL_PROCESS_B_NOTIF_WS_TEC, exc);
			errorCount++;
		}
		final long endTime = Calendar.getInstance().getTimeInMillis();
		try {
			STServiceLocator.getSuiviBatchService().createBatchResultFor(batchLoggerModel,
					"Exécution du batch de notification aux WS", endTime - startTime);
		} catch (Exception e) {
			LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_BATCH_RESULT_TEC, e);
		}
		LOGGER.info(session, STLogEnumImpl.END_B_NOTIF_WS_TEC);
	}

	abstract protected void doNotifications(final CoreSession session) throws Exception;

}
