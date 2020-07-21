package fr.dila.reponses.rest.api;

import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherRetourPublicationRequest;
import fr.sword.xsd.reponses.ChercherRetourPublicationResponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;

/**
 * 
 * @author fbarmes
 * 
 */
public interface WSControle {

	public static final String	SERVICE_NAME							= "WScontrole";

	public static final String	METHOD_NAME_TEST						= "test";
	public static final String	METHOD_NAME_VERSION						= "version";

	public static final String	METHOD_NAME_CONTROLE_PUBLICATION		= "controlePublication";
	public static final String	METHOD_NAME_CHERCHER_RETOUR_PUBLICATION	= "chercherRetourPublication";

	public String test() throws Exception;

	public VersionResponse version() throws Exception;

	public ControlePublicationResponse controlePublication(ControlePublicationRequest request) throws Exception;

	public ChercherRetourPublicationResponse chercherRetourPublication(ChercherRetourPublicationRequest request)
			throws Exception;

}
