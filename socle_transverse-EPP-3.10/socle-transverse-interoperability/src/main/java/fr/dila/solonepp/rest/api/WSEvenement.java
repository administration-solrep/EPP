package fr.dila.solonepp.rest.api;

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

public interface WSEvenement {

	public static final String	SERVICE_NAME					= "WSevenement";

	public static final String	METHOD_TEST						= "test";
	public static final String	METHOD_VERSION					= "version";

	public static final String	METHOD_CHERCHER_EVENEMENT		= "chercherEvenement";
	public static final String	METHOD_CHERCHER_PIECE_JOINTE	= "chercherPieceJointe";
	public static final String	METHOD_CREER_VERSION			= "creerVersion";
	public static final String	METHOD_CREER_VERSION_DELTA		= "creerVersionDelta";
	public static final String	METHOD_VALIDER_VERSION			= "validerVersion";
	public static final String	METHOD_ANNULER_EVENEMENT		= "annulerEvenement";
	public static final String	METHOD_SUPPRIMER_VERSION		= "supprimerVersion";
	public static final String	METHOD_ACCUSER_RECEPTION		= "accuserReception";
	public static final String	METHOD_INITIALISER_EVENEMENT	= "initialiserEvenement";
	public static final String	METHOD_ENVOYER_MEL				= "envoyerMel";
	public static final String	METHOD_RECHERCHER_EVENEMENT		= "rechercherEvenement";
	public static final String	METHOD_MAJ_INTERNE				= "majInterne";

	public String test() throws Exception;

	public VersionResponse version() throws Exception;

	public ChercherEvenementResponse chercherEvenement(ChercherEvenementRequest request) throws Exception;

	public ChercherPieceJointeResponse chercherPieceJointe(ChercherPieceJointeRequest request) throws Exception;

	public CreerVersionResponse creerVersion(CreerVersionRequest request) throws Exception;

	public CreerVersionDeltaResponse creerVersionDelta(CreerVersionDeltaRequest request) throws Exception;

	public ValiderVersionResponse validerVersion(ValiderVersionRequest request) throws Exception;

	public AnnulerEvenementResponse annulerEvenement(AnnulerEvenementRequest request) throws Exception;

	public SupprimerVersionResponse supprimerVersion(SupprimerVersionRequest request) throws Exception;

	public AccuserReceptionResponse accuserReception(AccuserReceptionRequest request) throws Exception;

	public InitialiserEvenementResponse initialiserEvenement(InitialiserEvenementRequest request) throws Exception;

	public EnvoyerMelResponse envoyerMel(EnvoyerMelRequest request) throws Exception;

	public RechercherEvenementResponse rechercherEvenement(RechercherEvenementRequest request) throws Exception;

	public MajInterneResponse majInterne(MajInterneRequest request) throws Exception;

}
