package fr.dila.st.api.constant;

/**
 * Vues du socle transverse.
 *
 * @author jtremeaux
 */
public final class STViewConstant {
    // *************************************************************
    // Vues transverses
    // *************************************************************
    /**
     * Vue vide.
     */
    public static final String EMPTY_VIEW = "view_empty";

    /**
     * Erreur de validation (raffiche la même page).
     */
    public static final String ERROR_VIEW = "ERROR";

    /**
     * Vue d'export CSV.
     */
    public static final String CSV_VIEW = "csv";

    // *************************************************************
    // Vues de l'espace d'administration
    // *************************************************************
    /**
     * Vue gestion de l'organigramme.
     */
    public static final String ORGANIGRAMME_VIEW_MANAGE = "manage_organigramme";

    /**
     * Vue edition d'un élement de l'organigramme.
     */
    public static final String ORGANIGRAMME_EDIT_POSTE = "edit_poste_organigramme";

    /**
     * Vue edition d'un élement de l'organigramme.
     */
    public static final String ORGANIGRAMME_EDIT_POSTE_WS = "edit_poste_ws_organigramme";

    /**
     * Vue edition d'un élement de l'organigramme.
     */
    public static final String ORGANIGRAMME_EDIT_UNITE_STRUCTURELLE = "edit_unite_structurelle_organigramme";

    /**
     * Vue edition d'un élement de l'organigramme.
     */
    public static final String ORGANIGRAMME_EDIT_ENTITE = "edit_entite_organigramme";

    /**
     * Vue edition d'un élement de l'organigramme.
     */
    public static final String ORGANIGRAMME_EDIT_GOUVERNEMENT = "edit_gouvernement_organigramme";

    /**
     * Vue création unité structurelle de l'organigramme.
     */
    public static final String ORGANIGRAMME_CREATE_UNITE_STRUCTURELLE = "create_unite_structurelle_organigramme";

    /**
     * Vue création poste de l'organigramme.
     */
    public static final String ORGANIGRAMME_CREATE_POSTE = "create_poste_organigramme";

    /**
     * Vue création poste webservice de l'organigramme.
     */
    public static final String ORGANIGRAMME_CREATE_POSTE_WS = "create_poste_ws_organigramme";

    /**
     * Vue création de gouvernement de l'organigramme.
     */
    public static final String ORGANIGRAMME_CREATE_GOUVERNEMENT = "create_gouvernement_organigramme";

    /**
     * Vue création entité de l'organigramme.
     */
    public static final String ORGANIGRAMME_CREATE_ENTITE = "create_entite_organigramme";

    /**
     * Vue de la liste des modèles de feuille de route.
     */
    public static final String MODELES_FEUILLE_ROUTE_VIEW = "view_modeles_feuille_route";

    /**
     * Vue du détail d'un modèle de feuille de route.
     */
    public static final String MODELE_FEUILLE_ROUTE_VIEW = "view_modele_feuille_route";

    /**
     * Vue du journal de l'espace d'administration.
     */
    public static final String JOURNAL_VIEW = "view_admin_journal";

    /**
     * Vue de la recherche d'utilisateur.
     */
    public static final String RECHERCHE_UTILISATEUR_VIEW = "view_recherche_utilisateur";

    /**
     * Vue de création d'un utilisateur.
     */
    public static final String CREATE_USER_VIEW = "create_user";

    /**
     * Vue de l'édition de l'état de l'application
     */
    public static final String ETAT_APPLICATION_VIEW = "edit_etat-application";

    /**
     * Vue des logs d'exécution des batchs
     */
    public static final String BATCH_SUIVI_VIEW = "view_suivi_batch";

    /**
     * Vue des planifications quartz
     */
    public static final String BATCH_SUIVI_PLANIFICATION = "view_suivi_batch_planification";

    /**
     * Vue des notifications de suivi des batchs
     */
    public static final String BATCH_SUIVI_NOTIFICATION = "view_suivi_batch_notification";

    // *************************************************************
    // Vues des feuilles de route
    // *************************************************************
    /**
     * Vue des notes d'étape.
     */
    public static final String VIEW_ROUTE_STEP_NOTE_VIEW = "view_route_step_note";

    /**
     * utility class
     */
    private STViewConstant() {
        // do nothing
    }
}
