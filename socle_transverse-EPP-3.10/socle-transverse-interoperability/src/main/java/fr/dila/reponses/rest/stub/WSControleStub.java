package fr.dila.reponses.rest.stub;

import fr.dila.reponses.rest.api.WSControle;
import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.reponses.ChercherRetourPublicationRequest;
import fr.sword.xsd.reponses.ChercherRetourPublicationResponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;

public class WSControleStub implements WSControle {

	private static final String	FILE_BASEPATH	= "fr/dila/st/rest/stub/wscontrole/";

	@Override
	public String test() throws Exception {
		return WSControleStub.SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSControle();
	}

	@Override
	public ControlePublicationResponse controlePublication(ControlePublicationRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH
				+ "controlepublication/WScontrole_controlePublicationResponse.xml", ControlePublicationResponse.class);
	}

	@Override
	public ChercherRetourPublicationResponse chercherRetourPublication(ChercherRetourPublicationRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(FILE_BASEPATH
				+ "chercherretourpublication/WScontrole_chercherRetourPublicationResponse.xml",
				ChercherRetourPublicationResponse.class);
	}

}
