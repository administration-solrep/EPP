package fr.dila.ss.ui.services.actions;

import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.ui.th.bean.ModeleFdrEtapeSupprimeForm;
import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Actions permettant de gérer un modèle de feuille de route dans le socle
 * transverse.
 *
 * @author jtremeaux
 */
public interface ModeleFeuilleRouteActionService {
    /**
     * Détermine si l'utilisateur a le droit de lecture sur le modèle de feuille
     * de route.
     *
     * @param doc
     *            Modèle de feuille de route
     * @return Droit d'écriture
     */
    boolean canUserReadRoute(SpecificContext context);

    /**
     * Détermine si l'utilisateur a le droit de créer un modèle de feuille
     * de route.
     *
     * @return Droit d'écriture
     */
    boolean canUserCreateRoute(SpecificContext context);

    /**
     * Détermine si l'utilisateur a le droit d'écriture sur le modèle de feuille
     * de route.
     *
     * @param doc
     *            Modèle de feuille de route
     * @return Droit d'écriture
     */
    boolean canUserModifyRoute(SpecificContext context);

    /**
     * Détermine si l'utilisateur a le droit de verrouiller le model
     *
     */
    boolean canUserLockRoute(SpecificContext context);

    /**
     * Détermine si l'utilisateur a le droit de suppression sur le modèle de
     * feuille de route.
     *
     * @param doc
     *            Modèle de feuille de route
     * @return Droit de suppression
     */
    boolean canUserDeleteRoute(SpecificContext context);

    /**
     * Détermine si l'utilisateur a le droit de libérer le verrou sur le
     * document.
     *
     * @return Droit de libérer le verrou
     */
    boolean canUserLibererVerrou(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur courant peut créer des feuilles de route.
     *
     * @return Condition
     */
    boolean canCreateRoute(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur courant est administrateur fonctionnel.
     *
     * @return Condition
     */
    boolean isAdminFonctionnel(SpecificContext context);

    /**
     * Retourne vrai si le document est crée par l'adminitrateur fonctionnel.
     *
     * @param doc
     * @return
     */
    boolean isFeuilleDeRouteCreeParAdminFonctionnel(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur courant est administrateur ministériel.
     *
     * @return Condition
     */
    boolean isAdminMinisteriel(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur courant peut demander la validation du
     * modèle de feuille de route.
     *
     * @return Condition
     */
    boolean canRequestValidateRoute(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur courant peut annuler la demande de
     * validation du modèle de feuille de route.
     *
     * @return Condition
     */
    boolean canCancelRequestValidateRoute(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur courant peut valider le modèle de feuille
     * de route.
     *
     * @return Condition
     */
    boolean canValidateRoute(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur courant peut refuser la validation du
     * modèle de feuille de route.
     *
     * @return Condition
     */
    boolean canRefuseValidateRoute(SpecificContext context);

    /**
     * Retourne vrai si l'utilisateur courant peut invalider le modèle de
     * feuille de route.
     *
     * @return Condition
     */
    boolean canInvalidateRoute(SpecificContext context);

    /**
     * Retourne la feuille de route chargée à partir du document courant.
     *
     * @return Feuille de route
     */
    SSFeuilleRoute getRelatedRoute(CoreSession session, DocumentModel doc);

    /**
     * Libère le verrou et retourne à la liste.
     *
     * @return Vue
     */
    void libererVerrou(SpecificContext context);

    /**
     * Demande la validation du modèle de feuille de route.
     *
     * @return Vue
     */
    void requestValidateRouteModel(SpecificContext context);

    /**
     * Annule la demande de validation du modèle de feuille de route en cours.
     *
     * @return Vue
     */
    void cancelRequestValidateRouteModel(SpecificContext context);

    /**
     * Valide la feuille de route.
     *
     * @return Vue
     */
    void validateRouteModel(SpecificContext context);

    /**
     * Refuse la validation du modèle de feuille de route.
     *
     * @return Vue
     */
    void refuseValidateRouteModel(SpecificContext context);

    /**
     * Invalide le modèle de feuille de route en cours.
     *
     * @return Vue
     */
    void invalidateRouteModel(SpecificContext context);

    /**
     * Duplique un modèle de feuille de route.
     *
     * @return vue
     */
    DocumentModel duplicateRouteModel(SpecificContext context);

    /**
     * Retourne le prédicat permettant de filtrer les modèles de feuilles de route pour la substitution
     *
     * @return Prédicat NXQL
     */
    String getContentViewCriteriaSubstitution(SpecificContext context);

    /**
     * Crée le modèle de feuille de route.
     *
     * @return Vue
     */
    DocumentModel createDocument(SpecificContext context);

    /**
     * Sauvegarde les modifications du modèle de feuille de route.
     *
     * @return Vue
     */
    DocumentModel updateDocument(SpecificContext context);

    /**
     * Retourne l'id du repertoire des modeles de feuille de route
     */
    String getFeuilleRouteModelFolderId(CoreSession session);

    /**
     * Controle l'accès à la vue correspondante
     *
     */
    boolean isAccessAuthorized(CoreSession session);

    /**
     * Init un document de type FeuilleRoute.
     *
     * @return DocumentModel
     */
    DocumentModel initFeuilleRoute(CoreSession session, ModeleFdrForm form, String creatorName);

    /**
     * Effectue la suppression des étapes dans les feuilles de route
     * sélectionnées
     */
    void deleteMultipleStepsFromRoute(SpecificContext context);

    /**
     * List les étapes à supprimer en masse
     */
    List<ModeleFdrEtapeSupprimeForm> listStepsToDelete(SpecificContext context);

    void deleteModele(SpecificContext context);
}
