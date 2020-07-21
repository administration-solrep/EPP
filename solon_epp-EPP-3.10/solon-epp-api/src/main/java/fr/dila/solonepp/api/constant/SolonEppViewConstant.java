package fr.dila.solonepp.api.constant;

/**
 * Vues de l'application SOLON EPP.
 * 
 * @author jtremeaux
 */
public final class SolonEppViewConstant {

    // *************************************************************
    // Espace corbeille
    // *************************************************************
    /**
     * Espace corbeille
     */
     public static final String ESPACE_CORBEILLE_VIEW = "view_corbeille";
     
     /**
      * Content view des corbeilles
      */
     public static final String  CORBEILLE_CONTENT_VIEW = "corbeille_message_list";
    /**
     * Vue de creation d'un evenement
     */
    public static final String VIEW_CREATE_EVENEMENT = "create_metadonnee_evenement";
    
    /**
     * Vue de creation d'un evenement
     */
    public static final String VIEW_CREATE_EVENEMENT_SUCCESSIF = "create_metadonnee_evenement_successif";
     
    /**
     * Vue rectifier un événement
     */
    public static final String VIEW_RECTIFIER_EVENEMENT = "rectifier_metadonnee_evenement";
     
    /**
     * Vue completer un événement
     */
    public static final String VIEW_COMPLETER_EVENEMENT = "completer_metadonnee_evenement";
    
    /**
     * Vue modifier un événement
     */
    public static final String VIEW_MODIFIER_EVENEMENT = "modifier_metadonnee_evenement";
    
    /**
     * Vue transmettre mail
     */
    public static final String VIEW_TRANSMETTRE_MAIL_EVENEMENT = "transmettre_mail_evenement";
    
    // *************************************************************
    // Espace de création
    // *************************************************************
    /**
     * Espace de création
     */
    public static final String ESPACE_CREATION_VIEW = "view_espace_creation";

    // *************************************************************
    // Espace de suivi
    // *************************************************************
    /**
     * Espace de suivi
     */
    public static final String ESPACE_SUIVI_VIEW = "view_espace_suivi";

    /**
     * Espace de suivi : dossier signalés
     */
    public static final String ESPACE_SUIVI_DOSSIER_SIGNALES_VIEW = "view_espace_suivi_dossiers_signales";
    
    /**
     * Espace de suivi : mes alertes
     */
    public static final String ESPACE_SUIVI_MES_ALERTES_VIEW = "view_espace_suivi_mes_alertes";
    
    /**
     * Dossiers similaires
     */
    public static final String DOSSIERS_SIMILAIRES_VIEW = "view_dossiers_similaires";
    
    /**
     * Bordereau dossier similaire
     */
    public static final String DOSSIER_SIMILAIRE_BORDEREAU_VIEW = "view_dossier_similaire_bordereau";

    // *************************************************************
    // Espace de recherche
    // *************************************************************
    /**
     * Vue de la saisie des critères de recherche.
     */
    public static final String RECHERCHE_CRITERIA_VIEW = "recherche_criteria";
    /**
     * Vue de la saisie de la recherche libre.
     */
    public static final String REQUETE_LIBRE_VIEW = "requete_libre";
    
    /**
     * Vue des résultats de recherche.
     */
    public static final String RECHERCHE_RESULT_VIEW = "recherche_result";
    
    // *************************************************************
    // Espace d'administration
    // *************************************************************
    /**
     * Vue des modèles de fond de dossier.
     */
    public static final String MODELES_PARAPHEUR_VIEW = "view_modeles_parapheur";

    /**
     * Vue des modèles de fond de dossier.
     */
    public static final String MODELES_FOND_DOSSIER_VIEW = "view_modeles_fond_dossier";
    
    /**
     * Vue du paramétrage de l'application.
     */
    public static final String PARAMETRAGE_APPLICATION_VIEW = "view_parametrage_application";
    
    /**
     * Vue de dossier a supprimer.
     */
    public static final String ADMIN_SUPPRESSION_VIEW = "view_admin_suppression";

	/**
	 * Vue de dossier transmis à l' administrateur ministriel a supprimer.
	 */
	public static final String ADMIN_MINISTERIELLE_SUPPRESSION_VIEW = "view_admin_ministrielle_suppression";
	
	/**
	 * Vue des dossiers a abandonner.
	 */
	public static final String ADMIN_ABANDON_VIEW = "view_admin_abandon";
	
	/**
	 * Vue des dossiers transmit au administrateur ministrielle par l'administrateur fonctionelle.
	 */
	public static final String ADMIN_SUPPRESSION_VIEW_SUIVI = "view_admin_suppression_suivi";
    
	/**
     * Vue des dossier a archiver.
     */
    public static final String ADMIN_ARCHIVAGE_INTERMEDIAIRE_VIEW = "view_admin_archivage_intermediaire";

    public static final String ADMIN_ARCHIVAGE_DEFINITIVE_VIEW = "view_admin_archivage_definitive";
    
    /**
     * Vue pour le transfert des dossiers clos à un autre ministere.
     */
    public static final String ADMIN_TRANSFERT_VIEW = "view_admin_transfert";
    
    /**
     * Vue des substitution de postes dans les modèles de feuille de route.
     */
    public static final String ADMIN_MODELE_FEUILLE_ROUTE_MASS_SUBSTITUER_POSTE = "admin_modele_feuille_route_mass_substituer_poste";
    
    /**
     * Espace Table de référence
     */
    public static final String TABLE_REFERENCE_VIEW = "view_table_reference";

    /**
     * utility class
     */
    private SolonEppViewConstant(){
    	// do nothing
    }
}
