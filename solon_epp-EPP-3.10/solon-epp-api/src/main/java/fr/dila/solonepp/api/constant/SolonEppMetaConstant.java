package fr.dila.solonepp.api.constant;

/**
 * Constantes du service de métadonnées.
 * 
 * @author jtremeaux
 */
public final class SolonEppMetaConstant {
    // *************************************************************
    // Types de métadonnées de SOLON EPP (types primitifs)
    // *************************************************************
    /**
     * Type de métadonnées : chaîne.
     */
    public static final String META_TYPE_STRING = "text";

    /**
     * Type de métadonnées : date (même format que xs:date).
     */
    public static final String META_TYPE_DATE = "date";

    /**
     * Type de métadonnées : entier.
     */
    public static final String META_TYPE_INTEGER = "int";

    /**
     * Type de métadonnées : booléen.
     */
    public static final String META_TYPE_BOOLEAN = "boolean";

    // *************************************************************
    // Types de métadonnées de SOLON EPP (vocabulaires)
    // *************************************************************
    /**
     * Type de métadonnées : vocabulaire attribution de commission.
     */
    public static final String META_TYPE_ATTRIBUTION_COMMISSION = "attribution_commission";

    /**
     * Type de métadonnées : vocabulaire motif d'irrecevabilité.
     */
    public static final String META_TYPE_MOTIF_IRRECEVABILITE = "motif_irrecevabilite";

    /**
     * Type de métadonnées : vocabulaire niveau de lecture.
     */
    public static final String META_TYPE_NIVEAU_LECTURE = "niveau_lecture";

    /**
     * Type de métadonnées : vocabulaire nature de loi.
     */
    public static final String META_TYPE_NATURE_LOI = "nature_loi";

    /**
     * Type de métadonnées : vocabulaire nature de rapport.
     */
    public static final String META_TYPE_NATURE_RAPPORT = "nature_rapport";

    /**
     * Type de métadonnées : vocabulaire rapport de parlement.
     */
    public static final String META_TYPE_RAPPORT_PARLEMENT = "rapport_parlement";

    /**
     * Type de métadonnées : vocabulaire sens d'avis.
     */
    public static final String META_TYPE_SENS_AVIS = "sens_avis";

    /**
     * Type de métadonnées : vocabulaire sort d'adoption.
     */
    public static final String META_TYPE_SORT_ADOPTION = "sort_adoption";
    
    /**
     * Type de métadonnées : vocabulaire résultat CMP.
     */
    public static final String META_TYPE_RESULTAT_CMP = "resultat_cmp";

    /**
     * Type de métadonnées : vocabulaire type d'acte.
     */
    public static final String META_TYPE_TYPE_ACTE = "type_acte";

    /**
     * Type de métadonnées : vocabulaire type de loi.
     */
    public static final String META_TYPE_TYPE_LOI = "type_loi";

    /**
     * Type de métadonnées : vocabulaire décision engagement procédure accelérée
     */
    public static final String META_TYPE_DECISION_PROC_ACC = "decision_proc_acc";

	/**
	 * Type de métadonnées : vocabulaire rubrique (EVT45)
	 */
	public static final String META_TYPE_RUBRIQUE = "rubrique";

    // *************************************************************
    // Types de métadonnées de SOLON EPP (objets de référence)
    // *************************************************************
    /**
     * Type de métadonnées : table de référence Acteur.
     */
    public static final String META_TYPE_ACTEUR = "acteur";

    /**
     * Type de métadonnées : table de référence Circonscription.
     */
    public static final String META_TYPE_CIRCONSCRIPTION = "circonscription";

    /**
     * Type de métadonnées : table de référence Circonscription.
     */
    public static final String META_TYPE_GOUVERNEMENT = "gouvernement";

    /**
     * Type de métadonnées : table de référence Identité.
     */
    public static final String META_TYPE_IDENTITE = "identite";

    /**
     * Type de métadonnées : table de référence Mandat.
     */
    public static final String META_TYPE_MANDAT = "mandat";

    /**
     * Type de métadonnées : table de référence Membre de groupe.
     */
    public static final String META_TYPE_MEMBRE_GROUPE = "membre_groupe";

    /**
     * Type de métadonnées : table de référence Ministère.
     */
    public static final String META_TYPE_MINISTERE = "ministere";

    /**
     * Type de métadonnées : table de référence Organisme.
     */
    public static final String META_TYPE_ORGANISME = "organisme";

    /**
     * Type de métadonnées : table de référence Période.
     */
    public static final String META_TYPE_PERIODE = "periode";
    
    /**
     * utility class
     */
    private SolonEppMetaConstant(){
    	// do nothing
    }
}
