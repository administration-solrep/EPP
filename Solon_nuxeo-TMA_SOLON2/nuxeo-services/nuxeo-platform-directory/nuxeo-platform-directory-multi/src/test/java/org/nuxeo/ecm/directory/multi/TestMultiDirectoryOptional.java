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
 *     Florent Guillaume
 *
 * $Id: TestMultiDirectory.java 30378 2008-02-20 17:37:26Z gracinet $
 */

package org.nuxeo.ecm.directory.multi;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.ecm.directory.memory.MemoryDirectory;
import org.nuxeo.ecm.directory.memory.MemoryDirectoryFactory;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.NXRuntimeTestCase;

/**
 * @author Florent Guillaume
 * @author Anahide Tchertchian
 *
 */
public class TestMultiDirectoryOptional extends NXRuntimeTestCase {

    private static final String TEST_BUNDLE = "org.nuxeo.ecm.directory.multi.tests";

    DirectoryService directoryService;

    MemoryDirectoryFactory memoryDirectoryFactory;

    MemoryDirectory memdir1;

    MemoryDirectory memdir2;

    MemoryDirectory memdir3;

    MultiDirectory multiDir;

    MultiDirectorySession dir;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        // platform dependencies
        deployBundle("org.nuxeo.ecm.core.schema");
        deployBundle("org.nuxeo.ecm.directory");

        // Bundle to be tested
        deployBundle("org.nuxeo.ecm.directory.multi");

        // Config for the tested bundle
        deployContrib(TEST_BUNDLE, "schemas-config.xml");
        deployContrib(TEST_BUNDLE, "directories-optional-config.xml");

        // mem dir factory
        directoryService = Framework.getLocalService(DirectoryService.class);
        memoryDirectoryFactory = new MemoryDirectoryFactory();
        directoryService.registerDirectory("memdirs", memoryDirectoryFactory);

        // create and register mem directories
        Map<String, Object> e;

        // dir 1
        Set<String> schema1Set = new HashSet<String>(
                Arrays.asList("uid", "foo"));
        memdir1 = new MemoryDirectory("dir1", "schema1", schema1Set, "uid",
                "foo");
        memoryDirectoryFactory.registerDirectory(memdir1);

        Session dir1 = memdir1.getSession();
        // do not create entry with uid "1" in this optional dir
        // e = new HashMap<String, Object>();
        // e.put("uid", "1");
        // e.put("foo", "foo1");
        // dir1.createEntry(e);
        e = new HashMap<String, Object>();
        e.put("uid", "2");
        e.put("foo", "foo2");
        dir1.createEntry(e);
        e = new HashMap<String, Object>();
        e.put("uid", "baz");
        e.put("foo", "baz");
        dir1.createEntry(e);

        // dir 2
        Set<String> schema2Set = new HashSet<String>(Arrays.asList("id", "bar"));
        memdir2 = new MemoryDirectory("dir2", "schema2", schema2Set, "id", null);
        memoryDirectoryFactory.registerDirectory(memdir2);

        Session dir2 = memdir2.getSession();
        e = new HashMap<String, Object>();
        e.put("id", "1");
        e.put("bar", "bar1");
        dir2.createEntry(e);
        e = new HashMap<String, Object>();
        e.put("id", "2");
        e.put("bar", "bar2");
        dir2.createEntry(e);

        // dir 3
        Set<String> schema3Set = new HashSet<String>(Arrays.asList("uid",
                "thefoo", "thebar"));
        memdir3 = new MemoryDirectory("dir3", "schema3", schema3Set, "uid",
                "thefoo");
        memoryDirectoryFactory.registerDirectory(memdir3);

        Session dir3 = memdir3.getSession();
        e = new HashMap<String, Object>();
        e.put("uid", "3");
        e.put("thefoo", "foo3");
        e.put("thebar", "bar3");
        dir3.createEntry(e);
        e = new HashMap<String, Object>();
        e.put("uid", "4");
        e.put("thefoo", "foo4");
        e.put("thebar", "bar4");
        dir3.createEntry(e);

