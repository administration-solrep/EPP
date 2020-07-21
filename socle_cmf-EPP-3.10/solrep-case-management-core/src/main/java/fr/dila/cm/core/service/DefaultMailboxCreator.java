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

package fr.dila.cm.core.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.exception.CaseManagementException;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.mailbox.MailboxConstants;
import fr.dila.cm.service.CaseManagementDocumentTypeService;
import fr.dila.cm.service.MailboxCreator;

/**
 * @author Anahide Tchertchian
 */
public class DefaultMailboxCreator implements MailboxCreator {

	protected static final String	CM_DEFAULT_MAILBOX_CREATOR_SKIP	= "cm.defaultMailboxCreator.skip";

	private static final Log		log								= LogFactory.getLog(DefaultMailboxCreator.class);

	public String getPersonalMailboxId(DocumentModel userModel) {
		String userId = userModel.getId();
		return IdUtils.generateId(NuxeoPrincipal.PREFIX + userId);
	}

	public List<Mailbox> createMailboxes(CoreSession session, String user) throws CaseManagementException {
		try {
			// Retrieve the user
			UserManager userManager = Framework.getService(UserManager.class);
			if (userManager == null) {
				throw new CaseManagementException("User manager not found");
			}

			DocumentModel userModel = userManager.getUserModel(user);
			if (userModel == null) {
				log.debug(String.format("No User by that name. Maybe a wrong id or virtual user"));
				return Collections.emptyList();
			}

			return createMailboxes(session, userModel);

		} catch (Exception e) {
			throw new CaseManagementException("Error during mailboxes creation", e);
		}
	}

	public List<Mailbox> createMailboxes(CoreSession session, DocumentModel userModel) throws CaseManagementException {
		String skipCreation = Framework.getProperty(CM_DEFAULT_MAILBOX_CREATOR_SKIP);
		if (Boolean.TRUE.toString().equals(skipCreation)) {
			return Collections.emptyList();
		}

		try {
			// Create the personal mailbox for the user
			DocumentModel mailboxModel = session.createDocumentModel(getMailboxType());
			Mailbox mailbox = mailboxModel.getAdapter(Mailbox.class);

			// Set mailbox properties
			mailbox.setId(getPersonalMailboxId(userModel));
			mailbox.setTitle(getUserDisplayName(userModel));
			mailbox.setOwner(userModel.getId());
			mailbox.setType(MailboxConstants.type.personal.name());

			// XXX: save it in first mailbox folder found for now
			DocumentModelList res = session.query(
					String.format("SELECT * from %s", MailboxConstants.MAILBOX_ROOT_DOCUMENT_TYPE), null, 1, 0, false);
			if (res == null || res.isEmpty()) {
				throw new CaseManagementException("Cannot find any mailbox folder");
			}

			mailboxModel.setPathInfo(res.get(0).getPathAsString(), IdUtils.generateId(mailbox.getTitle()));
			mailboxModel = session.createDocument(mailboxModel);
			session.save();// This will be queried after, needs a save to be found
			mailbox = mailboxModel.getAdapter(Mailbox.class);

			return Collections.singletonList(mailbox);
		} catch (Exception e) {
			throw new CaseManagementException("Error during mailboxes creation", e);
		}
	}

	protected String getUserSchemaName() {
		return "user";
	}

	protected String getUserDisplayName(DocumentModel userModel) throws ClientException {
		String schemaName = getUserSchemaName();
		String first = (String) userModel.getProperty(schemaName, "firstName");
		String last = (String) userModel.getProperty(schemaName, "lastName");
		if (first == null || first.length() == 0) {
			if (last == null || last.length() == 0) {
				return userModel.getId();
			} else {
				return last;
			}
		} else {
			if (last == null || last.length() == 0) {
				return first;
			} else {
				return first + ' ' + last;
			}
		}
	}

	private String getMailboxType() throws ClientException {
		CaseManagementDocumentTypeService correspDocumentTypeService;
		try {
			correspDocumentTypeService = Framework.getService(CaseManagementDocumentTypeService.class);
		} catch (Exception e) {
			throw new ClientException(e);
		}
		return correspDocumentTypeService.getMailboxType();
	}
}
