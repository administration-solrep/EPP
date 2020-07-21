package fr.dila.solonepp.api.constant;

/**
 * Constantes du cycle de vie des documents de SOLON EPP.
 * 
 * @author jtremeaux
 */
public final class SolonEppLifecycleConstant {
    // *************************************************************
    // État du type de document dossier.
    // *************************************************************
    /**
     * État du cycle de vie du type de document Dossier : en instance.
     */
    public static final String DOSSIER_INSTANCE_STATE = "instance";
    
    // *************************************************************
    // États du type de document événement.
    // *************************************************************
    /**
     * État du cycle de vie du type de document Evenement : initialisé.
     */
    public static final String EVENEMENT_INIT_STATE = "init";

    /**
     * État du cycle de vie du type de document Evenement : brouillon.
     */
    public static final String EVENEMENT_BROUILLON_STATE = "brouillon";
    
    /**
     * État du cycle de vie du type de document Evenement : publié.
     */
    public static final String EVENEMENT_PUBLIE_STATE = "publie";
    
    /**
     * État du cycle de vie du type de document Evenement : en instance.
     */
    public static final String EVENEMENT_INSTANCE_STATE = "instance";
    
    /**
     * État du cycle de vie du type de document Evenement : en attente de validation.
     */
    public static final String EVENEMENT_ATTENTE_VALIDATION_STATE = "attenteValidation";
    
    /**
     * État du cycle de vie du type de document Evenement : annulé.
     */
    public static final String EVENEMENT_ANNULE_STATE = "annule";
    
    // *************************************************************
    // Transitions du type de document événement.
    // *************************************************************
    /**
     * Transition du cycle de vie du type de document Evenement : toBrouillonFromInit.
     */
    public static final String EVENEMENT_TO_BROUILLON_FROM_INIT_TRANSITION = "toBrouillonFromInit";

    /**
     * Transition du cycle de vie du type de document Evenement : toBrouillonFromInit.
     */
    public static final String EVENEMENT_TO_PUBLIE_FROM_INIT_TRANSITION = "toPublieFromInit";

    /**
     * Transition du cycle de vie du type de document Evenement : toPublie.
     */
    public static final String EVENEMENT_TO_PUBLIE_TRANSITION = "toPublie";

    /**
     * Transition du cycle de vie du type de document Evenement : toInstance.
     */
    public static final String EVENEMENT_TO_INSTANCE_TRANSITION = "toInstance";

    /**
     * Transition du cycle de vie du type de document Evenement : toAttenteValidation.
     */
    public static final String EVENEMENT_TO_ATTENTE_VALIDATION_TRANSITION = "toAttenteValidation";

    /**
     * Transition du cycle de vie du type de document Evenement : backToInstance.
     */
    public static final String EVENEMENT_BACK_TO_INSTANCE_TRANSITION = "backToInstance";

    /**
     * Transition du cycle de vie du type de document Evenement : toAnnule.
     */
    public static final String EVENEMENT_TO_ANNULE_TRANSITION = "toAnnule";
    
    // *************************************************************
    // États du type de document version.
    // *************************************************************
    /**
     * État du cycle de vie du type de document Version : initialisé.
     */
    public static final String VERSION_INIT_STATE = "init";

    /**
     * État du cycle de vie du type de document Version : brouillon.
     */
    public static final String VERSION_BROUILLON_STATE = "brouillon";

    /**
     * État du cycle de vie du type de document Version : publié.
     */
    public static final String VERSION_PUBLIE_STATE = "publie";

    /**
     * État du cycle de vie du type de document Version : brouillon en attente de validation.
     */
    public static final String VERSION_BROUILLON_ATTENTE_VALIDATION_STATE = "brouillonAttenteValidation";

    /**
     * État du cycle de vie du type de document Version : en attente de validation.
     */
    public static final String VERSION_ATTENTE_VALIDATION_STATE = "attenteValidation";

    /**
     * État du cycle de vie du type de document Version : obsolète.
     */
    public static final String VERSION_OBSOLETE_STATE = "obsolete";

    /**
     * État du cycle de vie du type de document Version : rejeté.
     */
    public static final String VERSION_REJETE_STATE = "rejete";

    /**
     * État du cycle de vie du type de document Version : abandonné.
     */
    public static final String VERSION_ABANDONNE_STATE = "abandonne";

    // *************************************************************
    // Transitions du type de document version.
    // *************************************************************
    /**
     * Transition du cycle de vie du type de document Version : toBrouillonFromInit.
     */
    public static final String VERSION_TO_BROUILLON_FROM_INIT_TRANSITION = "toBrouillonFromInit";

