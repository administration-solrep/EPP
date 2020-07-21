package fr.dila.st.rest.helper;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.dila.st.rest.client.helper.MarshallerFactory;
import fr.sword.xsd.commons.VersionResponse;

public class MarshallerFactoryTest {

	private static final Logger	LOGGER	= Logger.getLogger(MarshallerFactoryTest.class);

	@Test
	public void testGetMarshaller() {

		try {
			LOGGER.info("--- testGetMarshaller");
			MarshallerFactory factory = new MarshallerFactory();
			Assert.assertNotNull(factory);
			Marshaller m = factory.getMarshaller(VersionResponse.class, false);
			Assert.assertNotNull(m);
			LOGGER.debug("marshaller class = " + m.getClass());
			LOGGER.debug("marshaller package = " + m.getClass().getPackage());

		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testGetMarshallerWithCdata() {

		try {
			LOGGER.info("--- testGetMarshallerWithCdata");
			MarshallerFactory factory = new MarshallerFactory();
			Assert.assertNotNull(factory);
			Marshaller m = factory.getMarshaller(VersionResponse.class, true);

			LOGGER.debug("marshaller class = " + m.getClass());
			LOGGER.debug("marshaller package = " + m.getClass().getPackage());

		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			Assert.fail(e.getMessage());
		}

	}

}
