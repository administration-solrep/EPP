/*
 * (C) Copyright 2009 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     arussel
 */
package fr.dila.ecm.platform.routing.test;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.api.security.UserEntry;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.api.security.impl.UserEntryImpl;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.runtime.api.Framework;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRouteElement;
import fr.dila.ecm.platform.routing.api.DocumentRouteStep;
import fr.dila.ecm.platform.routing.api.DocumentRouteTableElement;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteAlredayLockedException;
import fr.dila.ecm.platform.routing.api.exception.DocumentRouteNotLockedException;

/**
 * @author arussel
 * 
 */
public class TestDocumentRoutingService extends DocumentRoutingTestCase {

	public void testAddStepToDraftRoute() throws Exception {
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		session.save();
		assertNotNull(route);
		DocumentModel step = session.createDocumentModel(route.getDocument().getPathAsString(), "step31bis",
				DocumentRoutingConstants.STEP_DOCUMENT_TYPE);
		step.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, "step31bis");
		DocumentModelList stepFolders = session.query("Select * From Document WHERE dc:title = 'parallel1'");
		assertEquals(1, stepFolders.size());
		DocumentModel parallel1 = stepFolders.get(0);
		service.lockDocumentRoute(route, session);
		service.addRouteElementToRoute(parallel1.getRef(), "step32", step.getAdapter(DocumentRouteElement.class),
				session);
		service.unlockDocumentRoute(route, session);
		DocumentModelList parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
		assertEquals(3, parallel1Childs.size());
		step = parallel1Childs.get(1);
		assertEquals("step31bis", step.getTitle());