    /**
     * Transition du cycle de vie du type de document Version : backToBrouillon.
     */
    public static final String VERSION_BACK_TO_BROUILLON_TRANSITION = "backToBrouillon";

    /**
     * Transition du cycle de vie du type de document Version : toBrouillonFromInit.
     */
    public static final String VERSION_TO_PUBLIE_FROM_INIT_TRANSITION = "toPublieFromInit";

    /**
     * Transition du cycle de vie du type de document Version : toAttenteValidationFromInit.
     */
    public static final String VERSION_TO_ATTENTE_VALIDATION_FROM_INIT_TRANSITION = "toAttenteValidationFromInit";

    /**
     * Transition du cycle de vie du type de document Version : toPublie.
     */
    public static final String VERSION_TO_PUBLIE_TRANSITION = "toPublie";

    /**
     * Transition du cycle de vie du type de document Version : toBrouillonAttenteValidation.
     */
    public static final String VERSION_TO_BROUILLON_ATTENTE_VALIDATION_TRANSITION = "toBrouillonAttenteValidation";

    /**
     * Transition du cycle de vie du type de document Version : toAttenteValidation.
     */
    public static final String VERSION_TO_ATTENTE_VALIDATION_TRANSITION = "toAttenteValidation";

    /**
     * Transition du cycle de vie du type de document Version : toObsolete.
     */
    public static final String VERSION_TO_OBSOLETE_TRANSITION = "toObsolete";

    /**
     * Transition du cycle de vie du type de document Version : toAnnule.
     */
    public static final String VERSION_TO_REJETE_TRANSITION = "toRejete";

    /**
     * Transition du cycle de vie du type de document Version : toAbandonne.
     */
    public static final String VERSION_TO_ABANDONNE_TRANSITION = "toAbandonne";

    // *************************************************************
    // États du type de document Message.
    // *************************************************************
    /**
     * État du cycle de vie du type de document Message : non traité.
     */
    public static final String MESSAGE_NON_TRAITE_STATE = "nontraite";

    /**
     * État du cycle de vie du type de document Message : en cours.
     */
    public static final String MESSAGE_EN_COURS_STATE = "encours";

    /**
     * État du cycle de vie du type de document Message : traité.
     */
    public static final String MESSAGE_TRAITE_STATE = "traite";

    // *************************************************************
    // États du type de document Message dénormalisé (champs "etatMessage" dans le documentModel) les valeurs sont celles du ws
    // *************************************************************
    /**
     * État du document Message : non traité.
     */
    public static final String MESSAGE_ETAT_WS_NON_TRAITE = "NON_TRAITE";
    
    /**
     * État du document Message : en cours de traitement.
     */
    public static final String MESSAGE_ETAT_WS_EN_COURS_TRAITEMENT = "EN_COURS_TRAITEMENT";
    
    /**
     * État du document Message : traité.
     */
    public static final String MESSAGE_ETAT_WS_TRAITE = "TRAITE";
    
    /**
     * État du document Message : En cours de rédaction.
     */
    public static final String MESSAGE_ETAT_WS_EN_COURS_REDACTION = "EN_COURS_REDACTION";
    
    /**
     * État du document Message : En attente AR
     */
    public static final String MESSAGE_ETAT_WS_EN_ATTENTE_AR = "EN_ATTENTE_AR";
    
    /**
     * État du document Message : Emis
     */
    public static final String MESSAGE_ETAT_WS_EMIS = "EMIS";
    
    /**
     * État du document Message : AR reçu
     */
    public static final String MESSAGE_ETAT_WS_AR_RECU = "AR_RECU";
    
    
    // *************************************************************
    // Transitions du type de document Message.
    // *************************************************************
    /**
     * Transition du cycle de vie du type de document Message : toEnCours.
     */
    public static final String MESSAGE_TO_EN_COURS_TRANSITION = "toEnCours";

    /**
     * Transition du cycle de vie du type de document Message : toTraite.
     */
    public static final String MESSAGE_TO_TRAITE_TRANSITION = "toTraite";

    /**
     * Transition du cycle de vie du type de document Message : backToNonTraite.
     */
    public static final String MESSAGE_BACK_TO_NON_TRAITE_TRANSITION = "backToNonTraite";
    
    /**
     * utility class
     */
    private SolonEppLifecycleConstant(){
    	// do nothing
    }
}
