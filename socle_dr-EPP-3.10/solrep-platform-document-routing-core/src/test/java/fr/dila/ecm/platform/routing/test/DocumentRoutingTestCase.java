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

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.ecm.platform.content.template.service.ContentTemplateService;
import org.nuxeo.runtime.api.Framework;

import fr.dila.ecm.platform.routing.api.DocumentRoute;
import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ecm.platform.routing.api.DocumentRoutingService;
import fr.dila.ecm.platform.routing.core.api.DocumentRoutingEngineService;

/**
 * @author arussel
 * 
 */
public class DocumentRoutingTestCase extends SQLRepositoryTestCase {
	public static final String				ROOT_PATH		= "/";

	public static final String				WORKSPACES_PATH	= "/default-domain/workspaces";

	public static final String				TEST_BUNDLE		= "fr.dila.ecm.platform.routing.core.test";

	protected DocumentRoutingEngineService	engineService;

	protected DocumentRoutingService		service;

	public static final String				ROUTE1			= "route1";

	@Override
	protected void deployRepositoryContrib() throws Exception {
		super.deployRepositoryContrib();
		// deploy and test content template
		deployBundle("org.nuxeo.ecm.platform.content.template");
		deployBundle("org.nuxeo.ecm.automation.core");
		deployBundle("org.nuxeo.ecm.directory");
		deployBundle("org.nuxeo.ecm.platform.usermanager");
		deployBundle("org.nuxeo.ecm.directory.types.contrib");
		deployBundle("org.nuxeo.ecm.directory.sql");
		deployBundle("org.nuxeo.ecm.platform.userworkspace.core");
		deployBundle("org.nuxeo.ecm.platform.userworkspace.types");
		deployContrib(TEST_BUNDLE, "OSGI-INF/test-sql-directories-contrib.xml");
		deployBundle(TestConstants.CORE_BUNDLE);
		CounterListener.resetCouner();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		openSession();
		DocumentModel root = session.getRootDocument();
		ACP acpRoot = root.getACP();
		ACL aclRoot = acpRoot.getOrCreateACL("local");
		aclRoot.add(new ACE("Administrator", SecurityConstants.READ_WRITE, true));
		session.setACP(root.getRef(), acpRoot, true);
		session.saveDocument(root);
		session.save();
		ContentTemplateService ctService = Framework.getService(ContentTemplateService.class);
		ctService.executeFactoryForType(root);
		assertEquals(3, session.getChildren(session.getChildren(root.getRef()).get(0).getRef()).size());
		DocumentModel workspaces = session.getDocument(new PathRef(WORKSPACES_PATH));
		assertNotNull(workspaces);
		ACP acp = workspaces.getACP();
		ACL acl = acp.getOrCreateACL("local");
		acl.add(new ACE("bob", SecurityConstants.READ_WRITE, true));
		session.setACP(workspaces.getRef(), acp, true);
		session.saveDocument(workspaces);
		session.save();
		// test our services
		engineService = Framework.getService(DocumentRoutingEngineService.class);
		service = Framework.getService(DocumentRoutingService.class);
	}

	public void testServices() throws Exception {
		assertNotNull(engineService);
		assertNotNull(service);
	}

	public DocumentModel createDocumentRouteModel(CoreSession session, String name, String path) throws ClientException {
		DocumentModel route = createDocumentModel(session, name, DocumentRoutingConstants.DOCUMENT_ROUTE_DOCUMENT_TYPE,
				path);
		createDocumentModel(session, "step1", DocumentRoutingConstants.STEP_DOCUMENT_TYPE, route.getPathAsString());
		createDocumentModel(session, "step2", DocumentRoutingConstants.STEP_DOCUMENT_TYPE, route.getPathAsString());
		DocumentModel parallelFolder1 = createDocumentModel(session, "parallel1",
				DocumentRoutingConstants.STEP_FOLDER_DOCUMENT_TYPE, route.getPathAsString());
		parallelFolder1.setPropertyValue(DocumentRoutingConstants.EXECUTION_TYPE_PROPERTY_NAME,
				DocumentRoutingConstants.ExecutionTypeValues.parallel.name());
		session.saveDocument(parallelFolder1);
		createDocumentModel(session, "step31", DocumentRoutingConstants.STEP_DOCUMENT_TYPE,
				parallelFolder1.getPathAsString());
		createDocumentModel(session, "step32", DocumentRoutingConstants.STEP_DOCUMENT_TYPE,
				parallelFolder1.getPathAsString());
		session.save();
		return route;
	}

	public DocumentModel createDocumentModel(CoreSession session, String name, String type, String path)
			throws ClientException {
		DocumentModel route1 = session.createDocumentModel(path, name, type);
		route1.setPropertyValue(DocumentRoutingConstants.TITLE_PROPERTY_NAME, name);
		return session.createDocument(route1);
	}

	public DocumentRoute createDocumentRoute(CoreSession session, String name) throws ClientException {
		DocumentModel model = createDocumentRouteModel(session, name, WORKSPACES_PATH);
		return model.getAdapter(DocumentRoute.class);
	}

	protected DocumentModel createTestDocument(String name, CoreSession session) throws ClientException {
		return createDocumentModel(session, name, "Note", WORKSPACES_PATH);
	}

}
