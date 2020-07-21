package fr.dila.reponses.rest.api;

import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherAttributionsDateRequest;
import fr.sword.xsd.reponses.ChercherAttributionsDateResponse;
import fr.sword.xsd.reponses.ChercherAttributionsRequest;
import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.ChercherLegislaturesResponse;
import fr.sword.xsd.reponses.ChercherMembresGouvernementRequest;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;

/**
 * 
 * @author fbarmes
 * 
 */
public interface WSAttribution {

	public static final String	SERVICE_NAME								= "WSattribution";

	public static final String	METHOD_NAME_TEST							= "test";
	public static final String	METHOD_NAME_VERSION							= "version";

	public static final String	METHOD_NAME_CHERCHER_ATTRIBUTIONS			= "chercherAttributions";
	public static final String	METHOD_NAME_CHERCHER_ATTRIBUTIONS_DATE		= "chercherAttributionsDate";
	public static final String	METHOD_NAME_CHERCHER_MEMBRES_GOUVERNEMENT	= "chercherMembresGouvernement";
	public static final String	METHOD_NAME_CHERCHER_LEGISLATURES			= "chercherLegislatures";

	public String test() throws Exception;

	public VersionResponse version() throws Exception;

	public ChercherAttributionsResponse chercherAttributions(ChercherAttributionsRequest request) throws Exception;

	public ChercherAttributionsDateResponse chercherAttributionsDate(ChercherAttributionsDateRequest request)
			throws Exception;

	public ChercherMembresGouvernementResponse chercherMembresGouvernement(ChercherMembresGouvernementRequest request)
			throws Exception;

	public ChercherLegislaturesResponse chercherLegislatures() throws Exception;
}
