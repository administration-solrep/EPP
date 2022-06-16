package fr.sword.naiad.nuxeo.commons.core.listener;

import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener abstrait nécessitant un contexte de type DocumentEventContext.
 * 
 * @author fmh
 */
public abstract class AbstractDocumentEventListener implements EventListener {
	@Override
	public final void handleEvent(Event event) throws NuxeoException {
		DocumentEventContext context = null;
		if (event.getContext() instanceof DocumentEventContext) {
			context = (DocumentEventContext) event.getContext();
		} else {
			return;
		}

		handleDocumentEvent(event, context);
	}

	/**
	 * Méthode à implémenter dans les classes dérivées. Appelée uniquement si le
	 * contexte est bien de type DocumentEventContext.
	 * 
	 * @param event
	 *            Evènement.
	 * @param context
	 *            Contexte de l'évènment, casté en DocumentEventContext.
	 * @throws NuxeoException
	 */
	public abstract void handleDocumentEvent(Event event,
			DocumentEventContext context) throws NuxeoException;
}
