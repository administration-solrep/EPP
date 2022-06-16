package fr.dila.st.core.service;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.parametre.STParametre;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.helper.ParameterTestHelper;
import fr.dila.st.core.test.STFeature;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class TestSTParameterService {
    private static final Log LOG = LogFactory.getLog(TestSTParameterService.class);

    @Inject
    private CoreFeature coreFeature;

    @Test
    public void testService() {
        final STParametreService paramService = STServiceLocator.getSTParametreService();

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            // no parameter folder existing
            try {
                paramService.getParametreFolder(session);
                Assert.fail("No param folder : should raise exception");
            } catch (NuxeoException e) {
                LOG.info(e.getMessage());
                Assert.assertTrue(e.getMessage().contains("Racine des parametres non trouvée"));
            }

            // create a parameter folder
            DocumentModel doc = session.createDocumentModel(STConstant.PARAMETRE_FOLDER_DOCUMENT_TYPE);
            doc = session.createDocument(doc);
            session.save();

            Assert.assertEquals(doc.getId(), paramService.getParametreFolder(session).getId());
        }
    }

    @Test
    public void testParamAccess() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            final STParametreService paramService = STServiceLocator.getSTParametreService();
            final String paramName = "mon-param";
            final String paramValue = "ma valeur";

            Assert.assertNull(paramService.getParametre(session, paramName));

            STParametre createdParameter = ParameterTestHelper.changeOrCreateParammeter(session, paramName, paramValue);
            session.save();

            STParametre retrievedParameter = paramService.getParametre(session, paramName);
            Assert.assertEquals(createdParameter.getDocument().getId(), retrievedParameter.getDocument().getId());

            String value = paramService.getParametreValue(session, paramName);
            Assert.assertEquals(paramValue, value);
        }
    }
}
