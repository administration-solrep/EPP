package fr.dila.reponses.rest.stub;

import fr.dila.reponses.rest.api.WSReponse;
import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherErrataReponsesRequest;
import fr.sword.xsd.reponses.ChercherErrataReponsesResponse;
import fr.sword.xsd.reponses.ChercherReponsesRequest;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerReponseErrataRequest;
import fr.sword.xsd.reponses.EnvoyerReponseErrataResponse;
import fr.sword.xsd.reponses.EnvoyerReponsesRequest;
import fr.sword.xsd.reponses.EnvoyerReponsesResponse;

public class WSReponseStub implements WSReponse {

	private static final String	FILE_BASEPATH	= "fr/dila/st/rest/stub/wsreponse/";

	@Override
	public String test() throws Exception {
		return WSReponse.SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSReponse();
	}

	@Override
	public ChercherReponsesResponse chercherReponses(ChercherReponsesRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSreponse_chercherReponsesResponse.xml",
				ChercherReponsesResponse.class);
	}

	@Override
	public ChercherErrataReponsesResponse chercherErrataReponses(ChercherErrataReponsesRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSreponse_chercherErrataReponsesResponse.xml",
				ChercherErrataReponsesResponse.class);
	}

	@Override
	public EnvoyerReponsesResponse envoyerReponses(EnvoyerReponsesRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSreponse_envoyerReponsesResponse.xml",
				EnvoyerReponsesResponse.class);
	}

	@Override
	public EnvoyerReponseErrataResponse envoyerReponseErrata(EnvoyerReponseErrataRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSreponse_envoyerReponseErrataResponse.xml",
				EnvoyerReponseErrataResponse.class);
	}

}
