/*
 * (C) Copyright 2006-2009 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Anahide Tchertchian
 *
 * $Id$
 */

package fr.dila.cm.core.event;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;

import fr.dila.cm.cases.Case;
import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.cm.security.CaseManagementSecurityConstants;

/**
 * Listener for distribution events that sets recipient mailbox user/groups
 * rights on the envelope and related documents.
 *
 * @author Anahide Tchertchian
 */
public class DistributionListener implements EventListener {

    @SuppressWarnings("unchecked")
    public void handleEvent(Event event) throws ClientException {
        EventContext eventCtx = event.getContext();
        // set all rights to mailbox users

        Object envelopeObject = eventCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_CASE);
        if (!(envelopeObject instanceof Case)) {
            return;
        }
        Case envelope = (Case) envelopeObject;
        @SuppressWarnings("rawtypes")
        Map<String, List<String>> recipients = (Map) eventCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_INTERNAL_PARTICIPANTS);
        if (recipients == null) {
            return;
        }
        try {
            SetEnvelopeAclUnrestricted session = new SetEnvelopeAclUnrestricted(
                    eventCtx.getCoreSession(), envelope, recipients);
            session.runUnrestricted();
        } catch (Exception e) {
            throw new CaseManagementRuntimeException(e.getMessage(), e);
        }

    }

    public static class SetEnvelopeAclUnrestricted extends
            UnrestrictedSessionRunner {

        protected final Case envelope;

        protected final Map<String, List<String>> recipients;

        protected Set<String> allMailboxIds = new HashSet<String>();

        protected List<ACE> newACEs = new LinkedList<ACE>();

        protected String confidentiality;

        public SetEnvelopeAclUnrestricted(CoreSession session, Case envelope,
                Map<String, List<String>> recipients) {
            super(session);
            this.envelope = envelope;
            this.recipients = recipients;
        }

        @Override
        public void run() throws ClientException {
            for (Map.Entry<String, List<String>> recipient : recipients.entrySet()) {
                allMailboxIds.addAll(recipient.getValue());
            }
            if (!allMailboxIds.isEmpty()) {
                List<DocumentModel> docs = new ArrayList<DocumentModel>();
                DocumentModel envelopeDoc = envelope.getDocument();
                if (envelopeDoc != null) {
                    docs.add(envelopeDoc);
                }
                
                setRightsOnCaseItems(docs);
            }
        }

        protected void setRightsOnCaseItems(List<DocumentModel> docs)
                throws ClientException {
            for (DocumentModel doc : docs) {
                doc = session.getDocument(doc.getRef());
                ACP acp = doc.getACP();
                ACL mailboxACL = acp.getOrCreateACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
                List<ACE> newACE = getNewACEs();
                mailboxACL.addAll(newACE);
                acp.removeACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
                acp.addACL(mailboxACL);
                session.setACP(doc.getRef(), acp, true);
            }
        }

        protected List<ACE> getNewACEs() {
            if (newACEs.isEmpty()) {
                // compute private ace
                for (String mailboxId : allMailboxIds) {
                    newACEs.add(new ACE(
                            CaseManagementSecurityConstants.MAILBOX_PREFIX
                                    + mailboxId, SecurityConstants.READ_WRITE,
                            true));
                }
                // set public ACE if needed
                if (CaseManagementSecurityConstants.PUBLIC_SECURITY_LEVEL.equals(confidentiality)) {
                    newACEs.add(new ACE(SecurityConstants.EVERYONE,
                            SecurityConstants.READ, true));
                }
            }
            return newACEs;
        }
    }

}