/*
 * (C) Copyright 2006-2009 Nuxeo SA (http://nuxeo.com/) and contributors.
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
 *
 * $Id$
 */

package fr.dila.st.web.action;

import static org.jboss.seam.ScopeType.CONVERSATION;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.pathsegment.PathSegmentService;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.runtime.api.Framework;

/**
 * Surchage du DocumentActionsBean de nuxeo
 */
@Name("documentActions")
@Scope(CONVERSATION)
@Install(precedence = Install.DEPLOYMENT)
public class DocumentActionsBean extends org.nuxeo.ecm.webapp.contentbrowser.DocumentActionsBean {

	private static final long	serialVersionUID	= 1611842298333770337L;

	private static final Log	LOGGER				= LogFactory.getLog(DocumentActionsBean.class);

	/**
	 * Default constructor
	 */
	public DocumentActionsBean() {
		super();
	}

	/**
	 * Surcharge afin d'ajouter un custom message "document.created.'type du document'"
	 */
	@Override
	public String saveDocument(DocumentModel newDocument) throws ClientException {
		// Document has already been created if it has an id.
		// This will avoid creation of many documents if user hit create button
		// too many times.
		if (newDocument.getId() != null) {
			LOGGER.debug("Document " + newDocument.getName() + " already created");
			return navigationContext.navigateToDocument(newDocument, "after-create");
		}
		try {
			PathSegmentService pss;
			try {
				pss = Framework.getService(PathSegmentService.class);
			} catch (Exception e) {
				throw new ClientException(e);
			}
			if (parentDocumentPath == null) {
				if (currentDocument == null) {
					// creating item at the root
					parentDocumentPath = documentManager.getRootDocument().getPathAsString();
				} else {
					parentDocumentPath = navigationContext.getCurrentDocument().getPathAsString();
				}
			}

			newDocument.setPathInfo(parentDocumentPath, pss.generatePathSegment(newDocument));

			newDocument = documentManager.createDocument(newDocument);
			documentManager.save();

			logDocumentWithTitle("Created the document: ", newDocument);

			String key = "document.created." + newDocument.getType();
			String messageLabel = resourcesAccessor.getMessages().get(key);
			if (StringUtils.isNotEmpty(messageLabel) && !key.equals(messageLabel)) {
				facesMessages.add(StatusMessage.Severity.INFO, messageLabel);
			} else {
				facesMessages.add(StatusMessage.Severity.INFO, resourcesAccessor.getMessages().get("document_saved"),
						resourcesAccessor.getMessages().get(newDocument.getType()));
			}
			Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED, currentDocument);
			return navigationContext.navigateToDocument(newDocument, "after-create");
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	/**
	 * Saves changes hold by the changeableDocument document model.
	 */
	@Override
	public String updateDocument() throws ClientException {
		try {
			DocumentModel changeableDocument = navigationContext.getChangeableDocument();
			Events.instance().raiseEvent(EventNames.BEFORE_DOCUMENT_CHANGED, changeableDocument);
			changeableDocument = documentManager.saveDocument(changeableDocument);
			throwUpdateComments(changeableDocument);
			documentManager.save();
			// some changes (versioning) happened server-side, fetch new one
			navigationContext.invalidateCurrentDocument();

			String key = "document.modified." + changeableDocument.getType();
			String messageLabel = resourcesAccessor.getMessages().get(key);
			if (StringUtils.isNotEmpty(messageLabel) && !key.equals(messageLabel)) {
				facesMessages.add(StatusMessage.Severity.INFO, messageLabel);
			} else {
				facesMessages.add(StatusMessage.Severity.INFO,
						resourcesAccessor.getMessages().get("document_modified"),
						resourcesAccessor.getMessages().get(changeableDocument.getType()));
			}

			EventManager.raiseEventsOnDocumentChange(changeableDocument);
			return navigationContext.navigateToDocument(changeableDocument, "after-edit");
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	// Send the comment of the update to the Core
	private void throwUpdateComments(DocumentModel changeableDocument) {
		if (comment != null && !"".equals(comment)) {
			changeableDocument.getContextData().put("comment", comment);
		}
	}

}
