package fr.dila.solonepp.rest.stub;

import fr.dila.reponses.rest.helper.VersionHelper;
import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.epp.AccuserReceptionRequest;
import fr.sword.xsd.solon.epp.AccuserReceptionResponse;
import fr.sword.xsd.solon.epp.AnnulerEvenementRequest;
import fr.sword.xsd.solon.epp.AnnulerEvenementResponse;
import fr.sword.xsd.solon.epp.ChercherEvenementRequest;
import fr.sword.xsd.solon.epp.ChercherEvenementResponse;
import fr.sword.xsd.solon.epp.ChercherPieceJointeRequest;
import fr.sword.xsd.solon.epp.ChercherPieceJointeResponse;
import fr.sword.xsd.solon.epp.CreerVersionDeltaRequest;
import fr.sword.xsd.solon.epp.CreerVersionDeltaResponse;
import fr.sword.xsd.solon.epp.CreerVersionRequest;
import fr.sword.xsd.solon.epp.CreerVersionResponse;
import fr.sword.xsd.solon.epp.EnvoyerMelRequest;
import fr.sword.xsd.solon.epp.EnvoyerMelResponse;
import fr.sword.xsd.solon.epp.InitialiserEvenementRequest;
import fr.sword.xsd.solon.epp.InitialiserEvenementResponse;
import fr.sword.xsd.solon.epp.MajInterneRequest;
import fr.sword.xsd.solon.epp.MajInterneResponse;
import fr.sword.xsd.solon.epp.RechercherEvenementRequest;
import fr.sword.xsd.solon.epp.RechercherEvenementResponse;
import fr.sword.xsd.solon.epp.SupprimerVersionRequest;
import fr.sword.xsd.solon.epp.SupprimerVersionResponse;
import fr.sword.xsd.solon.epp.ValiderVersionRequest;
import fr.sword.xsd.solon.epp.ValiderVersionResponse;

public class WSEvenementStub implements WSEvenement {

	private static final String	FILE_BASEPATH					= "fr/dila/st/rest/stub/epp/wsevenement/";

	public static final String	CHERCHER_EVENEMENT_REQUEST		= FILE_BASEPATH
																		+ "chercherEvenement/WSevenement_chercherEvenementRequest.xml";
	public static final String	CHERCHER_EVENEMENT_RESPONSE		= FILE_BASEPATH
																		+ "chercherEvenement/WSevenement_chercherEvenementResponse.xml";

	public static final String	CHERCHER_PIECE_JOINTE_REQUEST	= FILE_BASEPATH
																		+ "chercherPieceJointe/WSevenement_chercherPieceJointeRequest.xml";
	public static final String	CHERCHER_PIECE_JOINTE_RESPONSE	= FILE_BASEPATH
																		+ "chercherPieceJointe/WSevenement_chercherPieceJointeResponse.xml";

	public static final String	CREER_VERSION_REQUEST			= FILE_BASEPATH
																		+ "creerVersion/WSevenement_creerVersionRequest.xml";
	public static final String	CREER_VERSION_RESPONSE			= FILE_BASEPATH
																		+ "creerVersion/WSevenement_creerVersionResponse.xml";

	public static final String	CREER_VERSION_DELTA_REQUEST		= FILE_BASEPATH
																		+ "creerVersionDelta/WSevenement_creerVersionDeltaRequest.xml";
	public static final String	CREER_VERSION_DELTA_RESPONSE	= FILE_BASEPATH
																		+ "creerVersionDelta/WSevenement_creerVersionDeltaResponse.xml";

	public static final String	VALIDER_VERSION_REQUEST			= FILE_BASEPATH
																		+ "validerVersion/WSevenement_validerVersionRequest.xml";
	public static final String	VALIDER_VERSION_RESPONSE		= FILE_BASEPATH
																		+ "validerVersion/WSevenement_validerVersionResponse.xml";

	public static final String	ANNULER_EVENEMENT_REQUEST		= FILE_BASEPATH
																		+ "annulerEvenement/WSevenement_annulerEvenementRequest.xml";
	public static final String	ANNULER_EVENEMENT_RESPONSE		= FILE_BASEPATH
																		+ "annulerEvenement/WSevenement_annulerEvenementResponse.xml";

	public static final String	SUPPRIMER_VERSION_REQUEST		= FILE_BASEPATH
																		+ "supprimerVersion/WSevenement_supprimerVersionRequest.xml";
	public static final String	SUPPRIMER_VERSION_RESPONSE		= FILE_BASEPATH
																		+ "supprimerVersion/WSevenement_supprimerVersionResponse.xml";

