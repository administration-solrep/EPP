package fr.dila.solonepg.rest.stub;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.solonepg.rest.api.WSSpe;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.commons.VersionResponse;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePERequest;
import fr.sword.xsd.solon.spe.EnvoyerPremiereDemandePEResponse;
import fr.sword.xsd.solon.spe.EnvoyerRetourPERequest;
import fr.sword.xsd.solon.spe.EnvoyerRetourPEResponse;
import fr.sword.xsd.solon.spe.PEstatut;

public class WSSpeStubTest {

	private static WSSpeStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new WSSpeStub();
		Assert.assertNotNull(stub);
	}

	@Test
	public void testTest() throws Exception {
		String result = stub.test();
		Assert.assertEquals(WSSpe.SERVICE_NAME, result);
	}

	@Test
	public void testVersion() throws Exception {
		VersionResponse result = stub.version();
		Assert.assertNotNull(result);
	}

	@Test
	public void testEnvoyerPremiereDemandePENullRequest() {

		try {
			EnvoyerPremiereDemandePEResponse response = stub.envoyerPremiereDemandePE(null);
			Assert.assertNotNull(response);

			boolean result = (response.getStatus() == PEstatut.OK || response.getStatus() == PEstatut.KO);
			Assert.assertTrue(result);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerPremiereDemandePEWithRequest() {

		try {

			EnvoyerPremiereDemandePERequest request = JaxBHelper.buildRequestFromFile(
					WSSpeStub.ENVOYER_PREMIERE_DEMANDE_PE_REQUEST, EnvoyerPremiereDemandePERequest.class);

			Assert.assertNotNull(request);

			EnvoyerPremiereDemandePEResponse response = stub.envoyerPremiereDemandePE(request);
			Assert.assertNotNull(response);

			boolean result = (response.getStatus() == PEstatut.OK || response.getStatus() == PEstatut.KO);
			Assert.assertTrue(result);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerRetourPENullRequest() {
		try {
			EnvoyerRetourPEResponse response = stub.envoyerRetourPE(null);
			Assert.assertNotNull(response);

			boolean result = (response.getStatus() == PEstatut.OK || response.getStatus() == PEstatut.KO);
			Assert.assertTrue(result);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerRetourPERequestPublication() {
		try {

			EnvoyerRetourPERequest request = JaxBHelper.buildRequestFromFile(WSSpeStub.ENVOYER_RETOUR_PE_REQUEST,
					EnvoyerRetourPERequest.class);
			Assert.assertNotNull(request);

			EnvoyerRetourPEResponse response = stub.envoyerRetourPE(request);
			Assert.assertNotNull(response);

			boolean result = (response.getStatus() == PEstatut.OK || response.getStatus() == PEstatut.KO);
			Assert.assertTrue(result);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerRetourPERequestEpreuvage() {
		try {

			EnvoyerRetourPERequest request = JaxBHelper.buildRequestFromFile(
					WSSpeStub.ENVOYER_RETOUR_PE_REQUEST_EPREUVAGE, EnvoyerRetourPERequest.class);
			Assert.assertNotNull(request);

			EnvoyerRetourPEResponse response = stub.envoyerRetourPE(request);
			Assert.assertNotNull(response);

			boolean result = (response.getStatus() == PEstatut.OK || response.getStatus() == PEstatut.KO);
			Assert.assertTrue(result);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