		step = session.createDocumentModel(route.getDocument().getPathAsString(), "step33",
				DocumentRoutingConstants.STEP_DOCUMENT_TYPE);
		step.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, "step33");
		service.lockDocumentRoute(route, session);
		service.addRouteElementToRoute(parallel1.getRef(), null, step.getAdapter(DocumentRouteElement.class), session);
		service.unlockDocumentRoute(route, session);
		parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
		assertEquals(4, parallel1Childs.size());
		step = parallel1Childs.get(3);
		assertEquals("step33", step.getTitle());

		step = session.createDocumentModel(route.getDocument().getPathAsString(), "step30",
				DocumentRoutingConstants.STEP_DOCUMENT_TYPE);
		step.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, "step30");
		service.lockDocumentRoute(route, session);
		service.addRouteElementToRoute(parallel1.getRef(), 0, step.getAdapter(DocumentRouteElement.class), session);
		parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
		service.unlockDocumentRoute(route, session);
		assertEquals(5, parallel1Childs.size());
		step = parallel1Childs.get(0);
		assertEquals("step30", step.getTitle());

		step = session.createDocumentModel(route.getDocument().getPathAsString(), "step34",
				DocumentRoutingConstants.STEP_DOCUMENT_TYPE);
		step.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, "step34");
		service.lockDocumentRoute(route, session);
		service.addRouteElementToRoute(parallel1.getRef(), 5, step.getAdapter(DocumentRouteElement.class), session);
		parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
		service.unlockDocumentRoute(route, session);
		assertEquals(6, parallel1Childs.size());
		step = parallel1Childs.get(5);
		assertEquals("step34", step.getTitle());

		step = session.createDocumentModel(route.getDocument().getPathAsString(), "step33bis",
				DocumentRoutingConstants.STEP_DOCUMENT_TYPE);
		step.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, "step33bis");
		service.lockDocumentRoute(route, session);
		service.addRouteElementToRoute(parallel1.getRef(), 5, step.getAdapter(DocumentRouteElement.class), session);
		service.unlockDocumentRoute(route, session);
		parallel1Childs = service.getOrderedRouteElement(parallel1.getId(), session);
		assertEquals(7, parallel1Childs.size());
		step = parallel1Childs.get(5);
		assertEquals("step33bis", step.getTitle());
	}

	public void testAddSameNamedStepToRunningRoute() throws Exception {
		deployBundle(TEST_BUNDLE);
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		DocumentModelList childrens = session.getChildren(route.getDocument().getRef());
		String firstStepId = childrens.get(0).getId();
		String secondStepId = childrens.get(1).getId();
		String folderId = childrens.get(2).getId();
		service.lockDocumentRoute(route, session);
		DocumentModel newStep = session.createDocumentModel(route.getDocument().getPathAsString(), "step1",
				DocumentRoutingConstants.STEP_DOCUMENT_TYPE);
		service.addRouteElementToRoute(route.getDocument().getRef(), null,
				newStep.getAdapter(DocumentRouteElement.class), session);
		session.save();
		assertNotNull(route);
		childrens = session.getChildren(route.getDocument().getRef());
		assertEquals(4, childrens.size());
		assertEquals(firstStepId, childrens.get(0).getId());
		assertEquals(secondStepId, childrens.get(1).getId());
		assertEquals(folderId, childrens.get(2).getId());
		// the new step's name should be step1.xxxxxx
		assertTrue(!"step1".equals(childrens.get(3).getName()));
	}

	public void testAddStepToRunningRoute() throws Exception {
		deployBundle(TEST_BUNDLE);
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		service.lockDocumentRoute(route, session);
		service.validateRouteModel(route, session);
		service.unlockDocumentRoute(route, session);
		route = service.createNewInstance(route, new ArrayList<String>(), session);
		session.save();
		assertNotNull(route);
		DocumentModel step = session.createDocumentModel(route.getDocument().getPathAsString(), "step31bis",
				DocumentRoutingConstants.STEP_DOCUMENT_TYPE);
		step.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, "step31bis");
		DocumentModelList stepFolders = session
				.query("Select * From Document WHERE dc:title = 'parallel1' and ecm:currentLifeCycleState = 'ready'");
		assertEquals(1, stepFolders.size());
		DocumentModel parallel1 = stepFolders.get(0);
		service.lockDocumentRoute(route, session);
		service.addRouteElementToRoute(parallel1.getRef(), "step32", step.getAdapter(DocumentRouteElement.class),
				session);
		service.unlockDocumentRoute(route, session);
		assertNotNull(route);
		assertFalse(route.isDone());
		List<String> waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(3, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(2, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		assertFalse(route.isDone());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		assertEquals(0, waiting.size());
		route = session.getDocument(route.getDocument().getRef()).getAdapter(DocumentRoute.class);
		assertTrue(route.isDone());
	}

	public void testRemoveStep() throws Exception {
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		session.save();
		DocumentModel stepFolder = session.getDocument(new PathRef(WORKSPACES_PATH + "/" + ROUTE1 + "/parallel1/"));
		DocumentModelList childs = service.getOrderedRouteElement(stepFolder.getId(), session);
		assertEquals(2, childs.size());

		DocumentModel step32 = session.getDocument(new PathRef(WORKSPACES_PATH + "/" + ROUTE1 + "/parallel1/step32"));
		assertNotNull(step32);
		service.lockDocumentRoute(route, session);
		service.removeRouteElement(step32.getAdapter(DocumentRouteElement.class), session);
		service.unlockDocumentRoute(route, session);
		childs = service.getOrderedRouteElement(stepFolder.getId(), session);
		assertEquals(1, childs.size());
	}

	public void testSaveInstanceAsNewModel() throws Exception {
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		service.lockDocumentRoute(route, session);
		route = service.validateRouteModel(route, session);
		service.unlockDocumentRoute(route, session);
		route = service.createNewInstance(route, new ArrayList<String>(), session);
		assertNotNull(route);
		session.save();
		session = openSessionAs("routeManagers");
		DocumentModel step = session.getChildren(route.getDocument().getRef()).get(0);
		service.lockDocumentRoute(route, session);
		service.removeRouteElement(step.getAdapter(DocumentRouteElement.class), session);
		service.unlockDocumentRoute(route, session);
		DocumentRoute newModel = service.saveRouteAsNewModel(route, session);
		assertNotNull(newModel);
		assertEquals("(COPY) route1", (String) newModel.getDocument().getPropertyValue("dc:title"));
	}

	public void testRemoveStepFromLockedRoute() throws Exception {
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		session.save();
		DocumentModel stepFolder = session.getDocument(new PathRef(WORKSPACES_PATH + "/" + ROUTE1 + "/parallel1/"));
		DocumentModelList childs = service.getOrderedRouteElement(stepFolder.getId(), session);
		assertEquals(2, childs.size());

		DocumentModel step32 = session.getDocument(new PathRef(WORKSPACES_PATH + "/" + ROUTE1 + "/parallel1/step32"));
		assertNotNull(step32);
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

		closeSession();
		session = openSessionAs("jdoe");
		Exception e = null;
		try {
			service.lockDocumentRoute(route, session);

		} catch (DocumentRouteAlredayLockedException e2) {
			e = e2;
		}
		assertNotNull(e);
		closeSession();
		openSession();
		// service.lockDocumentRoute(route, session);
		service.removeRouteElement(step32.getAdapter(DocumentRouteElement.class), session);
		service.unlockDocumentRoute(route, session);
		e = null;
		session = openSessionAs("jdoe");
		try {
			service.unlockDocumentRoute(route, session);
		} catch (DocumentRouteNotLockedException e2) {
			e = e2;
		}
		assertNotNull(e);
		childs = service.getOrderedRouteElement(stepFolder.getId(), session);
		assertEquals(1, childs.size());
	}

	public void testCreateNewInstance() throws Exception {
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		session.save();
		List<DocumentRoute> routes = service.getAvailableDocumentRouteModel(session);
		assertEquals(1, routes.size());
		DocumentRoute routeModel = routes.get(0);
		DocumentModel doc1 = createTestDocument("test1", session);
		session.save();
		service.lockDocumentRoute(route, session);
		route = service.validateRouteModel(route, session);
		service.unlockDocumentRoute(route, session);
		session.save();
		assertEquals("validated", route.getDocument().getCurrentLifeCycleState());
		assertEquals("validated", session.getChildren(route.getDocument().getRef()).get(0).getCurrentLifeCycleState());
		session.save();
		waitForAsyncExec();
		DocumentRoute routeInstance = service.createNewInstance(routeModel, doc1.getId(), session);
		assertTrue(routeInstance.isDone());
	}

	public void testDocumentRouteWithWaitState() throws Exception {
		closeSession();
		openSession();
		CounterListener.resetCouner();
		deployBundle(TEST_BUNDLE);
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		session.save();
		List<DocumentRoute> routes = service.getAvailableDocumentRouteModel(session);
		assertEquals(1, routes.size());
		DocumentRoute routeModel = routes.get(0);
		DocumentModel doc1 = createTestDocument("test1", session);
		session.save();
		service.lockDocumentRoute(route, session);
		route = service.validateRouteModel(route, session);
		service.unlockDocumentRoute(route, session);
		assertEquals("validated", route.getDocument().getCurrentLifeCycleState());
		assertEquals("validated", session.getChildren(route.getDocument().getRef()).get(0).getCurrentLifeCycleState());
		session.save();
		waitForAsyncExec();
		DocumentRoute routeInstance = service.createNewInstance(routeModel, doc1.getId(), session);
		assertNotNull(routeInstance);
		assertFalse(routeInstance.isDone());
		assertEquals(1, service.getDocumentRoutesForAttachedDocument(session, doc1.getId()).size());
		List<String> waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(2, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		assertFalse(routeInstance.isDone());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		assertEquals(0, waiting.size());
		routeInstance = session.getDocument(routeInstance.getDocument().getRef()).getAdapter(DocumentRoute.class);
		assertTrue(routeInstance.isDone());

		assertEquals(6/* route */+ 4 /* number of steps */* 3 /* number of event per waiting step */,
				CounterListener.getCounter());
	}

	public void testCancelRoute() throws Exception {
		CounterListener.resetCouner();
		deployBundle(TEST_BUNDLE);
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		session.save();
		List<DocumentRoute> routes = service.getAvailableDocumentRouteModel(session);
		assertEquals(1, routes.size());
		DocumentRoute routeModel = routes.get(0);
		DocumentModel doc1 = createTestDocument("test1", session);
		session.save();
		service.lockDocumentRoute(route, session);
		route = service.validateRouteModel(route, session);
		service.unlockDocumentRoute(route, session);
		assertEquals("validated", route.getDocument().getCurrentLifeCycleState());
		assertEquals("validated", session.getChildren(route.getDocument().getRef()).get(0).getCurrentLifeCycleState());
		session.save();
		waitForAsyncExec();
		DocumentRoute routeInstance = service.createNewInstance(routeModel, doc1.getId(), session);
		assertNotNull(routeInstance);
		assertFalse(routeInstance.isDone());
		assertEquals(1, service.getDocumentRoutesForAttachedDocument(session, doc1.getId()).size());
		List<String> waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		routeInstance.cancel(session);
		assertTrue(routeInstance.isCanceled());
		DocumentModelList children = session.getChildren(routeInstance.getDocument().getRef());
		while (true) {
			for (DocumentModel doc : children) {
				assertTrue(doc.getCurrentLifeCycleState().equals("canceled"));
			}
			children = new DocumentModelListImpl();
			for (DocumentModel doc : children) {
				children.addAll(session.getChildren(doc.getRef()));
			}
			if (children.isEmpty()) {
				break;
			}
		}
	}

	public void testDocumentRouteWithStepBack() throws Exception {
		CounterListener.resetCouner();
		WaitingStepRuntimePersister.resetAll();
		deployBundle(TEST_BUNDLE);
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		session.save();
		List<DocumentRoute> routes = service.getAvailableDocumentRouteModel(session);
		assertEquals(1, routes.size());
		DocumentRoute routeModel = routes.get(0);
		DocumentModel doc1 = createTestDocument("test1", session);
		session.save();
		service.lockDocumentRoute(route, session);
		route = service.validateRouteModel(route, session);
		service.unlockDocumentRoute(route, session);
		assertEquals("validated", route.getDocument().getCurrentLifeCycleState());
		assertEquals("validated", session.getChildren(route.getDocument().getRef()).get(0).getCurrentLifeCycleState());
		session.save();
		waitForAsyncExec();
		DocumentRoute routeInstance = service.createNewInstance(routeModel, doc1.getId(), session);
		assertNotNull(routeInstance);
		assertFalse(routeInstance.isDone());
		assertEquals(1, service.getDocumentRoutesForAttachedDocument(session, doc1.getId()).size());
		List<String> waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		String firstStepId = waiting.get(0);
		// run first step
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		// undo second step
		String secondStepId = waiting.get(0);
		DocumentRouteStep step = WaitingStepRuntimePersister.getStep(secondStepId, session);
		assertTrue(step.canUndoStep(session));
		step = step.undo(session);
		assertTrue(step.isReady());
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		// restart route
		routeInstance.run(session);
		// undo second and first step
		DocumentRouteStep firstStep = WaitingStepRuntimePersister.getStep(firstStepId, session);
		DocumentRouteStep secondStep = WaitingStepRuntimePersister.getStep(secondStepId, session);
		secondStep = secondStep.undo(session);
		firstStep = firstStep.undo(session);
		assertTrue(secondStep.isReady());
		assertTrue(firstStep.isReady());
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		// restart route
		routeInstance.run(session);
		// run first step
		WaitingStepRuntimePersister.resumeStep(firstStepId, session);
		// run second step
		WaitingStepRuntimePersister.resumeStep(secondStepId, session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(2, waiting.size());
		// run third (parallel) step
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		assertFalse(routeInstance.isDone());
		// run fourth (parallel) step
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		assertEquals(0, waiting.size());
		routeInstance = session.getDocument(routeInstance.getDocument().getRef()).getAdapter(DocumentRoute.class);
		assertTrue(routeInstance.isDone());
		assertFalse(routeInstance.canUndoStep(session));
	}

	public void testDocumentRouteWithWaitStateAndSecurity() throws Exception {
		// bob create the route and validate it
		CounterListener.resetCouner();
		WaitingStepRuntimePersister.resetAll();
		closeSession();
		session = openSessionAs("bob");
		deployBundle(TEST_BUNDLE);
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		session.save();
		List<DocumentRoute> routes = service.getAvailableDocumentRouteModel(session);
		assertEquals(1, routes.size());
		DocumentRoute routeModel = routes.get(0);
		DocumentModel doc1 = createTestDocument("test1", session);
		session.save();
		service.lockDocumentRoute(route, session);
		route = service.validateRouteModel(route, session);
		service.unlockDocumentRoute(route, session);
		assertEquals("validated", route.getDocument().getCurrentLifeCycleState());
		assertEquals("validated", session.getChildren(route.getDocument().getRef()).get(0).getCurrentLifeCycleState());
		session.save();
		waitForAsyncExec();
		DocumentRoute routeInstance = service.createNewInstance(routeModel, doc1.getId(), session);
		closeSession();
		openSession();
		session.saveDocument(routeInstance.getDocument());
		session.save();

		closeSession();
		// jack checks he can't do anything on it
		session = openSessionAs("jack");
		assertFalse(routeInstance.canValidateStep(session));
		List<String> waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		boolean exception = false;
		try {// jacks fails to resume the step
			WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
			fail();
		} catch (Exception e) {
			exception = true;
		}
		assertTrue(exception);
		closeSession();
		// bob finishes the route
		session = openSessionAs("bob");
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(2, waiting.size());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		waiting = WaitingStepRuntimePersister.getRunningStepIds();
		assertEquals(1, waiting.size());
		assertFalse(routeInstance.isDone());
		WaitingStepRuntimePersister.resumeStep(waiting.get(0), session);
		assertEquals(0, waiting.size());
		routeInstance = session.getDocument(routeInstance.getDocument().getRef()).getAdapter(DocumentRoute.class);
		assertTrue(routeInstance.isDone());
		assertEquals(6/* route */+ 4 /* number of steps */* 3 /* number of event per waiting step */,
				CounterListener.getCounter());
	}

	public void testGetAvailableDocumentRouteModel() throws ClientException {
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		session.save();
		List<DocumentRoute> routes = service.getAvailableDocumentRouteModel(session);
		assertEquals(1, routes.size());
	}

	public void testRouteModel() throws ClientException {
		DocumentModel folder = createDocumentModel(session, "TestFolder", "Folder", "/");
		session.save();
		assertNotNull(folder);
		setPermissionToUser(folder, "jdoe", SecurityConstants.WRITE);
		DocumentModel route = createDocumentRouteModel(session, ROUTE1, folder.getPathAsString());
		session.save();
		assertNotNull(route);
		service.lockDocumentRoute(route.getAdapter(DocumentRoute.class), session);
		route = service.validateRouteModel(route.getAdapter(DocumentRoute.class), session).getDocument();
		session.save();
		service.unlockDocumentRoute(route.getAdapter(DocumentRoute.class), session);
		route = session.getDocument(route.getRef());
		assertEquals("validated", route.getCurrentLifeCycleState());
		closeSession();
		session = openSessionAs("jdoe");
		assertFalse(session.hasPermission(route.getRef(), SecurityConstants.WRITE));
		assertTrue(session.hasPermission(route.getRef(), SecurityConstants.READ));
	}

	public void testGetRouteElements() throws ClientException {
		DocumentRoute route = createDocumentRoute(session, ROUTE1);
		assertNotNull(route);
		List<DocumentRouteTableElement> elements = service.getRouteElements(route, session);
		assertNotNull(elements);
		assertEquals(4, elements.size());
		for (DocumentRouteTableElement element : elements) {
			assertEquals(1, element.getRouteMaxDepth());
		}
		assertEquals(1, elements.get(2).getFirstChildFolders().size());
		assertEquals(0, elements.get(3).getFirstChildFolders().size());
		assertEquals(2, elements.get(2).getFirstChildFolders().get(0).getTotalChildCount());
		assertEquals(4, elements.get(0).getRouteTable().getTotalChildCount());
	}

	protected void setPermissionToUser(DocumentModel doc, String username, String... perms) throws ClientException {
		ACP acp = doc.getACP();
		if (acp == null) {
			acp = new ACPImpl();
		}
		UserEntry userEntry = new UserEntryImpl(username);
		for (String perm : perms) {
			userEntry.addPrivilege(perm, true, false);
		}
		acp.setRules("test", new UserEntry[] { userEntry });
		doc.setACP(acp, true);
		session.save();
	}

	protected void waitForAsyncExec() {
		Framework.getLocalService(EventService.class).waitForAsyncCompletion();
	}
}
