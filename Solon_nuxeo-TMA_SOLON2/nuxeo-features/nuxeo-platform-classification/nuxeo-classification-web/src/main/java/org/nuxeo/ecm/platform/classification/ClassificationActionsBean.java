/*
 * (C) Copyright 2006-2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id: ClassificationActionsBean.java 62933 2009-10-14 10:54:33Z ldoguin $
 */

package org.nuxeo.ecm.platform.classification;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.classification.api.ClassificationConstants;
import org.nuxeo.ecm.classification.api.ClassificationHelper;
import org.nuxeo.ecm.classification.api.ClassificationService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PagedDocumentsProvider;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.Sorter;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.core.api.event.DocumentEventCategories;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.impl.UserPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.audit.api.AuditEventTypes;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.model.SelectDataModel;
import org.nuxeo.ecm.platform.ui.web.model.impl.SelectDataModelImpl;
import org.nuxeo.ecm.platform.ui.web.pagination.ResultsProviderFarmUserException;
import org.nuxeo.ecm.webapp.action.TypesTool;
import org.nuxeo.ecm.webapp.documentsLists.DocumentsListsManager;
import org.nuxeo.ecm.webapp.helpers.EventNames;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.ecm.webapp.pagination.ResultsProvidersCache;
import org.nuxeo.ecm.webapp.querymodel.QueryModelActions;
import org.nuxeo.ecm.webapp.tree.DocumentTreeNode;
import org.nuxeo.ecm.webapp.tree.DocumentTreeNodeImpl;
import org.nuxeo.ecm.webapp.tree.TreeManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Handles classification actions
 *
 * @author Anahide Tchertchian
 */
@Name("classificationActions")
@Scope(ScopeType.CONVERSATION)
public class ClassificationActionsBean implements ClassificationActions {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(ClassificationActionsBean.class);

    @In(create = true, required = false)
    protected transient FacesMessages facesMessages;

