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
 *     Nicolas Ulrich
 *
 * $Id$
 */

package fr.dila.cm.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SimplePrincipal;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.exception.CaseManagementException;
import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.cm.service.MailboxManagementService;

/**
 * Listen to loginSuccess event and create the personal mailbox if needed
 * 
 * @author Nicolas Ulrich
 */
public class SuccessLoginListener implements EventListener {

	private static final Log	log	= LogFactory.getLog(SuccessLoginListener.class);

	public void handleEvent (Event event) throws ClientException {
		CoreSession session = null;
		try {
			MailboxManagementService nxcService = Framework.getService(MailboxManagementService.class);
			if (nxcService == null) {
				throw new CaseManagementException("CorrespondenceService not found.");
			}

			SimplePrincipal principal = (SimplePrincipal) event.getContext().getPrincipal();
			session = getCoreSession();

			UserManager userManager;
			try {
				userManager = Framework.getService(UserManager.class);
			} catch (Exception e) {
				throw new CaseManagementRuntimeException(e);
			}
			if (userManager == null) {
				throw new CaseManagementRuntimeException("User manager not found");
			}

			DocumentModel userModel;
			try {
				userModel = userManager.getUserModel(principal.getName());
			} catch (ClientException e) {
				throw new CaseManagementRuntimeException(e);
			}

			if (!nxcService.hasUserPersonalMailbox(session, userModel)) {
				nxcService.createPersonalMailboxes(session, userModel);
			}

		} catch (Exception e) {
			log.error("Error during personal mailbox creation.", e);
		} finally {
			if (session != null) {
				Repository.close(session);
			}
		}
	}

	protected CoreSession getCoreSession () throws Exception {
		RepositoryManager mgr = Framework.getService(RepositoryManager.class);
		if (mgr == null) {
			throw new ClientException("Cannot find RepositoryManager");
		}

		Repository repo = mgr.getDefaultRepository();
		return repo.open();
	}

}
