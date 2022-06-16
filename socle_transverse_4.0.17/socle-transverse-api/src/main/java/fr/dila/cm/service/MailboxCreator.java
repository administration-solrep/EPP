package fr.dila.cm.service;

import fr.dila.cm.mailbox.Mailbox;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface for creation of personal mailbox.
 *
 * @author Anahide Tchertchian
 */
public interface MailboxCreator {
    String getPersonalMailboxId(String userId);

    Mailbox createMailboxes(CoreSession session, DocumentModel userModel);
}
