package fr.dila.ss.ui.services.actions;

import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Actions permettant de gérer une feuille de route.
 *
 * @author jtremeaux
 */
public interface FeuilleRouteActionService {
    /**
     * Retourne la feuille de route chargée.
     *
     * @return Feuille de route
     */
    FeuilleRoute getRelatedRoute(CoreSession session, DocumentModel currentDocument, List<DocumentModel> relatedRoutes);

    /**
     * Invalide le modèle de feuille de route en cours.
     */
    void invalidateRouteModel(
        SpecificContext context,
        CoreSession session,
        DocumentModel currentDocument,
        List<DocumentModel> relatedRoutes
    );

    /*
     * Vérification des droits pour l'action d'ajout d'étape
     */
    boolean checkRightSaveEtape(SpecificContext context);

    /*
     * Vérification des droits pour supprimer une étape ou une branche
     */
    boolean checkRightDeleteBranchOrStep(SpecificContext context);

    /*
     * Vérification des droits de déplacer une étape
     */
    boolean checkRightMoveStep(SpecificContext context);

    /*
     * Vérification des droits d'édition d'une étape
     */
    boolean checkRightUpdateStep(SpecificContext context);

    /*
     * Vérification des droits de coller des étapes
     */
    boolean checkRightPasteStep(SpecificContext context);

    void initRoutingActionDto(SpecificContext context, String id);
}
