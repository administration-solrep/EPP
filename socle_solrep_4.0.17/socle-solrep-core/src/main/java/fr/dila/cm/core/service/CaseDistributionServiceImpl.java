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
 *     Nicolas Ulrich
 *
 * $Id$
 */

package fr.dila.cm.core.service;

import fr.dila.cm.core.caselink.CreateCaseLink;
import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.event.CaseManagementEventConstants.EventNames;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.cm.service.CaseManagementPersister;
import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.api.dossier.STDossier;
import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Correspondence service core implementation
 */
public class CaseDistributionServiceImpl implements CaseDistributionService {
    private static final long serialVersionUID = 1L;

    protected CaseManagementPersister persister;

    public void setPersister(CaseManagementPersister persister) {
        this.persister = persister;
    }

    @Override
    public STDossierLink sendCase(CoreSession session, STDossierLink postRequest) {
        SendPostUnrestricted sendPostUnrestricted = new SendPostUnrestricted(session, postRequest);
        sendPostUnrestricted.runUnrestricted();
        return sendPostUnrestricted.getPost();
    }

    protected List<STDossierLink> getPosts(CoreSession coreSession, long offset, long limit, String query) {
        DocumentModelList result = coreSession.query(query, null, limit, offset, false);

        List<STDossierLink> posts = new ArrayList<>();
        for (DocumentModel documentModel : result) {
            posts.add(documentModel.getAdapter(STDossierLink.class));
        }
        return posts;
    }

    @Override
    public List<STDossierLink> getReceivedCaseLinks(CoreSession coreSession, Mailbox mailbox, long offset, long limit) {
        if (mailbox == null) {
            return null;
        }
        String query = String.format(
            "SELECT * FROM Document WHERE ecm:mixinType = 'CaseLink' AND ecm:parentId ='%s'",
            mailbox.getDocument().getId()
        );
        return getPosts(coreSession, offset, limit, query);
    }

    @Override
    public <T extends STDossier> T createEmptyCase(
        CoreSession session,
        DocumentModel caseDoc,
        Mailbox mailbox,
        Class<T> adapter
    ) {
        String parentPath = getParentDocumentPathForCase(session);
        DocumentRef caseDocRef = CreateEmptyCaseUnrestricted.create(session, caseDoc, parentPath, mailbox);
        caseDoc = session.getDocument(caseDocRef);
        return caseDoc.getAdapter(adapter);
    }

    /**
     * Retrieves and caches the Event Producer Service.
     *
     * @return The Event Producer Service
     */

    public static class SendPostUnrestricted extends UnrestrictedSessionRunner {
        private final STDossierLink postRequest;

        private STDossierLink post;

        public SendPostUnrestricted(CoreSession session, STDossierLink postRequest) {
            super(session);
            this.postRequest = postRequest;
        }

        @Override
        public void run() {
            final MailboxService mailboxService = STServiceLocator.getMailboxService();

            String senderMailboxId = postRequest.getSender();
            Mailbox senderMailbox = mailboxService.getMailbox(session, senderMailboxId);
            String subject = postRequest.getSubject();

            String comment = postRequest.getComment();
            STDossier envelope = postRequest.getDossier(session, STDossier.class);
            // Create Event properties
            Map<String, Serializable> eventProperties = new HashMap<>();

            Map<String, List<String>> externalRecipients = postRequest.getInitialExternalParticipants();

            Map<String, List<String>> internalRecipientIds = postRequest.getInitialInternalParticipants();

            internalRecipientIds.forEach(
                (type, recipientIds) -> {
                    String mailboxTitles = Optional
                        .ofNullable(mailboxService.getMailboxes(session, recipientIds))
                        .map(mailboxes -> mailboxes.stream().map(Mailbox::getTitle).collect(Collectors.joining(", ")))
                        .orElse("");
                    eventProperties.put(
                        CaseManagementEventConstants.EVENT_CONTEXT_PARTICIPANTS_TYPE_ + type,
                        mailboxTitles
                    );
                }
            );

            eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_SENDER_MAILBOX, senderMailbox);

            eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_SUBJECT, subject);
            eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_COMMENT, comment);
            eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_CASE, envelope);
            eventProperties.put(
                CaseManagementEventConstants.EVENT_CONTEXT_INTERNAL_PARTICIPANTS,
                (Serializable) internalRecipientIds
            );
            eventProperties.put(
                CaseManagementEventConstants.EVENT_CONTEXT_EXTERNAL_PARTICIPANTS,
                (Serializable) externalRecipients
            );
            eventProperties.put("category", CaseManagementEventConstants.DISTRIBUTION_CATEGORY);
            fireEvent(
                session,
                envelope,
                eventProperties,
                EventNames.beforeCaseSentEvent.name(),
                EventNames.beforeCaseItemSentEvent.name()
            );

            if (senderMailbox != null) {
                // No draft, create the Post for the sender
                CreateCaseLink createPost = new CreateCaseLink(
                    null,
                    session,
                    subject,
                    comment,
                    envelope,
                    senderMailbox,
                    senderMailbox.getId(),
                    internalRecipientIds,
                    externalRecipients
                );
                createPost.create();
                post = createPost.getCreatedPost();
            }

            // Create the Posts for the recipients
            for (String type : internalRecipientIds.keySet()) {
                for (String recipient : internalRecipientIds.get(type)) {
                    CreateCaseLink createMessage = new CreateCaseLink(
                        postRequest,
                        session,
                        subject,
                        comment,
                        envelope,
                        senderMailbox,
                        recipient,
                        internalRecipientIds,
                        externalRecipients
                    );
                    createMessage.create();
                }
            }

            fireEvent(
                session,
                envelope,
                eventProperties,
                EventNames.afterCaseSentEvent.name(),
                EventNames.afterCaseItemSentEvent.name()
            );
        }

        public STDossierLink getPost() {
            return post;
        }

        /**
         * Fire an event for a MailEnvelope object.
         */
        private void fireEvent(
            CoreSession coreSession,
            STDossier envelope,
            Map<String, Serializable> eventProperties,
            String caseEventName,
            String caseItemEventName
        ) {
            DocumentEventContext envContext = new DocumentEventContext(
                coreSession,
                coreSession.getPrincipal(),
                envelope.getDocument()
            );
            envContext.setProperties(eventProperties);
            EventProducer eventProducer = ServiceUtil.getRequiredService(EventProducer.class);
            eventProducer.fireEvent(envContext.newEvent(caseEventName));
        }
    }

    private String getParentDocumentPathForCase(CoreSession session) {
        return persister.getParentDocumentPathForCase(session);
    }
}
