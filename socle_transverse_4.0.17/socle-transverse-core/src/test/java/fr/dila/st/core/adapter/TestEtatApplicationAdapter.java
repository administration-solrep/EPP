package fr.dila.st.core.adapter;

import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.test.STFeature;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
public class TestEtatApplicationAdapter {
    @Inject
    private CoreFeature coreFeature;

    /**
     * Test les getter/setter de l'apdapter EtatApplication
     *
     */
    @Test
    public void testEtatApplicationImpl() {
        final String descRestriction = "Une restriction test";
        final boolean restrictionON = true;
        final String title = "title";

        DocumentRef docRef;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.createDocumentModel("/", "eapp", STConstant.ETAT_APPLICATION_DOCUMENT_TYPE);
            Assert.assertNotNull(doc);

            doc = session.createDocument(doc);

            docRef = doc.getRef();

            EtatApplication etatApp = doc.getAdapter(EtatApplication.class);
            Assert.assertNotNull(etatApp);

            Assert.assertEquals(doc, etatApp.getDocument());

            Assert.assertEquals(false, etatApp.getRestrictionAccesTechnique());

            etatApp.setDescriptionRestriction(descRestriction);
            etatApp.setRestrictionAcces(restrictionON);
            etatApp.setRestrictionAccesTechnique(true);
            etatApp.setTitle(title);

            Assert.assertEquals(descRestriction, etatApp.getDescriptionRestriction());
            Assert.assertEquals(restrictionON, etatApp.getRestrictionAcces());
            Assert.assertEquals(true, etatApp.getRestrictionAccesTechnique());
            Assert.assertEquals(title, etatApp.getTitle());

            // check persistance
            etatApp.save(session);
            session.save();
        }

        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.getDocument(docRef);
            EtatApplication etatApp = doc.getAdapter(EtatApplication.class);

            Assert.assertEquals(doc, etatApp.getDocument());
            Assert.assertEquals(descRestriction, etatApp.getDescriptionRestriction());
            Assert.assertEquals(restrictionON, etatApp.getRestrictionAcces());
            Assert.assertEquals(title, etatApp.getTitle());
        }
    }
}
