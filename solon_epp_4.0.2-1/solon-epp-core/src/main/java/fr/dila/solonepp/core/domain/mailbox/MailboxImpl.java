package fr.dila.solonepp.core.domain.mailbox;

import fr.dila.solonepp.api.domain.mailbox.Mailbox;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Implémentation des Mailbox de l'application SOLON EPP.
 *
 * @author jtremeaux
 */
public class MailboxImpl extends fr.dila.cm.mailbox.MailboxImpl implements Mailbox {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 8820645351060312648L;

    /**
     * Constructeur de MailboxImpl.
     *
     * @param doc
     *            Modèle de document
     */
    public MailboxImpl(DocumentModel doc) {
        super(doc);
    }
}
