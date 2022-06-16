package fr.dila.cm.service;

import fr.dila.st.api.dossier.STDossier;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 *
 */
public interface CaseManagementPersister {
    DocumentModel getParentDocumentForCase(CoreSession session);

    /**
     * @param session
     * @return
     */
    String getParentDocumentPathForCase(CoreSession session);

    /**
     * @param session
     * @param kase
     * @return
     */
    String getParentDocumentPathForCaseItem(CoreSession session, STDossier kase);
}
