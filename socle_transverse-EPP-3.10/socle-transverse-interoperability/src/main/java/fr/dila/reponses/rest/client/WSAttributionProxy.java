package fr.dila.reponses.rest.client;

import fr.dila.reponses.rest.api.WSAttribution;
import fr.dila.st.rest.client.AbstractWsProxy;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherAttributionsDateRequest;
import fr.sword.xsd.reponses.ChercherAttributionsDateResponse;
import fr.sword.xsd.reponses.ChercherAttributionsRequest;
import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.ChercherLegislaturesResponse;
import fr.sword.xsd.reponses.ChercherMembresGouvernementRequest;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;

public class WSAttributionProxy extends AbstractWsProxy implements WSAttribution {

	public WSAttributionProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
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
	public ChercherAttributionsResponse chercherAttributions(ChercherAttributionsRequest request) throws Exception {
		return doPost(WSAttribution.METHOD_NAME_CHERCHER_ATTRIBUTIONS, request, ChercherAttributionsResponse.class);
	}

	@Override
	public ChercherMembresGouvernementResponse chercherMembresGouvernement(ChercherMembresGouvernementRequest request)
			throws Exception {
		return doPost(WSAttribution.METHOD_NAME_CHERCHER_MEMBRES_GOUVERNEMENT, request,
				ChercherMembresGouvernementResponse.class);
	}

	@Override
	public ChercherLegislaturesResponse chercherLegislatures() throws Exception {
		return doPost(WSAttribution.METHOD_NAME_CHERCHER_LEGISLATURES, null, ChercherLegislaturesResponse.class);
	}

	@Override
	protected String getServiceName() {
		return WSAttribution.SERVICE_NAME;
	}

	@Override
	public ChercherAttributionsDateResponse chercherAttributionsDate(ChercherAttributionsDateRequest request)
			throws Exception {
		return doPost(WSAttribution.METHOD_NAME_CHERCHER_ATTRIBUTIONS_DATE, request,
				ChercherAttributionsDateResponse.class);
	}

}
