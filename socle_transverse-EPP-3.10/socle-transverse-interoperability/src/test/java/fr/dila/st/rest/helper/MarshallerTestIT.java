package fr.dila.st.rest.helper;

import java.io.StringWriter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.apache.log4j.Logger;

import fr.dila.st.rest.client.helper.MarshallerFactory;
import fr.sword.xsd.reponses.ChercherReponsesResponse;
import fr.sword.xsd.reponses.Reponse;
import fr.sword.xsd.reponses.ReponseQuestion;
import fr.sword.xsd.reponses.TraitementStatut;

public class MarshallerTestIT {

	private static final Logger		LOGGER				= Logger.getLogger(MarshallerTestIT.class);

	// --- from com.sun.xml.internal.bind.v2.runtime.MarshallerImpl
	// protected static final java.lang.String ENCODING_HANDLER = "com.sun.xml.internal.bind.characterEscapeHandler";
	// protected static final java.lang.String ENCODING_HANDLER2 =
	// "com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler";

	// --- from com.sun.xml.bind.v2.runtime.MarshallerImpl
	// protected static final java.lang.String ENCODING_HANDLER = "com.sun.xml.bind.characterEscapeHandler";
	// protected static final java.lang.String ENCODING_HANDLER2 = "com.sun.xml.bind.marshaller.CharacterEscapeHandler";

	// --- from com.sun.xml.bind.v2.runtime.MarshallerImpl
	protected static final String	ENCODING_HANDLER	= "com.sun.xml.bind.characterEscapeHandler";
	protected static final String	ENCODING_HANDLER2	= "com.sun.xml.bind.marshaller.CharacterEscapeHandler";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			LOGGER.info("MarshallerTestIT- start");

			// --- create Cdata aware Marshaller
			MarshallerFactory factory = new MarshallerFactory();
			Assert.assertNotNull(factory);
			factory.setFormattedOutput(true);
			Marshaller m = factory.getMarshaller(ChercherReponsesResponse.class);
			Assert.assertNotNull(m);
			LOGGER.debug("marshaller class = " + m.getClass());
			LOGGER.debug("marshaller package = " + m.getClass().getPackage());

			boolean hasCdata = MarshallerFactory.isCdataHandler(m);
			LOGGER.debug("hasCdata = " + hasCdata);

			// --- Test mashall to String
			LOGGER.debug("Build test message");
			ChercherReponsesResponse message = builChercherReponsesResponse("Ceci est un message < ( ) > de test");

			LOGGER.debug("Test mashall");
			StringWriter sw = new StringWriter();

			m.marshal(message, sw);
			LOGGER.debug(sw.toString());

			LOGGER.info("MarshallerTestIT- stop");
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	private static ChercherReponsesResponse builChercherReponsesResponse(String txt) {
		ChercherReponsesResponse message = new ChercherReponsesResponse();
		message.setJetonReponses("3");
		message.setDernierRenvoi(false);
		message.setStatut(TraitementStatut.OK);

		Reponse reponse = new Reponse();
		reponse.setTexteReponse(txt);

		ReponseQuestion reponseQuestion = new ReponseQuestion();
		reponseQuestion.setReponse(reponse);
		message.getReponses().add(reponseQuestion);

		return message;
	}

}
