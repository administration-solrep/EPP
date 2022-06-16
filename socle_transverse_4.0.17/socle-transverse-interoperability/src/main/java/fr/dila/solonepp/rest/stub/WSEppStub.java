package fr.dila.solonepp.rest.stub;

import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.solonepp.rest.api.WSEpp;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epp.ChercherCorbeilleRequest;
import fr.sword.xsd.solon.epp.ChercherCorbeilleResponse;
import fr.sword.xsd.solon.epp.ChercherDossierRequest;
import fr.sword.xsd.solon.epp.ChercherDossierResponse;
import fr.sword.xsd.solon.epp.ChercherIdentiteRequest;
import fr.sword.xsd.solon.epp.ChercherIdentiteResponse;
import fr.sword.xsd.solon.epp.ChercherMandatParNORRequest;
import fr.sword.xsd.solon.epp.ChercherMandatParNORResponse;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceRequest;
import fr.sword.xsd.solon.epp.ChercherTableDeReferenceResponse;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesRequest;
import fr.sword.xsd.solon.epp.HasCommunicationNonTraiteesResponse;
import fr.sword.xsd.solon.epp.MajTableRequest;
import fr.sword.xsd.solon.epp.MajTableResponse;
import fr.sword.xsd.solon.epp.NotifierTransitionRequest;
import fr.sword.xsd.solon.epp.NotifierTransitionResponse;
import fr.sword.xsd.solon.epp.NotifierVerrouRequest;
import fr.sword.xsd.solon.epp.NotifierVerrouResponse;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJORequest;
import fr.sword.xsd.solon.epp.TransmissionDatePublicationJOResponse;

public class WSEppStub implements WSEpp {
	private static final String	FILE_BASEPATH						= "fr/dila/st/rest/stub/epp/wsepp/";

	public static final String	CHERCHER_CORBEILLE_REQUEST			= FILE_BASEPATH
																			+ "chercherCorbeille/WSepp_chercherCorbeilleRequest.xml";
	public static final String	CHERCHER_CORBEILLE_RESPONSE			= FILE_BASEPATH
																			+ "chercherCorbeille/WSepp_chercherCorbeilleResponse.xml";

	public static final String	CHERCHER_DOSSIER_REQUEST			= FILE_BASEPATH
																			+ "chercherDossier/WSepp_chercherDossierRequest.xml";
	public static final String	CHERCHER_DOSSIER_RESPONSE			= FILE_BASEPATH
																			+ "chercherDossier/WSepp_chercherDossierResponse.xml";

	public static final String	CHERCHER_TABLE_REFERENCE_REQUEST	= FILE_BASEPATH
																			+ "chercherTableDeReference/WSepp_chercherTableDeReferenceRequest.xml";
	public static final String	CHERCHER_TABLE_REFERENCE_RESPONSE	= FILE_BASEPATH
																			+ "chercherTableDeReference/WSepp_chercherTableDeReferenceResponse.xml";

	public static final String	NOTIFIER_TRANSITION_REQUEST			= FILE_BASEPATH
																			+ "notifierTransition/WSepp_notifierTransitionRequest.xml";
	public static final String	NOTIFIER_TRANSITION_RESPONSE		= FILE_BASEPATH
																			+ "notifierTransition/WSepp_notifierTransitionResponse.xml";

	public static final String	NOTIFIER_VERROU_REQUEST				= FILE_BASEPATH
																			+ "notifierVerrou/WSepp_notifierVerrouRequest.xml";
	public static final String	NOTIFIER_VERROU_RESPONSE			= FILE_BASEPATH
																			+ "notifierVerrou/WSepp_notifierVerrouResponse.xml";

	public static final String	MAJ_TABLE_REQUEST					= FILE_BASEPATH
																			+ "majTable/WSepp_majTableRequest.xml";
	public static final String	MAJ_TABLE_RESPONSE					= FILE_BASEPATH
																			+ "majTable/WSepp_majTableResponse.xml";

	public static final String	CHERCHER_IDENTITE_REQUEST			= FILE_BASEPATH
																			+ "chercherIdentite/WSepp_chercherIdentiteRequest.xml";
	public static final String	CHERCHER_IDENTITE_RESPONSE			= FILE_BASEPATH
																			+ "chercherIdentite/WSepp_chercherIdentiteResponse.xml";

	public static final String	CHERCHER_MANDAT_NOR_REQUEST			= FILE_BASEPATH
																			+ "chercherMandat/WSepp_chercherMandatParNORRequest.xml";
	public static final String	CHERCHER_MANDAT_NOR_RESPONSE		= FILE_BASEPATH
																			+ "chercherMandat/WSepp_chercherMandatParNORResponse.xml";

	public static final String	HAS_COMMUNICATION_REQUEST			= FILE_BASEPATH
																			+ "hasCommunication/WSepp_hasCommunicationNonTraiteesRequest.xml";
	public static final String	HAS_COMMUNICATION_RESPONSE			= FILE_BASEPATH
																			+ "hasCommunication/WSepp_hasCommunicationNonTraiteesResponse.xml";
	
	public static final String TRANSMISSION_DATE_PUBLI_JO_REQUEST	= FILE_BASEPATH
																			+ "transmissionDatePublicationJO/WSepp_tranmissionDatePublicationJORequest.xml";
	public static final String TRANSMISSION_DATE_PUBLI_JO_RESPONSE	= FILE_BASEPATH
																			+ "transmissionDatePublicationJO/WSepp_tranmissionDatePublicationJOResponse.xml";

	@Override
	public String test() throws Exception {
		return SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSEpp();
	}

	@Override
	public ChercherCorbeilleResponse chercherCorbeille(ChercherCorbeilleRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_CORBEILLE_RESPONSE, ChercherCorbeilleResponse.class);
	}

	@Override
	public ChercherDossierResponse chercherDossier(ChercherDossierRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_DOSSIER_RESPONSE, ChercherDossierResponse.class);
	}

	@Override
	public ChercherTableDeReferenceResponse chercherTableDeReference(ChercherTableDeReferenceRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_TABLE_REFERENCE_RESPONSE,
				ChercherTableDeReferenceResponse.class);
	}

	@Override
	public NotifierTransitionResponse notifierTransition(NotifierTransitionRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(NOTIFIER_TRANSITION_RESPONSE, NotifierTransitionResponse.class);
	}

	@Override
	public NotifierVerrouResponse notifierVerrou(NotifierVerrouRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(NOTIFIER_VERROU_RESPONSE, NotifierVerrouResponse.class);
	}

	@Override
	public MajTableResponse majTable(MajTableRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(MAJ_TABLE_RESPONSE, MajTableResponse.class);
	}

	@Override
	public ChercherIdentiteResponse chercherIdentite(ChercherIdentiteRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_IDENTITE_RESPONSE, ChercherIdentiteResponse.class);
	}

	@Override
	public ChercherMandatParNORResponse chercherMandatParNor(ChercherMandatParNORRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_MANDAT_NOR_RESPONSE, ChercherMandatParNORResponse.class);
	}

	@Override
	public HasCommunicationNonTraiteesResponse hasCommunicationNonTraitees(HasCommunicationNonTraiteesRequest request)
			throws Exception {
		return JaxBHelper.buildRequestFromFile(HAS_COMMUNICATION_RESPONSE, HasCommunicationNonTraiteesResponse.class);
	}

	@Override
	public TransmissionDatePublicationJOResponse transmissionDatePublicationJO(
			TransmissionDatePublicationJORequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(TRANSMISSION_DATE_PUBLI_JO_RESPONSE, TransmissionDatePublicationJOResponse.class);
	}

}
