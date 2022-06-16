package fr.dila.st.rest.client.helper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.dila.st.rest.helper.JaxbContextHolder;

/**
 * Marshaller factory, allows the construction of Marshaller for dealing with CData.
 * 
 * @author fbarmes
 * 
 */
public class MarshallerFactory {

	private static final Logger	LOGGER								= LogManager.getLogger(MarshallerFactory.class);

	private static final String	CHARACTER_ESCAPE_HANDLER_PROPERTY	= "com.sun.xml.bind.marshaller.CharacterEscapeHandler";

	private boolean				formattedOutput						= false;

	/**
	 * Check if the logic of handling CDATA through the custom CdataCharacterEscapeHandler has been used when creating
	 * the instance of the provided Marshaller
	 * 
	 * @param m
	 * @return
	 */
	public static boolean isCdataHandler(Marshaller m) {
		try {
			boolean result = false;

			Object o = m.getProperty(CHARACTER_ESCAPE_HANDLER_PROPERTY);

			result = (o != null && (o instanceof CdataCharacterEscapeHandler));

			return result;
		} catch (PropertyException e) {
			return false;
		}
	}

	public MarshallerFactory() {
		super();
	}

	public final void setFormattedOutput(boolean formattedOutput) {
		this.formattedOutput = formattedOutput;
	}

	/**
	 * Returns a standard marshaller
	 * 
	 * @param clazz
	 * @return a Marshaller
	 * @throws JAXBException
	 */
	public Marshaller getMarshaller(Class<? extends Object> clazz) throws JAXBException {
		return getMarshaller(clazz, true);
	}

	/**
	 * Returns a marshaller with CDATA support
	 * 
	 * @param clazz
	 * @return a Marshaller
	 * @throws JAXBException
	 */
	public Marshaller getMarshallerWithCdataSupport(Class<? extends Object> clazz) throws JAXBException {
		return getMarshaller(clazz, true);
	}

	/**
	 * Constructs a marshaller with possibly CDATA support
	 * 
	 * @param clazz
	 *            the class to marshall
	 * @param hasCdata
	 *            supports CDATA is true (induce no escaping)
	 * @return a Marshaller
	 * @throws JAXBException
	 */
	public Marshaller getMarshaller(Class<? extends Object> clazz, boolean hasCdata) throws JAXBException {

		LOGGER.debug("MarshallerFactory get marshaller for " + clazz.getName());

		JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(clazz);
		Marshaller m = jcb.createMarshaller();
		LOGGER.debug("MarshallerFactory got Marshaller " + m.getClass().getName());

		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(formattedOutput));
		LOGGER.debug("MarshallerFactory set property Marshaller.JAXB_FORMATTED_OUTPUT = " + formattedOutput);

		if (hasCdata) {
			CdataCharacterEscapeHandler characterEscapeHandler = new CdataCharacterEscapeHandler();
			m.setProperty(CHARACTER_ESCAPE_HANDLER_PROPERTY, characterEscapeHandler);
			LOGGER.debug("MarshallerFactory set property " + CHARACTER_ESCAPE_HANDLER_PROPERTY + " with new "
					+ characterEscapeHandler.getClass().getName());
		}

		return m;
	}

}
