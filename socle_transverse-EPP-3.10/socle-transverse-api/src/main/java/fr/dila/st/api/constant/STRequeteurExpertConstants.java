package fr.dila.st.api.constant;

/**
 * Les constantes du requêteur expert
 * 
 * @author jgomez
 * 
 */
public final class STRequeteurExpertConstants {

	/**
	 * Toutes les catégories du requêteur expert
	 */
	public static final String	REQUETEUR_EXPERT_ALL_CATEGORY						= "tous";

	public static final String REQUETEUR_EXPERT_HIDDEN_CATEGORY 					= "hidden";

	/**
	 * catégorie vide du requêteur expert
	 */

	public static final String	REQUETEUR_EXPERT_NO_CATEGORY						= "";

	/**
	 * Les propriétés supplémentaires possibles pour certains widgets.
	 * 
	 */

	public static final String	REQUETEUR_PROPERTIES_DIRECTORY_NAME					= "DIRECTORY";

	public static final String	REQUETEUR_PROPERTIES_DIRECTORY_HAS_TO_CONVERT_LABEL	= "HAS_TO_CONVERT_LABEL";

	public static final String	REQUETEUR_PROPERTIES_CONVERTER						= "CONVERTER";

	public static final Object	REQUETEUR_PROPERTIES_CONVERTER_CLASS				= "CONVERTER_CLASS";

	/**
	 * Les types de widget du requêteur expert
	 */
	public static final String	REQUETEUR_TYPE_ORGANIGRAMME							= "orga";
	public static final String	REQUETEUR_TYPE_POSTEORGANIGRAMME					= "posteorga";
	public static final String	REQUETEUR_TYPE_DATE									= "date";
	public static final String	REQUETEUR_TYPE_DIRECTORY							= "manyDirectory";
	public static final String	REQUETEUR_TYPE_USER									= "user";
	public static final String	REQUETEUR_TYPE_ETAPE								= "etapeDossier";


	/**
	 * utility class
	 */
	private STRequeteurExpertConstants() {
		// do nothing
	}
}
