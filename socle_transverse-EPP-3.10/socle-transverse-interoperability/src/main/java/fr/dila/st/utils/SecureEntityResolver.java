package fr.dila.st.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Circumvent unfixed CVE-2014-6517 for java6
 * 
 * @see https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet
 * @see https://www.owasp.org/index.php/Talk:XML_External_Entity_(XXE)_Processing
 */
public class SecureEntityResolver implements EntityResolver {

	private final Logger LOGGER = Logger.getLogger(SecureEntityResolver.class);

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		LOGGER.info(String.format("Résolution de l'entité %s:%s bloquée", publicId, systemId));
		return new InputSource(new ByteArrayInputStream("".getBytes()));
	}

}
