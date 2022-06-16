package fr.dila.ss.api.constant;

/**
 * Constantes du socle SOLREP.
 *
 * @author jtremeaux
 */
public final class SSConstant {
    // *************************************************************
    // Types de documents (constantes de Nuxeo).
    // *************************************************************

    /**
     * Facette des documents routables.
     */
    public static final String ROUTABLE_FACET = "Routable";

    // *************************************************************
    // Feuilles de route
    // *************************************************************
    /**
     * Type de document racine des modèles de feuille.
     */
    public static final String FEUILLE_ROUTE_MODEL_FOLDER_DOCUMENT_TYPE = "FeuilleRouteModelFolder";

    /**
     * Type de document feuille de route.
     */
    public static final String FEUILLE_ROUTE_DOCUMENT_TYPE = "FeuilleRoute";

    /**
     * Folder path modele feuille route
     */
    public static final String FDR_INSTANCE_FOLDER_PATH = "/case-management/document-route-instances-root";

    /**
     * Folder path modele feuille route
     */
    public static final String FDR_FOLDER_PATH = "/case-management/workspaces/admin/modele-route";

    /**
     * Type de document étape de feuille de route.
     */
    public static final String ROUTE_STEP_DOCUMENT_TYPE = "RouteStep";

    /**
     * Type de document conteneur d'étapes.
     */
    public static final String STEP_FOLDER_DOCUMENT_TYPE = "StepFolder";

    // *************************************************************
    // Groupes (principal)
    // *************************************************************
    /**
     * Préfixe des groupes associés aux ministères.
     */
    public static final String GROUP_MINISTERE_PREFIX = "min-";

    // *************************************************************
    // Mailbox
    // *************************************************************
    /**
     * Préfixe de l'ID technique des Mailbox poste.
     */
    public static final String MAILBOX_POSTE_ID_PREFIX = "poste-";

    /**
     * type de noeud poste.
     */
    public static final String POSTE_TYPE = "PST_TYPE";

    /**
     * type de noeud direction.
     */
    public static final String DIR_TYPE = "DIR_TYPE";

    /**
     * type de noeud ministère.
     */
    public static final String MIN_TYPE = "MIN_TYPE";

    // Migration
    public static final String EN_COURS_STATUS = "EN_COURS";
    public static final String TERMINEE_STATUS = "TERMINEE";
    public static final String FAILED_STATUS = "FAILED";

    /**
     * type de noeud unité structurelle.
     */
    public static final String UST_TYPE = "UST_TYPE";

    /**
     * Administration changement organigramme
     */
    public static final String ADMINISTRATION_CHANGEMENT_ORGANIGRAMME = "AdministrationChangementGouvernement";

    /**
     * utility class
     */
    private SSConstant() {
        // do nothing
    }
}
