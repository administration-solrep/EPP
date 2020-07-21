/*
 * (C) Copyright 2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
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

package org.nuxeo.ecm.platform.classification;

import java.io.Serializable;
import java.util.Collection;

import javax.faces.event.ValueChangeEvent;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PagedDocumentsProvider;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.ui.web.api.ResultsProviderFarm;
import org.nuxeo.ecm.platform.ui.web.model.SelectDataModel;
import org.nuxeo.ecm.platform.ui.web.pagination.ResultsProviderFarmUserException;
import org.nuxeo.ecm.webapp.tree.DocumentTreeNode;

// FIXME AT: this interface has been extracted automatically, must be reviewed.
public interface ClassificationActions extends ResultsProviderFarm,
        Serializable {

    String EVENT_CLASSIFICATION_TREE_CHANGED = "classificationTreeChanged";

    String CLASSIFICATION_ROOTS_PROVIDER_NAME = "CLASSIFICATION_ROOTS";

    String CURRENT_DOCUMENT_CLASSIFICATIONS_PROVIDER = "CURRENT_DOCUMENT_CLASSIFICATIONS";

    String CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION = "CURRENT_DOCUMENT_CLASSIFICATIONS_SELECTION";

    String CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER = "CURRENT_SELECTION_FOR_CLASSIFICATION_PROVIDER";

    String CURRENT_SELECTION_FOR_CLASSIFICATION = "CURRENT_SELECTION_FOR_CLASSIFICATION";

    String CURRENT_SELECTION_FOR_CLASSIFICATION_PAGE = "current_selection_classification_request";

    String TREE_PLUGIN_NAME = "classification";

    boolean getCanClassifyFromCurrentSelection() throws ClientException;

    /**
     * Returns target documents when classifying an envelope.
     * <p>
     * May take into account only current email, or all emails in current
     * envelope.
     * </p>
     */
    Collection<DocumentModel> getTargetDocuments()
            throws ClientException;

    /**
     * Returns selected target documents from a list of email documents.
     */
    Collection<DocumentModel> getMassTargetDocuments() throws ClientException;

    /**
     * Classifies current email or envelope in given folder and redirect to
     * current page.
     */
    String classify(ClassificationTreeNode node) throws ClientException;

    /**
     * Classifies a list of emails in given folder and redirect to current page.
     */
    String massClassify(ClassificationTreeNode node)
            throws ClientException;

    /**
     * Classifies given documents in given classification folder.
     *
     * @return true on error
     */
    boolean classify(Collection<DocumentModel> targetDocs,
            DocumentModel classificationFolder) throws ClientException;

    String cancelClassification() throws ClientException;

    String getCurrentClassificationRootId() throws ClientException;

    /**
     * Sets current classification root id, and set it as current document.
     */
    void setCurrentClassificationRootId(String newRootId)
            throws ClientException;

    String navigateToCurrentClassificationRoot() throws ClientException;

    DocumentModel getCurrentClassificationRoot() throws ClientException;

    String getCurrentEditableClassificationRootId()
            throws ClientException;

    void setCurrentEditableClassificationRootId(String newRootId)
            throws ClientException;

    DocumentModel getCurrentEditableClassificationRoot()
            throws ClientException;

    DocumentTreeNode getCurrentClassificationTree()
            throws ClientException;

    DocumentTreeNode getCurrentEditableClassificationTree()
            throws ClientException;

    DocumentModelList getClassificationRoots() throws ClientException;

    DocumentModelList getEditableClassificationRoots()
            throws ClientException;

    void editableClassificationRootSelected(ValueChangeEvent event)
            throws ClientException;

    void resetClassificationData();

    void resetCurrentDocumentClassifications();

    PagedDocumentsProvider getResultsProvider(String name,
            SortInfo sortInfo) throws ClientException,
            ResultsProviderFarmUserException;

    PagedDocumentsProvider getResultsProvider(String name)
            throws ClientException, ResultsProviderFarmUserException;

    DocumentModelList getCurrentDocumentClassifications()
            throws ClientException;

    SelectDataModel getCurrentDocumentClassificationsSelection()
            throws ClientException;

    /**
     * Returns classification form for selected documents
     *
     * @param currentViewId the current view id, so that redirection can be done
     *            correctly on cancel.
     */
    String showCurrentSelectionClassificationForm(String currentViewId)
            throws ClientException;

    String cancelCurrentSelectionClassificationForm()
            throws ClientException;

    /**
     * Returns select data model for selected documents from previous documents
     * selection.
     */
    SelectDataModel getCurrentSelectionEmailsSelection()
            throws ClientException;

    void unclassify() throws ClientException;

    /**
     * Unclassifies given document ids in given classification folder.
     *
     * @return true on error
     */
    boolean unclassify(Collection<String> targetDocIds,
            DocumentModel classificationFolder) throws ClientException;

}
