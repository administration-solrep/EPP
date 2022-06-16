package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRoutePersister;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteFeature;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteTestConstants;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteTestUtils;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteInstanceSchemaUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 *
 */
@RunWith(FeaturesRunner.class)
@Features(FeuilleRouteFeature.class)
public class TestFeuilleRouteTreePersister {
    private FeuilleRoutePersister persister;

    public TestFeuilleRouteTreePersister() {
        // do nothing
    }

    @Before
    public void setUp() {
        persister = new FeuilleRouteTreePersister();
    }

    @Test
    public void testGetOrCreateRootOfDocumentRouteInstanceStructure() throws Exception {
        DocumentRef docRef = null;
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            DocumentModel doc = persister.getOrCreateRootOfDocumentRouteInstanceStructure(session);
            Assert.assertNotNull(doc);
            Assert.assertEquals(
                doc.getPathAsString(),
                FeuilleRouteTestConstants.DEFAULT_DOMAIN_DOCUMENT_ROUTE_INSTANCES_ROOT
            );
            session.save();
            docRef = doc.getRef();
        }
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test", new NuxeoPrincipalImpl("members"))) {
            Assert.assertFalse(session.hasPermission(docRef, SecurityConstants.READ));
        }
    }

    @Test
    public void testGetParentFolderForDocumentRouteInstance() {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            DocumentModel parent = persister.getParentFolderForDocumentRouteInstance(null, session);
            Assert.assertNotNull(parent);
            Assert.assertTrue(
                parent
                    .getPathAsString()
                    .startsWith(FeuilleRouteTestConstants.DEFAULT_DOMAIN_DOCUMENT_ROUTE_INSTANCES_ROOT)
            );
        }
    }

    @Test
    public void testCreateDocumentRouteInstanceFromDocumentRouteModel() {
        DocumentRef instanceRef = null;
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            DocumentModel model = FeuilleRouteTestUtils.createDocumentRouteModel(
                session,
                FeuilleRouteTestConstants.ROUTE1,
                FeuilleRouteTestConstants.WORKSPACES_PATH
            );
            List<String> docsId = new ArrayList<String>();
            docsId.add("1");
            FeuilleRouteInstanceSchemaUtil.setAttachedDocuments(model, docsId);
            DocumentModel instance = persister.createDocumentRouteInstanceFromDocumentRouteModel(model, session);
            Assert.assertNotNull(instance);
            Assert.assertTrue(
                instance
                    .getPathAsString()
                    .startsWith(FeuilleRouteTestConstants.DEFAULT_DOMAIN_DOCUMENT_ROUTE_INSTANCES_ROOT)
            );
            docsId = FeuilleRouteInstanceSchemaUtil.getAttachedDocuments(instance);
            Assert.assertEquals("1", docsId.get(0));
            instanceRef = instance.getRef();
        }
        try (
            CloseableCoreSession session = CoreInstance.openCoreSession(
                "test",
                new NuxeoPrincipalImpl(FeuilleRouteConstant.ROUTE_MANAGERS_GROUP_NAME)
            )
        ) {
            Assert.assertEquals(3, session.getChildren(instanceRef).size());
        }
    }

    @Test
    public void testSaveDocumentRouteInstanceAsNewModel() {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            DocumentModel model = FeuilleRouteTestUtils.createDocumentRouteModel(
                session,
                FeuilleRouteTestConstants.ROUTE1,
                FeuilleRouteTestConstants.WORKSPACES_PATH
            );
            DocumentModel instance = persister.createDocumentRouteInstanceFromDocumentRouteModel(model, session);
            DocumentModel newModel = persister.saveDocumentRouteInstanceAsNewModel(
                instance,
                session.getRootDocument(),
                session
            );
            Assert.assertNotNull(newModel);
        }
    }
}
