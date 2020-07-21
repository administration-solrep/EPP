package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.cm.test.CaseManagementTestConstants;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.parametre.STParametre;

public class TestSTParametreAdapter extends SQLRepositoryTestCase {

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
		deployContrib("fr.dila.st.core", "OSGI-INF/st-life-cycle-contrib.xml");
		deployContrib("fr.dila.st.core", "OSGI-INF/st-life-cycle-type-contrib.xml");

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * Test les getter/setter de l'apdapter STParametre
	 * 
	 * @throws ClientException
	 */
	public void testSTParametreImpl() throws ClientException {
		openSession();

		final String description = "Une description de paramètre";
		final String unit = "noisettes";
		final String value = "Nutella";

		DocumentModel doc = session.createDocumentModel(STConstant.PARAMETRE_DOCUMENT_TYPE);
		assertNotNull(doc);

		doc = session.createDocument(doc);

		String id = doc.getId();

		STParametre parametre = doc.getAdapter(STParametre.class);
		assertNotNull(parametre);

		assertEquals(doc, parametre.getDocument());

		parametre.setDescription(description);
		parametre.setUnit(unit);
		parametre.setValue(value);

		assertEquals(description, parametre.getDescription());
		assertEquals(unit, parametre.getUnit());
		assertEquals(value, parametre.getValue());

		// check persistance
		session.saveDocument(parametre.getDocument());
		session.save();

		closeSession();
		openSession();

		doc = session.getDocument(new IdRef(id));
		parametre = doc.getAdapter(STParametre.class);

		assertEquals(doc, parametre.getDocument());
		assertEquals(description, parametre.getDescription());
		assertEquals(unit, parametre.getUnit());
		assertEquals(value, parametre.getValue());

		closeSession();
	}
}
