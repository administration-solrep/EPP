package fr.dila.st.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.st.api.service.ConfigService;

/**
 * Test du service de configuration de l'application.
 * 
 * @author jtremeaux
 */
public class TestConfigService extends SQLRepositoryTestCase {

	private static final Log	LOG	= LogFactory.getLog(TestConfigService.class);

	@Override
	public void setUp() throws Exception {
		super.setUp();
		deployContrib("fr.dila.st.core", "OSGI-INF/service/config-framework.xml");
		deployContrib("fr.dila.st.core.test", "OSGI-INF/service/test-config-contrib.xml");
	}

	public void testGetParameter() throws Exception {
		openSession();

		// Vérifie la présence du service
		ConfigService configService = STServiceLocator.getConfigService();
		assertNotNull(configService);
		assertNotNull(session);

		// Vérifie une valeur par défaut
		String value1 = configService.getValue("app.some.string.value");
		assertEquals("someName", value1);

		// Vérifie les conversions de type
		Boolean value2 = configService.getBooleanValue("app.some.boolean.value");
		assertTrue(value2);

		int value3 = configService.getIntegerValue("app.some.integer.value");
		assertEquals(42, value3);

		double value4 = configService.getDoubleValue("app.some.double.value");
		assertEquals(1.234, value4);

		try {
			configService.getValue("undefined.variable");
			fail("undefined.variable ne doit pas être défini");
		} catch (RuntimeException e) {
			// NOP
			LOG.error(e.getMessage());
			assertEquals(
					"Paramètre de configuration [undefined.variable] non défini, veuillez vérifier le fichier de configuration.",
					e.getMessage());
		}

		closeSession();
	}
}
