package fr.dila.st.api.constant;

/**
 * Constantes pour les requêtes.
 * 
 * @author jgomez
 */
public final class STQueryConstant {

	/**
	 * Les types de language de requêtes :
	 */
	public static final String	NXQL					= "NXQL";

	public static final String	UFNXQL					= "UFNXQL";

	/* **********************************************
	 * Constantes pour les requêtes ***********************************************
	 */
	// Mots Clés
	public static final String	SELECT					= "SELECT ";
	public static final String	FROM					= " FROM ";
	public static final String	WHERE					= " WHERE ";
	public static final String	AND						= " AND ";
	public static final String	OR						= " OR ";
	public static final String	AS						= " AS ";
	public static final String	IN						= " IN ";
	public static final String	EQUAL					= " = ";
	public static final String	EQUAL_PARAM				= EQUAL + "? ";
	public static final String	EQUAL_PARAM_QUOTED		= EQUAL + "'?' ";
	public static final String	NOT_EQUAL				= " != ";
	public static final String	NOT_EQUAL_PARAM			= NOT_EQUAL + "? ";
	public static final String	NOT_EQUAL_PARAM_QUOTED	= NOT_EQUAL + "'?' ";
	public static final String	GREATER_THAN			= " > ";
	public static final String	GREATER_THAN_PARAM		= GREATER_THAN + "?";
	public static final String	LOWER_THAN				= " < ";
	public static final String	LOWER_THAN_PARAM		= LOWER_THAN + "?";
	// Documents
	public static final String	DOSSIER_ALIAS			= "d";
	public static final String	DL_ALIAS				= "dl";
	public static final String	ALIAS_SEP				= ".";
	public static final String	PREFIX_SEP				= ":";
	public static final String	D_DOS_PREFIX			= DOSSIER_ALIAS + ALIAS_SEP + "dos" + PREFIX_SEP;
	public static final String	D_ECM_UUID				= DOSSIER_ALIAS + ALIAS_SEP + STSchemaConstant.ECM_UUID_XPATH;
	public static final String	DL_ECM_UUID				= DL_ALIAS + ALIAS_SEP + STSchemaConstant.ECM_UUID_XPATH;

	public static final String	DL_ACSLK_PREFIX			= DL_ALIAS + ALIAS_SEP
																+ STSchemaConstant.ACTIONABLE_CASE_LINK_SCHEMA_PREFIX
																+ PREFIX_SEP;

	/**
	 * utility class
	 */
	private STQueryConstant() {
		// do nothing
	}
}
