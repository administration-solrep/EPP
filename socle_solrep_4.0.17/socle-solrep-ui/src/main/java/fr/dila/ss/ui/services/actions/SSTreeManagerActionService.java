package fr.dila.ss.ui.services.actions;

import fr.dila.ss.api.tree.SSTreeFolder;
import fr.dila.st.ui.th.model.SpecificContext;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Action service de gestion de l'arborescence.
 *
 */
public interface SSTreeManagerActionService {
    void addFile(SpecificContext context);

    void deleteFile(SpecificContext context);

    /**
     * Suppression d'un fichier dans l'arborescence du document.
     *
     * @return null
     * @author ARN
     */
    boolean deleteFile(CoreSession session, DocumentModel currentDocument, DocumentModel fondDeDossierFileDoc);

    void deleteDocument(CoreSession session, String selectedNodeId);

    boolean isFolderEmpty(SpecificContext context, CoreSession session, SSTreeFolder folder);
}
