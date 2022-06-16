package fr.dila.solonepp.core.adapter.mailbox;

import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.solonepp.core.domain.mailbox.MailboxImpl;
import fr.dila.st.core.adapter.STDocumentAdapterFactory;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Fabrique d'adapteur de DocumentModel vers l'objet métier dossier Mailbox.
 *
 * @author jtremeaux
 */
public class MailboxAdapterFactory implements STDocumentAdapterFactory {

    @Override
    public Object getAdapter(DocumentModel doc, @SuppressWarnings("rawtypes") Class arg1) {
        checkDocumentFacet(doc, MailboxConstants.MAILBOX_FACET);

        return new MailboxImpl(doc);
    }
}
