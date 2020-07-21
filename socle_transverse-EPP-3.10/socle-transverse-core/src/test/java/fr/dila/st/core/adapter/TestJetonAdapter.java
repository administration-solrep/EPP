package fr.dila.st.core.adapter;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.cm.test.CaseManagementTestConstants;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.jeton.JetonDoc;

public class TestJetonAdapter extends SQLRepositoryTestCase {

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
	 * Test les getter/setter de l'apdapter JetonDoc
	 * 
	 * @throws ClientException
	 */
	public void testJetonDocImpl() throws ClientException {
		openSession();

		final String idDoc = "id doc";
		final Long idJeton = 5656L;
		final String idOwner = "id owner";
		final String type = "webservice type";

		DocumentModel doc = session.createDocumentModel(STConstant.JETON_DOC_TYPE);
		assertNotNull(doc);

		doc = session.createDocument(doc);

		String id = doc.getId();

		JetonDoc jetonDoc = doc.getAdapter(JetonDoc.class);
		assertNotNull(jetonDoc);

		assertEquals(doc, jetonDoc.getDocument());

		jetonDoc.setIdDoc(idDoc);
		jetonDoc.setNumeroJeton(idJeton);
		jetonDoc.setIdOwner(idOwner);
		jetonDoc.setTypeWebservice(type);

		assertEquals(idDoc, jetonDoc.getIdDoc());
		assertEquals(idOwner, jetonDoc.getIdOwner());
		assertEquals(idJeton, jetonDoc.getNumeroJeton());
		assertEquals(type, jetonDoc.getTypeWebservice());

		// check persistance
		jetonDoc.saveDocument(session);
		session.save();

		closeSession();
		openSession();

		doc = session.getDocument(new IdRef(id));
		jetonDoc = doc.getAdapter(JetonDoc.class);

		assertEquals(doc, jetonDoc.getDocument());

		assertEquals(idDoc, jetonDoc.getIdDoc());
		assertEquals(idOwner, jetonDoc.getIdOwner());
		assertEquals(idJeton, jetonDoc.getNumeroJeton());
		assertEquals(type, jetonDoc.getTypeWebservice());
		closeSession();
	}
}
