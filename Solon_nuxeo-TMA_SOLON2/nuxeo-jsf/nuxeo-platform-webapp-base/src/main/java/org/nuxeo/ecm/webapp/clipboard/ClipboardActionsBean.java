/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

package org.nuxeo.ecm.webapp.clipboard;

import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.ScopeType.SESSION;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Remove;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;
import org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants;
import org.nuxeo.ecm.platform.ui.web.cache.SeamCacheHelper;
import org.nuxeo.ecm.platform.ui.web.tag.fn.Functions;
import org.nuxeo.ecm.platform.ui.web.util.BaseURL;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.webapp.base.InputController;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListDescriptor;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.EventManager;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.runtime.api.Framework;

/**
 * This is the action listener behind the copy/paste template that knows how to
 * copy/paste the selected user data to the target action listener, and also
 * create/remove the corresponding objects into the backend.
 *
 * @author <a href="mailto:rcaraghin@nuxeo.com">Razvan Caraghin</a>
 */
@Name("clipboardActions")
@Scope(SESSION)
public class ClipboardActionsBean extends InputController implements
        ClipboardActions, Serializable {

    private static final long serialVersionUID = -2407222456116573225L;

    private static final Log log = LogFactory.getLog(ClipboardActionsBean.class);

    private static final String PASTE_OUTCOME = "after_paste";

    /**
     * @deprecated use {@link LifeCycleConstants#DELETED_STATE}
     */
    @Deprecated
    public static final String DELETED_LIFECYCLE_STATE = "deleted";

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected transient DocumentsListsManager documentsListsManager;

    @In(create = true)
    protected transient WebActions webActions; // it is serializable

    @In(create = true)
    protected transient LocaleSelector localeSelector;

    @RequestParameter()
    protected String workListDocId;

    private String currentSelectedList;

    private String previouslySelectedList;

    private transient List<String> availableLists;

    private transient List<DocumentsListDescriptor> descriptorsForAvailableLists;

    private Boolean canEditSelectedDocs;

    private transient Map<String, List<Action>> actionCache;

    public void releaseClipboardableDocuments() {
    }

    public boolean isInitialized() {
        return documentManager != null;
    }

    public void putSelectionInWorkList(Boolean forceAppend) {
        canEditSelectedDocs = null;
        if (!documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION)) {
            putSelectionInWorkList(
                    documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION),
                    forceAppend);
            autoSelectCurrentList(DocumentsListsManager.DEFAULT_WORKING_LIST);
        } else {
            log.debug("No selectable Documents in context to process copy on...");
        }
        log.debug("add to worklist processed...");
    }

    public void putSelectionInWorkList() {
        putSelectionInWorkList(false);
    }

    public void putSelectionInDefaultWorkList() {
        canEditSelectedDocs = null;
        if (!documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION)) {
            List<DocumentModel> docsList = documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
            Object[] params = { docsList.size() };
            facesMessages.add(FacesMessage.SEVERITY_INFO, "#0 "
                    + resourcesAccessor.getMessages().get("n_copied_docs"),
                    params);
            documentsListsManager.addToWorkingList(
                    DocumentsListsManager.DEFAULT_WORKING_LIST, docsList);

            // auto select clipboard
            autoSelectCurrentList(DocumentsListsManager.DEFAULT_WORKING_LIST);

        } else {
            log.debug("No selectable Documents in context to process copy on...");
        }
        log.debug("add to worklist processed...");
    }

    @WebRemote
    public void putInClipboard(String docId) throws ClientException {
        DocumentModel doc = documentManager.getDocument(new IdRef(docId));
        documentsListsManager.addToWorkingList(DocumentsListsManager.CLIPBOARD,
                doc);
        Object[] params = { 1 };
        FacesMessage message = FacesMessages.createFacesMessage(
                FacesMessage.SEVERITY_INFO, "#0 "
                        + resourcesAccessor.getMessages().get("n_copied_docs"),
                params);
        facesMessages.add(message);
        autoSelectCurrentList(DocumentsListsManager.CLIPBOARD);
    }

    public void putSelectionInClipboard() {
        canEditSelectedDocs = null;
        if (!documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION)) {
            List<DocumentModel> docsList = documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
            Object[] params = { docsList.size() };

            FacesMessage message = FacesMessages.createFacesMessage(
                    FacesMessage.SEVERITY_INFO, "#0 "
                            + resourcesAccessor.getMessages().get(
                                    "n_copied_docs"), params);

            facesMessages.add(message);

            documentsListsManager.addToWorkingList(
                    DocumentsListsManager.CLIPBOARD, docsList);

            // auto select clipboard
            autoSelectCurrentList(DocumentsListsManager.CLIPBOARD);

        } else {
            log.debug("No selectable Documents in context to process copy on...");
        }
        log.debug("add to worklist processed...");
    }

    public void putSelectionInWorkList(List<DocumentModel> docsList) {
        putSelectionInWorkList(docsList, false);
    }

    public void putSelectionInWorkList(List<DocumentModel> docsList,
            Boolean forceAppend) {
        canEditSelectedDocs = null;
        if (null != docsList) {
            Object[] params = { docsList.size() };
            facesMessages.add(FacesMessage.SEVERITY_INFO, "#0 "
                    + resourcesAccessor.getMessages().get(
                            "n_added_to_worklist_docs"), params);

            // Add to the default working list
            documentsListsManager.addToWorkingList(
                    getCurrentSelectedListName(), docsList, forceAppend);
            log.debug("Elements copied to clipboard...");

        } else {
            log.debug("No copiedDocs to process copy on...");
        }

        log.debug("add to worklist processed...");
    }

    @Deprecated
    public void copySelection(List<DocumentModel> copiedDocs) {
        if (null != copiedDocs) {
            Object[] params = { copiedDocs.size() };
            facesMessages.add(FacesMessage.SEVERITY_INFO, "#0 "
                    + resourcesAccessor.getMessages().get("n_copied_docs"),
                    params);

            // clipboard.copy(copiedDocs);

            // Reset + Add to clipboard list
            documentsListsManager.resetWorkingList(DocumentsListsManager.CLIPBOARD);
            documentsListsManager.addToWorkingList(
                    DocumentsListsManager.CLIPBOARD, copiedDocs);

            // Add to the default working list
            documentsListsManager.addToWorkingList(copiedDocs);
            log.debug("Elements copied to clipboard...");

        } else {
            log.debug("No copiedDocs to process copy on...");
        }

        log.debug("Copy processed...");
    }

    public String removeWorkListItem(DocumentRef ref) throws ClientException {
        DocumentModel doc = documentManager.getDocument(ref);
        documentsListsManager.removeFromWorkingList(
                getCurrentSelectedListName(), doc);
        return null;
    }

    public String clearWorkingList() {
        documentsListsManager.resetWorkingList(getCurrentSelectedListName());
        return null;
    }

    public String pasteDocumentList(String listName) throws ClientException {
        return pasteDocumentList(documentsListsManager.getWorkingList(listName));
    }

    public String pasteDocumentListInside(String listName, String docId)
            throws ClientException {
        return pasteDocumentListInside(
                documentsListsManager.getWorkingList(listName), docId);
    }

    public String pasteDocumentList(List<DocumentModel> docPaste)
            throws ClientException {
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        if (null != docPaste) {
            List<DocumentModel> newDocs = recreateDocumentsWithNewParent(
                    getParent(currentDocument), docPaste);

            Object[] params = { newDocs.size() };
            facesMessages.add(FacesMessage.SEVERITY_INFO, "#0 "
                    + resourcesAccessor.getMessages().get("n_pasted_docs"),
                    params);

            EventManager.raiseEventsOnDocumentSelected(currentDocument);
            Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
                    currentDocument);

            log.debug("Elements pasted and created into the backend...");
        } else {
            log.debug("No docPaste to process paste on...");
        }

        return computeOutcome(PASTE_OUTCOME);
    }

    public String pasteDocumentListInside(List<DocumentModel> docPaste,
            String docId) throws ClientException {
        DocumentModel targetDoc = documentManager.getDocument(new IdRef(docId));
        if (null != docPaste) {
            List<DocumentModel> newDocs = recreateDocumentsWithNewParent(
                    targetDoc, docPaste);

            Object[] params = { newDocs.size() };
            facesMessages.add(FacesMessage.SEVERITY_INFO, "#0 "
                    + resourcesAccessor.getMessages().get("n_pasted_docs"),
                    params);

            EventManager.raiseEventsOnDocumentSelected(targetDoc);
            Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
                    targetDoc);

            log.debug("Elements pasted and created into the backend...");
        } else {
            log.debug("No docPaste to process paste on...");
        }

        return null;
    }

    public List<DocumentModel> moveDocumentsToNewParent(
            DocumentModel destFolder, List<DocumentModel> docs)
            throws ClientException {
        DocumentRef destFolderRef = destFolder.getRef();
        List<DocumentModel> newDocs = new ArrayList<DocumentModel>();
        for (DocumentModel docModel : docs) {
            DocumentRef sourceFolderRef = docModel.getParentRef();
            String sourceType = docModel.getType();
            boolean canRemoveDoc = documentManager.hasPermission(
                    sourceFolderRef, SecurityConstants.REMOVE_CHILDREN);
            boolean canPasteInCurrentFolder = typeManager.isAllowedSubType(
                    sourceType, destFolder.getType(),
                    navigationContext.getCurrentDocument());
            boolean sameFolder = sourceFolderRef.equals(destFolderRef);
            if (canRemoveDoc && canPasteInCurrentFolder && !sameFolder) {
                DocumentModel newDoc = documentManager.move(docModel.getRef(),
                        destFolderRef, null);
                newDocs.add(newDoc);
            }
        }
        documentManager.save();

        return newDocs;
    }

    public String moveDocumentList(String listName, String docId)
            throws ClientException {
        List<DocumentModel> docs = documentsListsManager.getWorkingList(listName);
        DocumentModel targetDoc = documentManager.getDocument(new IdRef(docId));
        // Get all parent folders
        Set<DocumentRef> parentRefs = new HashSet<DocumentRef>();
        for (DocumentModel doc : docs) {
            parentRefs.add(doc.getParentRef());
        }

        List<DocumentModel> newDocs = moveDocumentsToNewParent(targetDoc, docs);

        documentsListsManager.resetWorkingList(listName);

        Object[] params = { newDocs.size() };
        facesMessages.add(FacesMessage.SEVERITY_INFO, "#0 "
                + resourcesAccessor.getMessages().get("n_moved_docs"), params);

        EventManager.raiseEventsOnDocumentSelected(targetDoc);
        Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
                targetDoc);

        // Send event to all initial parents
        for (DocumentRef docRef : parentRefs) {
            Events.instance().raiseEvent(EventNames.DOCUMENT_CHILDREN_CHANGED,
                    documentManager.getDocument(docRef));
        }

        log.debug("Elements moved and created into the backend...");

        return null;
    }

    public String moveDocumentList(String listName) throws ClientException {
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        return moveDocumentList(listName, currentDocument.getId());
    }

    public String moveWorkingList() throws ClientException {
        moveDocumentList(getCurrentSelectedListName());
        return null;
    }

    public String pasteWorkingList() throws ClientException {
        pasteDocumentList(getCurrentSelectedList());
        return null;
    }

    public String pasteClipboard() throws ClientException {
        pasteDocumentList(DocumentsListsManager.CLIPBOARD);
        returnToPreviouslySelectedList();
        return null;
    }

    @WebRemote
    public String pasteClipboardInside(String docId) throws ClientException {
        pasteDocumentListInside(DocumentsListsManager.CLIPBOARD, docId);
        return null;
    }

    @WebRemote
    public String moveClipboardInside(String docId) throws ClientException {
        moveDocumentList(DocumentsListsManager.CLIPBOARD, docId);
        return null;
    }

    /**
     * Creates the documents in the backend under the target parent.
     */
    protected List<DocumentModel> recreateDocumentsWithNewParent(
            DocumentModel parent, List<DocumentModel> documents)
            throws ClientException {

        List<DocumentModel> newDocuments = new ArrayList<DocumentModel>();

        if (null == parent || null == documents) {
            log.error("Null params received, returning...");
            return newDocuments;
        }

        List<DocumentModel> documentsToPast = new LinkedList<DocumentModel>();

        // filter list on content type
        for (DocumentModel doc : documents) {
            if (typeManager.isAllowedSubType(doc.getType(), parent.getType(),
                    navigationContext.getCurrentDocument())) {
                documentsToPast.add(doc);
            }
        }

        // copying proxy or document
        boolean isPublishSpace = isPublishSpace(parent);
        List<DocumentRef> docRefs = new ArrayList<DocumentRef>();
        List<DocumentRef> proxyRefs = new ArrayList<DocumentRef>();
        for (DocumentModel doc : documentsToPast) {
            if (doc.isProxy() && !isPublishSpace) {
                // in a non-publish space, we want to expand proxies into
                // normal docs
                proxyRefs.add(doc.getRef());
            } else {
                // copy as is
                docRefs.add(doc.getRef());
            }
        }
        if (!proxyRefs.isEmpty()) {
            newDocuments.addAll(documentManager.copyProxyAsDocument(proxyRefs,
                    parent.getRef()));
        }
        if (!docRefs.isEmpty()) {
            newDocuments.addAll(documentManager.copy(docRefs, parent.getRef()));
        }
        documentManager.save();

        return newDocuments;
    }

    /**
     * Check if the container is a publish space. If this is not the case, a
     * proxy copied to it will be recreated as a new document.
     */
    protected boolean isPublishSpace(DocumentModel container)
            throws ClientException {
        SchemaManager schemaManager;
        try {
            schemaManager = Framework.getService(SchemaManager.class);
        } catch (Exception e) {
            throw new ClientException(e);
        }
        Set<String> publishSpaces = null;
        if (schemaManager != null) {
            publishSpaces = schemaManager.getDocumentTypeNamesForFacet("PublishSpace");
        }
        if (publishSpaces == null || publishSpaces.isEmpty()) {
            publishSpaces = new HashSet<String>();
            publishSpaces.add("Section");
        }
        return publishSpaces.contains(container.getType());
    }

    @Destroy
    @Remove
    public void destroy() {
        log.debug("Removing Seam component: clipboardActions");
    }

    /**
     * Gets the parent document under the paste should be performed.
     * <p>
     * Rules:
     * <p>
     * In general the currentDocument is the parent. Exceptions to this rule:
     * when the currentDocument is a domain or null. If Domain then content
     * root is the parent. If null is passed, then the JCR root is taken as
     * parent.
     */
    protected DocumentModel getParent(DocumentModel currentDocument)
            throws ClientException {

        if (currentDocument.isFolder()) {
            return currentDocument;
        }

        DocumentModelList parents = navigationContext.getCurrentPath();
        for (int i = parents.size() - 1; i >= 0; i--) {
            DocumentModel parent = parents.get(i);
            if (parent.isFolder()) {
                return parent;
            }
        }

        return null;
    }

    @Factory(value = "isCurrentWorkListEmpty", scope = EVENT)
    public boolean factoryForIsCurrentWorkListEmpty() {
        return isWorkListEmpty();
    }

    public boolean isWorkListEmpty() {
        return documentsListsManager.isWorkingListEmpty(getCurrentSelectedListName());
    }

    public String exportWorklistAsZip() throws ClientException {
        return exportWorklistAsZip(documentsListsManager.getWorkingList(getCurrentSelectedListName()));
    }

    public String exportAllBlobsFromWorkingListAsZip() throws ClientException {
        return exportWorklistAsZip();
    }

    public String exportMainBlobFromWorkingListAsZip() throws ClientException {
        return exportWorklistAsZip();
    }

    public String exportWorklistAsZip(List<DocumentModel> documents)
            throws ClientException {
        return exportWorklistAsZip(documents, true);
    }

    public String exportWorklistAsZip(DocumentModel document)
            throws ClientException {
        return exportWorklistAsZip(
                Arrays.asList(new DocumentModel[] { document }), true);
    }

    /**
     * Checks if copy action is available in the context of the current
     * Document.
     * <p>
     * Condition: the list of selected documents is not empty.
     */
    public boolean getCanCopy() {
        if (navigationContext.getCurrentDocument() == null) {
            return false;
        }
        return !documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
    }

    /**
     * Checks if the Paste action is available in the context of the current
     * Document. Conditions:
     * <p>
     * <ul>
     * <li>list is not empty
     * <li>user has the needed permissions on the current document
     * <li>the content of the list can be added as children of the current
     * document
     * </ul>
     */
    public boolean getCanPaste(String listName) throws ClientException {

        DocumentModel currentDocument = navigationContext.getCurrentDocument();

        if (documentsListsManager.isWorkingListEmpty(listName)
                || currentDocument == null) {
            return false;
        }

        DocumentModel pasteTarget = getParent(navigationContext.getCurrentDocument());
        if (!documentManager.hasPermission(pasteTarget.getRef(),
                SecurityConstants.ADD_CHILDREN)) {
            return false;
        } else {
            // filter on allowed content types
            // see if at least one doc can be pasted
            // String pasteTypeName = clipboard.getClipboardDocumentType();
            List<String> pasteTypesName = documentsListsManager.getWorkingListTypes(listName);
            for (String pasteTypeName : pasteTypesName) {
                if (typeManager.isAllowedSubType(pasteTypeName,
                        pasteTarget.getType(),
                        navigationContext.getCurrentDocument())) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean getCanPasteInside(String listName, DocumentModel document)
            throws ClientException {
        if (documentsListsManager.isWorkingListEmpty(listName)
                || document == null) {
            return false;
        }

        if (!documentManager.hasPermission(document.getRef(),
                SecurityConstants.ADD_CHILDREN)) {
            return false;
        } else {
            // filter on allowed content types
            // see if at least one doc can be pasted
            // String pasteTypeName = clipboard.getClipboardDocumentType();
            List<String> pasteTypesName = documentsListsManager.getWorkingListTypes(listName);
            for (String pasteTypeName : pasteTypesName) {
                if (typeManager.isAllowedSubType(pasteTypeName,
                        document.getType(),
                        navigationContext.getCurrentDocument())) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Checks if the Move action is available in the context of the document
     * document. Conditions:
     * <p>
     * <ul>
     * <li>list is not empty
     * <li>user has the needed permissions on the document
     * <li>an element in the list can be removed from its folder and added as
     * child of the current document
     * </ul>
     */
    public boolean getCanMoveInside(String listName, DocumentModel document)
            throws ClientException {
        if (documentsListsManager.isWorkingListEmpty(listName)
                || document == null) {
            return false;
        }
        DocumentRef destFolderRef = document.getRef();
        DocumentModel destFolder = document;
        if (!documentManager.hasPermission(destFolderRef,
                SecurityConstants.ADD_CHILDREN)) {
            return false;
        } else {
            // filter on allowed content types
            // see if at least one doc can be removed and pasted
            for (DocumentModel docModel : documentsListsManager.getWorkingList(listName)) {
                DocumentRef sourceFolderRef = docModel.getParentRef();
                String sourceType = docModel.getType();
                boolean canRemoveDoc = documentManager.hasPermission(
                        sourceFolderRef, SecurityConstants.REMOVE_CHILDREN);
                boolean canPasteInCurrentFolder = typeManager.isAllowedSubType(
                        sourceType, destFolder.getType(),
                        navigationContext.getCurrentDocument());
                boolean sameFolder = sourceFolderRef.equals(destFolderRef);
                if (canRemoveDoc && canPasteInCurrentFolder && !sameFolder) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Checks if the Move action is available in the context of the current
     * Document. Conditions:
     * <p>
     * <ul>
     * <li>list is not empty
     * <li>user has the needed permissions on the current document
     * <li>an element in the list can be removed from its folder and added as
     * child of the current document
     * </ul>
     */
    public boolean getCanMove(String listName) throws ClientException {
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        return getCanMoveInside(listName, currentDocument);
    }

    public boolean getCanPasteWorkList() throws ClientException {
        return getCanPaste(getCurrentSelectedListName());
    }

    public boolean getCanMoveWorkingList() throws ClientException {
        return getCanMove(getCurrentSelectedListName());
    }

    public boolean getCanPasteFromClipboard() throws ClientException {
        return getCanPaste(DocumentsListsManager.CLIPBOARD);
    }

    public boolean getCanPasteFromClipboardInside(DocumentModel document)
            throws ClientException {
        return getCanPasteInside(DocumentsListsManager.CLIPBOARD, document);
    }

    public boolean getCanMoveFromClipboardInside(DocumentModel document)
            throws ClientException {
        return getCanMoveInside(DocumentsListsManager.CLIPBOARD, document);
    }

    public void setCurrentSelectedList(String listId) {
        if (listId != null && !listId.equals(currentSelectedList)) {
            currentSelectedList = listId;
            canEditSelectedDocs = null;
        }
    }

    @RequestParameter()
    String listIdToSelect;

    public void selectList() {
        if (listIdToSelect != null) {
            setCurrentSelectedList(listIdToSelect);
        }
    }

    public List<DocumentModel> getCurrentSelectedList() {
        return documentsListsManager.getWorkingList(getCurrentSelectedListName());
    }

    public String getCurrentSelectedListName() {
        if (currentSelectedList == null) {
            if (!getAvailableLists().isEmpty()) {
                setCurrentSelectedList(availableLists.get(0));
            }
        }
        return currentSelectedList;
    }

    public String getCurrentSelectedListTitle() {
        String title = null;
        String listName = getCurrentSelectedListName();
        if (listName != null) {
            DocumentsListDescriptor desc = documentsListsManager.getWorkingListDescriptor(listName);
            if (desc != null) {
                title = desc.getTitle();
            }
        }
        return title;
    }

    public List<String> getAvailableLists() {
        if (availableLists == null) {
            availableLists = documentsListsManager.getWorkingListNamesForCategory("CLIPBOARD");
        }
        return availableLists;
    }

    public List<DocumentsListDescriptor> getDescriptorsForAvailableLists() {
        if (descriptorsForAvailableLists == null) {
            List<String> availableLists = getAvailableLists();
            descriptorsForAvailableLists = new ArrayList<DocumentsListDescriptor>();
            for (String lName : availableLists) {
                descriptorsForAvailableLists.add(documentsListsManager.getWorkingListDescriptor(lName));
            }
        }
        return descriptorsForAvailableLists;
    }

    public List<Action> getActionsForCurrentList() {
        String lstName = getCurrentSelectedListName();
        if (isWorkListEmpty()) {
            // we use cache here since this is a very common case ...
            if (actionCache == null) {
                actionCache = new HashMap<String, List<Action>>();
            }
            if (!actionCache.containsKey(lstName)) {
                actionCache.put(lstName, webActions.getActionsList(lstName
                        + "_LIST"));
            }
            return actionCache.get(lstName);
        } else {
            return webActions.getActionsList(lstName + "_LIST");
        }
    }

    public List<Action> getActionsForSelection() {
        return webActions.getUnfiltredActionsList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION
                + "_LIST");
    }

    private void autoSelectCurrentList(String listName) {
        previouslySelectedList = getCurrentSelectedListName();
        setCurrentSelectedList(listName);
    }

    private void returnToPreviouslySelectedList() {
        setCurrentSelectedList(previouslySelectedList);
    }

    public boolean getCanEditSelectedDocs() throws ClientException {
        if (canEditSelectedDocs == null) {
            if (getCurrentSelectedList().isEmpty()) {
                canEditSelectedDocs = false;
            } else {
                final List<DocumentModel> selectedDocs = getCurrentSelectedList();

                // check selected docs
                canEditSelectedDocs = checkWritePerm(selectedDocs);
            }
        }
        return canEditSelectedDocs;
    }

    @Deprecated
    // no longer used by the user_clipboard.xhtml template
    public boolean getCanEditListDocs(String listName) throws ClientException {
        final List<DocumentModel> docs = documentsListsManager.getWorkingList(listName);

        final boolean canEdit;
        if (docs.isEmpty()) {
            canEdit = false;
        } else {
            // check selected docs
            canEdit = checkWritePerm(docs);
        }
        return canEdit;
    }

    private boolean checkWritePerm(List<DocumentModel> selectedDocs)
            throws ClientException {
        for (DocumentModel documentModel : selectedDocs) {
            boolean canWrite = documentManager.hasPermission(
                    documentModel.getRef(), SecurityConstants.WRITE_PROPERTIES);
            if (!canWrite) {
                return false;
            }
        }
        return true;
    }

    public boolean isCacheEnabled() {
        if (!SeamCacheHelper.canUseSeamCache()) {
            return false;
        }
        return isWorkListEmpty();
    }

    public String getCacheKey() {
        return getCurrentSelectedListName() + "::"
                + localeSelector.getLocaleString();
    }

    public boolean isCacheEnabledForSelection() {
        if (!SeamCacheHelper.canUseSeamCache()) {
            return false;
        }
        return documentsListsManager.isWorkingListEmpty(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
    }

    @Override
    public String exportWorklistAsZip(List<DocumentModel> documents,
            boolean exportAllBlobs) throws ClientException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            DocumentListZipExporter zipExporter = new DocumentListZipExporter();
            File tmpFile = zipExporter.exportWorklistAsZip(documents,
                    documentManager, exportAllBlobs);
            if (tmpFile == null) {
                // empty zip file, do nothing
                setFacesMessage("label.clipboard.emptyDocuments");
                return null;
            } else {
                if (tmpFile.length() > Functions.getBigFileSizeLimit()) {
                    HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
                    request.setAttribute(
                            NXAuthConstants.DISABLE_REDIRECT_REQUEST_KEY, true);
                    String zipDownloadURL = BaseURL.getBaseURL(request);
                    zipDownloadURL += "nxbigzipfile" + "/";
                    zipDownloadURL += tmpFile.getName();
                    try {
                        context.getExternalContext().redirect(zipDownloadURL);
                    } catch (IOException e) {
                        log.error(
                                "Error while redirecting for big file downloader",
                                e);
                    }
                } else {
                    ComponentUtils.downloadFile(context, "clipboard.zip",
                            tmpFile);
                }

                return "";
            }
        } catch (IOException io) {
            throw ClientException.wrap(io);
        }
    }
}
