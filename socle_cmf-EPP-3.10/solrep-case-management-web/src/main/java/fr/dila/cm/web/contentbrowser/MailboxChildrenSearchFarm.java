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
 *     Nicolas Ulrich
 *
 * $Id$
 */
package fr.dila.cm.web.contentbrowser;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PagedDocumentsProvider;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.search.api.client.querymodel.QueryModel;
import org.nuxeo.ecm.platform.ui.web.api.ResultsProviderFarm;
import org.nuxeo.ecm.webapp.base.InputController;
import org.nuxeo.ecm.webapp.pagination.ResultsProvidersCache;
import org.nuxeo.ecm.webapp.querymodel.QueryModelActions;

//TODO: change privacy of DocumentChildrenSearchFarm methods
/**
 * @author nicolas
 *
 */
@Name("mailboxChildrenSearchFarm")
@Scope(SESSION)
public class MailboxChildrenSearchFarm extends InputController implements
ResultsProviderFarm, Serializable {

    private static final long serialVersionUID = 8331654530334881666L;

    protected static final String FILTER_SCHEMA_NAME = "browsing_filters";

    protected static final String FILTER_FIELD_NAME_PARENT_ID = "query_parentId";

    @In(create = true)
    protected transient QueryModelActions queryModelActions;

    @In(create = true)
    protected transient ResultsProvidersCache resultsProvidersCache;

    @In(create = true, required = false)
    protected transient CoreSession documentManager;

    public PagedDocumentsProvider getResultsProvider(String name)
            throws ClientException {
        return getResultsProvider(name, null);
    }

    public PagedDocumentsProvider getResultsProvider(String name,
            SortInfo sortInfo) throws ClientException {
        final DocumentModel currentDoc = navigationContext.getCurrentDocument();

            PagedDocumentsProvider provider = getChildrenResultsProviderQMPattern(
                    name, currentDoc, sortInfo);
            provider.setName(name);
            return provider;
    }

    /**
     * Usable with a queryModel that defines a pattern NXQL.
     */
    protected PagedDocumentsProvider getChildrenResultsProviderQMPattern(
            String queryModelName, DocumentModel parent, SortInfo sortInfo)
            throws ClientException {

        final String parentId = parent.getId();
        Object[] params = { parentId };
        return getResultsProvider(queryModelName, params, sortInfo);
    }

    protected PagedDocumentsProvider getResultsProvider(String qmName,
            Object[] params, SortInfo sortInfo) throws ClientException {
        QueryModel qm = queryModelActions.get(qmName);
        return qm.getResultsProvider(documentManager, params, sortInfo);
    }

    /**
     * Usable with a queryModel that defines a WhereClause with predicates.
     */
    protected PagedDocumentsProvider getChildrenResultsProviderQMPred(
            String queryModelName, DocumentModel currentDoc)
            throws ClientException {
        QueryModel qm = queryModelActions.get(queryModelName);
        if (qm == null) {
            throw new ClientException("no QueryModel registered under name: "
                    + queryModelName);
        }

        // Invalidation code. TODO Would be better to listen to an event
        String currentRef = currentDoc.getRef().toString();
        if (!currentRef.equals(qm.getProperty(FILTER_SCHEMA_NAME,
                FILTER_FIELD_NAME_PARENT_ID))) {
            qm.setProperty(FILTER_SCHEMA_NAME, FILTER_FIELD_NAME_PARENT_ID,
                    currentRef);
            resultsProvidersCache.invalidate(queryModelName);
        }

        return resultsProvidersCache.get(queryModelName);
    }

}
