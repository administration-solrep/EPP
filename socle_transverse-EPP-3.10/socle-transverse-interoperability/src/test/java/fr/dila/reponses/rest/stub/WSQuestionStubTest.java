package fr.dila.reponses.rest.stub;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.reponses.ChangerEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherChangementDEtatQuestionsResponse;
import fr.sword.xsd.reponses.ChercherErrataQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsErrataResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.RechercherDossierRequest;
import fr.sword.xsd.reponses.RechercherDossierResponse;

public class WSQuestionStubTest {

	private static final String		FILE_BASEPATH	= "fr/dila/st/rest/stub/wsquestion/";
	private static WSQuestionStub	stub;

	@BeforeClass
	public static void setup() {
		stub = new WSQuestionStub();
		Assert.assertNotNull(stub);
	}

	@Test
	public void testTest() throws Exception {
		String expected = WSQuestion.SERVICE_NAME;
		String actual = stub.test();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testChercherQuestions() {
		try {
			ChercherQuestionsResponse response = stub.chercherQuestions(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChercherErrataQuestions() {
		try {
			ChercherErrataQuestionsResponse response = stub.chercherErrataQuestions(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChercherChangementDEtatQuestions() {
		try {
			ChercherChangementDEtatQuestionsResponse response = stub.chercherChangementDEtatQuestions(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerQuestions() {
		try {
			EnvoyerQuestionsResponse response = stub.envoyerQuestions(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerQuestionsErrata() {
		try {
			EnvoyerQuestionsErrataResponse response = stub.envoyerQuestionsErrata(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChangerEtatQuestions() {
		try {
			ChangerEtatQuestionsResponse response = stub.changerEtatQuestions(null);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testRechercherDossier() {

		try {
			RechercherDossierRequest request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ "rechercherdossier/WSquestion_rechercherDossierRequest.xml", RechercherDossierRequest.class);

			Assert.assertNotNull(request);

			RechercherDossierResponse response = stub.rechercherDossier(request);

			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}
}
