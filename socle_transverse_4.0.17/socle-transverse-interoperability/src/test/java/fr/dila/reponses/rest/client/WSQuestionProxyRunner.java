package fr.dila.reponses.rest.client;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.st.rest.client.WSProxyFactory;
import fr.dila.st.rest.helper.JaxBHelper;
import fr.sword.xsd.reponses.Auteur;
import fr.sword.xsd.reponses.ChercherErrataQuestionsRequest;
import fr.sword.xsd.reponses.ChercherErrataQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsRequest;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.Civilite;
import fr.sword.xsd.reponses.EnvoyerQuestionsRequest;
import fr.sword.xsd.reponses.EnvoyerQuestionsResponse;
import fr.sword.xsd.reponses.Ministre;
import fr.sword.xsd.reponses.Question;
import fr.sword.xsd.reponses.QuestionId;
import fr.sword.xsd.reponses.QuestionSource;
import fr.sword.xsd.reponses.QuestionType;

public class WSQuestionProxyRunner {

	private static final Logger		LOGGER		= Logger.getLogger(WSQuestionProxyRunner.class);

	private static WSQuestion		service;

	// -- uncomment for Nuxeo
	// protected static final String ENDPOINT = "http://localhost:8080";
	// protected static final String BASEPATH = "reponses/site/reponses";
	// protected static final String USERNAME = "ws_an";
	// protected static final String PASSWORD = "ws_an";

	// --- uncomment for stub
	protected static final String	ENDPOINT	= "http://localhost:8180";
	protected static final String	BASEPATH	= "solrep-ws-server-stub/ws/rest/";
	protected static final String	USERNAME	= "user";
	protected static final String  PASSWORD    = "secret";

	private static WSProxyFactory	proxyFactory;

	@BeforeClass
	public static void setup() {
		try {
			proxyFactory = new WSProxyFactory(ENDPOINT, BASEPATH, USERNAME, null);
			Assert.assertNotNull(proxyFactory);

			service = proxyFactory.getService(WSQuestion.class, PASSWORD);
			Assert.assertNotNull(service);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testEnvoyerQuestions() {

		try {
			LOGGER.debug("-- testEnvoyerQuestions");

			String filename = "fr/dila/st/rest/stub/wsquestion/WSquestion_envoyerQuestionsRequest.xml";
			EnvoyerQuestionsRequest request = JaxBHelper.buildRequestFromFile(filename, EnvoyerQuestionsRequest.class);
			Assert.assertNotNull(request);

			LOGGER.debug("request / question / texte = "
					+ request.getQuestionReponse().get(0).getQuestion().get(0).getTexte());

			EnvoyerQuestionsResponse response = service.envoyerQuestions(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testChercherQuestions() {
		try {

			LOGGER.debug("-- testChercherQuestions");

			String filePath = "WSquestion_chercherQuestions.xml";

			ChercherQuestionsRequest request = JaxBHelper
					.buildRequestFromFile(filePath, ChercherQuestionsRequest.class);

			ChercherQuestionsResponse response = service.chercherQuestions(request);

			LOGGER.debug("-- reponse question texte " + response.getQuestions().get(0).getTexte());

			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testChercherJetonfile() {

		try {
			ChercherQuestionsRequest request = new ChercherQuestionsRequest();

			ChercherQuestionsResponse response = service.chercherQuestions(request);

			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testChercherErrataQuestions() {

		try {
			String filePath = "WSquestion_chercherErrataQuestions.xml";

			ChercherErrataQuestionsRequest request = JaxBHelper.buildRequestFromFile(filePath,
					ChercherErrataQuestionsRequest.class);
			Assert.assertNotNull(request);

			ChercherErrataQuestionsResponse response = service.chercherErrataQuestions(request);
			Assert.assertNotNull(response);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testChercherQuestionJetonForMan() {

		try {
			String endpoint = "http://sword-solon-dev:8080";
			String username = "ws_agriculture";
			// --- construction de la requête
			ChercherQuestionsRequest request = new ChercherQuestionsRequest();

			// --- récuperation du proxy client
			WSProxyFactory factory = new WSProxyFactory(endpoint, BASEPATH, username, null);
			WSQuestion service = factory.getService(WSQuestion.class, PASSWORD);
			ChercherQuestionsResponse response = service.chercherQuestions(request);
			Assert.assertNotNull(response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testConversion() {

		try {
			String endpoint = "http://localhost:8080";
			String basePath = BASEPATH;
			String username = "ws_an";

			// --- construction de la requête
			EnvoyerQuestionsRequest request = new EnvoyerQuestionsRequest();
			fr.sword.xsd.reponses.QuestionReponse qr = new fr.sword.xsd.reponses.QuestionReponse();

			Question question = new Question();
			Auteur auteur = new Auteur();
			auteur.setIdMandat("386399");
			auteur.setCivilite(Civilite.M);
			auteur.setPrenom("Martin");
			auteur.setNom("Philippe Armand");
			auteur.setGrpPol("Non connu");
			question.setAuteur(auteur);

			QuestionId qid = new QuestionId();
			qid.setNumeroQuestion(502);
			qid.setType(QuestionType.QE);
			qid.setSource(QuestionSource.AN);
			qid.setLegislature(13);

			XMLGregorianCalendar xmldate = DatatypeFactory.newInstance().newXMLGregorianCalendar(
					new GregorianCalendar());
			question.setDatePublicationJo(xmldate);
			question.setIdQuestion(qid);

			question.setPageJo(13);

			Ministre ministreDepot = new Ministre();
			ministreDepot.setId(17);
			ministreDepot.setTitreJo("Economie");
			ministreDepot.setIntituleMinistere("Economie");
			question.setMinistreDepot(ministreDepot);

			question.setTexte("Contenu en clair sensé etre en CDATA");

			qr.getQuestion().add(question);
			request.getQuestionReponse().add(qr);

			// --- récuperation du proxy client
			WSProxyFactory factory = new WSProxyFactory(endpoint, basePath, username, null);
			WSQuestion service = factory.getService(WSQuestion.class, PASSWORD);
			EnvoyerQuestionsResponse response = service.envoyerQuestions(request);

			response.getResultatTraitement();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
