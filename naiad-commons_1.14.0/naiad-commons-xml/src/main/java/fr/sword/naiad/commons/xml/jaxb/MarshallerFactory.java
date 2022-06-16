package fr.sword.naiad.commons.xml.jaxb;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.apache.log4j.Logger;

import fr.sword.naiad.commons.xml.context.JaxbContextHolder;

/**
 * Marshaller factory, allows the construction of Marshaller for dealing with CData.
 * 
 * @author fbarmes
 * 
 */
public class MarshallerFactory {

	private static final Logger LOGGER = Logger.getLogger(MarshallerFactory.class);
	private static final String CHARACTER_ESCAPE_HANDLER_PROPERTY = "com.sun.xml.bind.marshaller.CharacterEscapeHandler";
	private boolean formattedOutput = false;

	/**
	 * Check if the logic of handling CDATA through the custom CdataCharacterEscapeHandler has been used when
	 * creating the instance of the provided Marshaller
	 * 
	 * @param m
	 * @return
	 */
	public static boolean isCdataHandler(final Marshaller m) {
		try {
			final Object o = m.getProperty(CHARACTER_ESCAPE_HANDLER_PROPERTY);

			return o instanceof CdataCharacterEscapeHandler;

		} catch (final PropertyException e) {
			return false;
		}
	}

	public MarshallerFactory() {
		super();
	}

	public final void setFormattedOutput(final boolean formattedOutput) {
		this.formattedOutput = formattedOutput;
	}

	/**
	 * Returns a standard marshaller
	 * 
	 * @param clazz
	 * @return a Marshaller
	 * @throws JAXBException
	 */
	public Marshaller getMarshaller(final Class<? extends Object> clazz) throws JAXBException {
		return getMarshaller(clazz, true);
	}

	/**
	 * Returns a marshaller with CDATA support
	 * 
	 * @param clazz
	 * @return a Marshaller
	 * @throws JAXBException
	 */
	public Marshaller getMarshallerWithCdataSupport(final Class<? extends Object> clazz) throws JAXBException {
		return getMarshaller(clazz, true);
	}

	/**
	 * Constructs a marshaller with possibly CDATA support
	 * 
	 * @param clazz the class to marshall
	 * @param hasCdata supports CDATA is true (induce no escaping)
	 * @return a Marshaller
	 * @throws JAXBException
	 */
	public Marshaller getMarshaller(final Class<? extends Object> clazz, final boolean hasCdata) throws JAXBException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MarshallerFactory get marshaller for " + clazz.getName());
		}

		final JAXBContext jcb = JaxbContextHolder.getInstance().getJaxbContext(clazz);
		final Marshaller m = jcb.createMarshaller();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MarshallerFactory got Marshaller " + m.getClass().getName());
		}

		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(formattedOutput));

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("MarshallerFactory set property Marshaller.JAXB_FORMATTED_OUTPUT = " + formattedOutput);
		}

		if (hasCdata) {
			final CdataCharacterEscapeHandler characterEscapeHandler = new CdataCharacterEscapeHandler();
			m.setProperty(CHARACTER_ESCAPE_HANDLER_PROPERTY, characterEscapeHandler);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("MarshallerFactory set property " + CHARACTER_ESCAPE_HANDLER_PROPERTY + " with new " + characterEscapeHandler.getClass().getName());
			}
		}

		return m;
	}

}
