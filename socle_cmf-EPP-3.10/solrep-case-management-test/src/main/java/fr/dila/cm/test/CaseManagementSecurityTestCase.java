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

package fr.dila.cm.test;

import static fr.dila.cm.caselink.CaseLinkConstants.CASE_DOCUMENT_ID_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.CASE_LINK_DOCUMENT_TYPE;
import static fr.dila.cm.caselink.CaseLinkConstants.IS_DRAFT_FIELD;
import static fr.dila.cm.caselink.CaseLinkConstants.SENDER_FIELD;

import java.util.UUID;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.storage.sql.TXSQLRepositoryTestCase;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.cases.Case;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.cm.service.MailboxManagementService;

/**
 * @author Anahide Tchertchian
 */
public class CaseManagementSecurityTestCase extends TXSQLRepositoryTestCase {

	protected UserManager				userManager;

	protected CaseDistributionService	caseDistributionService;

	protected MailboxManagementService	mailboxManagementService;

	protected static final String		administrator	= "Administrator";

	protected static final String		user			= "user";

	protected static final String		user1			= "user1";

	protected static final String		user2			= "user2";

	protected static final String		user3			= "user3";

	protected static DocumentModel		mailEnvelopeModel;

	protected Case						envelope1;

	protected static DocumentModel		mailEnvelopItemeModel;

	public CaseManagementSecurityTestCase() {
		super();
	}

	public CaseManagementSecurityTestCase(String name) {
		super();
	}

	@Override
	protected void deployRepositoryContrib() throws Exception {
		super.deployRepositoryContrib();
		// deploy repository manager
		deployBundle("org.nuxeo.ecm.core.api");

		// deploy search
		deployBundle("org.nuxeo.ecm.platform.search.api");

		// deploy api and core bundles
		deployBundle("org.nuxeo.ecm.platform.classification.core");
		deployBundle("fr.dila.ecm.platform.routing.core");
		deployBundle("org.nuxeo.ecm.automation.core");
		// deploy api and core bundles
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_API_BUNDLE);
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_CORE_BUNDLE);

		// needed for users
		deployBundle(CaseManagementTestConstants.DIRECTORY_BUNDLE);
		deployBundle(CaseManagementTestConstants.USERMANAGER_BUNDLE);
		deployBundle(CaseManagementTestConstants.TYPES_BUNDLE);
		deployBundle(CaseManagementTestConstants.DIRECTORY_SQL_BUNDLE);
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_TEST_BUNDLE);

		// needed for default hierarchy
		deployBundle(CaseManagementTestConstants.TEMPLATE_BUNDLE);

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		userManager = Framework.getService(UserManager.class);
		assertNotNull(userManager);

		caseDistributionService = Framework.getService(CaseDistributionService.class);
		assertNotNull(caseDistributionService);

		mailboxManagementService = Framework.getService(MailboxManagementService.class);
		assertNotNull(mailboxManagementService);
	}

	protected DocumentModel createDocument(String type, String id) throws Exception {
		DocumentModel document = session.createDocumentModel(type);
		document.setPathInfo("/", id);
		document = session.createDocument(document);
		return document;
	}

	public Case getMailEnvelope() throws Exception {
		DocumentModel model = getMailEnvelopeModel();
		DocumentModel doc = session.createDocument(model);
		session.saveDocument(doc);
		session.save();
		return doc.getAdapter(Case.class);
	}

	public DocumentModel getMailEnvelopeModel() throws Exception {
		CaseManagementDocumentTypeService correspDocumentTypeService = Framework
				.getService(CaseManagementDocumentTypeService.class);

		if (mailEnvelopeModel == null) {
			mailEnvelopeModel = session.createDocumentModel(CaseConstants.CASE_ROOT_DOCUMENT_PATH, UUID.randomUUID()
					.toString(), correspDocumentTypeService.getCaseType());
		}
		return mailEnvelopeModel;
	}

	public void createDraftPost(Mailbox mb, Case envelope) throws Exception {
		DocumentModel model = session.createDocumentModel(mb.getDocument().getPathAsString(), UUID.randomUUID()
				.toString(), CASE_LINK_DOCUMENT_TYPE);
		DocumentModel doc = session.createDocument(model);

		doc.setPropertyValue(CASE_DOCUMENT_ID_FIELD, envelope.getDocument().getId());
		doc.setPropertyValue(IS_DRAFT_FIELD, true);
		doc.setPropertyValue(SENDER_FIELD, mb.getId());

		session.saveDocument(doc);
		session.save();
	}

	protected Mailbox createPersonalMailbox(String name) throws Exception {
		return mailboxManagementService.createPersonalMailboxes(session, name).get(0);
	}

	protected DocumentModel getMailEnvelopeItemModel() throws Exception {
		if (mailEnvelopItemeModel == null) {
			mailEnvelopItemeModel = session.createDocumentModel("/", UUID.randomUUID().toString(),
					"IncomingCorrespondenceDocument");
		}
		return mailEnvelopItemeModel;
	}

	protected void setMailRootRigts() throws Exception {
		DocumentModel mailRootdoc = session.getDocument(new PathRef(CaseConstants.CASE_ROOT_DOCUMENT_PATH));
		ACL localACL = new ACLImpl(ACL.LOCAL_ACL);
		ACE ace = new ACE(user1, SecurityConstants.EVERYTHING, true);
		localACL.add(ace);
		ACP acp = mailRootdoc.getACP();
		acp.removeACL(ACL.LOCAL_ACL);
		acp.addACL(localACL);
		mailRootdoc.setACP(acp, true);
		session.save();
	}

}
