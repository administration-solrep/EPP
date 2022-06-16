package fr.dila.st.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

public interface STLockActionService {
    /**
     * Verrouille une liste de documents.
     *
     * @param session
     * @param documents
     * @param type
     * @return false si au moins 1 lock faux.
     */
    boolean lockDocuments(SpecificContext context, CoreSession session, List<DocumentModel> documents, String type);

    /**
     * Déverrouille un ensemble de documents sans tenir compte des droits de l'utilisateur courant.
     *
     * @param session
     * @param documents
     * @param type
     * @return Vue
     */
    String unlockDocumentsUnrestricted(SpecificContext context, List<DocumentModel> documents, String type);

    /**
     * Tests if the user can get the lock of a document.
     *
     * @return true if the user has this right, false otherwise
     */
    boolean getCanLockDoc(DocumentModel document, CoreSession session);

    /**
     * Tests if the user can unlock a document.
     *
     * @return true if the user has this right, false otherwise
     */
    boolean getCanUnlockDoc(DocumentModel document, CoreSession session);

    /**
     * Retourne les détails de verrou d'un document.
     *
     * @param document
     *            Document
     * @return Détails de verrou d'un document
     */
    Map<String, String> getLockDetails(DocumentModel document, CoreSession session);

    String getLockTime(DocumentModel document, CoreSession session);

    /**
     * Retourne le nom de la personne qui a verrouillé un document.
     *
     * @param document
     *            Document
     * @return Nom de la personne qui a verrouillé un document
     */
    String getLockOwnerName(DocumentModel document, CoreSession session);

    boolean currentDocIsLockActionnableByCurrentUser(
        CoreSession session,
        DocumentModel currentDocument,
        NuxeoPrincipal currentUser
    );
}
