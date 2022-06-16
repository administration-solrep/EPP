package fr.dila.ss.core.event;

import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.security.CaseManagementSecurityConstants;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.core.event.AbstractEventListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;

/**
 * Surcharge de l'évenement Nuxeo pour ne mettre les droits à la distribution que sur le Case
 *
 * @author bgamard
 */
public class DistributionListener extends AbstractEventListener {

    /**
     * Default constructor
     */
    public DistributionListener() {
        super();
    }

    @SuppressWarnings("unchecked")
    public void doHandleEvent(Event event) {
        EventContext eventCtx = event.getContext();
        // set all rights to mailbox users

        Object envelopeObject = eventCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_CASE);
        if (!(envelopeObject instanceof STDossier)) {
            return;
        }
        STDossier envelope = (STDossier) envelopeObject;

        Map<String, List<String>> recipients = (Map<String, List<String>>) eventCtx.getProperty(
            CaseManagementEventConstants.EVENT_CONTEXT_INTERNAL_PARTICIPANTS
        );
        if (recipients == null) {
            return;
        }
        try {
            SetEnvelopeAclUnrestricted session = new SetEnvelopeAclUnrestricted(
                eventCtx.getCoreSession(),
                envelope,
                recipients
            );
            session.runUnrestricted();
        } catch (Exception e) {
            throw new NuxeoException(e.getMessage(), e);
        }
    }

    public static class SetEnvelopeAclUnrestricted extends UnrestrictedSessionRunner {
        protected final STDossier envelope;

        protected final Map<String, List<String>> recipients;

        protected Set<String> allMailboxIds = new HashSet<String>();

        public SetEnvelopeAclUnrestricted(
            CoreSession session,
            STDossier envelope,
            Map<String, List<String>> recipients
        ) {
            super(session);
            this.envelope = envelope;
            this.recipients = recipients;
        }

        @Override
        public void run() {
            for (Map.Entry<String, List<String>> recipient : recipients.entrySet()) {
                allMailboxIds.addAll(recipient.getValue());
            }
            if (!allMailboxIds.isEmpty()) {
                DocumentModel envelopeDoc = envelope.getDocument();
                if (envelopeDoc != null) {
                    setRightsOnCaseItems(envelopeDoc.getRef());
                }
            }
        }

        protected void setRightsOnCaseItems(DocumentRef docRef) {
            final ACP acp = session.getACP(docRef);
            final ACL mailboxACL = acp.getOrCreateACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
            final List<ACE> newACE = getNewACEs();
            mailboxACL.addAll(newACE);
            acp.removeACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
            acp.addACL(mailboxACL);
            session.setACP(docRef, acp, true);
        }

        protected List<ACE> getNewACEs() {
            final List<ACE> newACEs = new LinkedList<ACE>();
            // compute private ace
            for (String mailboxId : allMailboxIds) {
                newACEs.add(
                    new ACE(
                        CaseManagementSecurityConstants.MAILBOX_PREFIX + mailboxId,
                        SecurityConstants.READ_WRITE,
                        true
                    )
                );
            }
            return newACEs;
        }
    }
}
