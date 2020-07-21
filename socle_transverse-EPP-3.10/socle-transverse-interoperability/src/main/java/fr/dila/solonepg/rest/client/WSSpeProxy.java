package fr.dila.solonepg.rest.client;

import fr.dila.solonepg.rest.api.WSSpe;
import fr.dila.st.rest.client.AbstractWsProxy;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePERequest;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePEResponse;
import fr.sword.xsd.solon.spe.EnvoyerRetourPERequest;
import fr.sword.xsd.solon.spe.EnvoyerRetourPEResponse;

/**
 * classe utilisée pour lancer la requête envoyerPremiereDemandePE par web service sur une machine de la dila lors de
 * l'arrivée à une étape de publication.
 * 
 * @author arolin
 */
public class WSSpeProxy extends AbstractWsProxy implements WSSpe {

	public WSSpeProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
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
	protected String getServiceName() {
		return WSSpe.SERVICE_NAME;
	}

	@Override
	public EnvoyerPremiereDemandePEResponse envoyerPremiereDemandePE(EnvoyerPremiereDemandePERequest request)
			throws Exception {
		return doPost(METHOD_NAME_ENVOYER_PREMIERE_DEMANDE_PE, request, EnvoyerPremiereDemandePEResponse.class);
	}

	@Override
	public EnvoyerRetourPEResponse envoyerRetourPE(EnvoyerRetourPERequest request) throws Exception {
		return doPost(METHOD_NAME_ENVOYER_RETOUR_PE, request, EnvoyerRetourPEResponse.class);
	}

}
