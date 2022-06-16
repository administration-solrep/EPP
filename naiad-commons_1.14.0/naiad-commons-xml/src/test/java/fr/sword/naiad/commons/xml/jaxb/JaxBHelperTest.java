package fr.sword.naiad.commons.xml.jaxb;

import javax.xml.bind.JAXBException;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.LoggerFactory;

import fr.sword.naiad.commons.xml.hello.HelloMessage;

public class JaxBHelperTest {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JaxBHelper.class);
	
	
	@Test
	public void testMarshallToString() {
		
		try {
			HelloMessage helloMessage = new HelloMessage();
			helloMessage.setPrefix("my-prefix");
			helloMessage.getName().add("Fred");
			helloMessage.getName().add("Wilma");			
			
			String result = JaxBHelper.marshallToString(helloMessage, HelloMessage.class);
			Assert.assertNotNull(result);
			LOGGER.debug(result);
		} catch (JAXBException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}		
	}
	
}
