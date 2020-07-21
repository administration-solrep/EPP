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

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.caselink.CaseLinkConstants;
import fr.dila.cm.cases.Case;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.cases.CaseLifeCycleConstants;
import fr.dila.cm.core.caselink.CreateCaseLink;
import fr.dila.cm.core.caselink.CreateDraftCaseLinkUnrestricted;
import fr.dila.cm.core.caselink.UpdateCaseLinkUnrestricted;
import fr.dila.cm.event.CaseManagementEventConstants;
import fr.dila.cm.event.CaseManagementEventConstants.EventNames;
import fr.dila.cm.exception.CaseManagementException;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxHeader;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.cm.service.CaseManagementPersister;

/**
 * Correspondence service core implementation
 */
public class CaseDistributionServiceImpl implements CaseDistributionService {

	private static final long			serialVersionUID	= 1L;

	private static final Log			log					= LogFactory.getLog(CaseDistributionServiceImpl.class);

	protected EventProducer				eventProducer;

	protected Map<String, Serializable>	context;

	protected CaseManagementPersister	persister;

	public void setPersister(CaseManagementPersister persister) {
		this.persister = persister;
	}

	public CaseLink sendCase(CoreSession session, CaseLink postRequest, boolean isInitial) {
		try {
			SendPostUnrestricted sendPostUnrestricted = new SendPostUnrestricted(session, postRequest, isInitial);
			sendPostUnrestricted.runUnrestricted();
			return sendPostUnrestricted.getPost();
		} catch (Exception e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	public CaseLink sendCase(CoreSession session, CaseLink postRequest, boolean isInitial, boolean actionable) {
		try {
			SendPostUnrestricted sendPostUnrestricted = new SendPostUnrestricted(session, postRequest, isInitial,
					actionable);
			sendPostUnrestricted.runUnrestricted();
			return sendPostUnrestricted.getPost();
		} catch (Exception e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	protected List<CaseLink> getPosts(CoreSession coreSession, long offset, long limit, String query) {
		List<CaseLink> posts = new ArrayList<CaseLink>();
		DocumentModelList result;

		try {
			result = coreSession.query(query, null, limit, offset, false);
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}

		for (DocumentModel documentModel : result) {
			// TODO Lazy Loading
			posts.add(documentModel.getAdapter(CaseLink.class));
		}
		return posts;
	}

	public List<CaseLink> getReceivedCaseLinks(CoreSession coreSession, Mailbox mailbox, long offset, long limit) {
		if (mailbox == null) {
			return null;
		}
		String query = String.format("SELECT * FROM Document WHERE ecm:parentId ='%s' and %s=0", mailbox.getDocument()
				.getId(), CaseLinkConstants.IS_SENT_FIELD);
		return getPosts(coreSession, offset, limit, query);
	}

	public List<CaseLink> getCaseLinks(CoreSession session, Mailbox mailbox, Case kase) {
		String query = String.format(
				"SELECT * FROM Document WHERE ecm:mixinType = 'CaseLink' and cslk:caseDocumentId = '%s'", kase
						.getDocument().getId());
		if (mailbox != null) {
			query += String.format(" and ecm:parentId = '%s'", mailbox.getDocument().getId());
		}
		return getPosts(session, 0, 0, query);
	}

	public List<CaseLink> getSentCaseLinks(CoreSession coreSession, Mailbox mailbox, long offset, long limit) {
		if (mailbox == null) {
			return null;
		}
		String query = String.format("SELECT * FROM Document WHERE ecm:parentId ='%s' and %s=1", mailbox.getDocument()
				.getId(), CaseLinkConstants.IS_SENT_FIELD);
		return getPosts(coreSession, offset, limit, query);
	}

	public List<CaseLink> getDraftCaseLinks(CoreSession coreSession, Mailbox mailbox, long offset, long limit) {
		if (mailbox == null) {
			return null;
		}
		String query = String.format("SELECT * FROM Document WHERE ecm:parentId ='%s' and %s=1", mailbox.getDocument()
				.getId(), CaseLinkConstants.IS_DRAFT_FIELD);
		return getPosts(coreSession, offset, limit, query);
	}

	@Override
	public Case createEmptyCase(CoreSession session, String title, String id, Mailbox mailbox) {
		return createEmptyCase(session, title, id, Collections.singletonList(mailbox));
	}

	public Case createEmptyCase(CoreSession session, String title, String id, List<Mailbox> mailboxes) {
		String parentPath = getParentDocumentPathForCase(session);
		DocumentModel kase;
		try {
			kase = session.createDocumentModel(parentPath, id, getTypeService().getCaseType());
			kase.setPropertyValue(CaseConstants.TITLE_PROPERTY_NAME, title);
			kase.putContextData("initialLifecycleState", null);
			return createEmptyCase(session, kase, mailboxes);
		} catch (ClientException e) {
			throw new RuntimeException(e);
		}
	}

	public CaseManagementDocumentTypeService getTypeService() {
		try {
			return Framework.getService(CaseManagementDocumentTypeService.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Case createEmptyCase(CoreSession session, DocumentModel caseDoc, Mailbox mailbox) {
		return createEmptyCase(session, caseDoc, Collections.singletonList(mailbox));
	}

	@Override
	public Case createEmptyCase(CoreSession session, DocumentModel caseDoc, List<Mailbox> mailboxes) {
		String parentPath = getParentDocumentPathForCase(session);
		CreateEmptyCaseUnrestricted emptyCaseCreator = new CreateEmptyCaseUnrestricted(session, caseDoc, parentPath,
				mailboxes);
		try {
			emptyCaseCreator.runUnrestricted();
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
		DocumentRef caseDocRef = emptyCaseCreator.getEmptyCaseDocumentRef();
		try {
			caseDoc = session.getDocument(caseDocRef);
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
		return caseDoc.getAdapter(Case.class);
	}

	public CaseLink createDraftCaseLink(CoreSession session, Mailbox mailbox, Case envelope) {
		try {

			Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
			eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_CASE, envelope);
			eventProperties.put("category", CaseManagementEventConstants.DISTRIBUTION_CATEGORY);
			fireEvent(session, envelope, eventProperties, EventNames.beforeDraftCreated.name());
			CreateDraftCaseLinkUnrestricted runner = new CreateDraftCaseLinkUnrestricted(session.getRepositoryName(),
					(String) envelope.getDocument().getPropertyValue(CaseConstants.TITLE_PROPERTY_NAME), envelope,
					mailbox);
			runner.runUnrestricted();
			CaseLink draft = runner.getCreatedPost();
			eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_DRAFT, draft);
			fireEvent(session, envelope, eventProperties, EventNames.afterDraftCreated.name());
			return draft;
		} catch (ClientException e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	public CaseLink getDraftCaseLink(CoreSession coreSession, Mailbox mailbox, String envelopeId) {
		if (mailbox == null) {
			return null;
		}
		String query = String.format("SELECT * FROM Document WHERE ecm:parentId ='%s' AND %s='%s' and %s=1", mailbox
				.getDocument().getId(), CaseLinkConstants.CASE_DOCUMENT_ID_FIELD, envelopeId,
				CaseLinkConstants.IS_DRAFT_FIELD);
		List<CaseLink> result = getPosts(coreSession, 0, 0, query);
		int size = result.size();
		if (size > 1) {
			log.error("More than one draft for envelope '" + envelopeId + "'.");
			return null;
		} else if (size == 0) {
			return null;
		}

		return result.get(0);
	}

	/**
	 * Fire an event for a MailEnvelope object.
	 */
	protected void fireEvent(CoreSession coreSession, Case envelope, Map<String, Serializable> eventProperties,
			String caseEventName, String caseItemEventName) {
		try {
			DocumentEventContext envContext = new DocumentEventContext(coreSession, coreSession.getPrincipal(),
					envelope.getDocument());
			envContext.setProperties(eventProperties);
			getEventProducer().fireEvent(envContext.newEvent(caseEventName));
		} catch (Exception e) {
			throw new CaseManagementRuntimeException(e);
		}
	}

	/**
	 * Fire an event for a MailEnvelope object.
	 */
	protected void fireEvent(CoreSession coreSession, Case envelope, Map<String, Serializable> eventProperties,
			String caseEventName) {
		fireEvent(coreSession, envelope, eventProperties, caseEventName, null);
	}

	@Override
	public void removeCaseLink(final CaseLink link, CoreSession session) {
		final DocumentRef ref = link.getDocument().getRef();
		try {
			final Principal principal = session.getPrincipal();
			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() throws ClientException {
					try {
						Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();
						eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_CASE_LINK, link);
						eventProperties.put("category", CaseManagementEventConstants.DISTRIBUTION_CATEGORY);
						final DocumentEventContext envContext = new DocumentEventContext(session, principal,
								link.getDocument());
						envContext.setProperties(eventProperties);
						getEventProducer().fireEvent(envContext.newEvent(EventNames.beforeCaseLinkRemovedEvent.name()));
						session.removeDocument(ref);
						envContext.getProperties().remove(CaseManagementEventConstants.EVENT_CONTEXT_CASE_LINK);
						getEventProducer().fireEvent(envContext.newEvent(EventNames.afterCaseLinkRemovedEvent.name()));
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}.runUnrestricted();
		} catch (ClientException e) {
			throw new RuntimeException(e);
		}
	}

	public void notify(CoreSession session, String name, DocumentModel document,
			Map<String, Serializable> eventProperties) {
		DocumentEventContext envContext = new DocumentEventContext(session, session.getPrincipal(), document);
		envContext.setProperties(eventProperties);
		try {
			getEventProducer().fireEvent(envContext.newEvent(name));
		} catch (Exception e) {
			throw new CaseManagementRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Retrieves and caches the Event Producer Service.
	 * 
	 * @return The Event Producer Service
	 * @throws Exception
	 */
	protected EventProducer getEventProducer() throws Exception {
		if (eventProducer == null) {
			eventProducer = Framework.getService(EventProducer.class);
		}
		return eventProducer;
	}

	public class SendPostUnrestricted extends UnrestrictedSessionRunner {

		protected final CaseLink	postRequest;

		protected final boolean		isInitial;

		protected EventProducer		eventProducer;

		protected CaseLink			post;

		protected boolean			isActionable	= false;

		public SendPostUnrestricted(CoreSession session, CaseLink postRequest, boolean isInitial) {
			super(session);
			this.postRequest = postRequest;
			this.isInitial = isInitial;
		}

		public SendPostUnrestricted(CoreSession session, CaseLink postRequest, boolean isInitial, boolean isActionable) {
			super(session);
			this.postRequest = postRequest;
			this.isInitial = isInitial;
			this.isActionable = isActionable;
		}

		@Override
		public void run() throws CaseManagementException {

			try {
				String senderMailboxId = postRequest.getSender();
				GetMailboxesUnrestricted getMailboxesUnrestricted = new GetMailboxesUnrestricted(session,
						senderMailboxId);
				getMailboxesUnrestricted.run();
				List<Mailbox> senderMailboxes = getMailboxesUnrestricted.getMailboxes();
				Mailbox senderMailbox = null;
				if (senderMailboxes != null && !senderMailboxes.isEmpty()) {
					senderMailbox = senderMailboxes.get(0);
				}

				String subject = postRequest.getSubject();
				String comment = postRequest.getComment();
				Case envelope = postRequest.getCase(session);

				// Create Event properties
				Map<String, Serializable> eventProperties = new HashMap<String, Serializable>();

				Map<String, List<String>> externalRecipients = postRequest.getInitialExternalParticipants();
				Map<String, List<String>> internalRecipientIds = postRequest.getInitialInternalParticipants();
				List<String> mailboxTitles = new ArrayList<String>();
				for (String type : internalRecipientIds.keySet()) {
					// TODO: optimize;
					mailboxTitles.clear();
					List<MailboxHeader> mailboxesHeaders = new MailboxManagementServiceImpl().getMailboxesHeaders(
							session, internalRecipientIds.get(type));
					if (senderMailboxes != null) {
						for (MailboxHeader mailboxHeader : mailboxesHeaders) {
							mailboxTitles.add(mailboxHeader.getTitle());
						}
					}
					eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_PARTICIPANTS_TYPE_ + type,
							StringUtils.join(mailboxTitles, ", "));
				}

				eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_SENDER_MAILBOX, senderMailbox);
				eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_SUBJECT, subject);
				eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_COMMENT, comment);
				eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_CASE, envelope);
				eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_INTERNAL_PARTICIPANTS,
						(Serializable) internalRecipientIds);
				eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_EXTERNAL_PARTICIPANTS,
						(Serializable) externalRecipients);
				eventProperties.put("category", CaseManagementEventConstants.DISTRIBUTION_CATEGORY);
				eventProperties.put(CaseManagementEventConstants.EVENT_CONTEXT_IS_INITIAL, isInitial);

				fireEvent(session, envelope, eventProperties, EventNames.beforeCaseSentEvent.name(),
						EventNames.beforeCaseItemSentEvent.name());

				if (isInitial) {

					// Update the lifecycle of the envelope
					envelope.getDocument().followTransition(CaseLifeCycleConstants.TRANSITION_SEND);

					if (senderMailbox != null) {
						// Update the Draft post for the sender
						CaseLink draft = getDraftCaseLink(session, senderMailbox, envelope.getDocument().getId());

						if (draft == null) {
							throw new CaseManagementException("No draft for an initial send.");
						}

						UpdateCaseLinkUnrestricted createPostUnrestricted = new UpdateCaseLinkUnrestricted(session,
								subject, comment, envelope, senderMailbox, senderMailbox.getId(), internalRecipientIds,
								externalRecipients, true, isInitial, draft);
						createPostUnrestricted.run();
						post = createPostUnrestricted.getUpdatedPost();
					}

				} else if (senderMailbox != null) {
					// No draft, create the Post for the sender
					CreateCaseLink createPost = new CreateCaseLink(null, session, subject, comment, envelope,
							senderMailbox, senderMailbox.getId(), internalRecipientIds, externalRecipients, true,
							isInitial);
					createPost.create();
					post = createPost.getCreatedPost();
				}

				// Create the Posts for the recipients
				for (String type : internalRecipientIds.keySet()) {
					for (String recipient : internalRecipientIds.get(type)) {
						if (isActionable) {
							CreateCaseLink createMessage = new CreateCaseLink(postRequest, session, subject, comment,
									envelope, senderMailbox, recipient, internalRecipientIds, externalRecipients,
									false, isInitial);
							createMessage.create();
						} else {
							CreateCaseLink createMessage = new CreateCaseLink(post, session, subject, comment,
									envelope, senderMailbox, recipient, internalRecipientIds, externalRecipients,
									false, isInitial);
							createMessage.create();
						}
					}
				}

				fireEvent(session, envelope, eventProperties, EventNames.afterCaseSentEvent.name(),
						EventNames.afterCaseItemSentEvent.name());
			} catch (ClientException e) {
				throw new CaseManagementException(e);
			}
		}

		public CaseLink getPost() {
			return post;
		}
	}

	@Override
	public DocumentModel getParentDocumentForCase(CoreSession session) {
		return persister.getParentDocumentForCase(session);
	}

	@Override
	public String getParentDocumentPathForCaseItem(CoreSession session, Case kase) {
		return persister.getParentDocumentPathForCaseItem(session, kase);
	}

	@Override
	public String getParentDocumentPathForCase(CoreSession session) {
		return persister.getParentDocumentPathForCase(session);
	}
}
