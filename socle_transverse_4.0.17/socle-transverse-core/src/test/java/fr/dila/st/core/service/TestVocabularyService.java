package fr.dila.st.core.service;

import fr.dila.st.api.service.VocabularyService;
import fr.dila.st.core.test.STFeature;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Test du service vocabulaire.
 *
 * @author jgomez
 * @author jtremeaux
 */
@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
@Deploy("fr.dila.st.core.test:OSGI-INF/service/test-vocabulary-contrib.xml")
public class TestVocabularyService { // extends CaseManagementRepositoryTestCase {
    private static final String TA_RUBRIQUE_TEST = "TA_rubrique_test";

    @Inject
    private VocabularyService vocService;

    //	@Override
    //	public void setUp() throws Exception {
    //
    //		super.setUp();
    //		// deploy directory service + sql factory
    //		deployBundle("org.nuxeo.ecm.directory");
    //		deployBundle("org.nuxeo.ecm.directory.sql");
    //
    //		// deploy schemas for test dirs
    //		deployContrib("fr.dila.st.core", "OSGI-INF/service/vocabulary-framework.xml");
    //		deployContrib("fr.dila.st.core.test", "OSGI-INF/service/test-vocabulary-contrib.xml");
    //
    //		vocService = STServiceLocator.getVocabularyService();
    //		Assert.assertNotNull(vocService);
    //	}

    /**
     * Teste une valeur présente dans le vocabulaire.
     *
     * @throws Exception
     */
    @Test
    public void testCheckVocabulary() throws Exception {
        Assert.assertTrue(vocService.checkData(TA_RUBRIQUE_TEST, "label", "drainage"));
    }

    /**
     * Teste la récupération d'un libellé d'une entrée du vocabulaire.
     *
     * @throws Exception
     */
    @Test
    public void testGetLabel() throws Exception {
        String label = vocService.getEntryLabel(TA_RUBRIQUE_TEST, "2");
        Assert.assertEquals("affiliation", label);

        label = vocService.getEntryLabel(TA_RUBRIQUE_TEST, "2", "label");
        Assert.assertEquals("affiliation", label);
    }
}
