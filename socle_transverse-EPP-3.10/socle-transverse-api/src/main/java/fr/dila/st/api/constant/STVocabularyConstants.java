package fr.dila.st.api.constant;

/**
 * Constantes des vocabulaires.
 * 
 * @author jgomez
 * @author jtremeaux
 */
public final class STVocabularyConstants {
	// *************************************************************
	// Schémas des vocabulaires (Nuxeo)
	// *************************************************************
	/**
	 * Schéma vocabulaire par défaut.
	 */
	public static final String	VOCABULARY									= "vocabulary";

	/**
	 * Schéma vocabulaire hiérarchique.
	 */
	public static final String	XVOCABULARY									= "xvocabulary";

	/**
	 * Attribut par défaut contenant libellé.
	 */
	public static final String	COLUMN_LABEL								= "label";

	/**
	 * Attribut par défaut contenant l'identifiant.
	 */
	public static final String	COLUMN_ID									= "id";

	// *************************************************************
	// ?? pas un vocabulaire
	// *************************************************************
	/**
	 * Origine de la création d'une feuille de route : substitution
	 */
	public static final String	FEUILLE_ROUTE_TYPE_CREATION_SUBSTITUTION	= "substitution";

	/**
	 * Origine de la création d'une feuille de route : instanciation
	 */
	public static final String	FEUILLE_ROUTE_TYPE_CREATION_INSTANCIATION	= "instanciation";

	/**
	 * Origine de la création d'une feuille de route : reattribution
	 */
	public static final String	FEUILLE_ROUTE_TYPE_CREATION_REATTRIBUTION	= "reattribution";

	/**
	 * Origine de la création d'une feuille de route : reaffectation
	 */
	public static final String	FEUILLE_ROUTE_TYPE_CREATION_REAFFECTATION	= "reaffectation";

	// *************************************************************
	// Vocabulaire type d'étape de feuille de route
	// *************************************************************
	/**
	 * Vocabulaire des types d'étapes de feuilles de route.
	 */
	public static final String	ROUTING_TASK_TYPE_VOCABULARY				= "cm_routing_task_type";

	/**
	 * Vocabulaire label bordereau.
	 */
	public static final String	BORDEREAU_LABEL								= "bordereau_label";

	/**
	 * Vocabulaire label bordereau.
	 */
	public static final String	TRAITEMENT_PAPIER_LABEL						= "traitement_papier_label";

	/**
	 * utility class
	 */
	private STVocabularyConstants() {
		// do nothing
	}
}
