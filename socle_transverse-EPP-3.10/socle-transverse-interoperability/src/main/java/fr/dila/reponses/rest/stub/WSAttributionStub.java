package fr.dila.reponses.rest.stub;

import fr.dila.reponses.rest.api.WSAttribution;
import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherAttributionsDateRequest;
import fr.sword.xsd.reponses.ChercherAttributionsDateResponse;
import fr.sword.xsd.reponses.ChercherAttributionsRequest;
import fr.sword.xsd.reponses.ChercherAttributionsResponse;
import fr.sword.xsd.reponses.ChercherLegislaturesResponse;
import fr.sword.xsd.reponses.ChercherMembresGouvernementRequest;
import fr.sword.xsd.reponses.ChercherMembresGouvernementResponse;

public class WSAttributionStub implements WSAttribution {

	private static final String	FILE_BASEPATH	= "fr/dila/st/rest/stub/wsattribution/";

	@Override
	public String test() throws Exception {
		return WSAttribution.SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSAttribution();
	}

	@Override
	public ChercherAttributionsResponse chercherAttributions(ChercherAttributionsRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSattribution_chercherAttributionResponse.xml",
				ChercherAttributionsResponse.class);
	}

	@Override
	public ChercherMembresGouvernementResponse chercherMembresGouvernement(ChercherMembresGouvernementRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSattribution_chercherMembresGouvernementResponse.xml",
				ChercherMembresGouvernementResponse.class);
	}

	@Override
	public ChercherLegislaturesResponse chercherLegislatures() throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSattribution_chercherLegislaturesResponse.xml",
				ChercherLegislaturesResponse.class);
	}

	@Override
	public ChercherAttributionsDateResponse chercherAttributionsDate(ChercherAttributionsDateRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH + "WSattribution_chercherAttributionsDateResponse.xml",
				ChercherAttributionsDateResponse.class);
	}

}
