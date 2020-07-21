package fr.dila.st.core.service;

import fr.dila.cm.test.CaseManagementRepositoryTestCase;
import fr.dila.st.api.service.VocabularyService;

/**
 * Test du service vocabulaire.
 * 
 * @author jgomez
 * @author jtremeaux
 */
public class TestVocabularyService extends CaseManagementRepositoryTestCase {

	private static final String	TA_RUBRIQUE_TEST	= "TA_rubrique_test";

	// initialize by setUp
	private VocabularyService	vocService;

	@Override
	public void setUp() throws Exception {

		super.setUp();
		// deploy directory service + sql factory
		deployBundle("org.nuxeo.ecm.directory");
		deployBundle("org.nuxeo.ecm.directory.sql");

		// deploy schemas for test dirs
		deployContrib("fr.dila.st.core", "OSGI-INF/service/vocabulary-framework.xml");
		deployContrib("fr.dila.st.core.test", "OSGI-INF/service/test-vocabulary-contrib.xml");

		vocService = STServiceLocator.getVocabularyService();
		assertNotNull(vocService);
	}

	/**
	 * Teste une valeur présente dans le vocabulaire.
	 * 
	 * @throws Exception
	 */
	public void testCheckVocabulary() throws Exception {
		assertTrue(vocService.checkData(TA_RUBRIQUE_TEST, "label", "drainage"));
	}

	/**
	 * Teste la récupération d'un libellé d'une entrée du vocabulaire.
	 * 
	 * @throws Exception
	 */
	public void testGetLabel() throws Exception {
		String label = vocService.getEntryLabel(TA_RUBRIQUE_TEST, "2");
		assertEquals("affiliation", label);

		label = vocService.getEntryLabel(TA_RUBRIQUE_TEST, "2", "label");
		assertEquals("affiliation", label);
	}
}
