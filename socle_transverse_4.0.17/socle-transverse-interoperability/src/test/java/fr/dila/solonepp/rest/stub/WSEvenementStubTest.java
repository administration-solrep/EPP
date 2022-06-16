package fr.dila.solonepp.rest.stub;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

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

public class WSEvenementStubTest {

	private static WSEvenementStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new WSEvenementStub();
		Assert.assertNotNull(stub);
	}

	@Test
	public void testTest() throws Exception {
		String expected = WSEvenement.SERVICE_NAME;
		String actual = stub.test();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testVersion() throws Exception {
		VersionResponse response = stub.version();
		Assert.assertNotNull(response);
	}

	@Test
	public void testChercherEvenement() {
		try {
			ChercherEvenementRequest request = JaxBHelper.buildRequestFromFile(
					WSEvenementStub.CHERCHER_EVENEMENT_REQUEST, ChercherEvenementRequest.class);
			Assert.assertNotNull(request);

			ChercherEvenementResponse response = stub.chercherEvenement(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testChercherPieceJointe() {
		try {
			ChercherPieceJointeRequest request = JaxBHelper.buildRequestFromFile(
					WSEvenementStub.CHERCHER_PIECE_JOINTE_REQUEST, ChercherPieceJointeRequest.class);
			Assert.assertNotNull(request);

			ChercherPieceJointeResponse response = stub.chercherPieceJointe(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testCreerVersion() {
		try {

			CreerVersionRequest request = JaxBHelper.buildRequestFromFile(WSEvenementStub.CREER_VERSION_REQUEST,
					CreerVersionRequest.class);
			Assert.assertNotNull(request);

			CreerVersionResponse response = stub.creerVersion(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testAnnulerEvenement() {
		try {
			AnnulerEvenementRequest request = JaxBHelper.buildRequestFromFile(
					WSEvenementStub.ANNULER_EVENEMENT_REQUEST, AnnulerEvenementRequest.class);
			Assert.assertNotNull(request);

			AnnulerEvenementResponse response = stub.annulerEvenement(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testAccuserReception() {
		try {
			AccuserReceptionRequest request = JaxBHelper.buildRequestFromFile(
					WSEvenementStub.ACCUSER_RECEPTION_REQUEST, AccuserReceptionRequest.class);
			Assert.assertNotNull(request);

			AccuserReceptionResponse response = stub.accuserReception(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testInitialiserEvenement() {
		try {
			InitialiserEvenementRequest request = JaxBHelper.buildRequestFromFile(
					WSEvenementStub.INITIALISER_EVENEMENT_REQUEST, InitialiserEvenementRequest.class);
			Assert.assertNotNull(request);

			InitialiserEvenementResponse response = stub.initialiserEvenement(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testEnvoyerMel() {
		try {
			EnvoyerMelRequest request = JaxBHelper.buildRequestFromFile(WSEvenementStub.ENVOYER_MEL_REQUEST,
					EnvoyerMelRequest.class);
			Assert.assertNotNull(request);

			EnvoyerMelResponse response = stub.envoyerMel(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testRechercherEvenement() {
		try {
			RechercherEvenementRequest request = JaxBHelper.buildRequestFromFile(
					WSEvenementStub.RECHERCHER_EVENEMENT_REQUEST, RechercherEvenementRequest.class);
			Assert.assertNotNull(request);

			RechercherEvenementResponse response = stub.rechercherEvenement(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testSupprimerVersion() {
		try {
			SupprimerVersionRequest request = JaxBHelper.buildRequestFromFile(
					WSEvenementStub.SUPPRIMER_VERSION_REQUEST, SupprimerVersionRequest.class);
			Assert.assertNotNull(request);

			SupprimerVersionResponse response = stub.supprimerVersion(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

	@Test
	public void testValiderVersion() {
		try {
			ValiderVersionRequest request = JaxBHelper.buildRequestFromFile(WSEvenementStub.VALIDER_VERSION_REQUEST,
					ValiderVersionRequest.class);
			Assert.assertNotNull(request);

			ValiderVersionResponse response = stub.validerVersion(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}
	}

	@Test
	public void testMajVisaInterne() {
		try {
			MajInterneRequest request = JaxBHelper.buildRequestFromFile(WSEvenementStub.MAJ_VISA_INTERNE_REQUEST,
					MajInterneRequest.class);
			Assert.assertNotNull(request);

			MajInterneResponse response = stub.majInterne(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			Assert.fail(sw.toString());
		}

	}

}
