package fr.dila.st.core.adapter;

import fr.dila.st.api.administration.NotificationsSuiviBatchs;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.test.STFeature;
import java.util.ArrayList;
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
public class TestNotificationsSuiviBatchsAdapter {
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
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-life-cycle-contrib.xml");
    //		deployContrib("fr.dila.st.core", "OSGI-INF/st-life-cycle-type-contrib.xml");
    //
    //	}
    //
    //	@Override
    //	public void setUp() throws Exception {
    //		super.setUp();
    //	}

    /**
     * Test les getter/setter de l'apdapter NotificationsSuiviBatchs
     */
    @Test
    public void testNotificationsSuiviBatchsImpl() {
        final boolean etatNotif = true;
        final ArrayList<String> receiverMailList = new ArrayList<String>();
        receiverMailList.add("Spirou");
        receiverMailList.add("Fantasio");
        receiverMailList.add("Spip");

        DocumentRef docRef;
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.createDocumentModel(STConstant.NOTIFICATIONS_SUIVI_BATCHS_DOCUMENT_TYPE);
            Assert.assertNotNull(doc);

            doc = session.createDocument(doc);

            docRef = doc.getRef();

            NotificationsSuiviBatchs notif = doc.getAdapter(NotificationsSuiviBatchs.class);
            Assert.assertNotNull(notif);

            Assert.assertEquals(doc, notif.getDocument());

            notif.setEtatNotification(etatNotif);
            notif.setReceiverMailList(receiverMailList);

            Assert.assertEquals(etatNotif, notif.getEtatNotification());
            Assert.assertEquals(receiverMailList, notif.getReceiverMailList());

            // check persistance
            notif.save(session);
            session.save();
        }
        try (CloseableCoreSession session = coreFeature.openCoreSession()) {
            DocumentModel doc = session.getDocument(docRef);
            NotificationsSuiviBatchs notif = doc.getAdapter(NotificationsSuiviBatchs.class);

            Assert.assertEquals(doc, notif.getDocument());
            Assert.assertEquals(etatNotif, notif.getEtatNotification());
            Assert.assertEquals(receiverMailList, notif.getReceiverMailList());
        }
    }
}