    @In
    protected transient Context eventContext;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true)
    private transient NavigationContext navigationContext;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected transient QueryModelActions queryModelActions;

    @In(create = true)
    private transient ResultsProvidersCache resultsProvidersCache;

    @In(create = true)
    protected transient DocumentsListsManager documentsListsManager;

    @In(create = true)
    protected TypesTool typesTool;

    protected DocumentModelList currentDocumentClassifications;

    protected DocumentModelList classificationRoots;

    protected DocumentModel currentClassificationRoot;

    protected DocumentTreeNode currentClassificationTree;

    protected DocumentModelList editableClassificationRoots;

    protected DocumentModel currentEditableClassificationRoot;

    protected DocumentTreeNode currentEditableClassificationTree;

    protected String currentSelectionViewId;

    protected List<DocumentModel> getFilteredSelectedDocumentsForClassification()
            throws ClientException {
        ClassificationService clService;
        try {
          clService = Framework.getService(ClassificationService.class);
        } catch (Exception e){
            throw new ClientException("Could not find Classification Service", e);
        }
        List<DocumentModel> filtered = new DocumentModelListImpl();
        List<DocumentModel> docs = documentsListsManager.getWorkingList(DocumentsListsManager.CURRENT_DOCUMENT_SELECTION);
        if (docs != null) {
            for (DocumentModel doc : docs) {
                if (doc != null
                        && clService.isClassifiable(doc)) {
                    filtered.add(doc);
                }
            }
        }
        return filtered;
    }

    public boolean getCanClassifyFromCurrentSelection() throws ClientException {
        List<DocumentModel> classifiable = getFilteredSelectedDocumentsForClassification();
        return !classifiable.isEmpty();
    }

    public Collection<DocumentModel> getTargetDocuments()
            throws ClientException {
        Collection<DocumentModel> res = new ArrayList<DocumentModel>();
        res.add(navigationContext.getCurrentDocument());
        return res;
    }

    public String classify(ClassificationTreeNode node) throws ClientException {
        Collection<DocumentModel> targetDocs = getTargetDocuments();
        if (node != null) {
            classify(targetDocs, node.getDocument());
            // refresh tree
            node.resetChildren();
        }
        return null;
    }

    public Collection<DocumentModel> getMassTargetDocuments()
            throws ClientException {
        if (!documentsListsManager.isWorkingListEmpty(CURRENT_SELECTION_FOR_CLASSIFICATION)) {
            return documentsListsManager.getWorkingList(CURRENT_SELECTION_FOR_CLASSIFICATION);
        } else {
            log.debug("No documents selection in context to process classification on");
            return null;
        }
    }

    public String massClassify(ClassificationTreeNode node)
            throws ClientException {
        Collection<DocumentModel> targetDocs = getMassTargetDocuments();
        if (node != null && targetDocs != null) {
            classify(targetDocs, node.getDocument());
            // refresh tree
            node.resetChildren();
        }
        return null;
    }

    /**
     * Classifies given documents in given classification folder.
     *
     * @return true on error
     */
    @SuppressWarnings("unchecked")
    public boolean classify(Collection<DocumentModel> targetDocs,
            DocumentModel classificationFolder) throws ClientException {
        if (targetDocs.isEmpty()) {
            facesMessages.add(FacesMessage.SEVERITY_ERROR,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.noDocumentsToClassify"));
            return true;
        }
        if (classificationFolder == null) {
            facesMessages.add(FacesMessage.SEVERITY_ERROR,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.noClassificationFolder"));
            return true;
        }
        if (!classificationFolder.hasSchema(ClassificationConstants.CLASSIFICATION_SCHEMA_NAME)) {
            facesMessages.add(
                    FacesMessage.SEVERITY_ERROR,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.invalidClassificationFolder"));
            return true;
        }
        DocumentRef classificationRef = classificationFolder.getRef();
        if (!documentManager.hasPermission(classificationRef,
                ClassificationConstants.CLASSIFY)) {
            facesMessages.add(FacesMessage.SEVERITY_ERROR,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.unauthorized"));
            return true;
        }

        // edit classification folder adding given document ids
        ArrayList<String> targets = (ArrayList<String>) classificationFolder.getPropertyValue(ClassificationConstants.CLASSIFICATION_TARGETS_PROPERTY_NAME);
        if (targets == null) {
            targets = new ArrayList<String>();
        }
        String targetNotificationComment = String.format("%s:%s",
                documentManager.getRepositoryName(),
                classificationRef.toString());
        boolean alreadyClassified = false;
        // track if some documents cannot be classified
        boolean invalid = false;
        // avoid duplicates
        ClassificationService clService;
        try {
            clService = Framework.getService(ClassificationService.class);
        } catch (Exception e) {
            throw new ClientException("Could not find Classification Service", e);
        }
        for (DocumentModel targetDoc : targetDocs) {
            if (targetDoc == null) {
                continue;
            }
            if (!clService.isClassifiable(targetDoc)) {
                invalid = true;
                continue;
            }
            String targetDocId = targetDoc.getId();
            if (targets.contains(targetDocId)) {
                alreadyClassified = true;
                continue;
            }
            targets.add(targetDocId);
            // notify on classification folder
            String comment = String.format("%s:%s",
                    documentManager.getRepositoryName(), targetDocId);
            notifyEvent(documentManager,
                    ClassificationConstants.EVENT_CLASSIFICATION_DONE,
                    classificationFolder, null, comment, null, null);
            // notify on each classified document
            notifyEvent(documentManager,
                    ClassificationConstants.EVENT_CLASSIFICATION_DONE,
                    targetDoc, null, targetNotificationComment, null, null);

        }
        classificationFolder.setPropertyValue(
                ClassificationConstants.CLASSIFICATION_TARGETS_PROPERTY_NAME,
                targets);
        documentManager.saveDocument(classificationFolder);
        documentManager.save();

        Events.instance().raiseEvent(AuditEventTypes.HISTORY_CHANGED);

        if (invalid && alreadyClassified) {
            facesMessages.add(
                    FacesMessage.SEVERITY_WARN,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.requestDoneButSomeWereAlreadyClassifiedAndSomeInvalid"));
        } else if (invalid) {
            facesMessages.add(
                    FacesMessage.SEVERITY_WARN,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.requestDoneButSomeInvalid"));
        } else if (alreadyClassified) {
            facesMessages.add(
                    FacesMessage.SEVERITY_WARN,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.requestDoneButSomeWereAlreadyClassified"));
        } else {
            facesMessages.add(FacesMessage.SEVERITY_INFO,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.requestDone"));
        }
        return false;
    }

    public String cancelClassification() throws ClientException {
        DocumentModel currentDoc = navigationContext.getCurrentDocument();
        return navigationContext.navigateToDocument(currentDoc);
    }

    public String getCurrentClassificationRootId() throws ClientException {
        DocumentModel root = getCurrentClassificationRoot();
        if (root != null) {
            return root.getId();
        }
        return null;
    }

    /**
     * Sets current classification root id, and set it as current document.
     */
    public void setCurrentClassificationRootId(String newRootId)
            throws ClientException {
        if (newRootId != null) {
            DocumentModelList roots = getClassificationRoots();
            for (DocumentModel root : roots) {
                if (newRootId.equals(root.getId())) {
                    currentClassificationRoot = root;
                    break;
                }
            }
            // force reset of current tree
            currentClassificationTree = null;
            eventContext.remove("currentClassificationTree");
        }
    }

    public String navigateToCurrentClassificationRoot() throws ClientException {
        return navigationContext.navigateToDocument(currentClassificationRoot);
    }

    public DocumentModel getCurrentClassificationRoot() throws ClientException {
        DocumentModelList roots = getClassificationRoots();
        // reset root if needed
        if (!roots.contains(currentClassificationRoot)) {
            currentClassificationRoot = null;
            currentClassificationTree = null;
        }
        if (currentClassificationRoot == null) {
            // take first available root
            if (!roots.isEmpty()) {
                currentClassificationRoot = roots.get(0);
            }
        }
        return currentClassificationRoot;
    }

    @Factory(value = "currentEditableClassificationRootId", scope = EVENT)
    public String getCurrentEditableClassificationRootId()
            throws ClientException {
        DocumentModel root = getCurrentEditableClassificationRoot();
        if (root != null) {
            return root.getId();
        }
        return null;
    }

    public void setCurrentEditableClassificationRootId(String newRootId)
            throws ClientException {
        if (newRootId != null) {
            DocumentModelList roots = getEditableClassificationRoots();
            for (DocumentModel root : roots) {
                if (newRootId.equals(root.getId())) {
                    currentEditableClassificationRoot = root;
                    break;
                }
            }
            // force reset of current tree
            currentEditableClassificationTree = null;
            eventContext.remove("currentEditableClassificationTree");
        }
    }

    public DocumentModel getCurrentEditableClassificationRoot()
            throws ClientException {
        if (currentEditableClassificationRoot == null) {
            // initialize roots and take first
            DocumentModelList roots = getEditableClassificationRoots();
            if (!roots.isEmpty()) {
                currentEditableClassificationRoot = roots.get(0);
            }
        }
        return currentEditableClassificationRoot;
    }

    @Factory(value = "currentClassificationTree", scope = EVENT)
    public DocumentTreeNode getCurrentClassificationTree()
            throws ClientException {
        if (currentClassificationTree == null) {
            // initialize current root
            DocumentModel root = getCurrentClassificationRoot();
            if (root != null) {
                Filter filter = null;
                Sorter sorter = null;
                try {
                    TreeManager treeManager = Framework.getService(TreeManager.class);
                    filter = treeManager.getFilter(TREE_PLUGIN_NAME);
                    sorter = treeManager.getSorter(TREE_PLUGIN_NAME);
                } catch (Exception e) {
                    log.error(
                            "Could not fetch filter, sorter or node type for tree ",
                            e);
                }
                // standard tree node: no need to show classified documents
                currentClassificationTree = new DocumentTreeNodeImpl(root,
                        filter, sorter);
            }
        }
        return currentClassificationTree;
    }

    @Factory(value = "currentEditableClassificationTree", scope = EVENT)
    public DocumentTreeNode getCurrentEditableClassificationTree()
            throws ClientException {
        if (currentEditableClassificationTree == null) {
            // initialize current root
            DocumentModel root = getCurrentEditableClassificationRoot();
            if (root != null) {
                Filter filter = null;
                Sorter sorter = null;
                try {
                    TreeManager treeManager = Framework.getService(TreeManager.class);
                    filter = treeManager.getFilter(TREE_PLUGIN_NAME);
                    sorter = treeManager.getSorter(TREE_PLUGIN_NAME);
                } catch (Exception e) {
                    log.error(
                            "Could not fetch filter, sorter or node type for tree ",
                            e);
                }
                currentEditableClassificationTree = new ClassificationTreeNode(
                        root, filter, sorter);
            }
        }
        return currentEditableClassificationTree;
    }

    @Factory(value = "classificationRoots", scope = EVENT)
    public DocumentModelList getClassificationRoots() throws ClientException {
        if (classificationRoots == null) {
            classificationRoots = new DocumentModelListImpl();
            try {
                PagedDocumentsProvider provider = getResultsProvider(CLASSIFICATION_ROOTS_PROVIDER_NAME);
                List<DocumentModel> resultDocuments = provider.getCurrentPage();
                for (DocumentModel doc : resultDocuments) {
                    // XXX refetch it to be a real document model instead of a
                    // ResultDocumentModel that does not handle lists correctly
                    // (dc:contributors is Object[] instead of String[]) + get
                    // a session id that's needed to retrieve a tree node
                    // children
                    classificationRoots.add(documentManager.getDocument(doc.getRef()));
                }
            } catch (ResultsProviderFarmUserException e) {
                log.error(e);
            } catch (ClientException e) {
                log.error(e);
            }
        }
        return classificationRoots;
    }

    @Factory(value = "editableClassificationRoots", scope = EVENT)
    public DocumentModelList getEditableClassificationRoots()
            throws ClientException {
        if (editableClassificationRoots == null) {
            editableClassificationRoots = new DocumentModelListImpl();
            for (DocumentModel classificationRoot : getClassificationRoots()) {
                DocumentRef rootRef = classificationRoot.getRef();
                if (documentManager.hasPermission(rootRef,
                        ClassificationConstants.CLASSIFY)) {
                    // XXX refetch it to be a real document model instead of a
                    // ResultDocumentModel that does not handle lists correctly
                    // (dc:contributors is Object[] instead of String[]) + get
                    // a session id that's needed to retrieve a tree node
                    // children
                    editableClassificationRoots.add(documentManager.getDocument(rootRef));
                }
            }
        }
        return editableClassificationRoots;
    }

    public void editableClassificationRootSelected(ValueChangeEvent event)
            throws ClientException {
        Object newValue = event.getNewValue();
        if (newValue instanceof String) {
            String newRootId = (String) newValue;
            setCurrentEditableClassificationRootId(newRootId);
        }
    }

    @Observer(value = { EventNames.GO_HOME,
            EventNames.DOMAIN_SELECTION_CHANGED, EventNames.DOCUMENT_CHANGED,
            EventNames.DOCUMENT_SECURITY_CHANGED,
            EventNames.DOCUMENT_CHILDREN_CHANGED }, create = false)
    public void resetClassificationData() {
        classificationRoots = null;
        // do not reset current classification root to not lose current
        // selection. it will be reset later if it's not available anymore.
        currentClassificationTree = null;
        editableClassificationRoots = null;
        currentEditableClassificationRoot = null;
        currentEditableClassificationTree = null;
        resultsProvidersCache.invalidate(CLASSIFICATION_ROOTS_PROVIDER_NAME);
        resetCurrentDocumentClassifications();
    }

    @Observer(value = { EventNames.GO_HOME,
            EventNames.DOMAIN_SELECTION_CHANGED,
            EventNames.DOCUMENT_SELECTION_CHANGED }, create = false)
    public void resetCurrentDocumentClassifications() {
        currentDocumentClassifications = null;
        resultsProvidersCache.invalidate(CURRENT_DOCUMENT_CLASSIFICATIONS_PROVIDER);
        documentsListsManager.resetWorkingList(CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION);
    }

    public PagedDocumentsProvider getResultsProvider(String name,
            SortInfo sortInfo) throws ClientException,
            ResultsProviderFarmUserException {
        PagedDocumentsProvider provider;
        int pageSize = getDocumentBatchSize();

        if (CLASSIFICATION_ROOTS_PROVIDER_NAME.equals(name)) {
            Object[] params = null;
            try {
                provider = getQmDocuments(name, params, sortInfo);
            } catch (Exception e) {
                log.error("sorted query failed");
                log.debug(e);
                log.error("retrying without sort parameters");
                provider = getQmDocuments(name, params, null);
            }
            provider.setName(name);
        } else if (CURRENT_DOCUMENT_CLASSIFICATIONS_PROVIDER.equals(name)) {
            provider = new PagedClassificationsProvider(
                    getCurrentDocumentClassifications(), pageSize, name,
                    sortInfo);
        } else if (CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER.equals(name)) {
            try {
                // initialize list from current selection list
                List<DocumentModel> docs = getFilteredSelectedDocumentsForClassification();
                // initialize selection list too
                documentsListsManager.resetWorkingList(CURRENT_SELECTION_FOR_CLASSIFICATION);
                documentsListsManager.addToWorkingList(
                        CURRENT_SELECTION_FOR_CLASSIFICATION, docs);
                return new PagedClassificationsProvider(docs, pageSize, name,
                        sortInfo);
            } catch (Exception e) {
                throw new ClientException(e);
            }
        } else {
            throw new ClientException("Unknown board: " + name);
        }
        return provider;
    }

    protected PagedDocumentsProvider getQmDocuments(String qmName,
            Object[] params, SortInfo sortInfo) throws ClientException {
        return queryModelActions.get(qmName).getResultsProvider(documentManager,
                params, sortInfo);
    }

    public PagedDocumentsProvider getResultsProvider(String name)
            throws ClientException, ResultsProviderFarmUserException {
        return getResultsProvider(name, null);
    }

    @Factory(value = "currentDocumentClassifications", scope = EVENT)
    public DocumentModelList getCurrentDocumentClassifications()
            throws ClientException {
        if (currentDocumentClassifications == null) {
            DocumentModel currentDocument = navigationContext.getCurrentDocument();
            currentDocumentClassifications = ClassificationHelper.getClassifiedDocuments(
                    currentDocument, documentManager);
        }
        return currentDocumentClassifications;
    }

    @Factory(value = "currentDocumentClassificationsSelection", scope = EVENT)
    public SelectDataModel getCurrentDocumentClassificationsSelection()
            throws ClientException {
        DocumentModelList documents = resultsProvidersCache.get(
                CURRENT_DOCUMENT_CLASSIFICATIONS_PROVIDER).getCurrentPage();
        List<DocumentModel> selectedDocuments = documentsListsManager.getWorkingList(CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION);
        SelectDataModel model = new SelectDataModelImpl(
                CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION, documents,
                selectedDocuments);
        return model;
    }

    /**
     * Returns classification form for selected documents
     *
     * @param currentviewId the current view id, so that redirection can be done
     *            correctly on cancel.
     */
    public String showCurrentSelectionClassificationForm(String currentViewId)
            throws ClientException {
        // invalidate provider: it'll rebuild selection for classification list
        // and selection
        resultsProvidersCache.invalidate(CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER);
        currentSelectionViewId = currentViewId;
        return CURRENT_SELECTION_FOR_CLASSIFICATION_PAGE;
    }

    public String cancelCurrentSelectionClassificationForm()
            throws ClientException {
        // XXX AT: this is a hack to redirect to correct page
        if ("/search/search_results_simple.xhtml".equals(currentSelectionViewId)) {
            return "search_results_simple";
        } else if ("/search/search_results_advanced.xhtml".equals(currentSelectionViewId)) {
            return "search_results_advanced";
        } else if ("/search/dynsearch_results.xhtml".equals(currentSelectionViewId)) {
            return "dynsearch_results";
        }
        // navigate to current document default view
        DocumentModel currentDoc = navigationContext.getCurrentDocument();
        if (currentDoc != null) {
            navigationContext.navigateToDocument(currentDoc);
        }
        // default: do not move
        return null;
    }

    /**
     * Returns select data model for selected documents from previous documents
     * selection.
     */
    @Factory(value = "currentSelectionEmailsSelection", scope = EVENT)
    public SelectDataModel getCurrentSelectionEmailsSelection()
            throws ClientException {
        List<DocumentModel> docs = resultsProvidersCache.get(
                CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER).getCurrentPage();
        List<DocumentModel> selectedDocuments = documentsListsManager.getWorkingList(CURRENT_SELECTION_FOR_CLASSIFICATION);
        SelectDataModel model = new SelectDataModelImpl(
                CURRENT_SELECTION_FOR_CLASSIFICATION, docs, selectedDocuments);
        return model;
    }

    public void unclassify() throws ClientException {
        if (!documentsListsManager.isWorkingListEmpty(CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION)) {
            List<DocumentModel> toDel = documentsListsManager.getWorkingList(CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION);
            List<String> targetDocIds = new ArrayList<String>();
            for (DocumentModel doc : toDel) {
                targetDocIds.add(doc.getId());
            }
            DocumentModel currentDocument = navigationContext.getCurrentDocument();
            unclassify(targetDocIds, currentDocument);
            resetCurrentDocumentClassifications();
        } else {
            log.debug("No documents selection in context to process unclassify on...");
        }
    }

    protected void notifyEvent(CoreSession coreSession, String eventId,
            DocumentModel source, String category, String comment,
            String author, Map<String, Serializable> options) throws ClientException {

        // Default category
        if (category == null) {
            category = DocumentEventCategories.EVENT_DOCUMENT_CATEGORY;
        }

        if (options == null) {
            options = new HashMap<String, Serializable>();
        }

        // Name of the current repository
        options.put(CoreEventConstants.REPOSITORY_NAME,
                coreSession.getRepositoryName());

        // Document life cycle
        if (source != null) {
            String currentLifeCycleState = null;
            try {
                currentLifeCycleState = source.getCurrentLifeCycleState();
            } catch (ClientException err) {
                // FIXME no lifecycle -- this shouldn't generated an
                // exception (and ClientException logs the spurious error)
            }
            options.put(CoreEventConstants.DOC_LIFE_CYCLE,
                    currentLifeCycleState);
        }
        // Add the session ID
        options.put(CoreEventConstants.SESSION_ID, coreSession.getSessionId());

        Principal principal;
        if (author != null) {
            // make fake principal for logs
            principal = new UserPrincipal(author);
        } else {
            principal = coreSession.getPrincipal();
        }


        DocumentEventContext ctx = new DocumentEventContext(coreSession, principal, source);
        ctx.setCategory(category);
        ctx.setComment(comment);
        ctx.setProperties(options);
        Event event = ctx.newEvent(eventId);

        try {
            EventProducer evtProducer = Framework.getService(EventProducer.class);
            log.debug("Notify RepositoryEventListener listeners list for event="
                    + eventId);
            evtProducer.fireEvent(event);
        } catch (Exception e) {
            log.error("Impossible to notify core events ! "
                    + "EventProducer service is missing...");
        }
    }

    /**
     * Unclassifies given document ids in given classification folder.
     *
     * @return true on error
     */
    public boolean unclassify(Collection<String> targetDocIds,
            DocumentModel classificationFolder) throws ClientException {
        if (targetDocIds.isEmpty()) {
            facesMessages.add(
                    FacesMessage.SEVERITY_ERROR,
                    resourcesAccessor.getMessages().get(
                            "feedback.unclassification.noDocumentsToUnclassify"));
            return true;
        }
        if (classificationFolder == null) {
            facesMessages.add(FacesMessage.SEVERITY_ERROR,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.noClassificationFolder"));
            return true;
        }
        if (!classificationFolder.hasSchema(ClassificationConstants.CLASSIFICATION_SCHEMA_NAME)) {
            facesMessages.add(
                    FacesMessage.SEVERITY_ERROR,
                    resourcesAccessor.getMessages().get(
                            "feedback.classification.invalidClassificationFolder"));
            return true;
        }
        DocumentRef classificationRef = classificationFolder.getRef();
        if (!documentManager.hasPermission(classificationRef,
                ClassificationConstants.CLASSIFY)) {
            facesMessages.add(FacesMessage.SEVERITY_ERROR,
                    resourcesAccessor.getMessages().get(
                            "feedback.unclassification.unauthorized"));
            return true;
        }

        // edit classification folder adding given document ids
        @SuppressWarnings("unchecked")
        ArrayList<String> targets = (ArrayList<String>) classificationFolder.getPropertyValue(ClassificationConstants.CLASSIFICATION_TARGETS_PROPERTY_NAME);
        if (targets == null) {
            targets = new ArrayList<String>();
        }
        String targetNotificationComment = String.format("%s:%s",
                documentManager.getRepositoryName(),
                classificationRef.toString());
        boolean notClassified = false;
        // avoid duplicates
        for (String targetDocId : targetDocIds) {
            if (targets.contains(targetDocId)) {
                targets.remove(targetDocId);
                // notify on classification folder
                String comment = String.format("%s:%s",
                        documentManager.getRepositoryName(), targetDocId);
                notifyEvent(documentManager,
                        ClassificationConstants.EVENT_UNCLASSIFICATION_DONE,
                        classificationFolder, null, comment, null, null);
                // notify on each classified document
                DocumentModel targetDoc = documentManager.getDocument(new IdRef(
                        targetDocId));
                if (targetDoc != null) {
                    notifyEvent(
                            documentManager,
                            ClassificationConstants.EVENT_UNCLASSIFICATION_DONE,
                            targetDoc, null, targetNotificationComment, null,
                            null);
                }
            } else {
                notClassified = true;
            }
        }
        classificationFolder.setPropertyValue(
                ClassificationConstants.CLASSIFICATION_TARGETS_PROPERTY_NAME,
                targets);
        documentManager.saveDocument(classificationFolder);
        documentManager.save();

        Events.instance().raiseEvent(AuditEventTypes.HISTORY_CHANGED);

        if (notClassified) {
            facesMessages.add(
                    FacesMessage.SEVERITY_WARN,
                    resourcesAccessor.getMessages().get(
                            "feedback.unclassification.requestDoneButSomeWereNotClassified"));
        } else {
            facesMessages.add(FacesMessage.SEVERITY_INFO,
                    resourcesAccessor.getMessages().get(
                            "feedback.unclassification.requestDone"));
        }
        return false;
    }

    // XXX AT: copy/pasted

    public static final String DOCUMENTS_PAGE_SIZE_PROPERTY_NAME = "classification.documents.pageSize";

    public static int getDocumentBatchSize() {
        int pageSize = 20;
        // try to set it using properties
        String propsBatchSize = Framework.getProperty(DOCUMENTS_PAGE_SIZE_PROPERTY_NAME);
        if (propsBatchSize != null && propsBatchSize.length() > 0) {
            pageSize = Integer.parseInt(propsBatchSize);
        } else {
            log.debug(String.format("Property '%s' not set: "
                    + "using default page size for documents providers",
                    DOCUMENTS_PAGE_SIZE_PROPERTY_NAME));
        }
        return pageSize;
    }

}
