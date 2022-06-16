package fr.dila.st.core.service;

import fr.dila.st.api.service.CleanupService;
import fr.dila.st.core.test.STFeature;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(STFeature.class)
@Ignore("Nouveau life cycle avec Nuxeo 10.10 -> le service et le test associés deviennent obsolètes.")
public class TestCleanupService {
    private static final Log log = LogFactory.getLog(TestCleanupService.class);

    private final int TOTAL_NOTE = 300;

    private CleanupService cleanupService;

    @Inject
    private CoreFeature coreFeature;

    //	@Override
    //	protected void deployRepositoryContrib() throws Exception {
    //		super.deployRepositoryContrib();
    //
    //		NamingContextFactory.setAsInitial();
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
    //		deployBundle("org.nuxeo.ecm.core.persistence");
    //		deployBundle("org.nuxeo.ecm.platform.uidgen.core");
    //
    //		deployContrib("fr.dila.st.core", "OSGI-INF/querymaker-contrib.xml");
    //
    //	}

    @Before
    public void setUp() throws Exception {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            cleanupService = new CleanupServiceImpl();

            DocumentModel doc = null;

            for (int i = 0; i < TOTAL_NOTE; ++i) {
                doc = session.createDocumentModel("Note");
                doc = session.createDocument(doc);
                session.followTransition(doc.getRef(), "delete");
                doc = session.getDocument(doc.getRef());
                session.saveDocument(doc);
            }

            doc = session.createDocumentModel("File");
            doc = session.createDocument(doc);

            doc = session.createDocumentModel("File");
            doc = session.createDocument(doc);
            session.followTransition(doc.getRef(), "delete");

            session.saveDocument(doc);

            session.save();
        }
    }

    @Test
    public void testCleanup() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModelList dml = null;

            dml = session.query("SELECT * from File");
            Assert.assertEquals(2, dml.size());

            dml = session.query("SELECT * from File where ecm:currentLifeCycleState != 'deleted'");
            Assert.assertEquals(1, dml.size());

            dml = session.query("SELECT * from Note");
            Assert.assertEquals(TOTAL_NOTE, dml.size());

            int nbRemoved = cleanupService.removeDeletedDocument(session, "File", 0);
            Assert.assertEquals(1, nbRemoved);
            dml = session.query("SELECT * from File");
            Assert.assertEquals(1, dml.size());
            dml = session.query("SELECT * from File where ecm:currentLifeCycleState != 'deleted'");
            Assert.assertEquals(1, dml.size());

            dml = session.query("SELECT * from Note");
            Assert.assertEquals(TOTAL_NOTE, dml.size());

            nbRemoved = cleanupService.removeDeletedDocument(session, "Note", 0);
            Assert.assertEquals(TOTAL_NOTE, nbRemoved);

            dml = session.query("SELECT * from Note");
            Assert.assertEquals(0, dml.size());
        }
    }

    @Test
    public void testCleanupWithTimeout() {
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModelList dml;

            dml = session.query("SELECT * from Note");
            Assert.assertEquals(TOTAL_NOTE, dml.size());

            int totalRemoved = 0;
            int nbRemoved = 1;
            while (nbRemoved > 0) {
                nbRemoved = cleanupService.removeDeletedDocument(session, "Note", 1);
                // Assert.assertTrue(nbRemoved < TOTAL_NOTE);
                log.info("nb removed : " + nbRemoved);
                totalRemoved += nbRemoved;
            }
            Assert.assertEquals(TOTAL_NOTE, totalRemoved);

            dml = session.query("SELECT * from Note");
            Assert.assertEquals(0, dml.size());
        }
    }
}
