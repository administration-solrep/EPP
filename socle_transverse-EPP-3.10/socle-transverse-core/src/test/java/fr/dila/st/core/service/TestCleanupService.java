package fr.dila.st.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.jndi.NamingContextFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.cm.test.CaseManagementTestConstants;
import fr.dila.st.api.service.CleanupService;

public class TestCleanupService extends SQLRepositoryTestCase {

	private static final Log	log			= LogFactory.getLog(TestCleanupService.class);

	private final int			TOTAL_NOTE	= 300;

	private CleanupService		cleanupService;

	@Override
	protected void deployRepositoryContrib() throws Exception {
		super.deployRepositoryContrib();

		NamingContextFactory.setAsInitial();

		// deploy repository manager
		deployBundle("org.nuxeo.ecm.core.api");

		// deploy api and core bundles
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_API_BUNDLE);
		deployBundle(CaseManagementTestConstants.CASE_MANAGEMENT_CORE_BUNDLE);
		deployBundle("fr.dila.ecm.platform.routing.core");
		deployBundle("org.nuxeo.ecm.automation.core");
		deployBundle("org.nuxeo.ecm.directory");
		deployBundle("org.nuxeo.ecm.platform.usermanager");
		deployBundle("org.nuxeo.ecm.directory.types.contrib");
		deployBundle("org.nuxeo.ecm.core.persistence");
		deployBundle("org.nuxeo.ecm.platform.uidgen.core");

		deployContrib("fr.dila.st.core", "OSGI-INF/querymaker-contrib.xml");

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

		openSession();

		cleanupService = new CleanupServiceImpl();

		DocumentModel doc = null;

		for (int i = 0; i < TOTAL_NOTE; ++i) {
			doc = session.createDocumentModel("Note");
			doc = session.createDocument(doc);
			session.followTransition(doc.getRef(), "delete");
			session.saveDocument(doc);
		}

		doc = session.createDocumentModel("File");
		doc = session.createDocument(doc);

		doc = session.createDocumentModel("File");
		doc = session.createDocument(doc);
		session.followTransition(doc.getRef(), "delete");

		session.saveDocument(doc);

		session.save();

		closeSession();
	}

	public void testCleanup() throws ClientException {
		openSession();

		DocumentModelList dml = null;

		dml = session.query("SELECT * from File");
		assertEquals(2, dml.size());

		dml = session.query("SELECT * from File where ecm:currentLifeCycleState != 'deleted'");
		assertEquals(1, dml.size());

		dml = session.query("SELECT * from Note");
		assertEquals(TOTAL_NOTE, dml.size());

		int nbRemoved = cleanupService.removeDeletedDocument(session, "File", 0);
		assertEquals(1, nbRemoved);
		dml = session.query("SELECT * from File");
		assertEquals(1, dml.size());
		dml = session.query("SELECT * from File where ecm:currentLifeCycleState != 'deleted'");
		assertEquals(1, dml.size());

		dml = session.query("SELECT * from Note");
		assertEquals(TOTAL_NOTE, dml.size());

		nbRemoved = cleanupService.removeDeletedDocument(session, "Note", 0);
		assertEquals(TOTAL_NOTE, nbRemoved);

		dml = session.query("SELECT * from Note");
		assertEquals(0, dml.size());

		closeSession();
	}

	public void testCleanupWithTimeout() throws ClientException {
		openSession();

		DocumentModelList dml;

		dml = session.query("SELECT * from Note");
		assertEquals(TOTAL_NOTE, dml.size());

		int totalRemoved = 0;
		int nbRemoved = 1;
		while (nbRemoved > 0) {
			nbRemoved = cleanupService.removeDeletedDocument(session, "Note", 1);
			// assertTrue(nbRemoved < TOTAL_NOTE);
			log.info("nb removed : " + nbRemoved);
			totalRemoved += nbRemoved;
		}
		assertEquals(TOTAL_NOTE, totalRemoved);

		dml = session.query("SELECT * from Note");
		assertEquals(0, dml.size());

		closeSession();
	}

}
