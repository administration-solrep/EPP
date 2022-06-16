package fr.sword.naiad.nuxeo.commons.core.listener;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.transaction.TransactionHelper;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

/**
 * Listener essayant d'ouvrir une session unrestricted.
 * 
 * @author fmh
 */
public abstract class AbstractUnrestrictedEventListener implements EventListener {
	/**
	 * Constructeur par défaut.
	 */
	public AbstractUnrestrictedEventListener() {
		super();
	}

	@Override
	public void handleEvent(Event event) throws NuxeoException {
		boolean transactionStarted = false;

		try {
			final CoreSession session = event.getContext().getCoreSession();
			UnrestrictedSessionRunner usr;

			if (!TransactionHelper.isTransactionActive()) {
				TransactionHelper.startTransaction();
				transactionStarted = true;
			}

			if (session == null) {
				RepositoryManager mgr = ServiceUtil.getService(RepositoryManager.class);
				Repository repository = mgr.getDefaultRepository();

				usr = new EventListenerUnrestrictedSessionRunner(repository.getName(), event);
			} else {
				usr = new EventListenerUnrestrictedSessionRunner(session, event);
			}

			usr.runUnrestricted();
		} finally {
			if (transactionStarted) {
				TransactionHelper.commitOrRollbackTransaction();
			}
		}
	}

	/**
	 * Méthode à implémenter par le listener.
	 * 
	 * @param event
	 *            Evènement à l'origine de l'appel.
	 * @param session
	 *            Session Nuxeo unrestricted.
	 * @throws NuxeoException
	 */
	protected abstract void handleEvent(Event event, CoreSession session) throws NuxeoException;

	private class EventListenerUnrestrictedSessionRunner extends UnrestrictedSessionRunner {
		private final Event event;

		public EventListenerUnrestrictedSessionRunner(CoreSession session, Event event) {
			super(session);
			this.event = event;
		}

		public EventListenerUnrestrictedSessionRunner(String repositoryName, Event event) {
			super(repositoryName);
			this.event = event;
		}

		@Override
		public void run() throws NuxeoException {
			handleEvent(event, session);
		}
	}
}
