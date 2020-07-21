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
 *    Mariana Cedica
 *
 * $Id$
 */

package fr.dila.ecm.platform.routing.web;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.search.api.client.querymodel.QueryModel;
import org.nuxeo.ecm.core.search.api.client.querymodel.QueryModelService;
import org.nuxeo.ecm.core.search.api.client.querymodel.descriptor.QueryModelDescriptor;
import org.nuxeo.ecm.platform.ui.web.invalidations.AutomaticDocumentBasedInvalidation;
import org.nuxeo.ecm.platform.ui.web.invalidations.DocumentContextBoundActionBean;
import org.nuxeo.runtime.api.Framework;

/**
 * Retrieves relations for current document route
 *
 * @author Mariana Cedica
 */
@Name("docRoutingSuggestionActions")
@Scope(CONVERSATION)
@AutomaticDocumentBasedInvalidation
public class DocumentRoutingSuggestionActionsBean extends
        DocumentContextBoundActionBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String CURRENT_DOC_ROUTING_SEARCH_ATTACHED_DOC = "CURRENT_DOC_ROUTING_SEARCH_ATTACHED_DOC";

    public static final String DOC_ROUTING_SEARCH_ALL_ROUTE_MODELS_QUERYMODEL = "DOC_ROUTING_SEARCH_ALL_ROUTE_MODELS";

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    public DocumentModel getDocumentModel(String id) throws ClientException {
        return documentManager.getDocument(new IdRef(id));
    }

    public List<DocumentModel> getDocumentSuggestions(Object input)
            throws ClientException {
        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        try {
            QueryModelService qms = Framework.getService(QueryModelService.class);
            if (qms == null) {
                return docs;
            }

            QueryModelDescriptor qmDescriptor = qms.getQueryModelDescriptor(CURRENT_DOC_ROUTING_SEARCH_ATTACHED_DOC);
            if (qmDescriptor == null) {
                return docs;
            }

            List<Object> queryParams = new ArrayList<Object>();
            queryParams.add(0, String.format("%s%%", input));
            QueryModel qm = new QueryModel(qmDescriptor);
            docs = qm.getDocuments(documentManager, queryParams.toArray());
        } catch (Exception e) {
            throw new ClientException("error searching for documents", e);
        }
        return docs;
    }

    public List<DocumentModel> getRouteModelSuggestions(Object input)
            throws ClientException {
        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        try {
            QueryModelService qms = Framework.getService(QueryModelService.class);
            if (qms == null) {
                return docs;
            }

            QueryModelDescriptor qmDescriptor = qms.getQueryModelDescriptor(DOC_ROUTING_SEARCH_ALL_ROUTE_MODELS_QUERYMODEL);
            if (qmDescriptor == null) {
                return docs;
            }

            List<Object> queryParams = new ArrayList<Object>();
            queryParams.add(0, String.format("%s%%", input));
            QueryModel qm = new QueryModel(qmDescriptor);
            docs = qm.getDocuments(documentManager, queryParams.toArray());
        } catch (Exception e) {
            throw new ClientException("error searching for documents", e);
        }
        return docs;
    }

    @Override
    protected void resetBeanCache(DocumentModel newCurrentDocumentModel) {
    }

}
