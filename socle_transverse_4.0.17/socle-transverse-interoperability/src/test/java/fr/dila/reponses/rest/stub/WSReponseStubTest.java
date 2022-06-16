package fr.dila.reponses.rest.stub;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.sword.xsd.reponses.ChercherErrataReponsesResponse;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.EnvoyerReponseErrataResponse;
import fr.sword.xsd.reponses.EnvoyerReponsesResponse;

public class WSReponseStubTest {

	private static WSReponseStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new WSReponseStub();

	}

	@Test
	public void testChercherReponses() {

		try {
			ChercherReponsesResponse response = stub.chercherReponses(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChercherErrataReponses() {
		try {
			ChercherErrataReponsesResponse response = stub.chercherErrataReponses(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerReponses() {
		try {
			EnvoyerReponsesResponse response = stub.envoyerReponses(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerReponseErrata() {
		try {
			EnvoyerReponseErrataResponse response = stub.envoyerReponseErrata(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
