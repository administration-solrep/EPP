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

package fr.dila.cm.web.caseitem;

import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.preview.seam.PreviewActionBean;
import org.nuxeo.ecm.platform.types.Type;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.ecm.platform.types.adapter.TypeInfo;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.UserAction;
import org.nuxeo.ecm.webapp.action.TypesTool;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.cm.cases.Case;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.cm.web.CaseManagementWebConstants;
import fr.dila.cm.web.invalidations.CaseManagementContextBound;
import fr.dila.cm.web.invalidations.CaseManagementContextBoundInstance;

/**
 * @author Anahide Tchertchian
 *
 */
@Name("cmDocumentActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
@CaseManagementContextBound
public class CaseItemDocumentActionsBean extends
        CaseManagementContextBoundInstance implements
        CaseManagementDocumentActions {

    private static final String DOCUMENT_SAVED = "document_saved";

    private static final long serialVersionUID = 1L;

    @In(create = true)
    protected TypeManager typeManager;

    @In(create = true)
    protected transient NavigationContext navigationContext;

    @In(create = true)
    protected transient CaseDistributionService caseDistributionService;

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true)
    protected transient PreviewActionBean previewActions;

    @In(create = true)
    protected TypesTool typesTool;

    protected Boolean editingMail = false;

    /**
     * Returns the create view of given document type.
     */
    public String createDocument(String typeName) throws ClientException {
        Type docType = typeManager.getType(typeName);
        // we cannot use typesTool as intermediary since the DataModel callback
        // will alter whatever type we set
        typesTool.setSelectedType(docType);
        try {
            DocumentModel changeableDocument = documentManager.createDocumentModel(typeName);
            changeableDocument.putContextData(
                    CaseManagementWebConstants.CREATE_NEW_CASE_KEY, true);
            navigationContext.setChangeableDocument(changeableDocument);
            return navigationContext.getActionResult(changeableDocument,
                    UserAction.CREATE);
        } catch (Throwable t) {
            throw ClientException.wrap(t);
        }
    }

    public String createEmptyCase() throws ClientException {
        DocumentModel caseDoc = navigationContext.getChangeableDocument();
        Mailbox currentMailbox = getCurrentMailbox();
        Case emptyCase = caseDistributionService.createEmptyCase(
                documentManager, caseDoc, currentMailbox);
        caseDistributionService.createDraftCaseLink(documentManager,
                getCurrentMailbox(), emptyCase);
        facesMessages.add(FacesMessage.SEVERITY_INFO,
                resourcesAccessor.getMessages().get(DOCUMENT_SAVED),
                resourcesAccessor.getMessages().get(caseDoc.getType()));
        Events.instance().raiseEvent(
                EventNames.DOCUMENT_CHILDREN_CHANGED,
                documentManager.getDocument(currentMailbox.getDocument().getRef()));
        caseDoc = emptyCase.getDocument();
        navigationContext.setCurrentDocument(caseDoc);
        TypeInfo typeInfo = caseDoc.getAdapter(TypeInfo.class);
        return typeInfo.getDefaultView();
    }

    protected DocumentModel getParentFolder() throws ClientException {
        return caseDistributionService.getParentDocumentForCase(documentManager);
    }

    public String backToMailbox() throws ClientException {
        return returnToDocView();
    }

    protected String returnToDocView() throws ClientException {
        DocumentModel doc = getCurrentCase().getDocument();
        TypeInfo typeInfo = doc.getAdapter(TypeInfo.class);
        return typeInfo.getDefaultView();
    }

    public String save() throws ClientException {
        return returnToDocView();
    }

    @Override
    protected void resetCurrentCaseItemCache(DocumentModel cachedEmail,
            DocumentModel newEmail) throws ClientException {
        editingMail = false;
    }

}
