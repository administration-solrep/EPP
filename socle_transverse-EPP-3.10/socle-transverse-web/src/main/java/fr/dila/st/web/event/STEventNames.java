package fr.dila.st.web.event;

/**
 * Les événements seam du socle.
 * 
 * @author jgomez
 * 
 */
public final class STEventNames {

	/**
	 * Requêteur expert de dossier EPG
	 */
	public static final String	REQUETEUR_QUERY_PART_ADDED			= "queryPartAdded";
	public static final String	REQUETEUR_QUERY_PART_CHANGED		= "queryPartChanged";

	/**
	 * Requêteur expert d'EPG pour l'activté normative
	 */
	public static final String	REQUETEUR_EPG_AN_QUERY_PART_ADDED	= "epganQueryPartAdded";
	public static final String	REQUETEUR_EPG_AN_PART_CHANGED		= "epganQueryPartChanged";

	/**
	 * Requeteur expert MGPP
	 */
	public static final String	REQUETEUR_MGPP_QUERY_PART_ADDED		= "mgppQueryPartAdded";
	public static final String	REQUETEUR_MGPP_QUERY_PART_CHANGED	= "mgppQueryPartChanged";

	/**
	 * Utility class
	 */
	private STEventNames() {
		// do nothing
	}
}
