package fr.dila.solonepp.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.cm.mailbox.MailboxConstants;

/**
 * Gestionnaire d'évènements qui permet de traiter les évènements de création des documents de type Mailbox.
 * 
 * @author jtremeaux
 */
public class MailboxCreationListener implements EventListener {

	public MailboxCreationListener() {
	}

	@Override
	public void handleEvent(Event event) throws ClientException {
		// Traite uniquement les évènements de création de document
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}
		DocumentEventContext context = (DocumentEventContext) ctx;

		// Traite uniquement les documents de type Mailbox
		DocumentModel doc = context.getSourceDocument();
		String docType = doc.getType();
		if (!MailboxConstants.MAILBOX_DOCUMENT_TYPE.equals(docType)) {
			return;
		}

		// NOP
	}
}
