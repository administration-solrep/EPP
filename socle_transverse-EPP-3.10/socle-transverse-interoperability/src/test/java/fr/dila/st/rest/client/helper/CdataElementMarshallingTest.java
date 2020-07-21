package fr.dila.st.rest.client.helper;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.dila.reponses.rest.api.WSQuestion;
import fr.dila.reponses.rest.stub.WSQuestionStub;
import fr.dila.st.rest.helper.JaxbContextHolder;
import fr.dila.st.utils.InteropUtils;
import fr.sword.xsd.reponses.ChercherQuestionsRequest;
import fr.sword.xsd.reponses.ChercherQuestionsResponse;
import fr.sword.xsd.reponses.ChercherQuestionsResponse.Questions;

public class CdataElementMarshallingTest {

	private static final Logger	LOGGER			= Logger.getLogger(CdataElementMarshallingTest.class);

	private static final String	XML_FILENAME	= "fr/dila/st/rest/stub/wsquestion/WSquestion_chercherQuestionsResponse.xml";

	private static String		xmlMessage;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		InputStream is = CdataElementMarshallingTest.class.getClassLoader().getResourceAsStream(XML_FILENAME);
		Assert.assertNotNull(is);
		xmlMessage = InteropUtils.inputStreamToString(is);
		Assert.assertNotNull(xmlMessage);

		LOGGER.debug("-- setUpBeforeClass");
		LOGGER.debug("xmlMessage=\n" + xmlMessage);
	}

	@Test
	public void testUnMarshalling() {

		try {
			LOGGER.debug("-- testUnMarshalling");

			InputStream is = CdataElementMarshallingTest.class.getClassLoader().getResourceAsStream(XML_FILENAME);

			JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(ChercherQuestionsResponse.class);
			Unmarshaller u = jcb.createUnmarshaller();

			ChercherQuestionsResponse jaxbObject = (ChercherQuestionsResponse) u.unmarshal(is);

			Questions question = jaxbObject.getQuestions().get(0);
			Assert.assertNotNull(question);

			String texte = question.getTexte();

			LOGGER.debug("question texte = " + texte);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testMarshalling() {

		try {
			LOGGER.debug("-- testMarshalling");

			// --- load data
			InputStream is = CdataElementMarshallingTest.class.getClassLoader().getResourceAsStream(XML_FILENAME);

			JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(ChercherQuestionsResponse.class);
			Unmarshaller u = jcb.createUnmarshaller();

			// --- make object
			ChercherQuestionsResponse jaxbObject = (ChercherQuestionsResponse) u.unmarshal(is);

			Questions question = jaxbObject.getQuestions().get(0);
			Assert.assertNotNull(question);

			String texte = question.getTexte();

			LOGGER.debug("question texte = " + texte);

			// --- marshall back to String

			MarshallerFactory factory = new MarshallerFactory();
			factory.setFormattedOutput(true);
			Marshaller m = factory.getMarshallerWithCdataSupport(ChercherQuestionsResponse.class);
			StringWriter sw = new StringWriter();
			m.marshal(jaxbObject, sw);

			LOGGER.debug("marshall result =\n" + sw.toString());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUsingStub() {

		try {
			LOGGER.debug("-- testUsingStub");
			WSQuestion wsQuestion = new WSQuestionStub();
			ChercherQuestionsRequest request = null;
			ChercherQuestionsResponse response = wsQuestion.chercherQuestions(request);

			Assert.assertNotNull(response);
			Questions question = response.getQuestions().get(0);
			Assert.assertNotNull(question);

			String texte = question.getTexte();

			LOGGER.debug("question texte = " + texte);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

	}

}
