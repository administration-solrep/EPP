package fr.dila.ss.ui.services.actions.impl;

import fr.dila.ss.api.service.DocumentRoutingService;
import fr.dila.ss.ui.services.actions.RelatedRouteActionService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

public class RelatedRouteActionServiceImpl implements RelatedRouteActionService {

    @Override
    public List<DocumentModel> findRelatedRoute(CoreSession session, DocumentModel currentDocument) {
        List<DocumentModel> docs = new ArrayList<>();
        if (currentDocument == null) {
            return docs;
        }

        List<FeuilleRoute> relatedRoutes = getDocumentRoutingService()
            .getRoutesForAttachedDocument(session, currentDocument.getId());
        for (FeuilleRoute documentRoute : relatedRoutes) {
            docs.add(documentRoute.getDocument());
        }
        return docs;
    }

    private DocumentRoutingService getDocumentRoutingService() {
        return Framework.getService(DocumentRoutingService.class);
    }
}
