package fr.dila.ss.api.service;

import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.query.api.PageProvider;

/**
 * Interface du service des actualités.
 * @author olejacques
 */
public interface ActualiteService {
    /**
     * Créé une actualité.
     */
    DocumentModel createActualite(CoreSession session, DocumentModel actualiteDoc);

    List<DocumentModel> fetchUserActualitesNonLues(CoreSession session);

    PageProvider<DocumentModel> getActualitesPageProvider(
        CoreSession session,
        DocumentModel actualiteRequeteDoc,
        List<SortInfo> sortInfos,
        long pageSize,
        long currentPage
    );
}
