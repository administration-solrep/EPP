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
package fr.dila.ecm.platform.routing.web;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.ClientRuntimeException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRoutingService;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
@Name("relatedRouteAction")
@Scope(ScopeType.EVENT)
public class RelatedRouteActionBean {

    @In(required = true, create = true)
    protected NavigationContext navigationContext;

    @In(create = true, required = false)
    protected CoreSession documentManager;

    @Factory(value = "relatedRoutes")
    public List<DocumentModel> findRelatedRoute() throws ClientException {
        List<DocumentModel> docs = new ArrayList<DocumentModel>();
        DocumentModel currentDocument = navigationContext.getCurrentDocument();
        if(currentDocument == null) {
            return docs;
        }
        List<DocumentRoute> relatedRoutes = getDocumentRoutingService().getDocumentRoutesForAttachedDocument(
                documentManager, currentDocument.getId());
        for (DocumentRoute documentRoute : relatedRoutes) {
            docs.add(documentRoute.getDocument());
        }
        return docs;
    }

    public DocumentRoutingService getDocumentRoutingService() {
        try {
            return Framework.getService(DocumentRoutingService.class);
        } catch (Exception e) {
            throw new ClientRuntimeException(e);
        }
    }
}
