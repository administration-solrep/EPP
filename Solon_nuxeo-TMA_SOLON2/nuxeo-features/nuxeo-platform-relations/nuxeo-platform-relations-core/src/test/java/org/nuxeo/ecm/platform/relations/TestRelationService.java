/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id: TestRelationService.java 22121 2007-07-06 16:33:15Z gracinet $
 */

package org.nuxeo.ecm.platform.relations;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.platform.relations.api.Graph;
import org.nuxeo.ecm.platform.relations.api.QNameResource;
import org.nuxeo.ecm.platform.relations.api.Resource;
import org.nuxeo.ecm.platform.relations.api.impl.QNameResourceImpl;
import org.nuxeo.ecm.platform.relations.services.RelationService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

public class TestRelationService extends NXRuntimeTestCase {

    private RelationService service;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.nuxeo.ecm.relations.tests",
                "nxrelations-test-bundle.xml");
        service = (RelationService) Framework.getRuntime().getComponent(
                RelationService.NAME);
    }

    public void testGetGraphByType() {
        assertNotNull(service.getGraphByType("dummygraph"));
        // no implementation
        assertNull(service.getGraphByType("unexistentgraph"));
        // no such graph type
        assertNull(service.getGraphByType("foo"));
    }

    public void testGetGraphTypes() {
        List<String> types = service.getGraphTypes();
        assertSame(2, types.size());
        assertTrue(types.contains("dummygraph"));
        assertTrue(types.contains("unexistentgraph"));
        assertFalse(types.contains("foo"));
    }

    public void testGetGraphByNameOk() throws Exception {
        Graph graph = service.getGraphByName("myrelations");
        assertNotNull(graph);
        assertEquals(graph.getClass(), DummyGraphType.class);
        DummyGraphType realGraph = (DummyGraphType) graph;
        assertEquals("myrelations", realGraph.name);
        assertEquals("sql", realGraph.backend);
        assertEquals("localhost", realGraph.host);
        assertEquals("8080", realGraph.port);
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        namespaces.put("dc", "http://purl.org/dc/elements/1.1/");
        namespaces.put("nxtest", "http://nuxeo/org/nxrelations/test/");
        assertEquals(namespaces, realGraph.namespaces);
    }

    public void testGetGraphByNameNoImpl() throws Exception {
        try {
            service.getGraphByName("unexistentgraph");
            fail("Should have raised a RuntimeException");
        } catch (RuntimeException e) {
        }
    }

    public void testGetGraphByNameUnexistent() throws Exception {
        try {
            service.getGraphByName("foo");
            fail("Should have raised a RuntimeException");
        } catch (RuntimeException e) {
        }
    }

    public void testGetResourceOK() throws Exception {
        Serializable resourceLike = new DummyResourceLike("test");
        Resource resource = service.getResource(
                "http://nuxeo.org/nxrelations/test/", resourceLike, null);
        assertNotNull(resource);
        assertTrue(resource.isQNameResource());
        QNameResource qnameres = (QNameResource) resource;
        assertEquals("http://nuxeo.org/nxrelations/test/",
                qnameres.getNamespace());
        assertEquals("test", qnameres.getLocalName());
    }

    public void testGetAllResourcesFor() throws Exception {
        Serializable o = new DummyResourceLike("test");
        Set<Resource> resources = service.getAllResources(o, null);
        assertNotNull(resources);
        assertEquals(2, resources.size());

        // Check local names and extract namespaces
        Set<String> nameSpaces = new HashSet<String>();
        for (Resource res : resources) {
            assertTrue(res instanceof QNameResource);
            QNameResource qn = (QNameResource) res;
            assertEquals("test", qn.getLocalName());
            nameSpaces.add(qn.getNamespace());
        }

        HashSet<String> expectedNameSpaces = new HashSet<String>();
        expectedNameSpaces.addAll(Arrays.asList(
                "http://nuxeo.org/nxrelations/test2/",
                "http://nuxeo.org/nxrelations/test/"));

        assertEquals(expectedNameSpaces, nameSpaces);
    }

    public void testGetResourceNoImpl() throws Exception {
        Serializable resourceLike = new DummyResourceLike("test");
        Resource resource = service.getResource(
                "http://nuxeo.org/nxrelations/test-dummy/", resourceLike, null);
        assertNull(resource);
    }

    public void testGetResourceUnexistent() throws Exception {
        Serializable resourceLike = new DummyResourceLike("test");
        Resource resource = service.getResource(
                "http://nuxeo.org/nxrelations/unexistent/", resourceLike, null);
        assertNull(resource);
    }

    public void testGetResourceRepresentationOK() throws Exception {
        Resource resource = new QNameResourceImpl(
                "http://nuxeo.org/nxrelations/test/", "test");
        Object object = service.getResourceRepresentation(
                "http://nuxeo.org/nxrelations/test/", resource, null);
        assertNotNull(object);
        assertTrue(object instanceof DummyResourceLike);
        assertEquals("test", ((DummyResourceLike) object).getId());
    }

    public void testGetResourceRepresentationNoImpl() throws Exception {
        Resource resource = new QNameResourceImpl(
                "http://nuxeo.org/nxrelations/test-dummy/", "test");
        Object object = service.getResourceRepresentation(
                "http://nuxeo.org/nxrelations/test-dummy/", resource, null);
        assertNull(object);
    }

    public void testGetResourceRepresentationUnexistent() throws Exception {
        Resource resource = new QNameResourceImpl(
                "http://nuxeo.org/nxrelations/unexistent/", "test");
        Object object = service.getResourceRepresentation(
                "http://nuxeo.org/nxrelations/unexistent/", resource, null);
        assertNull(object);
    }

    public void testGetResourceOKWithContext() throws Exception {
        Serializable resourceLike = new DummyResourceLike("test");
        Resource resource = service.getResource(
                "http://nuxeo.org/nxrelations/test/", resourceLike, null);
        assertNotNull(resource);
        assertTrue(resource.isQNameResource());
        QNameResource qnameres = (QNameResource) resource;
        assertEquals("http://nuxeo.org/nxrelations/test/",
                qnameres.getNamespace());
        assertEquals("test", qnameres.getLocalName());
    }

    public void testGetAllResourcesForWithContext() throws Exception {
        Serializable o = new DummyResourceLike("test");
        Set<Resource> resources = service.getAllResources(o, null);
        assertNotNull(resources);
        assertEquals(2, resources.size());

        // Check local names and extract namespaces
        Set<String> nameSpaces = new HashSet<String>();
        for (Resource res : resources) {
            assertTrue(res instanceof QNameResource);
            QNameResource qn = (QNameResource) res;
            assertEquals("test", qn.getLocalName());
            nameSpaces.add(qn.getNamespace());
        }

        HashSet<String> expectedNameSpaces = new HashSet<String>();
        expectedNameSpaces.addAll(Arrays.asList(
                "http://nuxeo.org/nxrelations/test2/",
                "http://nuxeo.org/nxrelations/test/"));

        assertEquals(expectedNameSpaces, nameSpaces);
    }

    public void testGetResourceNoImplWithContext() throws Exception {
        Serializable resourceLike = new DummyResourceLike("test");
        Resource resource = service.getResource(
                "http://nuxeo.org/nxrelations/test-dummy/", resourceLike, null);
        assertNull(resource);
    }

    public void testGetResourceUnexistentWithContext() throws Exception {
        Serializable resourceLike = new DummyResourceLike("test");
        Resource resource = service.getResource(
                "http://nuxeo.org/nxrelations/unexistent/", resourceLike, null);
        assertNull(resource);
    }

    public void testGetResourceRepresentationOKWithContext() throws Exception {
        Resource resource = new QNameResourceImpl(
                "http://nuxeo.org/nxrelations/test/", "test");
        Object object = service.getResourceRepresentation(
                "http://nuxeo.org/nxrelations/test/", resource, null);
        assertNotNull(object);
        assertTrue(object instanceof DummyResourceLike);
        assertEquals("test", ((DummyResourceLike) object).getId());
    }

    public void testGetResourceRepresentationNoImplWithContext()
            throws Exception {
        Resource resource = new QNameResourceImpl(
                "http://nuxeo.org/nxrelations/test-dummy/", "test");
        Object object = service.getResourceRepresentation(
                "http://nuxeo.org/nxrelations/test-dummy/", resource, null);
        assertNull(object);
    }

    public void testGetResourceRepresentationUnexistentWithContext()
            throws Exception {
        Resource resource = new QNameResourceImpl(
                "http://nuxeo.org/nxrelations/unexistent/", "test");
        Object object = service.getResourceRepresentation(
                "http://nuxeo.org/nxrelations/unexistent/", resource, null);
        assertNull(object);
    }

}
