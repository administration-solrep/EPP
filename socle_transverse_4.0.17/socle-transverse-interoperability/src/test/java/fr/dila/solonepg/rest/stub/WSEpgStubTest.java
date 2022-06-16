package fr.dila.solonepg.rest.stub;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.solon.epg.AttribuerNorRequest;
import fr.sword.xsd.solon.epg.AttribuerNorResponse;
import fr.sword.xsd.solon.epg.ChercherDossierRequest;
import fr.sword.xsd.solon.epg.ChercherDossierResponse;
import fr.sword.xsd.solon.epg.DonnerAvisCERequest;
import fr.sword.xsd.solon.epg.DonnerAvisCEResponse;
import fr.sword.xsd.solon.epg.ModifierDossierCERequest;
import fr.sword.xsd.solon.epg.ModifierDossierCEResponse;

public class WSEpgStubTest {

	private static WSEpgStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new WSEpgStub();
		Assert.assertNotNull(stub);

	}

	@Test
	public void testAttribuerNor() {

		try {
			AttribuerNorRequest request = JaxBHelper.buildRequestFromFile(WSEpgStub.ATTRIBUER_NOR_REQUEST,
					AttribuerNorRequest.class);
			Assert.assertNotNull(request);
			AttribuerNorResponse response = stub.attribuerNor(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChercherDossier() {

		try {
			ChercherDossierResponse response = null;
			ChercherDossierRequest request = null;

			// --- byJeton
			request = JaxBHelper.buildRequestFromFile(WSEpgStub.CHERCHER_DOSSIER_REQUEST, ChercherDossierRequest.class);
			Assert.assertNotNull(request);
			response = stub.chercherDossierEpg(request);
			Assert.assertNotNull(response);

			// --- byNor
			request = JaxBHelper.buildRequestFromFile(WSEpgStub.CHERCHER_DOSSIER_REQUEST_BYNOR,
					ChercherDossierRequest.class);
			Assert.assertNotNull(request);
			response = stub.chercherDossierEpg(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testDonnerAvisCE() {

		try {
			DonnerAvisCERequest request = JaxBHelper.buildRequestFromFile(WSEpgStub.DONNER_AVIS_CE_REQUEST,
					DonnerAvisCERequest.class);
			Assert.assertNotNull(request);
			DonnerAvisCEResponse response = stub.donnerAvisCE(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testModifierDossierCE() {

		try {
			ModifierDossierCERequest request = JaxBHelper.buildRequestFromFile(WSEpgStub.MODIFIER_DOSSIER_CE_REQUEST,
					ModifierDossierCERequest.class);
			Assert.assertNotNull(request);

			ModifierDossierCEResponse response = JaxBHelper.buildRequestFromFile(
					WSEpgStub.MODIFIER_DOSSIER_CE_RESPONSE, ModifierDossierCEResponse.class);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
