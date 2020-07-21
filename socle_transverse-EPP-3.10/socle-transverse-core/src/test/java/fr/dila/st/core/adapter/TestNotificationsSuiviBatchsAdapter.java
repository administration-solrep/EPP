package fr.dila.st.core.adapter;

import java.util.ArrayList;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;

import fr.dila.cm.test.CaseManagementTestConstants;
import fr.dila.st.api.administration.NotificationsSuiviBatchs;
import fr.dila.st.api.constant.STConstant;

public class TestNotificationsSuiviBatchsAdapter extends SQLRepositoryTestCase {

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
	 * Test les getter/setter de l'apdapter NotificationsSuiviBatchs
	 * 
	 * @throws ClientException
	 */
	public void testNotificationsSuiviBatchsImpl() throws ClientException {
		openSession();

		final boolean etatNotif = true;
		final ArrayList<String> receiverMailList = new ArrayList<String>();
		receiverMailList.add("Spirou");
		receiverMailList.add("Fantasio");
		receiverMailList.add("Spip");

		DocumentModel doc = session.createDocumentModel(STConstant.NOTIFICATIONS_SUIVI_BATCHS_DOCUMENT_TYPE);
		assertNotNull(doc);

		doc = session.createDocument(doc);

		String id = doc.getId();

		NotificationsSuiviBatchs notif = doc.getAdapter(NotificationsSuiviBatchs.class);
		assertNotNull(notif);

		assertEquals(doc, notif.getDocument());

		notif.setEtatNotification(etatNotif);
		notif.setReceiverMailList(receiverMailList);

		assertEquals(etatNotif, notif.getEtatNotification());
		assertEquals(receiverMailList, notif.getReceiverMailList());

		// check persistance
		notif.save(session);
		session.save();

		closeSession();
		openSession();

		doc = session.getDocument(new IdRef(id));
		notif = doc.getAdapter(NotificationsSuiviBatchs.class);

		assertEquals(doc, notif.getDocument());
		assertEquals(etatNotif, notif.getEtatNotification());
		assertEquals(receiverMailList, notif.getReceiverMailList());

		closeSession();
	}
}
