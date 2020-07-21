package fr.dila.st.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;

/**
 * Check si une transaction est active. Si ce n'est pas le cas affiche une trace d'erreur précisant la stack
 * 
 * @author SPL
 * 
 */
public abstract class AbstractEventListener implements EventListener {

	private static final STLogger	LOGGER	= STLogFactory.getLog(AbstractFilterEventListener.class);
	private static final String		ERR_MSG	= "Not active transaction in event listener (stack display in debug mode) [%s]";

	@Override
	public final void handleEvent(Event event) throws ClientException {

		if (!TransactionHelper.isTransactionActive()) {
			LOGGER.warn(null, STLogEnumImpl.ANO_UNEXPECTED_TRANSACTION_TEC,
					String.format(ERR_MSG, this.getClass().toString()));
			LOGGER.debug(null, STLogEnumImpl.ANO_UNEXPECTED_TRANSACTION_TEC,
					String.format(ERR_MSG, this.getClass().toString()));
		}
		doHandleEvent(event);
	}

	protected abstract void doHandleEvent(Event event) throws ClientException;

}
