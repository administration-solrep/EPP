package fr.dila.reponses.rest.api;

import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherErrataReponsesRequest;
import fr.sword.xsd.reponses.ChercherErrataReponsesResponse;
import fr.sword.xsd.reponses.ChercherReponsesRequest;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerReponseErrataRequest;
import fr.sword.xsd.reponses.EnvoyerReponseErrataResponse;
import fr.sword.xsd.reponses.EnvoyerReponsesRequest;
import fr.sword.xsd.reponses.EnvoyerReponsesResponse;

public interface WSReponse {

	public static final String	SERVICE_NAME							= "WSreponse";

	public static final String	METHOD_NAME_TEST						= "test";
	public static final String	METHOD_NAME_VERSION						= "version";

	public static final String	METHOD_NAME_CHERCHER_REPONSES			= "chercherReponses";
	public static final String	METHOD_NAME_CHERCHER_ERRATA_REPONSES	= "chercherErrataReponses";
	public static final String	METHOD_NAME_ENVOYER_REPONSES			= "envoyerReponses";
	public static final String	METHOD_NAME_ENVOYER_REPONSE_ERRATA		= "envoyerReponseErrata";

	public String test() throws Exception;

	public VersionResponse version() throws Exception;

	public ChercherReponsesResponse chercherReponses(ChercherReponsesRequest request) throws Exception;

	public ChercherErrataReponsesResponse chercherErrataReponses(ChercherErrataReponsesRequest request)
			throws Exception;

	public EnvoyerReponsesResponse envoyerReponses(EnvoyerReponsesRequest request) throws Exception;

	public EnvoyerReponseErrataResponse envoyerReponseErrata(EnvoyerReponseErrataRequest request) throws Exception;

}
