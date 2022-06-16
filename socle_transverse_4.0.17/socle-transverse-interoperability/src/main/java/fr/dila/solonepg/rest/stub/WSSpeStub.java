package fr.dila.solonepg.rest.stub;

import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.solonepg.rest.api.WSSpe;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePERequest;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePEResponse;
import fr.sword.xsd.solon.spe.EnvoyerRetourPERequest;
import fr.sword.xsd.solon.spe.EnvoyerRetourPEResponse;

public class WSSpeStub implements WSSpe {

	private static final String	FILE_BASEPATH							= "fr/dila/st/rest/stub/epg/wsspe/";

	public static final String	ENVOYER_PREMIERE_DEMANDE_PE_REQUEST		= FILE_BASEPATH
																				+ "envoyerpremieredemandepe/EnvoyerPremiereDemandePERequest.xml";
	public static final String	ENVOYER_PREMIERE_DEMANDE_PE_RESPONSE	= FILE_BASEPATH
																				+ "envoyerpremieredemandepe/EnvoyerPremiereDemandePEResponse.xml";

	public static final String	ENVOYER_DEMANDE_SUIVANTE_PE_REQUEST		= FILE_BASEPATH
																				+ "envoyerdemandesuivantepe/EnvoyerDemandeSuivantePERequest.xml";
	public static final String	ENVOYER_DEMANDE_SUIVANTE_PE_RESPONSE	= FILE_BASEPATH
																				+ "envoyerdemandesuivantepe/EnvoyerDemandeSuivantePEResponse.xml";

	public static final String	ENVOYER_RETOUR_PE_REQUEST				= FILE_BASEPATH
																				+ "envoyerretourpe/EnvoyerRetourPERequest_publication.xml";
	public static final String	ENVOYER_RETOUR_PE_REQUEST_EPREUVAGE		= FILE_BASEPATH
																				+ "envoyerretourpe/EnvoyerRetourPERequest_epreuvage.xml";
	public static final String	ENVOYER_RETOUR_PE_RESPONSE				= FILE_BASEPATH
																				+ "envoyerretourpe/EnvoyerRetourPEResponse.xml";

	@Override
	public String test() throws Exception {
		return SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSspe();
	}

	@Override
	public EnvoyerPremiereDemandePEResponse envoyerPremiereDemandePE(EnvoyerPremiereDemandePERequest request)
			throws Exception {

		return JaxBHelper.buildRequestFromFile(ENVOYER_PREMIERE_DEMANDE_PE_RESPONSE,
				EnvoyerPremiereDemandePEResponse.class);
	}

	@Override
	public EnvoyerRetourPEResponse envoyerRetourPE(EnvoyerRetourPERequest request) throws Exception {

		return JaxBHelper.buildRequestFromFile(ENVOYER_RETOUR_PE_RESPONSE, EnvoyerRetourPEResponse.class);
	}

}
