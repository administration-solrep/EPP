package fr.dila.reponses.rest.stub;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.reponses.rest.client.WSNotificationCaller;
import fr.dila.st.rest.api.WSNotification;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.dila.st.rest.stub.WSNotificationStub;
import fr.sword.xsd.reponses.EnvoyerNotificationRequest;
import fr.sword.xsd.reponses.EnvoyerNotificationResponse;
import fr.sword.xsd.reponses.NotificationType;

public class WSNotificationStubTest {

	private static final String			FILE_BASEPATH	= "fr/dila/st/rest/stub/wsnotification/";

	private static WSNotificationStub	stub;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		stub = new WSNotificationStub();
	}

	@Test
	public void testEnvoyerNotificationRequest() {

		EnvoyerNotificationRequest request = null;

		try {
			request = null;
			request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ "WSnotification_envoyerNotificationRequest_questions_byJeton.xml",
					EnvoyerNotificationRequest.class);
			Assert.assertNotNull(request);
			Assert.assertEquals(NotificationType.REPONSES_QUESTIONS, request.getTypeNotification());
			Assert.assertNotNull(request.getJeton());

			request = null;
			request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ "WSnotification_envoyerNotificationRequest_questions_byId.xml", EnvoyerNotificationRequest.class);
			Assert.assertNotNull(request);
			Assert.assertEquals(NotificationType.REPONSES_QUESTIONS, request.getTypeNotification());
			Assert.assertNull(request.getJeton());
			Assert.assertNotNull(request.getIdQuestions());
			Assert.assertEquals(4, request.getIdQuestions().size());

			request = null;
			request = JaxBHelper
					.buildRequestFromFile(FILE_BASEPATH
							+ "WSnotification_envoyerNotificationRequest_questionsErrata.xml",
							EnvoyerNotificationRequest.class);
			Assert.assertNotNull(request);
			Assert.assertEquals(NotificationType.REPONSES_QUESTIONS_ERRATA, request.getTypeNotification());
			Assert.assertNotNull(request.getJeton());

			request = null;
			request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ "WSnotification_envoyerNotificationRequest_changementEtat.xml", EnvoyerNotificationRequest.class);
			Assert.assertNotNull(request);
			Assert.assertEquals(NotificationType.REPONSES_CHANGEMENT_ETAT, request.getTypeNotification());
			Assert.assertNotNull(request.getJeton());

			request = null;
			request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ "WSnotification_envoyerNotificationRequest_attributions.xml", EnvoyerNotificationRequest.class);
			Assert.assertNotNull(request);
			Assert.assertEquals(NotificationType.REPONSES_ATTRIBUTIONS, request.getTypeNotification());
			Assert.assertNotNull(request.getJeton());

			request = null;
			request = JaxBHelper.buildRequestFromFile(FILE_BASEPATH
					+ "WSnotification_envoyerNotificationRequest_retourPublication.xml",
					EnvoyerNotificationRequest.class);
			Assert.assertNotNull(request);
			Assert.assertEquals(NotificationType.REPONSES_RETOUR_PUBLICATION, request.getTypeNotification());
			Assert.assertNotNull(request.getJeton());

		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testEnvoyerNotification() {
		try {
			WSNotification wsNotification = stub;
			WSNotificationCaller wsNotificationCaller = new WSNotificationCaller(wsNotification);

			EnvoyerNotificationRequest request = WSNotificationCaller.buildRequest(
					NotificationType.REPONSES_ATTRIBUTIONS, "1", null);
			EnvoyerNotificationResponse response = wsNotificationCaller.notifier(request);

			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
