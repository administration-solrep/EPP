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

package fr.dila.cm.web.cases;

import static org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager.CURRENT_DOCUMENT_SELECTION;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.trash.TrashService;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.caselink.CaseLink;
import fr.dila.cm.caselink.CaseLinkRequestImpl;
import fr.dila.cm.cases.Case;
import fr.dila.cm.cases.CaseConstants;
import fr.dila.cm.mailbox.Mailbox;
import fr.dila.cm.service.CaseDistributionService;
import fr.dila.cm.web.distribution.CaseManagementDistributionActionsBean;
import fr.dila.cm.web.invalidations.CaseManagementContextBound;
import fr.dila.cm.web.mailbox.CaseManagementAbstractActionsBean;
import fr.dila.ecm.platform.routing.api.DocumentRoutingService;

/**
 * @author Nicolas Ulrich
 */
@Name("cmCaseActions")
@Scope(ScopeType.CONVERSATION)
@CaseManagementContextBound
@Install(precedence = Install.FRAMEWORK)
public class CaseManagementCaseActionsBean extends
        CaseManagementAbstractActionsBean {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(CaseManagementDistributionActionsBean.class);

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(required = true, create = true)
    protected NavigationContext navigationContext;

    @In(create = true)
    protected transient CaseDistributionService caseDistributionService;

    protected transient TrashService trashService;

    /**
     * @return true if this envelope is still in draft
     */
    public boolean isInitialCase() throws ClientException {
        Case env = getCurrentCase();

        if (env != null) {
            return getCurrentCase().isDraft();
        } else {
            return false;
        }
    }

    public DocumentRoutingService getDocumentRoutingService() {
        try {
            return Framework.getService(DocumentRoutingService.class);

        } catch (Exception e) {
            throw new ClientRuntimeException(e);
        }
    }

    @Override
    protected void resetCaseCache(Case cachedEnvelope, Case newEnvelope)
            throws ClientException {
        super.resetCaseCache(cachedEnvelope, newEnvelope);
    }

    /**
     * Returns true if we have an empty case
     * */
    public boolean isEmptyCase() throws ClientException {
        Case currentCase = getCurrentCase();
        if (currentCase != null) {
            return getCurrentCase().isEmpty();
        }
        return true;
    }

    public String markAsSent() throws ClientException {
        if (!documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION)) {
            List<DocumentModel> workingList = documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
            CaseLink post = null;
            DocumentModel parentDoc = null;
            Mailbox parentMailbox = null;
            Case envelope = null;
            CaseLink postRequest = null;
            for (DocumentModel documentModel : workingList) {
                post = documentModel.getAdapter(CaseLink.class);
                parentDoc = documentManager.getParentDocument(post.getDocument().getRef());
                parentMailbox = parentDoc.getAdapter(Mailbox.class);
                envelope = post.getCase(documentManager);
                postRequest = new CaseLinkRequestImpl(parentMailbox.getId(),
                        post.getDate(),
                        (String) envelope.getDocument().getPropertyValue(
                                CaseConstants.TITLE_PROPERTY_NAME),
                        post.getComment(), envelope,
                        post.getInitialInternalParticipants(),
                        post.getInitialExternalParticipants());

                caseDistributionService.sendCase(documentManager, postRequest,
                        true);
                EventManager.raiseEventsOnDocumentChildrenChange(parentDoc);
            }
        }
        return null;
    }

    public String purgeCaseSelection() throws ClientException {
        if (!isEmptyDraft()) {
            List<DocumentModel> currentDraftCasesList = documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
            purgeCaseSelection(currentDraftCasesList);
            EventManager.raiseEventsOnDocumentChildrenChange(getCurrentMailbox().getDocument());
        } else {
            log.debug("No documents selection in context to process delete on...");
        }
        return null;
    }

    public boolean isEmptyDraft() {
        return documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
    }

    public boolean getCanPurge() {
        List<DocumentModel> docs = documentsListsManager.getWorkingList(CURRENT_DOCUMENT_SELECTION);
        if (docs.isEmpty()) {
            return false;
        }
        try {
            return getTrashService().canDelete(docs,
                    documentManager.getPrincipal(), false);
        } catch (ClientException e) {
            log.error("Cannot check delete permission", e);
            return false;
        }
    }

    protected void purgeCaseSelection(List<DocumentModel> workingList)
            throws ClientException {
        final List<DocumentRef> caseRefs = new ArrayList<DocumentRef>();
        final List<DocumentRef> postRefs = new ArrayList<DocumentRef>();
        for (DocumentModel documentModel : workingList) {
            CaseLink caselink = documentModel.getAdapter(CaseLink.class);
            caseRefs.add(caselink.getCase(documentManager).getDocument().getRef());
            postRefs.add(documentModel.getRef());
        }
        new UnrestrictedSessionRunner(documentManager) {
            @Override
            public void run() throws ClientException {
                // permanently delete cases
                getTrashService().purgeDocuments(session, caseRefs);
                // permanently delete caseLinks
                getTrashService().purgeDocuments(session, postRefs);
            }
        }.runUnrestricted();
    }

    protected TrashService getTrashService() {
        if (trashService == null) {
            try {
                trashService = Framework.getService(TrashService.class);
            } catch (Exception e) {
                throw new RuntimeException("TrashService not available", e);
            }
        }
        return trashService;
    }
}