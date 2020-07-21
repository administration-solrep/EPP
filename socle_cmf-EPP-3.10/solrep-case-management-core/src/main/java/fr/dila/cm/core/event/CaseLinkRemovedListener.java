/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 */
package fr.dila.cm.core.event;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;

import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.security.CaseManagementSecurityConstants;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public class CaseLinkRemovedListener implements EventListener {

    @Override
    public void handleEvent(Event event) throws ClientException {
        EventContext eventCtx = event.getContext();
        Object envelopeObject = eventCtx.getProperty(CaseManagementEventConstants.EVENT_CONTEXT_CASE_LINK);
        if (!(envelopeObject instanceof CaseLink)) {
            return;
        }
        CaseLink link = (CaseLink) envelopeObject;
        CoreSession session = eventCtx.getCoreSession();
        DocumentModel mailbox = session.getParentDocument(link.getDocument().getRef());
        String caseId = link.getCase(session).getDocument().getId();
        String query = String.format(
                "Select * from CaseLink where ecm:parentId = '%s' and cslk:caseDocumentId = '%s'",
                mailbox.getId(), caseId);
        DocumentModelList links = session.query(query);
        if (links.size() == 1) { // this is a before event listener, we should
                                 // have at least the removed link still there
            DocumentModel kaseDocument = session.getDocument(new IdRef(caseId));
            ACP acp = kaseDocument.getACP();
            ACL mailboxACL = acp.getOrCreateACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
            List<ACE> newACES = new ArrayList<ACE>();
            for (ACE ace : mailboxACL.getACEs()) {
                if (!ace.getUsername().equals(
                        CaseManagementSecurityConstants.MAILBOX_PREFIX
                                + mailbox.getAdapter(Mailbox.class).getId())) {
                    newACES.add(ace);
                }
            }
            acp.removeACL(CaseManagementSecurityConstants.ACL_MAILBOX_PREFIX);
            mailboxACL.setACEs(newACES.toArray(new ACE[] {}));
            acp.addACL(mailboxACL);
            session.setACP(kaseDocument.getRef(), acp, true);
        }
    }
}
