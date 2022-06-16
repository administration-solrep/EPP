package fr.dila.st.core.adapter;

import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.jeton.JetonDoc;
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
public class TestJetonAdapter { //extends SQLRepositoryTestCase {
    @Inject
    private CoreFeature coreFeature;

    //	@Override
    //	protected void deployRepositoryContrib() throws Exception {
    //		super.deployRepositoryContrib();
    //
    //		// deploy repository manager
    //		deployBundle("org.nuxeo.ecm.core.api");
    //
    //		// deploy api and core bundles
    //		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_API_BUNDLE);
    //		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_CORE_BUNDLE);
    //		deployBundle("fr.dila.ecm.platform.routing.core");
    //		deployBundle("org.nuxeo.ecm.automation.core");
    //		deployBundle("org.nuxeo.ecm.directory");
    //		deployBundle("org.nuxeo.ecm.platform.usermanager");
    //		deployBundle("org.nuxeo.ecm.directory.types.contrib");
    //
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-schema-contrib.xml");
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-core-type-contrib.xml");
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-adapter-contrib.xml");
    //
    //	}
    //
    //	@Override
    //	public void setUp() throws Exception {
    //		super.setUp();
    //	}

    /**
     * Test les getter/setter de l'apdapter JetonDoc
     *
     * @throws ClientException
     */
    @Test
    public void testJetonDocImpl() {
        final String idDoc = "id doc";
        final Long idJeton = 5656L;
        final String idOwner = "id owner";
        final String type = "webservice type";

        DocumentRef docRef;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.createDocumentModel(STConstant.JETON_DOC_TYPE);
            Assert.assertNotNull(doc);

            doc = session.createDocument(doc);

            docRef = doc.getRef();

            JetonDoc jetonDoc = doc.getAdapter(JetonDoc.class);
            Assert.assertNotNull(jetonDoc);

            Assert.assertEquals(doc, jetonDoc.getDocument());

            jetonDoc.setIdDoc(idDoc);
            jetonDoc.setNumeroJeton(idJeton);
            jetonDoc.setIdOwner(idOwner);
            jetonDoc.setTypeWebservice(type);

            Assert.assertEquals(idDoc, jetonDoc.getIdDoc());
            Assert.assertEquals(idOwner, jetonDoc.getIdOwner());
            Assert.assertEquals(idJeton, jetonDoc.getNumeroJeton());
            Assert.assertEquals(type, jetonDoc.getTypeWebservice());

            // check persistance
            jetonDoc.saveDocument(session);
            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.getDocument(docRef);
            JetonDoc jetonDoc = doc.getAdapter(JetonDoc.class);

            Assert.assertEquals(doc, jetonDoc.getDocument());

            Assert.assertEquals(idDoc, jetonDoc.getIdDoc());
            Assert.assertEquals(idOwner, jetonDoc.getIdOwner());
            Assert.assertEquals(idJeton, jetonDoc.getNumeroJeton());
            Assert.assertEquals(type, jetonDoc.getTypeWebservice());
        }
    }
}
