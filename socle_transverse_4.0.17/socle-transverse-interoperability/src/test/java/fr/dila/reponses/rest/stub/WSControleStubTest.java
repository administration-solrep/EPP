package fr.dila.reponses.rest.stub;

import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.reponses.ChercherRetourPublicationRequest;
import fr.sword.xsd.reponses.ChercherRetourPublicationResponse;
import fr.sword.xsd.reponses.ControlePublicationRequest;
import fr.sword.xsd.reponses.ControlePublicationResponse;

public class WSControleStubTest {

	private static final String		FILE_BASEPATH	= "fr/dila/st/rest/stub/wscontrole/";
	private static WSControleStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new WSControleStub();
	}

	@Test
	public void testControlePublicationNull() {
		try {
			ControlePublicationResponse response = stub.controlePublication(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testControlePublication() {

		try {
			ControlePublicationRequest request = JaxBHelper
					.buildRequestFromFile(FILE_BASEPATH
							+ "controlepublication/WScontrole_controlePublicationRequest.xml",
							ControlePublicationRequest.class);
			Assert.assertNotNull(request);

			ControlePublicationResponse response = stub.controlePublication(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testRetourPublication() {

		try {
			ChercherRetourPublicationRequest request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ "chercherretourpublication/WScontrole_chercherRetourPublicationRequest.xml",
					ChercherRetourPublicationRequest.class);

			Assert.assertNotNull(request);

			ChercherRetourPublicationResponse response = stub.chercherRetourPublication(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

}
