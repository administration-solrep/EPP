package fr.dila.cm.core.event;

import fr.dila.cm.core.service.SetMailboxAclUnrestricted;
import fr.dila.cm.mailbox.MailboxConstants;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener for mailbox events that sets user/groups rights when mailbox is
 * created/edited.
 *
 * @author Anahide Tchertchian
 */
public class UpdateMailboxRightsListener implements EventListener {

    public void handleEvent(Event event) {
        DocumentEventContext docCtx;
        if (event.getContext() instanceof DocumentEventContext) {
            docCtx = (DocumentEventContext) event.getContext();
        } else {
            return;
        }
        // set all rights to mailbox users

        DocumentModel doc = docCtx.getSourceDocument();
        if (!doc.hasFacet(MailboxConstants.MAILBOX_FACET)) {
            return;
        }
        CoreSession session = docCtx.getCoreSession();
        SetMailboxAclUnrestricted sessionCreator = new SetMailboxAclUnrestricted(session, doc.getRef());
        sessionCreator.runUnrestricted();
    }
}
