package fr.dila.st.core.adapter;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.cm.test.CaseManagementTestConstants;
import fr.dila.st.api.administration.EtatApplication;
import fr.dila.st.api.constant.STConstant;

public class TestEtatApplicationAdapter extends SQLRepositoryTestCase {

	@Override
	protected void deployRepositoryContrib() throws Exception {
		super.deployRepositoryContrib();

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

		deployContrib("fr.dila.st.core", "OSGI-INF/st-schema-contrib.xml");
		deployContrib("fr.dila.st.core", "OSGI-INF/st-core-type-contrib.xml");
		deployContrib("fr.dila.st.core", "OSGI-INF/st-adapter-contrib.xml");

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test les getter/setter de l'apdapter EtatApplication
	 * 
	 * @throws ClientException
	 */
	public void testEtatApplicationImpl() throws ClientException {
		openSession();

		final String descRestriction = "Une restriction test";
		final boolean restrictionON = true;
		final String creator = "Spirou";
		final String lastContributor = "Fantasio";
		final String title = "title";
		final Calendar modifiedDate = Calendar.getInstance();

		DocumentModel doc = session.createDocumentModel(STConstant.ETAT_APPLICATION_DOCUMENT_TYPE);
		assertNotNull(doc);

		doc = session.createDocument(doc);

		String id = doc.getId();

		EtatApplication etatApp = doc.getAdapter(EtatApplication.class);
		assertNotNull(etatApp);

		assertEquals(doc, etatApp.getDocument());

		etatApp.setDescriptionRestriction(descRestriction);
		etatApp.setRestrictionAcces(restrictionON);
		etatApp.setCreator(creator);
		etatApp.setLastContributor(lastContributor);
		etatApp.setModifiedDate(modifiedDate);
		etatApp.setTitle(title);

		assertEquals(creator, etatApp.getCreator());
		assertEquals(descRestriction, etatApp.getDescriptionRestriction());
		assertEquals(lastContributor, etatApp.getLastContributor());
		assertEquals(restrictionON, etatApp.getRestrictionAcces());
		assertEquals(title, etatApp.getTitle());
		assertEquals(modifiedDate, etatApp.getModifiedDate());

		// check persistance
		etatApp.save(session);
		session.save();

		closeSession();
		openSession();

		doc = session.getDocument(new IdRef(id));
		etatApp = doc.getAdapter(EtatApplication.class);

		assertEquals(doc, etatApp.getDocument());
		assertEquals(creator, etatApp.getCreator());
		assertEquals(descRestriction, etatApp.getDescriptionRestriction());
		assertEquals(lastContributor, etatApp.getLastContributor());
		assertEquals(restrictionON, etatApp.getRestrictionAcces());
		assertEquals(title, etatApp.getTitle());
		assertEquals(modifiedDate, etatApp.getModifiedDate());

		closeSession();
	}
}
