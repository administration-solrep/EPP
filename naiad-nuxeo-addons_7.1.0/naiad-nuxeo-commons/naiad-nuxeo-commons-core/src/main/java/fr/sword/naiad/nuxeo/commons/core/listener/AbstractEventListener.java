package fr.sword.naiad.nuxeo.commons.core.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.sword.naiad.nuxeo.commons.core.util.SessionUtil;

/**
 * Listener essayant d'ouvrir une session si le contexte ne dispose pas déjà d'une session ouverte.
 * 
 * @author fmh
 */
public abstract class AbstractEventListener implements EventListener {
	private static final Log LOG = LogFactory.getLog(AbstractEventListener.class);

	/**
	 * Constructeur par défaut.
	 */
	public AbstractEventListener() {
		super();
	}

	@Override
	public void handleEvent(Event event) throws NuxeoException {
		boolean sessionOpened = false;
		boolean transactionStarted = false;
		CoreSession session = event.getContext().getCoreSession();

		try {
			if (session == null) {
				if (!TransactionHelper.isTransactionActive()) {
					TransactionHelper.startTransaction();
					transactionStarted = true;
				}

				session = SessionUtil.openSession();
				sessionOpened = true;
			}
			if (session == null) {
				LOG.error("Failed to open core session");
			}

			handleEvent(event, session);
		} finally {
			if (sessionOpened) {
				SessionUtil.closeSession(session);

				if (transactionStarted) {
					TransactionHelper.commitOrRollbackTransaction();
				}
			}
		}
	}

	/**
	 * Méthode à implémenter par le listener.
	 * 
	 * @param event
	 *            Evènement à l'origine de l'appel.
	 * @param session
	 *            Session Nuxeo, récupérée à partir du contexte, ou créée.
	 * @throws NuxeoException
	 */
	protected abstract void handleEvent(Event event, CoreSession session) throws NuxeoException;
}
