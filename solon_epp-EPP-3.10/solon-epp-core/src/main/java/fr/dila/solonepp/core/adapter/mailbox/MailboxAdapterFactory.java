package fr.dila.solonepp.core.adapter.mailbox;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.solonepp.core.domain.mailbox.MailboxImpl;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier dossier Mailbox.
 * 
 * @author jtremeaux
 */
public class MailboxAdapterFactory implements DocumentAdapterFactory {

	@Override
	public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
		if (!checkDocument(doc)) {
			return null;
		}

		return new MailboxImpl(doc);
	}

	protected Boolean checkDocument(DocumentModel doc) {
		// Vérifie si le document est une Mailbox
		if (!doc.hasFacet(MailboxConstants.MAILBOX_FACET)) {
			return false;
		}

		return true;
	}

}