        // the multi directory
        multiDir = (MultiDirectory) directoryService.getDirectory("multiOptional");
        dir = (MultiDirectorySession) multiDir.getSession();
    }

    @Override
    public void tearDown() throws Exception {
        memoryDirectoryFactory.unregisterDirectory(memdir1);
        memoryDirectoryFactory.unregisterDirectory(memdir2);
        memoryDirectoryFactory.unregisterDirectory(memdir3);
        directoryService.unregisterDirectory("memdirs", memoryDirectoryFactory);
        super.tearDown();
    }

    public void testDirectoryOptionalInvalid() throws Exception {
        MultiDirectory multiDir = (MultiDirectory) directoryService.getDirectory("multiOptionalInvalid");
        MultiDirectorySession dir = (MultiDirectorySession) multiDir.getSession();
        assertNotNull(dir);

        try {
            // invalid config => will throw an exception
            dir.query(null);
            fail("Should have raised an DirectoryException");
        } catch (DirectoryException e) {
        }
    }

    public void testGetEntry() throws Exception {
        DocumentModel entry;
        entry = dir.getEntry("1");
        assertEquals("1", entry.getProperty("schema3", "uid"));
        assertEquals("defaultFooValue", entry.getProperty("schema3", "thefoo"));
        assertEquals("bar1", entry.getProperty("schema3", "thebar"));
        entry = dir.getEntry("2");
        assertEquals("2", entry.getProperty("schema3", "uid"));
        assertEquals("foo2", entry.getProperty("schema3", "thefoo"));
        assertEquals("bar2", entry.getProperty("schema3", "thebar"));
        entry = dir.getEntry("3");
        assertEquals("3", entry.getProperty("schema3", "uid"));
        assertEquals("foo3", entry.getProperty("schema3", "thefoo"));
        assertEquals("bar3", entry.getProperty("schema3", "thebar"));
        entry = dir.getEntry("4");
        assertEquals("4", entry.getProperty("schema3", "uid"));
        assertEquals("foo4", entry.getProperty("schema3", "thefoo"));
        assertEquals("bar4", entry.getProperty("schema3", "thebar"));
        entry = dir.getEntry("no-such-entry");
        assertNull(entry);
    }

    @SuppressWarnings("null")
    public void testGetEntries() throws Exception {
        DocumentModelList l;
        l = dir.getEntries();
        assertEquals(4, l.size());
        DocumentModel entry = null;
        for (DocumentModel e : l) {
            if (e.getId().equals("1")) {
                entry = e;
                break;
            }
        }
        assertNotNull(entry);
        assertEquals("defaultFooValue", entry.getProperty("schema3", "thefoo"));
    }

    public void testCreate() throws Exception {
        Session dir1 = memdir1.getSession();
        Session dir2 = memdir2.getSession();
        Session dir3 = memdir3.getSession();

        // multi-subdir create
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("uid", "5");
        map.put("thefoo", "foo5");
        map.put("thebar", "bar5");
        DocumentModel entry = dir.createEntry(map);
        assertEquals("5", entry.getProperty("schema3", "uid"));
        assertEquals("foo5", entry.getProperty("schema3", "thefoo"));
        assertEquals("bar5", entry.getProperty("schema3", "thebar"));
        boolean exceptionThrown = false;
        try {
            entry.getProperty("schema3", "xyz");
        } catch (ClientException ce) {
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown);
        // check underlying directories
        assertNotNull(dir1.getEntry("5"));
        assertEquals("foo5", dir1.getEntry("5").getProperty("schema1", "foo"));
        assertNotNull(dir2.getEntry("5"));
        assertEquals("bar5", dir2.getEntry("5").getProperty("schema2", "bar"));
        assertNull(dir3.getEntry("5"));

        // create another with colliding id
        map = new HashMap<String, Object>();
        map.put("uid", "5");
        try {
            entry = dir.createEntry(map);
            fail("Should raise an error, entry already exists");
        } catch (DirectoryException e) {
        }
    }

    public void testAuthenticate() throws Exception {
        // sub dirs
        Session dir1 = memdir1.getSession();
        Session dir3 = memdir3.getSession();
        // cannot authenticate using default value on sub directory directly
        assertFalse(dir1.authenticate("1", "defaultFooValue"));
        assertFalse(dir1.authenticate("1", "haha"));
        assertFalse(dir1.authenticate("3", "foo3"));
        assertFalse(dir3.authenticate("1", "defaultFooValue"));
        assertTrue(dir3.authenticate("3", "foo3"));
        assertFalse(dir3.authenticate("3", "haha"));
        // multi dir
        assertTrue(dir.authenticate("1", "defaultFooValue"));
        assertFalse(dir.authenticate("1", "lalala"));
        assertFalse(dir.authenticate("1", "haha"));
        assertTrue(dir.authenticate("3", "foo3"));
        assertFalse(dir.authenticate("3", "haha"));
    }

    public void testUpdateEntry() throws Exception {
        Session dir1 = memdir1.getSession();
        Session dir2 = memdir2.getSession();
        Session dir3 = memdir3.getSession();

        // multi-subdirs update
        DocumentModel e = dir.getEntry("1");
        assertEquals("defaultFooValue", e.getProperty("schema3", "thefoo"));
        assertEquals("bar1", e.getProperty("schema3", "thebar"));
        e.setProperty("schema3", "thefoo", "fffooo1");
        e.setProperty("schema3", "thebar", "babar1");
        dir.updateEntry(e);
        e = dir.getEntry("1");
        assertEquals("fffooo1", e.getProperty("schema3", "thefoo"));
        assertEquals("babar1", e.getProperty("schema3", "thebar"));

        // check underlying directories
        assertEquals("fffooo1",
                dir1.getEntry("1").getProperty("schema1", "foo"));
        assertEquals("babar1", dir2.getEntry("1").getProperty("schema2", "bar"));
        assertNull(dir3.getEntry("1"));

        // single subdir update
        e = dir.getEntry("3");
        assertEquals("foo3", e.getProperty("schema3", "thefoo"));
        assertEquals("bar3", e.getProperty("schema3", "thebar"));
        e.setProperty("schema3", "thefoo", "fffooo3");
        e.setProperty("schema3", "thebar", "babar3");
        dir.updateEntry(e);
        e = dir.getEntry("3");
        assertEquals("fffooo3", e.getProperty("schema3", "thefoo"));
        assertEquals("babar3", e.getProperty("schema3", "thebar"));

        // check underlying directories
        assertNull(dir1.getEntry("3"));
        assertNull(dir2.getEntry("3"));
        assertNotNull(dir3.getEntry("3"));
        assertEquals("fffooo3", dir3.getEntry("3").getProperty("schema3",
                "thefoo"));
        assertEquals("babar3", dir3.getEntry("3").getProperty("schema3",
                "thebar"));

        dir.getEntries();
    }

    public void testDeleteEntry() throws Exception {
        Session dir1 = memdir1.getSession();
        Session dir2 = memdir2.getSession();
        Session dir3 = memdir3.getSession();
        dir.deleteEntry("no-such-entry");
        assertEquals(4, dir.getEntries().size());
        assertEquals(2, dir1.getEntries().size());
        assertEquals(2, dir2.getEntries().size());
        assertEquals(2, dir3.getEntries().size());
        dir.deleteEntry("1");
        assertNull(dir.getEntry("1"));
        assertEquals(3, dir.getEntries().size());
        assertEquals(2, dir1.getEntries().size());
        assertEquals(1, dir2.getEntries().size());
        assertEquals(2, dir3.getEntries().size());
        dir.deleteEntry("3");
        assertNull(dir.getEntry("3"));
        assertEquals(2, dir.getEntries().size());
        assertEquals(2, dir1.getEntries().size());
        assertEquals(1, dir2.getEntries().size());
        assertEquals(1, dir3.getEntries().size());
    }

    public void testQuery() throws Exception {
        Map<String, Serializable> filter = new HashMap<String, Serializable>();
        DocumentModelList entries;
        DocumentModel e;

        // empty filter means everything (like getEntries)
        entries = dir.query(filter);
        assertNotNull(entries);
        assertEquals(4, entries.size());

        // no result
        filter.put("thefoo", "f");
        entries = dir.query(filter);
        assertEquals(0, entries.size());

        // query matching one source
        // source with two subdirs
        filter.put("thefoo", "defaultFooValue");
        entries = dir.query(filter);
        assertEquals(1, entries.size());
        e = entries.get(0);
        assertEquals("1", e.getId());
        assertEquals("bar1", e.getProperty("schema3", "thebar"));
        // simple source
        filter.put("thefoo", "foo3");
        entries = dir.query(filter);
        assertEquals(1, entries.size());
        e = entries.get(0);
        assertEquals("3", e.getId());
        assertEquals("bar3", e.getProperty("schema3", "thebar"));

        // query matching two subdirectories in one source
        filter.put("thefoo", "defaultFooValue");
        filter.put("thebar", "bar1");
        entries = dir.query(filter);
        assertEquals(1, entries.size());
        e = entries.get(0);
        assertEquals("1", e.getId());
        assertEquals("defaultFooValue", e.getProperty("schema3", "thefoo"));
        assertEquals("bar1", e.getProperty("schema3", "thebar"));

        // query not matching although each subdirectory in the source matches
        filter.put("thefoo", "defaultFooValue");
        filter.put("thebar", "bar2");
        entries = dir.query(filter);
        assertEquals(0, entries.size());

        // query matching two sources
        filter.clear();
        e = dir.getEntry("1");
        e.setProperty("schema3", "thefoo", "matchme");
        dir.updateEntry(e);
        e = dir.getEntry("3");
        e.setProperty("schema3", "thefoo", "matchme");
        dir.updateEntry(e);
        filter.put("thefoo", "matchme");
        entries = dir.query(filter);
        assertEquals(2, entries.size());
        e = entries.get(0);
        assertEquals("1", e.getId());
        assertEquals("bar1", e.getProperty("schema3", "thebar"));
        e = entries.get(1);
        assertEquals("3", e.getId());
        assertEquals("bar3", e.getProperty("schema3", "thebar"));
    }

    public void testGetProjection() throws Exception {
        Map<String, Serializable> filter = new HashMap<String, Serializable>();
        List<String> list;

        // empty filter means everything (like getEntries)
        list = dir.getProjection(filter, "uid");
        Collections.sort(list);
        assertEquals(Arrays.asList("1", "2", "3", "4"), list);
        list = dir.getProjection(filter, "thefoo");
        Collections.sort(list);
        assertEquals(Arrays.asList("defaultFooValue", "foo2", "foo3", "foo4"),
                list);
        list = dir.getProjection(filter, "thebar");
        Collections.sort(list);
        assertEquals(Arrays.asList("bar1", "bar2", "bar3", "bar4"), list);

        // XXX test projection on unknown column

        // no result
        filter.put("thefoo", "f");
        list = dir.getProjection(filter, "uid");
        assertEquals(0, list.size());
        list = dir.getProjection(filter, "thefoo");
        assertEquals(0, list.size());
        list = dir.getProjection(filter, "thebar");
        assertEquals(0, list.size());

        // query matching one source
        // source with two subdirs
        filter.put("thefoo", "defaultFooValue");
        list = dir.getProjection(filter, "uid");
        assertEquals(Arrays.asList("1"), list);
        list = dir.getProjection(filter, "thefoo");
        assertEquals(Arrays.asList("defaultFooValue"), list);
        list = dir.getProjection(filter, "thebar");
        assertEquals(Arrays.asList("bar1"), list);
        // simple source
        filter.put("thefoo", "foo3");
        list = dir.getProjection(filter, "uid");
        assertEquals(Arrays.asList("3"), list);
        list = dir.getProjection(filter, "thefoo");
        assertEquals(Arrays.asList("foo3"), list);
        list = dir.getProjection(filter, "thebar");
        assertEquals(Arrays.asList("bar3"), list);

        // query matching two subdirectories in one source
        filter.put("thefoo", "defaultFooValue");
        filter.put("thebar", "bar1");
        list = dir.getProjection(filter, "uid");
        assertEquals(Arrays.asList("1"), list);
        list = dir.getProjection(filter, "thefoo");
        assertEquals(Arrays.asList("defaultFooValue"), list);
        list = dir.getProjection(filter, "thebar");
        assertEquals(Arrays.asList("bar1"), list);

        // query not matching although each subdirectory in the source matches
        filter.put("thefoo", "defaultFooValue");
        filter.put("thebar", "bar2");
        list = dir.getProjection(filter, "uid");
        assertEquals(0, list.size());
        list = dir.getProjection(filter, "thefoo");
        assertEquals(0, list.size());
        list = dir.getProjection(filter, "thebar");
        assertEquals(0, list.size());

        // query matching two sources
        DocumentModel e;
        filter.clear();
        e = dir.getEntry("1");
        e.setProperty("schema3", "thefoo", "matchme");
        dir.updateEntry(e);
        e = dir.getEntry("3");
        e.setProperty("schema3", "thefoo", "matchme");
        dir.updateEntry(e);
        filter.put("thefoo", "matchme");
        list = dir.getProjection(filter, "uid");
        Collections.sort(list);
        assertEquals(Arrays.asList("1", "3"), list);
        list = dir.getProjection(filter, "thefoo");
        assertEquals(Arrays.asList("matchme", "matchme"), list);
        list = dir.getProjection(filter, "thebar");
        Collections.sort(list);
        assertEquals(Arrays.asList("bar1", "bar3"), list);
    }

    public void testCreateFromModel() throws Exception {
        String schema = "schema3";
        DocumentModel entry = BaseSession.createEntryModel(null, schema, null,
                null);
        entry.setProperty("schema3", "uid", "yo");

        assertNull(dir.getEntry("yo"));
        dir.createEntry(entry);
        assertNotNull(dir.getEntry("yo"));

        // create one with existing same id, must fail
        entry.setProperty("schema3", "uid", "1");
        try {
            entry = dir.createEntry(entry);
            fail("Should raise an error, entry already exists");
        } catch (DirectoryException e) {
        }
    }

    public void testHasEntry() throws Exception {
        assertTrue(dir.hasEntry("1"));
        assertFalse(dir.hasEntry("foo"));
    }

}
