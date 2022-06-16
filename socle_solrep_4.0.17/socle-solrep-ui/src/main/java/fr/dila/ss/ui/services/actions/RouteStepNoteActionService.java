package fr.dila.ss.ui.services.actions;

import fr.dila.ss.ui.bean.fdr.NoteEtapeFormDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Action service permettant de gérer les notes d'étapes.
 *
 * @author jtremeaux
 */
public interface RouteStepNoteActionService {
    /**
     * Retourne la liste des commentaires à la racine du document.
     *
     * @param routeStepDoc
     *            Document
     * @return Liste des commentaires
     */
    List<DocumentModel> getCommentList(SpecificContext context);

    NoteEtapeFormDTO getNoteEtape(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur a le droit de voir une note d'étape.
     *
     * @param routeStepDoc
     *            Étape de feuille de route
     * @return Condition
     */
    boolean isViewableNote(DocumentModel routeStepDoc);

    /**
     * Retourne vrai si l'utilisateur a le droit de modifier une note d'étape.
     *
     * @param routeStepDoc
     *            Étape de feuille de route
     * @return Condition
     */
    boolean isEditableNote(
        SpecificContext context,
        CoreSession session,
        DocumentModel dossierDoc,
        DocumentModel routeStepDoc,
        DocumentModel currentDoc
    );

    /**
     * Retourne le dossier courant associé à l'étape.
     *
     * @return Dossier courant
     */
    DocumentModel getCurrentDossierDoc(DocumentModel dossierDoc, DocumentModel currentDoc);

    /**
     * Renvoie le nombre total de notes sur cette étape. Indépendamment des
     * restrictions d'accès (on compte même celles auxquelles l'utilisateur n'a
     * pas accès).
     *
     * @param routeStepDoc
     *            DocumentModel route step
     * @return un nombre supérieur ou égal à 0.
     */
    int getCommentNumber(DocumentModel routeStepDoc);

    void initCreateSharedNote(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument,
        List<DocumentModel> currentDocumentSelection
    );
}
