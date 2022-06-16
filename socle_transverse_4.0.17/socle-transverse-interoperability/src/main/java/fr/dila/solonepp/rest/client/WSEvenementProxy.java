package fr.dila.solonepp.rest.client;

import fr.dila.solonepp.rest.api.WSEvenement;
import fr.dila.st.rest.client.AbstractWsProxy;
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

public class WSEvenementProxy extends AbstractWsProxy implements WSEvenement {

	public WSEvenementProxy(String endpoint, String basePath, String username, String password, String keyAlias) {
		super(endpoint, basePath, username, password, keyAlias);
	}

	@Override
	protected String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public String test() throws Exception {
		return doGet(METHOD_TEST, String.class);
	}

	@Override
	public VersionResponse version() throws Exception {
		return doGet(METHOD_VERSION, VersionResponse.class);
	}

	@Override
	public ChercherEvenementResponse chercherEvenement(ChercherEvenementRequest request) throws Exception {
		return doPost(METHOD_CHERCHER_EVENEMENT, request, ChercherEvenementResponse.class);
	}

	@Override
	public ChercherPieceJointeResponse chercherPieceJointe(ChercherPieceJointeRequest request) throws Exception {
		return doPost(METHOD_CHERCHER_PIECE_JOINTE, request, ChercherPieceJointeResponse.class);
	}

	@Override
	public CreerVersionResponse creerVersion(CreerVersionRequest request) throws Exception {
		return doPost(METHOD_CREER_VERSION, request, CreerVersionResponse.class);
	}

	@Override
	public CreerVersionDeltaResponse creerVersionDelta(CreerVersionDeltaRequest request) throws Exception {
		return doPost(METHOD_CREER_VERSION_DELTA, request, CreerVersionDeltaResponse.class);
	}

	@Override
	public AnnulerEvenementResponse annulerEvenement(AnnulerEvenementRequest request) throws Exception {
		return doPost(METHOD_ANNULER_EVENEMENT, request, AnnulerEvenementResponse.class);
	}

	@Override
	public AccuserReceptionResponse accuserReception(AccuserReceptionRequest request) throws Exception {
		return doPost(METHOD_ACCUSER_RECEPTION, request, AccuserReceptionResponse.class);
	}

	@Override
	public InitialiserEvenementResponse initialiserEvenement(InitialiserEvenementRequest request) throws Exception {
		return doPost(METHOD_INITIALISER_EVENEMENT, request, InitialiserEvenementResponse.class);
	}

	@Override
	public EnvoyerMelResponse envoyerMel(EnvoyerMelRequest request) throws Exception {
		return doPost(METHOD_ENVOYER_MEL, request, EnvoyerMelResponse.class);
	}

	@Override
	public RechercherEvenementResponse rechercherEvenement(RechercherEvenementRequest request) throws Exception {
		return doPost(METHOD_RECHERCHER_EVENEMENT, request, RechercherEvenementResponse.class);
	}

	@Override
	public SupprimerVersionResponse supprimerVersion(SupprimerVersionRequest request) throws Exception {
		return doPost(METHOD_SUPPRIMER_VERSION, request, SupprimerVersionResponse.class);
	}

	@Override
	public ValiderVersionResponse validerVersion(ValiderVersionRequest request) throws Exception {
		return doPost(METHOD_VALIDER_VERSION, request, ValiderVersionResponse.class);
	}

	@Override
	public MajInterneResponse majInterne(MajInterneRequest request) throws Exception {
		return doPost(METHOD_MAJ_INTERNE, request, MajInterneResponse.class);
	}

}
