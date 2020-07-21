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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *     Nicolas Ulrich
 *
 */

package fr.dila.cm.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.cases.Case;
import fr.dila.cm.mailbox.Mailbox;

/**
 * Correspondence service.
 * <p>
 * Distributes an email to users/groups/mailboxes and manages mailboxes.
 */
public interface CaseDistributionService extends Serializable {
    /**
     * Send an envelope to a mailbox.
     */
    CaseLink sendCase(CoreSession session, CaseLink postRequest, boolean initial);

    /**
     * Remove a case link from the mailbox. It is the duty of a listener to
     * update the security on the case if necessary.
     *
     * @param link
     */
    void removeCaseLink(CaseLink link, CoreSession sessiion);

    /**
     * Returns the sent posts for given mailbox
     */
    List<CaseLink> getSentCaseLinks(CoreSession coreSession, Mailbox mailbox,
            long offset, long limit);

    /**
     * Returns the received posts for given mailbox
     */
    List<CaseLink> getReceivedCaseLinks(CoreSession coreSession,
            Mailbox mailbox, long offset, long limit);

    /**
     * Returns all the case links for this kase in this mailbox.
     *
     * @param session
     * @param mailbox if <code>null</code> returns the links of all mailboxes.
     * @param kase
     * @return
     */
    List<CaseLink> getCaseLinks(CoreSession session, Mailbox mailbox, Case kase);

    /**
     * Returns the draft posts for given mailbox
     */
    List<CaseLink> getDraftCaseLinks(CoreSession coreSession, Mailbox mailbox,
            long offset, long limit);

    /**
     * Returns the draft post of an envelope in given mailbox. Returns null if
     * post is not found.
     */
    CaseLink getDraftCaseLink(CoreSession session, Mailbox mailbox,
            String envelopeId);

    /**
     * @param session
     * @param changeableDocument
     * @param the mailbox in which this case is created
     * @param parentPath the path where the document and its envelope are
     *            created
     * @return an emptyCase
     */
    Case createEmptyCase(CoreSession session, DocumentModel caseDoc,
            Mailbox mailbox);
    Case createEmptyCase(CoreSession session, DocumentModel caseDoc,
            List<Mailbox> mailboxes);
    Case createEmptyCase(CoreSession session, String title, String id,
            List<Mailbox> mailboxes);
    Case createEmptyCase(CoreSession session, String title, String id,
                Mailbox mailbox);

    /**
     * Create a draft post for an envelope in given mailbox.
     */
    CaseLink createDraftCaseLink(CoreSession session, Mailbox mailbox,
            Case envelope);

    /**
     * Throw a core event.
     *
     * @param session The session use in the event context and to get the
     *            principal.
     * @param name the name of the event
     * @param document The document use for DocumentEventContext
     * @param eventProperties The properties used in the event context.
     */
    void notify(CoreSession session, String name, DocumentModel document,
            Map<String, Serializable> eventProperties);

    /**
     * Send an case to a mailbox.
     */
    CaseLink sendCase(CoreSession session, CaseLink postRequest,
            boolean initial, boolean actionable);

    /**
     * @return
     */
    DocumentModel getParentDocumentForCase(CoreSession session);

    /**
     * @param session
     * @return
     */
    String getParentDocumentPathForCase(CoreSession session);

    /**
     * @param session
     * @param kase
     * @return
     */
    String getParentDocumentPathForCaseItem(CoreSession session, Case kase);
}
