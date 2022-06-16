package fr.dila.solonepp.api.constant;

/**
 * Constantes des événements de SOLON EPP.
 *
 * @author jtremeaux
 */
public final class SolonEppEventConstant {
    // *************************************************************
    // Catégorie d'Événements
    // *************************************************************

    //    /**
    //     * type d'événement lié au traitement papier
    //     */
    //    public static final String CATEGORY_TRAITEMENT_PAPIER = "ListeTraitementPapier";

    // *************************************************************
    // Événements pour l'audit trail
    // *************************************************************

    /**
     * Événement levé après la création d'un caseLink dans la distribution
     */
    public static final String AFTER_CASE_LINK_CREATED_EVENT = "afterCaseLinkCreated";

    // *************************************************************
    // Événements des dossiers
    // *************************************************************
    /**
     * Après la création du dossier (avant le démarrage de la feuille de route).
     */
    public static final String AFTER_DOSSIER_CREATED = "afterDossierCreated";

    /**
     * Changement de statut d'un dossier
     */
    public static final String DOSSIER_STATUT_CHANGED = "dossierStatutChanged";

    /**
     * Après la création du dossier (avant le démarrage de la feuille de route).
     */
    public static final String INJECTION_AFTER_DOSSIER_CREATED = "injectionAfterDossierCreated";

    /**
     * Après la création du dossier (avant le démarrage de la feuille de route).
     */
    public static final String AFTER_VALIDER_TRANSMISSION_TO_SUPPRESSION = "afterValiderTransmissionToSuppresion";

    public static final String DOCUMENT_ID_LIST = "documentIdList";

    // *************************************************************
    // Paramètres des événements inline
    // *************************************************************

    /**
     * Paramètre de l'événement "afterCaseLinkCreated" : Dossier.
     */
    public static final String AFTER_CASE_LINK_CREATED_DOSSIER_DOC_PARAM = "dossierDoc";

    /**
     * Paramètre de l'événement "afterCaseLinkCreated" : RouteStep.
     */
    public static final String AFTER_CASE_LINK_CREATED_ROUTE_STEP_DOC_PARAM = "routeStep";

    /**
     * Dossier SOLON EPP.
     */
    public static final String DOSSIER_EVENT_PARAM = "dossier";

    /**
     * Identifiant technique du poste.
     */
    public static final String POSTE_ID_EVENT_PARAM = "posteId";

    /**
     * Evenement lors du changement de document dans la recherche
     */
    public static final String CURRENT_DOCUMENT_SEARCH_CHANGED_EVENT = "currentDocumentSearchChangedEvent";

    /**
     * Parametre du chemin pour la sauvegarde du resultat consulte
     */
    public static final String RESULTAT_CONSULTE_CURRENT_PATH = "resultatConsulteCurrentPath";

    /**
     * utility class
     */
    private SolonEppEventConstant() {
        // do nothing
    }
}