	public static final String	ACCUSER_RECEPTION_REQUEST		= FILE_BASEPATH
																		+ "accuserReception/WSevenement_accuserReceptionRequest.xml";
	public static final String	ACCUSER_RECEPTION_RESPONSE		= FILE_BASEPATH
																		+ "accuserReception/WSevenement_accuserReceptionResponse.xml";

	public static final String	INITIALISER_EVENEMENT_REQUEST	= FILE_BASEPATH
																		+ "initialiserEvenement/WSevenement_initialiserEvenementRequest.xml";
	public static final String	INITIALISER_EVENEMENT_RESPONSE	= FILE_BASEPATH
																		+ "initialiserEvenement/WSevenement_initialiserEvenementResponse.xml";

	public static final String	ENVOYER_MEL_REQUEST				= FILE_BASEPATH
																		+ "envoyerMel/WSevenement_envoyerMelRequest.xml";
	public static final String	ENVOYER_MEL_RESPONSE			= FILE_BASEPATH
																		+ "envoyerMel/WSevenement_envoyerMelResponse.xml";

	public static final String	RECHERCHER_EVENEMENT_REQUEST	= FILE_BASEPATH
																		+ "rechercherEvenement/WSevenement_rechercherEvenementRequest.xml";
	public static final String	RECHERCHER_EVENEMENT_RESPONSE	= FILE_BASEPATH
																		+ "rechercherEvenement/WSevenement_rechercherEvenementResponse.xml";

	public static final String	MAJ_VISA_INTERNE_REQUEST		= FILE_BASEPATH
																		+ "majVisaInterne/WSevenement_majVisaInterneRequest.xml";
	public static final String	MAJ_VISA_INTERNE_RESPONSE		= FILE_BASEPATH
																		+ "majVisaInterne/WSevenement_majVisaInterneResponse.xml";

	@Override
	public String test() throws Exception {
		return SERVICE_NAME;
	}

	@Override
	public VersionResponse version() throws Exception {
		return VersionHelper.getVersionForWSEvenement();
	}

	@Override
	public ChercherEvenementResponse chercherEvenement(ChercherEvenementRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_EVENEMENT_RESPONSE, ChercherEvenementResponse.class);
	}

	@Override
	public ChercherPieceJointeResponse chercherPieceJointe(ChercherPieceJointeRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CHERCHER_PIECE_JOINTE_RESPONSE, ChercherPieceJointeResponse.class);
	}

	@Override
	public CreerVersionResponse creerVersion(CreerVersionRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CREER_VERSION_RESPONSE, CreerVersionResponse.class);
	}

	@Override
	public CreerVersionDeltaResponse creerVersionDelta(CreerVersionDeltaRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(CREER_VERSION_DELTA_RESPONSE, CreerVersionDeltaResponse.class);
	}

	@Override
	public AnnulerEvenementResponse annulerEvenement(AnnulerEvenementRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(ANNULER_EVENEMENT_RESPONSE, AnnulerEvenementResponse.class);
	}

	@Override
	public AccuserReceptionResponse accuserReception(AccuserReceptionRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(ACCUSER_RECEPTION_RESPONSE, AccuserReceptionResponse.class);
	}

	@Override
	public InitialiserEvenementResponse initialiserEvenement(InitialiserEvenementRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(INITIALISER_EVENEMENT_RESPONSE, InitialiserEvenementResponse.class);
	}

	@Override
	public EnvoyerMelResponse envoyerMel(EnvoyerMelRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(ENVOYER_MEL_RESPONSE, EnvoyerMelResponse.class);
	}

	@Override
	public RechercherEvenementResponse rechercherEvenement(RechercherEvenementRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(RECHERCHER_EVENEMENT_RESPONSE, RechercherEvenementResponse.class);
	}

	@Override
	public SupprimerVersionResponse supprimerVersion(SupprimerVersionRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(SUPPRIMER_VERSION_RESPONSE, SupprimerVersionResponse.class);
	}

	@Override
	public ValiderVersionResponse validerVersion(ValiderVersionRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(VALIDER_VERSION_RESPONSE, ValiderVersionResponse.class);
	}

	@Override
	public MajInterneResponse majInterne(MajInterneRequest request) throws Exception {
		return JaxBHelper.buildRequestFromFile(MAJ_VISA_INTERNE_RESPONSE, MajInterneResponse.class);
	}

}
