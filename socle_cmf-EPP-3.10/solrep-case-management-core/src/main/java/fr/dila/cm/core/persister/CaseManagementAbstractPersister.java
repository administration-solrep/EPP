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
package fr.dila.cm.core.persister;

import java.util.Date;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

import fr.dila.cm.cases.Case;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.cases.CaseTreeHelper;
import fr.dila.cm.service.CaseManagementPersister;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 * 
 */
public abstract class CaseManagementAbstractPersister implements CaseManagementPersister {

	@Override
	public DocumentModel getParentDocumentForCase (CoreSession session) {
		GetParentPathUnrestricted runner = new GetParentPathUnrestricted(session);
		return runner.getParentDocument();
	}

	@Override
	public String getParentDocumentPathForCase (CoreSession session) {
		GetParentPathUnrestricted runner = new GetParentPathUnrestricted(session);
		try {
			runner.runUnrestricted();
		} catch (ClientException e) {
			throw new RuntimeException(e);
		}
		return runner.getParentPath();
	}

	@Override
	public String getParentDocumentPathForCaseItem (CoreSession session, Case kase) {
		return getParentDocumentPathForCase(session);
	}

	public class GetParentPathUnrestricted extends UnrestrictedSessionRunner {
		protected String		parentPath;
		protected DocumentModel	parent;

		public GetParentPathUnrestricted(CoreSession session) {
			super(session);
		}

		public String getParentPath () {
			return parentPath;
		}

		public DocumentModel getParentDocument () {
			return parent;
		}

		@Override
		public void run () throws ClientException {
			// Retrieve the MailRoot folder
			DocumentModel mailRootdoc = session.getDocument(new PathRef(CaseConstants.CASE_ROOT_DOCUMENT_PATH));
			// Create (or retrieve) the current MailRoot folder
			// (/mail/YYYY/MM/DD)
			Date now = new Date();
			parent = CaseTreeHelper.getOrCreateDateTreeFolder(session, mailRootdoc, now, CaseConstants.CASE_TREE_TYPE);
			parentPath = parent.getPathAsString();
		}

	}
}
