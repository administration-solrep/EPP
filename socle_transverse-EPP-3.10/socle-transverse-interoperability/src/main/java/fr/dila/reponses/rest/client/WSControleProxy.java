package fr.dila.reponses.rest.client;

import fr.dila.reponses.rest.api.WSControle;
import fr.dila.st.rest.client.AbstractWsProxy;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherRetourPublicationRequest;
import fr.sword.xsd.reponses.ChercherRetourPublicationResponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;

public class WSControleProxy extends AbstractWsProxy implements WSControle {

	public WSControleProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
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
	public ControlePublicationResponse controlePublication(ControlePublicationRequest request) throws Exception {
		return doPost(METHOD_NAME_CONTROLE_PUBLICATION, request, ControlePublicationResponse.class);
	}

	@Override
	public ChercherRetourPublicationResponse chercherRetourPublication(ChercherRetourPublicationRequest request)
			throws Exception {
		return doPost(METHOD_NAME_CHERCHER_RETOUR_PUBLICATION, request, ChercherRetourPublicationResponse.class);
	}

	@Override
	protected String getServiceName() {
		return WSControle.SERVICE_NAME;
	}

}
