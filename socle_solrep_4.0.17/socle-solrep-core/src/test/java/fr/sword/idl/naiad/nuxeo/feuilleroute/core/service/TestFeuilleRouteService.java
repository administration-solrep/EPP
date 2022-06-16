package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteAlreadyLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.exception.FeuilleRouteNotLockedException;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.listener.CounterListener;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteFeature;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteTestConstants;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.test.FeuilleRouteTestUtils;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import java.util.List;
import javax.inject.Inject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * @author arussel
 *
 */
@RunWith(FeaturesRunner.class)
@Features(FeuilleRouteFeature.class)
public class TestFeuilleRouteService {
    @Inject
    private FeuilleRouteService service;

    @Before
    public void setup() {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            DocumentModel root = session.getRootDocument();
            ACP acpRoot = root.getACP();
            ACL aclRoot = acpRoot.getOrCreateACL("local");
            aclRoot.add(new ACE("Administrator", SecurityConstants.READ_WRITE, true));
            session.setACP(root.getRef(), acpRoot, true);
            session.saveDocument(root);
            session.save();

            //ContentTemplateService ctService = Framework.getService(ContentTemplateService.class);
            //ctService.executeFactoryForType(root);
            //assertEquals(3, session.getChildren(session.getChildren(root.getRef()).get(0).getRef()).size());
            DocumentModel workspaces = session.getDocument(new PathRef(FeuilleRouteTestConstants.WORKSPACES_PATH));
            Assert.assertNotNull(workspaces);
            ACP acp = workspaces.getACP();
            ACL acl = acp.getOrCreateACL("local");
            acl.add(new ACE("bob", SecurityConstants.READ_WRITE, true));
            session.setACP(workspaces.getRef(), acp, true);
            session.saveDocument(workspaces);
            session.save();
        }
    }

    @Test
    public void testAddStepToDraftRoute() throws Exception {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = FeuilleRouteTestUtils.createDocumentRoute(session, FeuilleRouteTestConstants.ROUTE1);
            session.save();
            Assert.assertNotNull(route);
            DocumentModel step = session.createDocumentModel(
                route.getDocument().getPathAsString(),
                "step31bis",
                FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP
            );
            DublincorePropertyUtil.setTitle(step, "step31bis");
            DocumentModelList stepFolders = session.query("Select * From Document WHERE dc:title = 'parallel1'");
            Assert.assertEquals(1, stepFolders.size());
            DocumentModel parallel1 = stepFolders.get(0);
            service.lockDocumentRoute(route, session);
            service.addRouteElementToRoute(
                parallel1.getRef(),
                "step32",
                step.getAdapter(FeuilleRouteElement.class),
                session
            );
            service.unlockDocumentRoute(route, session);
            DocumentModelList parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
            Assert.assertEquals(3, parallel1Childs.size());
            step = parallel1Childs.get(1);
            Assert.assertEquals("step31bis", step.getTitle());

            step =
                session.createDocumentModel(
                    route.getDocument().getPathAsString(),
                    "step33",
                    FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP
                );
            DublincorePropertyUtil.setTitle(step, "step33");
            service.lockDocumentRoute(route, session);
            service.addRouteElementToRoute(
                parallel1.getRef(),
                null,
                step.getAdapter(FeuilleRouteElement.class),
                session
            );
            service.unlockDocumentRoute(route, session);
            parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
            Assert.assertEquals(4, parallel1Childs.size());
            step = parallel1Childs.get(3);
            Assert.assertEquals("step33", step.getTitle());

            step =
                session.createDocumentModel(
                    route.getDocument().getPathAsString(),
                    "step30",
                    FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP
                );
            DublincorePropertyUtil.setTitle(step, "step30");
            service.lockDocumentRoute(route, session);
            service.addRouteElementToRoute(parallel1.getRef(), 0, step.getAdapter(FeuilleRouteElement.class), session);
            parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
            service.unlockDocumentRoute(route, session);
            Assert.assertEquals(5, parallel1Childs.size());
            step = parallel1Childs.get(0);
            Assert.assertEquals("step30", step.getTitle());

            step =
                session.createDocumentModel(
                    route.getDocument().getPathAsString(),
                    "step34",
                    FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP
                );
            DublincorePropertyUtil.setTitle(step, "step34");
            service.lockDocumentRoute(route, session);
            service.addRouteElementToRoute(parallel1.getRef(), 5, step.getAdapter(FeuilleRouteElement.class), session);
            parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
            service.unlockDocumentRoute(route, session);
            Assert.assertEquals(6, parallel1Childs.size());
            step = parallel1Childs.get(5);
            Assert.assertEquals("step34", step.getTitle());

            step =
                session.createDocumentModel(
                    route.getDocument().getPathAsString(),
                    "step33bis",
                    FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP
                );
            DublincorePropertyUtil.setTitle(step, "step33bis");
            service.lockDocumentRoute(route, session);
            service.addRouteElementToRoute(parallel1.getRef(), 5, step.getAdapter(FeuilleRouteElement.class), session);
            service.unlockDocumentRoute(route, session);
            parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
            Assert.assertEquals(7, parallel1Childs.size());
            step = parallel1Childs.get(5);
            Assert.assertEquals("step33bis", step.getTitle());
        }
    }

    @Test
    public void testAddSameNamedStepToRunningRoute() throws Exception {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = FeuilleRouteTestUtils.createDocumentRoute(session, FeuilleRouteTestConstants.ROUTE1);
            DocumentModelList childrens = session.getChildren(route.getDocument().getRef());
            String firstStepId = childrens.get(0).getId();
            String secondStepId = childrens.get(1).getId();
            String folderId = childrens.get(2).getId();
            service.lockDocumentRoute(route, session);
            DocumentModel newStep = session.createDocumentModel(
                route.getDocument().getPathAsString(),
                "step1",
                FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP
            );
            service.addRouteElementToRoute(
                route.getDocument().getRef(),
                null,
                newStep.getAdapter(FeuilleRouteElement.class),
                session
            );
            session.save();
            Assert.assertNotNull(route);
            childrens = session.getChildren(route.getDocument().getRef());
            Assert.assertEquals(4, childrens.size());
            Assert.assertEquals(firstStepId, childrens.get(0).getId());
            Assert.assertEquals(secondStepId, childrens.get(1).getId());
            Assert.assertEquals(folderId, childrens.get(2).getId());
            // the new step's name should be step1.xxxxxx
            Assert.assertTrue(!"step1".equals(childrens.get(3).getName()));
        }
    }

    @Test
    public void testRemoveStep() throws Exception {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = FeuilleRouteTestUtils.createDocumentRoute(session, FeuilleRouteTestConstants.ROUTE1);
            Assert.assertNotNull(route);
            session.save();
            DocumentModel stepFolder = session.getDocument(
                new PathRef(
                    FeuilleRouteTestConstants.WORKSPACES_PATH + "/" + FeuilleRouteTestConstants.ROUTE1 + "/parallel1/"
                )
            );
            DocumentModelList childs = service.getOrderedRouteElement(stepFolder.getId(), session);
            Assert.assertEquals(2, childs.size());

            DocumentModel step32 = session.getDocument(
                new PathRef(
                    FeuilleRouteTestConstants.WORKSPACES_PATH +
                    "/" +
                    FeuilleRouteTestConstants.ROUTE1 +
                    "/parallel1/step32"
                )
            );
            Assert.assertNotNull(step32);
            service.lockDocumentRoute(route, session);
            service.removeRouteElement(step32.getAdapter(FeuilleRouteElement.class), session);
            service.unlockDocumentRoute(route, session);
            childs = service.getOrderedRouteElement(stepFolder.getId(), session);
            Assert.assertEquals(1, childs.size());
        }
    }

    @Test
    public void testRemoveStepFromLockedRoute() throws Exception {
        DocumentRef routeRef = null;
        DocumentRef stepFolderRef = null;
        DocumentRef step32Ref = null;
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = FeuilleRouteTestUtils.createDocumentRoute(session, FeuilleRouteTestConstants.ROUTE1);
            Assert.assertNotNull(route);
            session.save();
            DocumentModel stepFolder = session.getDocument(
                new PathRef(
                    FeuilleRouteTestConstants.WORKSPACES_PATH + "/" + FeuilleRouteTestConstants.ROUTE1 + "/parallel1/"
                )
            );
            DocumentModelList childs = service.getOrderedRouteElement(stepFolder.getId(), session);
            Assert.assertEquals(2, childs.size());

            DocumentModel step32 = session.getDocument(
                new PathRef(
                    FeuilleRouteTestConstants.WORKSPACES_PATH +
                    "/" +
                    FeuilleRouteTestConstants.ROUTE1 +
                    "/parallel1/step32"
                )
            );
            Assert.assertNotNull(step32);
            service.lockDocumentRoute(route, session);
            // grant everyting permission on the route to jdoe
            DocumentModel routeModel = route.getDocument();
            ACP acp = routeModel.getACP();
            ACL localACL = acp.getOrCreateACL(ACL.LOCAL_ACL);
            localACL.add(new ACE(SecurityConstants.EVERYONE, SecurityConstants.EVERYTHING, true));
            acp.addACL(localACL);
            routeModel.setACP(acp, true);
            session.saveDocument(routeModel);
            session.save();

            routeRef = route.getDocument().getRef();
            stepFolderRef = stepFolder.getRef();
            step32Ref = step32.getRef();
        }
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test", new NuxeoPrincipalImpl("jdoe"))) {
            FeuilleRoute route = session.getDocument(routeRef).getAdapter(FeuilleRoute.class);
            try {
                service.lockDocumentRoute(route, session);
                Assert.fail("exception should occurs");
            } catch (FeuilleRouteAlreadyLockedException e2) {
                Assert.assertNotNull(e2);
            }
        }
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = session.getDocument(routeRef).getAdapter(FeuilleRoute.class);
            DocumentModel step32 = session.getDocument(step32Ref);
            // 	service.lockDocumentRoute(route, session);
            service.removeRouteElement(step32.getAdapter(FeuilleRouteElement.class), session);
            service.unlockDocumentRoute(route, session);
        }
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test", new NuxeoPrincipalImpl("jdoe"))) {
            FeuilleRoute route = session.getDocument(routeRef).getAdapter(FeuilleRoute.class);
            try {
                service.unlockDocumentRoute(route, session);
                Assert.fail("exception should occurs");
            } catch (FeuilleRouteNotLockedException e2) {
                Assert.assertNotNull(e2);
            }
            DocumentModel stepFolder = session.getDocument(stepFolderRef);
            DocumentModelList childs = service.getOrderedRouteElement(stepFolder.getId(), session);
            Assert.assertEquals(1, childs.size());
        }
    }

    @Test
    public void testCreateNewInstance() throws Exception {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = FeuilleRouteTestUtils.createDocumentRoute(session, FeuilleRouteTestConstants.ROUTE1);
            Assert.assertNotNull(route);
            session.save();
            List<FeuilleRoute> routes = service.getAvailableDocumentRouteModel(session);
            Assert.assertEquals(1, routes.size());
            FeuilleRoute routeModel = routes.get(0);
            DocumentModel doc1 = FeuilleRouteTestUtils.createTestDocument("test1", session);
            session.save();

            route = validateRoute(session, route);

            FeuilleRoute routeInstance = service.createNewInstance(routeModel, doc1.getId(), session);
            Assert.assertTrue(routeInstance.isDone());
        }
    }

    public void testDocumentRouteWithWaitState() throws Exception {
        CounterListener.resetCouner();
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = FeuilleRouteTestUtils.createDocumentRoute(session, FeuilleRouteTestConstants.ROUTE1);
            Assert.assertNotNull(route);
            session.save();
            List<FeuilleRoute> routes = service.getAvailableDocumentRouteModel(session);
            Assert.assertEquals(1, routes.size());
            FeuilleRoute routeModel = routes.get(0);
            DocumentModel doc1 = FeuilleRouteTestUtils.createTestDocument("test1", session);
            session.save();
            route = validateRoute(session, route);
            FeuilleRoute routeInstance = service.createNewInstance(routeModel, doc1.getId(), session);
            Assert.assertNotNull(routeInstance);
            Assert.assertFalse(routeInstance.isDone());
            Assert.assertEquals(1, service.getDocumentRoutesForAttachedDocument(session, doc1.getId()).size());
            List<String> waiting = WaitingStepRuntimePersister.getRunningStepIds();
            Assert.assertEquals(1, waiting.size());
            WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
            waiting = WaitingStepRuntimePersister.getRunningStepIds();
            Assert.assertEquals(1, waiting.size());
            WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
            waiting = WaitingStepRuntimePersister.getRunningStepIds();
            Assert.assertEquals(2, waiting.size());
            WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
            waiting = WaitingStepRuntimePersister.getRunningStepIds();
            Assert.assertEquals(1, waiting.size());
            Assert.assertFalse(routeInstance.isDone());
            WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
            Assert.assertEquals(0, waiting.size());
            routeInstance = session.getDocument(routeInstance.getDocument().getRef()).getAdapter(FeuilleRoute.class);
            Assert.assertTrue(routeInstance.isDone());

            Assert.assertEquals(
                6 + /* route */4 * /* number of steps */3/* number of event per waiting step */,
                CounterListener.getCounter()
            );
        }
    }

    @Test
    public void testGetAvailableDocumentRouteModel() {
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            FeuilleRoute route = FeuilleRouteTestUtils.createDocumentRoute(session, FeuilleRouteTestConstants.ROUTE1);
            Assert.assertNotNull(route);
            session.save();
            List<FeuilleRoute> routes = service.getAvailableDocumentRouteModel(session);
            Assert.assertEquals(1, routes.size());
        }
    }

    @Test
    public void testRouteModel() {
        DocumentRef routeRef = null;
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test")) {
            DocumentModel folder = FeuilleRouteTestUtils.createDocumentModel(session, "TestFolder", "Folder", "/");
            session.save();
            Assert.assertNotNull(folder);
            setPermissionToUser(session, folder, "jdoe", SecurityConstants.WRITE);
            DocumentModel route = FeuilleRouteTestUtils.createDocumentRouteModel(
                session,
                FeuilleRouteTestConstants.ROUTE1,
                folder.getPathAsString()
            );
            session.save();
            Assert.assertNotNull(route);
            service.lockDocumentRoute(route.getAdapter(FeuilleRoute.class), session);
            route = service.validateRouteModel(route.getAdapter(FeuilleRoute.class), session).getDocument();
            session.save();
            service.unlockDocumentRoute(route.getAdapter(FeuilleRoute.class), session);
            route = session.getDocument(route.getRef());
            Assert.assertEquals("validated", route.getCurrentLifeCycleState());

            routeRef = route.getRef();
        }
        try (CloseableCoreSession session = CoreInstance.openCoreSession("test", new NuxeoPrincipalImpl("jdoe"))) {
            Assert.assertFalse(session.hasPermission(routeRef, SecurityConstants.WRITE));
            Assert.assertTrue(session.hasPermission(routeRef, SecurityConstants.READ));
        }
    }

    protected void setPermissionToUser(CoreSession session, DocumentModel doc, String username, String... perms) {
        ACP acp = doc.getACP();
        if (acp == null) {
            acp = new ACPImpl();
        }
        ACL acl = new ACLImpl("test");
        for (String perm : perms) {
            acl.add(new ACE(username, perm));
        }
        acp.addACL(acl);
        doc.setACP(acp, true);
        session.save();
    }

    protected void waitForAsyncExec() {
        Framework.getService(EventService.class).waitForAsyncCompletion();
    }

    private FeuilleRoute validateRoute(CoreSession session, FeuilleRoute route) {
        service.lockDocumentRoute(route, session);
        route = service.validateRouteModel(route, session);
        service.unlockDocumentRoute(route, session);
        Assert.assertEquals("validated", route.getDocument().getCurrentLifeCycleState());
        Assert.assertEquals(
            "validated",
            session.getChildren(route.getDocument().getRef()).get(0).getCurrentLifeCycleState()
        );
        session.save();
        waitForAsyncExec();
        return route;
    }
}
