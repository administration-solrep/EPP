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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

import fr.dila.ecm.platform.routing.api.DocumentRoutingConstants;
import fr.dila.ecm.platform.routing.api.DocumentRoutingPersister;
import fr.dila.ecm.platform.routing.core.impl.DocumentRoutingTreePersister;

/**
 * @author arussel
 *
 */
public class TestDocumentRoutingTreePersister extends DocumentRoutingTestCase {

    protected DocumentRoutingPersister persister;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        persister = new DocumentRoutingTreePersister();
    }

    public void testGetOrCreateRootOfDocumentRouteInstanceStructure()
            throws Exception {
        deployBundle(TEST_BUNDLE);
        DocumentModel doc = persister.getOrCreateRootOfDocumentRouteInstanceStructure(session);
        assertNotNull(doc);
        assertEquals(doc.getPathAsString(),
                TestConstants.DEFAULT_DOMAIN_DOCUMENT_ROUTE_INSTANCES_ROOT);
        session.save();
        closeSession();
        session = openSessionAs("members");
        assertFalse(session.hasPermission(doc.getRef(), SecurityConstants.READ));
    }

    public void testGetParentFolderForDocumentRouteInstance() {
        DocumentModel parent = persister.getParentFolderForDocumentRouteInstance(
                null, session);
        assertNotNull(parent);
        assertTrue(parent.getPathAsString().startsWith(
                TestConstants.DEFAULT_DOMAIN_DOCUMENT_ROUTE_INSTANCES_ROOT));
    }

    @SuppressWarnings("unchecked")
    public void testCreateDocumentRouteInstanceFromDocumentRouteModel()
            throws ClientException {
        DocumentModel model = createDocumentRouteModel(session,
                DocumentRoutingTestCase.ROUTE1, ROOT_PATH);
        List<String> docsId = new ArrayList<String>();
        docsId.add("1");
        model.setPropertyValue(
                DocumentRoutingConstants.ATTACHED_DOCUMENTS_PROPERTY_NAME,
                (Serializable) docsId);
        DocumentModel instance = persister.createDocumentRouteInstanceFromDocumentRouteModel(
                model, session);
        assertNotNull(instance);
        assertTrue(instance.getPathAsString().startsWith(
                TestConstants.DEFAULT_DOMAIN_DOCUMENT_ROUTE_INSTANCES_ROOT));
        docsId = (List<String>) instance.getPropertyValue(DocumentRoutingConstants.ATTACHED_DOCUMENTS_PROPERTY_NAME);
        assertEquals("1", docsId.get(0));
        closeSession();
        session = openSessionAs(DocumentRoutingConstants.ROUTE_MANAGERS_GROUP_NAME);
        assertEquals(3, session.getChildren(instance.getRef()).size());
    }

    public void testSaveDocumentRouteInstanceAsNewModel()
            throws ClientException {
        DocumentModel model = createDocumentRouteModel(session,
                DocumentRoutingTestCase.ROUTE1, ROOT_PATH);
        DocumentModel instance = persister.createDocumentRouteInstanceFromDocumentRouteModel(
                model, session);
        DocumentModel newModel = persister.saveDocumentRouteInstanceAsNewModel(
                instance, session.getRootDocument(), session);
        assertNotNull(newModel);
    }
}
