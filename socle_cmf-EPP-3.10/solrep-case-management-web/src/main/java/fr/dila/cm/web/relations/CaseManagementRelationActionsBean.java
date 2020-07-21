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

package fr.dila.cm.web.relations;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.relations.api.Node;
import org.nuxeo.ecm.platform.relations.api.QNameResource;
import org.nuxeo.ecm.platform.relations.api.RelationManager;
import org.nuxeo.ecm.platform.relations.api.Resource;
import org.nuxeo.ecm.platform.relations.api.ResourceAdapter;
import org.nuxeo.ecm.platform.relations.api.Statement;
import org.nuxeo.ecm.platform.relations.api.Subject;
import org.nuxeo.ecm.platform.relations.api.event.RelationEvents;
import org.nuxeo.ecm.platform.relations.api.util.RelationConstants;
import org.nuxeo.ecm.platform.relations.web.NodeInfo;
import org.nuxeo.ecm.platform.relations.web.NodeInfoImpl;
import org.nuxeo.ecm.platform.relations.web.StatementInfo;
import org.nuxeo.ecm.platform.relations.web.StatementInfoImpl;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;
import org.nuxeo.ecm.webapp.querymodel.QueryModelActions;
import org.nuxeo.runtime.api.Framework;

import fr.dila.cm.web.invalidations.CaseManagementContextBound;
import fr.dila.cm.web.invalidations.CaseManagementContextBoundInstance;

/**
 * Retrieves relations for current email.
 *
 * @author Anahide Tchertchian
 */
@Name("cmRelationActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
@CaseManagementContextBound
public class CaseManagementRelationActionsBean extends
        CaseManagementContextBoundInstance {

    private static final long serialVersionUID = 1L;

    private static final Log log = LogFactory.getLog(CaseManagementRelationActionsBean.class);

    public static final String CURRENT_CASE_ITEM_RELATION_SEARCH_QUERYMODEL = "CURRENT_CASE_ITEM_RELATION_SEARCH";

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    @In(create = true)
    protected RelationManager relationManager;

    @In(create = true)
    protected transient ResourcesAccessor resourcesAccessor;

    @In(create = true, required = false)
    protected FacesMessages facesMessages;

    @In(create = true)
    protected transient QueryModelActions queryModelActions;

    @In(required = false)
    protected transient Principal currentUser;

    // statements lists
    protected List<Statement> incomingStatements;

    protected List<StatementInfo> incomingStatementsInfo;

    protected List<Statement> outgoingStatements;

    protected List<StatementInfo> outgoingStatementsInfo;

    // fields for relation creation

    protected Boolean showCreateForm = false;

    protected String predicateUri;

    protected String comment;

    protected String searchKeywords;

    protected List<String> targetCreationDocuments;

    public DocumentModel getDocumentModel(Node node) throws ClientException {
        if (node.isQNameResource()) {
            QNameResource resource = (QNameResource) node;
            Map<String, Serializable> context = new HashMap<String, Serializable>();
            context.put(ResourceAdapter.CORE_SESSION_ID_CONTEXT_KEY,
                    documentManager.getSessionId());
            Object o = relationManager.getResourceRepresentation(
                    resource.getNamespace(), resource, context);
            if (o instanceof DocumentModel) {
                return (DocumentModel) o;
            }
        }
        return null;
    }

    public QNameResource getDocumentResource(DocumentModel document)
            throws ClientException {
        QNameResource documentResource = null;
        if (document != null) {
            documentResource = (QNameResource) relationManager.getResource(
                    RelationConstants.DOCUMENT_NAMESPACE, document, null);
        }
        return documentResource;
    }

    protected List<StatementInfo> getStatementsInfo(List<Statement> statements)
            throws ClientException {
        if (statements == null) {
            return null;
        }
        List<StatementInfo> infoList = new ArrayList<StatementInfo>();
        for (Statement statement : statements) {
            Subject subject = statement.getSubject();
            // TODO: filter on doc visibility (?)
            NodeInfo subjectInfo = new NodeInfoImpl(subject,
                    getDocumentModel(subject), true);
            Resource predicate = statement.getPredicate();
            Node object = statement.getObject();
            NodeInfo objectInfo = new NodeInfoImpl(object,
                    getDocumentModel(object), true);
            StatementInfo info = new StatementInfoImpl(statement, subjectInfo,
                    new NodeInfoImpl(predicate), objectInfo);
            infoList.add(info);
        }
        return infoList;
    }

    public void resetStatements() {
        incomingStatements = null;
        incomingStatementsInfo = null;
        outgoingStatements = null;
        outgoingStatementsInfo = null;
    }

    // getters & setters for creation items

    public Boolean getShowCreateForm() {
        return showCreateForm;
    }

    public void toggleCreateForm(ActionEvent event) {
        showCreateForm = !showCreateForm;
    }

    public String getPredicateUri() {
        return predicateUri;
    }

    public void setPredicateUri(String predicateUri) {
        this.predicateUri = predicateUri;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(String searchKeywords) {
        this.searchKeywords = searchKeywords;
    }

    public List<String> getTargetCreationDocuments() {
        if (targetCreationDocuments == null) {
            targetCreationDocuments = new ArrayList<String>();
        }
        return targetCreationDocuments;
    }

    public void setTargetCreationDocuments(List<String> targetCreationDocuments) {
        this.targetCreationDocuments = targetCreationDocuments;
    }

    protected void resetCreateFormValues() {
        showCreateForm = false;
        predicateUri = null;
        comment = null;
        searchKeywords = null;
        targetCreationDocuments = null;
    }

    protected void notifyEvent(String eventId, DocumentModel source,
            Map<String, Serializable> options, String comment) {

        EventProducer evtProducer = null;

        try {
            evtProducer = Framework.getService(EventProducer.class);
        } catch (Exception e) {
            log.error("Unable to get EventProducer to send event notification",
                    e);
        }

        DocumentEventContext docCtx = new DocumentEventContext(documentManager,
                documentManager.getPrincipal(), source);
        options.put("category", RelationEvents.CATEGORY);
        options.put("comment", comment);

		if (evtProducer != null) {
			try {
				evtProducer.fireEvent(docCtx.newEvent(eventId));
			} catch (ClientException e) {
				log.error("Error while trying to send notification message", e);
			}
		}
    }

    public DocumentModel getDocumentModel(String id) throws ClientException {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        return documentManager.getDocument(new IdRef(id));
    }

    @Override
    protected void resetCurrentCaseItemCache(DocumentModel cachedEmail,
            DocumentModel newEmail) throws ClientException {
        resetStatements();
        resetCreateFormValues();
    }
}
