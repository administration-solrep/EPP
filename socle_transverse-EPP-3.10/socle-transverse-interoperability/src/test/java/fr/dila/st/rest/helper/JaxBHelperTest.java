package fr.dila.st.rest.helper;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.dila.reponses.rest.stub.WSQuestionStub;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.EnvoyerQuestionsRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.TraitementStatut;

public class JaxBHelperTest {

	private static final Logger	LOGGER	= Logger.getLogger(JaxBHelper.class);

	@Test
	public void testMarshallToString() {

		try {
			ChercherQuestionsResponse tag = new ChercherQuestionsResponse();
			tag.setJetonQuestions("1234");
			tag.setStatut(TraitementStatut.KO);
			tag.setMessageErreur("Lorem Ipsum");

			String result = JaxBHelper.marshallToString(tag, ChercherQuestionsResponse.class);
			Assert.assertNotNull(result);

			LOGGER.debug(result);

		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testLogTransaction() throws Exception {

		EnvoyerQuestionsRequest request = JaxBHelper.buildRequestFromFile(WSQuestionStub.ENVOYER_QUESTIONS_REQUEST,
				EnvoyerQuestionsRequest.class);
		Assert.assertNotNull(request);

		EnvoyerQuestionsResponse response = JaxBHelper.buildRequestFromFile(WSQuestionStub.ENVOYER_QUESTIONS_RESPONSE,
				EnvoyerQuestionsResponse.class);
		Assert.assertNotNull(response);

		String message = JaxBHelper.logInWsTransaction("myService", "method", "username", request,
				EnvoyerQuestionsRequest.class, response, EnvoyerQuestionsResponse.class);
		Assert.assertNotNull("message");
		LOGGER.info("message=\n" + message);

		message = JaxBHelper.logInWsTransaction("myService", "method", "username", null, null, response,
				EnvoyerQuestionsResponse.class);
		Assert.assertNotNull("message");
		LOGGER.info("message=\n" + message);

		message = JaxBHelper.logInWsTransaction("myService", "method", "username", request,
				EnvoyerQuestionsRequest.class, null, null);
		Assert.assertNotNull("message");
		LOGGER.info("message=\n" + message);

		message = JaxBHelper.logInWsTransaction("myService", "method", null, null, null, null, null);
		Assert.assertNotNull("message");
		LOGGER.info("message=\n" + message);

	}

}
