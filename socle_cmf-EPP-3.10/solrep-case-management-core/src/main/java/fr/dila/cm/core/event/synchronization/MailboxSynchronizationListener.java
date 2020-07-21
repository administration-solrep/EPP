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
 *     Laurent Doguin
 */
package fr.dila.cm.core.event.synchronization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.exception.CaseManagementRuntimeException;
import fr.dila.cm.service.synchronization.MailboxSynchronizationService;

/**
 * @author <a href="mailto:ldoguin@nuxeo.com">Laurent Doguin</a>
 */
public class MailboxSynchronizationListener implements EventListener {

	private static final Log	log			= LogFactory.getLog(MailboxSynchronizationListener.class);

	public static Boolean		doingSync	= false;

	public void handleEvent(Event event) throws ClientException {
		try {
			if (!doingSync) {
				doingSync = true;
				MailboxSynchronizationService syncService = Framework.getService(MailboxSynchronizationService.class);
				syncService.doSynchronize();
				doingSync = false;

			} else {
				log.info("Scheduled synchronization won't run because previous one ain't done yet.");
			}
		} catch (Exception e) {
			doingSync = false;
			throw new CaseManagementRuntimeException("Error during Mailboxes synchronization", e);
		}
	}

}
