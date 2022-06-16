package fr.dila.ss.ui.services.actions;

import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface RelatedRouteActionService {
    List<DocumentModel> findRelatedRoute(CoreSession session, DocumentModel currentDocument);
}
