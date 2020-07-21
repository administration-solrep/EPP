package fr.dila.reponses.rest.client;

import fr.dila.reponses.rest.api.WSReponse;
import fr.dila.st.rest.client.AbstractWsProxy;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherErrataReponsesRequest;
import fr.sword.xsd.reponses.ChercherErrataReponsesResponse;
import fr.sword.xsd.reponses.ChercherReponsesRequest;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerReponseErrataRequest;
import fr.sword.xsd.reponses.EnvoyerReponseErrataResponse;
import fr.sword.xsd.reponses.EnvoyerReponsesRequest;
import fr.sword.xsd.reponses.EnvoyerReponsesResponse;

public class WSReponseProxy extends AbstractWsProxy implements WSReponse {

	public WSReponseProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
		super(endpoint, basePath, username, password, keyAlias);
	}

	@Override
	public String test() throws Exception {
		return doGet(METHOD_NAME_TEST, String.class);
	}

	@Override
	public VersionResponse version() throws Exception {
		return doGet(METHOD_NAME_VERSION, VersionResponse.class);
	}

	@Override
	public ChercherReponsesResponse chercherReponses(ChercherReponsesRequest request) throws Exception {
		return doPost(WSReponse.METHOD_NAME_CHERCHER_REPONSES, request, ChercherReponsesResponse.class);
	}

	@Override
	public ChercherErrataReponsesResponse chercherErrataReponses(ChercherErrataReponsesRequest request)
			throws Exception {
		return doPost(WSReponse.METHOD_NAME_CHERCHER_ERRATA_REPONSES, request, ChercherErrataReponsesResponse.class);
	}

	@Override
	public EnvoyerReponsesResponse envoyerReponses(EnvoyerReponsesRequest request) throws Exception {
		return doPost(WSReponse.METHOD_NAME_ENVOYER_REPONSES, request, EnvoyerReponsesResponse.class);
	}

	@Override
	public EnvoyerReponseErrataResponse envoyerReponseErrata(EnvoyerReponseErrataRequest request) throws Exception {
		return doPost(WSReponse.METHOD_NAME_ENVOYER_REPONSE_ERRATA, request, EnvoyerReponseErrataResponse.class);
	}

	@Override
	protected String getServiceName() {
		return WSReponse.SERVICE_NAME;
	}

}
