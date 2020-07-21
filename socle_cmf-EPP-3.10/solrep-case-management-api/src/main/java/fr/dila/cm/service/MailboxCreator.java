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

package fr.dila.cm.service;

import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.cm.exception.CaseManagementException;
import fr.dila.cm.mailbox.Mailbox;

/**
 * Interface for creation of personal mailbox.
 * 
 * @author Anahide Tchertchian
 */
public interface MailboxCreator {

	String getPersonalMailboxId (DocumentModel userModel);

	List<Mailbox> createMailboxes (CoreSession session, String user) throws CaseManagementException;

	List<Mailbox> createMailboxes (CoreSession session, DocumentModel userModel) throws CaseManagementException;

}
