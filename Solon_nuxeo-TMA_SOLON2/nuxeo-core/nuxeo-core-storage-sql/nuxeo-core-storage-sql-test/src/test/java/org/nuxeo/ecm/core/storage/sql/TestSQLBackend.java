/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.storage.sql;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.XidImpl;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.query.QueryFilter;
import org.nuxeo.ecm.core.storage.PartialList;
import org.nuxeo.ecm.core.storage.StorageException;
import org.nuxeo.ecm.core.storage.sql.RepositoryDescriptor.FulltextIndexDescriptor;
import org.nuxeo.ecm.core.storage.sql.jdbc.JDBCMapper;

public class TestSQLBackend extends SQLBackendTestCase {

    private static final Log log = LogFactory.getLog(TestSQLBackend.class);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.nuxeo.ecm.core.storage.sql.test.tests",
                "OSGI-INF/test-backend-core-types-contrib.xml");
    }

    public void testRootNode() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        assertNotNull(root);
        assertEquals("", root.getName());
        assertEquals("/", session.getPath(root));
        assertEquals("Root",
                root.getSimpleProperty("ecm:primaryType").getString());
        try {
            root.getSimpleProperty("tst:title");
            fail("Property should not exist");
        } catch (IllegalArgumentException e) {
            // ok
        }
        session.save();
        session.close();
    }

    public void testSchemaWithLongName() throws Exception {
        deployContrib("org.nuxeo.ecm.core.storage.sql.test.tests",
                "OSGI-INF/test-schema-longname.xml");
        Session session = repository.getConnection();
        session.getRootNode();
    }

    protected int getChildrenHardSize(Session session) {
        return ((SessionImpl) session).context.hierContext.childrenRegularHard.size();
    }

    public void testChildren() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();

        try {
            session.addChildNode(root, "foo", null, "not_a_type", false);
            fail("Should not allow illegal type");
        } catch (IllegalArgumentException e) {
            // ok
        }

        // root doc /foo
        Node nodefoo = session.addChildNode(root, "foo", null, "TestDoc", false);
        assertEquals(root.getId(), session.getParentNode(nodefoo).getId());
        assertEquals("TestDoc", nodefoo.getPrimaryType());
        assertEquals("/foo", session.getPath(nodefoo));
        Node nodeabis = session.getChildNode(root, "foo", false);
        assertEquals(nodefoo.getId(), nodeabis.getId());

        // root is in hard because it has a created child
        assertEquals(1, getChildrenHardSize(session));

        // first child /foo/bar
        Node nodeb = session.addChildNode(nodefoo, "bar", null, "TestDoc",
                false);
        assertEquals("/foo/bar", session.getPath(nodeb));
        assertEquals(nodefoo.getId(), session.getParentNode(nodeb).getId());
        assertEquals(nodeb.getId(),
                session.getNodeByPath("/foo/bar", null).getId());

        // foo is now in hard as well
        assertEquals(2, getChildrenHardSize(session));

        session.save();
        // everything moved back to soft, therefore GCable
        assertEquals(0, getChildrenHardSize(session));
        session.close();

        /*
         * now from another session
         */
        session = repository.getConnection();
        root = session.getRootNode();
        nodefoo = session.getChildNode(root, "foo", false);
        assertEquals("foo", nodefoo.getName());
        assertEquals("/foo", session.getPath(nodefoo));

        // second child /foo/gee
        Node nodec = session.addChildNode(nodefoo, "gee", null, "TestDoc",
                false);
        assertEquals("/foo/gee", session.getPath(nodec));
        List<Node> children = session.getChildren(nodefoo, null, false);
        assertEquals(2, children.size());

        session.save();

        children = session.getChildren(nodefoo, null, false);
        assertEquals(2, children.size());

        // delete bar
        session.removeNode(nodefoo);
        // root in hard, has one removed child
        assertEquals(1, getChildrenHardSize(session));
        session.save();
        // everything moved back to soft
        assertEquals(0, getChildrenHardSize(session));
    }

    public void testChildrenRemoval() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Serializable fooId = session.addChildNode(root, "foo", null, "TestDoc",
                false).getId();
        Serializable barId = session.addChildNode(root, "bar", null, "TestDoc",
                false).getId();
        session.save();
        session.close();

        // from another session
        // get one and remove it
        session = repository.getConnection();
        root = session.getRootNode();
        session.getNodeById(fooId); // one known child
        Node nodebar = session.getNodeById(barId); // another
        session.removeNode(nodebar); // remove one known
        // check removal in Children cache
        nodebar = session.getChildNode(root, "bar", false);
        assertNull(nodebar);
        // the following gets a complete list but skips deleted ones
        List<Node> children = session.getChildren(root, null, false);
        assertEquals(1, children.size());
        session.save();
    }

    public void testChildrenRemoval2() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node foo = session.addChildNode(root, "foo", null, "TestDoc", false);
        session.removeNode(foo);
        List<Node> children = session.getChildren(root, null, false);
        assertEquals(0, children.size());
        session.save(); // important for the test
    }

    public void testChildrenRemoval3() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node foo = session.addChildNode(root, "foo", null, "TestDoc", false);
        session.addChildNode(foo, "bar", null, "TestDoc", false);
        session.removeNode(foo);
        List<Node> children = session.getChildren(root, null, false);
        assertEquals(0, children.size());
        session.save(); // important for the test
    }

    public void testRecursiveRemoval() throws Exception {
        int depth = DatabaseHelper.DATABASE.getRecursiveRemovalDepthLimit();
        if (depth == 0) {
            // no limit
            depth = 70;
        }
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = root;
        Serializable[] ids = new Serializable[depth];
        for (int i = 0; i < depth; i++) {
            node = session.addChildNode(node, String.valueOf(i), null,
                    "TestDoc", false);
            ids[i] = node.getId();
        }
        session.save(); // TODO shouldn't be needed
        // delete the second one
        session.removeNode(session.getNodeById(ids[1]));
        session.save();
        session.close();

        // check all children were really deleted recursively
        session = repository.getConnection();
        for (int i = 1; i < depth; i++) {
            assertNull(session.getNodeById(ids[i]));
        }
    }

    // same as above but without opening a new session
    public void testRecursiveRemoval2() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = root;
        int depth = 5;
        Serializable[] ids = new Serializable[depth];
        for (int i = 0; i < depth; i++) {
            node = session.addChildNode(node, String.valueOf(i), null,
                    "TestDoc", false);
            ids[i] = node.getId();
        }
        session.save();
        // delete the second one
        session.removeNode(session.getNodeById(ids[1]));
        session.save();

        // check all children were really deleted recursively
        for (int i = 1; i < depth; i++) {
            assertNull("" + i, session.getNodeById(ids[i]));
        }
    }

    public void testBasics() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node nodea = session.addChildNode(root, "foo", null, "TestDoc", false);

        nodea.setSimpleProperty("tst:title", "hello world");
        nodea.setSimpleProperty("tst:rate", Double.valueOf(1.5));
        nodea.setSimpleProperty("tst:count", Long.valueOf(123456789));
        Calendar cal = new GregorianCalendar(2008, Calendar.JULY, 14, 12, 34,
                56);
        nodea.setSimpleProperty("tst:created", cal);
        nodea.setCollectionProperty("tst:subjects", new String[] { "a", "b",
                "c" });
        nodea.setCollectionProperty("tst:tags", new String[] { "1", "2" });

        assertEquals("hello world",
                nodea.getSimpleProperty("tst:title").getString());
        assertEquals(Double.valueOf(1.5),
                nodea.getSimpleProperty("tst:rate").getValue());
        assertEquals(Long.valueOf(123456789),
                nodea.getSimpleProperty("tst:count").getValue());
        assertNotNull(nodea.getSimpleProperty("tst:created").getValue());
        String[] subjects = nodea.getCollectionProperty("tst:subjects").getStrings();
        String[] tags = nodea.getCollectionProperty("tst:tags").getStrings();
        assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(subjects));
        assertEquals(Arrays.asList("1", "2"), Arrays.asList(tags));

        session.save();

        // now modify a property and re-save
        nodea.setSimpleProperty("tst:title", "another");
        nodea.setSimpleProperty("tst:rate", Double.valueOf(3.14));
        nodea.setSimpleProperty("tst:count", Long.valueOf(1234567891234L));
        nodea.setCollectionProperty("tst:subjects", new String[] { "z", "c" });
        nodea.setCollectionProperty("tst:tags", new String[] { "3" });
        session.save();

        // again
        nodea.setSimpleProperty("tst:created", null);
        session.save();

        // check the logs to see that the following doesn't do anything because
        // the value is unchanged since the last save (UPDATE optimizations)
        nodea.setSimpleProperty("tst:title", "blah");
        nodea.setSimpleProperty("tst:title", "another");
        session.save();

        // now read from another session
        session.close();
        session = repository.getConnection();
        root = session.getRootNode();
        assertNotNull(root);
        nodea = session.getChildNode(root, "foo", false);
        assertEquals("another",
                nodea.getSimpleProperty("tst:title").getString());
        assertEquals(Double.valueOf(3.14),
                nodea.getSimpleProperty("tst:rate").getValue());
        assertEquals(Long.valueOf(1234567891234L),
                nodea.getSimpleProperty("tst:count").getValue());
        subjects = nodea.getCollectionProperty("tst:subjects").getStrings();
        tags = nodea.getCollectionProperty("tst:tags").getStrings();
        assertEquals(Arrays.asList("z", "c"), Arrays.asList(subjects));
        assertEquals(Arrays.asList("3"), Arrays.asList(tags));

        // delete the node
        // session.removeNode(nodea);
        // session.save();
    }

    // SPL broken since DC_CONTRIBUTORS remove
//    public void testBasicsUpgrade() throws Exception {
//        JDBCMapper.testProps.put(JDBCMapper.TEST_UPGRADE, Boolean.TRUE);
//        try {
//            testBasics();
//        } finally {
//            JDBCMapper.testProps.clear();
//        }
//    }

    public void testBigText() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node nodea = session.addChildNode(root, "foo", null, "TestDoc", false);

        StringBuilder buf = new StringBuilder(5000);
        for (int i = 0; i < 1000; i++) {
            buf.append(String.format("%-5d", Integer.valueOf(i)));
        }
        String bigtext = buf.toString();
        assertEquals(5000, bigtext.length());
        nodea.setSimpleProperty("tst:bignote", bigtext);
        nodea.setCollectionProperty("tst:bignotes", new String[] { bigtext });
        assertEquals(bigtext,
                nodea.getSimpleProperty("tst:bignote").getString());
        assertEquals(bigtext,
                nodea.getCollectionProperty("tst:bignotes").getStrings()[0]);
        session.save();

        // now read from another session
        session.close();
        session = repository.getConnection();
        root = session.getRootNode();
        assertNotNull(root);
        nodea = session.getChildNode(root, "foo", false);
        String readtext = nodea.getSimpleProperty("tst:bignote").getString();
        assertEquals(bigtext, readtext);
        String[] readtexts = nodea.getCollectionProperty("tst:bignotes").getStrings();
        assertEquals(bigtext, readtexts[0]);
    }

    public void testPropertiesSameName() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node nodea = session.addChildNode(root, "foo", null, "TestDoc", false);

        nodea.setSimpleProperty("tst:title", "hello world");
        assertEquals("hello world",
                nodea.getSimpleProperty("tst:title").getString());

        try {
            nodea.setSimpleProperty("tst2:title", "aha");
            fail("shouldn't allow setting property from foreign schema");
        } catch (Exception e) {
            // ok
        }

        session.save();
    }

    public void testBinary() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node nodea = session.addChildNode(root, "foo", null, "TestDoc", false);

        InputStream in = new ByteArrayInputStream("abc".getBytes("UTF-8"));
        Binary bin = session.getBinary(in);
        assertEquals(3, bin.getLength());
        assertEquals("900150983cd24fb0d6963f7d28e17f72", bin.getDigest());
        assertEquals("abc", readAllBytes(bin.getStream()));
        assertEquals("abc", readAllBytes(bin.getStream())); // readable twice
        nodea.setSimpleProperty("tst:bin", bin);
        session.save();
        session.close();

        // now read from another session
        session = repository.getConnection();
        root = session.getRootNode();
        nodea = session.getChildNode(root, "foo", false);
        SimpleProperty binProp = nodea.getSimpleProperty("tst:bin");
        assertNotNull(binProp);
        Serializable value = binProp.getValue();
        assertTrue(value instanceof Binary);
        bin = (Binary) value;
        in = bin.getStream();
        assertEquals(3, bin.getLength());
        assertEquals("900150983cd24fb0d6963f7d28e17f72", bin.getDigest());
        assertEquals("abc", readAllBytes(bin.getStream()));
        assertEquals("abc", readAllBytes(bin.getStream())); // readable twice

    }

    // assumes one read will read everything
    protected String readAllBytes(InputStream in) throws IOException {
        if (!(in instanceof BufferedInputStream)) {
            in = new BufferedInputStream(in);
        }
        int len = in.available();
        byte[] bytes = new byte[len];
        int read = in.read(bytes);
        assertEquals(len, read);
        assertEquals(-1, in.read()); // EOF
        return new String(bytes, "ISO-8859-1");
    }

    public void testACLs() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        CollectionProperty prop = root.getCollectionProperty(Model.ACL_PROP);
        assertNotNull(prop);
        assertEquals(3, prop.getValue().length); // root acls preexist
        ACLRow acl1 = new ACLRow(1, "test", true, "Write", "steve", null);
        ACLRow acl2 = new ACLRow(0, "test", true, "Read", null, "Members");
        prop.setValue(new ACLRow[] { acl1, acl2 });
        session.save();
        session.close();
        session = repository.getConnection();
        root = session.getRootNode();
        prop = root.getCollectionProperty(Model.ACL_PROP);
        ACLRow[] acls = (ACLRow[]) prop.getValue();
        assertEquals(2, acls.length);
        assertEquals("Members", acls[0].group);
        assertEquals("test", acls[0].name);
        assertEquals("steve", acls[1].user);
        assertEquals("test", acls[1].name);
    }

    public void XXX_TODO_testConcurrentModification() throws Exception {
        Session session1 = repository.getConnection();
        Node root1 = session1.getRootNode();
        Node node1 = session1.addChildNode(root1, "foo", null, "TestDoc", false);
        SimpleProperty title1 = node1.getSimpleProperty("tst:title");
        session1.save();

        Session session2 = repository.getConnection();
        Node root2 = session2.getRootNode();
        Node node2 = session2.getChildNode(root2, "foo", false);
        SimpleProperty title2 = node2.getSimpleProperty("tst:title");

        // change title1
        title1.setValue("yo");
        assertNull(title2.getString());
        // save session1 and queue its invalidations to others
        session1.save();
        // session2 has not saved (committed) yet, so still unmodified
        assertNull(title2.getString());
        session2.save();
        // after commit, invalidations have been processed
        assertEquals("yo", title2.getString());

        // written properties aren't shared
        title1.setValue("mama");
        title2.setValue("glop");
        session1.save();
        assertEquals("mama", title1.getString());
        assertEquals("glop", title2.getString());
        try {
            session2.save();
            fail("expected ConcurrentModificationException");
        } catch (ConcurrentModificationException e) {
            // expected
        }
    }

    public void testConcurrentNameCreation() throws Exception {
        // two docs with same name (possible at this low level)
        Session session1 = repository.getConnection();
        Node root1 = session1.getRootNode();
        Node foo1 = session1.addChildNode(root1, "foo", null, "TestDoc", false);
        session1.save();
        Session session2 = repository.getConnection();
        Node root2 = session2.getRootNode();
        Node foo2 = session2.addChildNode(root2, "foo", null, "TestDoc", false);
        session2.save();
        // on read we get one or the other, but no crash
        Session session3 = repository.getConnection();
        Node root3 = session3.getRootNode();
        Node foo3 = session3.getChildNode(root3, "foo", false);
        assertTrue(foo3.getId().equals(foo1.getId())
                || foo3.getId().equals(foo2.getId()));
        // try again, has been fixed (only one error in logs)
        Session session4 = repository.getConnection();
        Node root4 = session4.getRootNode();
        Node foo4 = session4.getChildNode(root4, "foo", false);
        assertEquals(foo3.getId(), foo4.getId());
    }

    public void TODOtestConcurrentUpdate() throws Exception {
        Session session1 = repository.getConnection();
        Node root1 = session1.getRootNode();
        Node node1 = session1.addChildNode(root1, "foo", null, "TestDoc", false);
        SimpleProperty title1 = node1.getSimpleProperty("tst:title");
        session1.save();

        Session session2 = repository.getConnection();
        Node root2 = session2.getRootNode();
        Node node2 = session2.getChildNode(root2, "foo", false);
        SimpleProperty title2 = node2.getSimpleProperty("tst:title");

        title1.setValue("mama");
        title2.setValue("glop");
        session1.save();
        assertEquals("mama", title1.getString());
        assertEquals("glop", title2.getString());
        session2.save(); // and notifies invalidations
        // in non-transaction mode, session1 has not processed its invalidations
        // yet, call save() to process them artificially
        session1.save();
        // session2 save wins
        assertEquals("glop", title1.getString());
        assertEquals("glop", title2.getString());
    }

    public void testCrossSessionChildrenInvalidationAdd() throws Exception {
        // in first session, create base folder
        Session session1 = repository.getConnection();
        Node root1 = session1.getRootNode();
        Node folder1 = session1.addChildNode(root1, "foo", null, "TestDoc",
                false);
        session1.save();

        // in second session, retrieve folder and check children
        Session session2 = repository.getConnection();
        Node root2 = session2.getRootNode();
        Node folder2 = session2.getChildNode(root2, "foo", false);
        session2.getChildren(folder2, null, false);

        // in first session, add document
        session1.addChildNode(folder1, "gee", null, "TestDoc", false);
        session1.save();

        // in second session, try to get document
        session2.save(); // process invalidations (non-transactional)
        Node doc2 = session2.getChildNode(folder2, "gee", false);
        assertNotNull(doc2);
    }

    public void testCrossSessionChildrenInvalidationRemove() throws Exception {
        // in first session, create base folder and doc
        Session session1 = repository.getConnection();
        Node root1 = session1.getRootNode();
        Node folder1 = session1.addChildNode(root1, "foo", null, "TestDoc",
                false);
        Node doc1 = session1.addChildNode(folder1, "gee", null, "TestDoc",
                false);
        session1.save();

        // in second session, retrieve folder and check children
        Session session2 = repository.getConnection();
        Node root2 = session2.getRootNode();
        Node folder2 = session2.getChildNode(root2, "foo", false);
        List<Node> children2 = session2.getChildren(folder2, null, false);
        assertEquals(1, children2.size());

        // in first session, remove child
        session1.removeNode(doc1);
        session1.save();

        // in second session, check no more children
        session2.save(); // process invalidations (non-transactional)
        children2 = session2.getChildren(folder2, null, false);
        assertEquals(0, children2.size());
    }

    public void testCrossSessionChildrenInvalidationMove() throws Exception {
        // in first session, create base folders and doc
        Session session1 = repository.getConnection();
        Node root1 = session1.getRootNode();
        Node foldera1 = session1.addChildNode(root1, "foo", null, "TestDoc",
                false);
        Node folderb1 = session1.addChildNode(root1, "bar", null, "TestDoc",
                false);
        Node doc1 = session1.addChildNode(foldera1, "gee", null, "TestDoc",
                false);
        session1.save();

        // in second session, retrieve folders and check children
        Session session2 = repository.getConnection();
        Node root2 = session2.getRootNode();
        Node foldera2 = session2.getChildNode(root2, "foo", false);
        List<Node> childrena2 = session2.getChildren(foldera2, null, false);
        assertEquals(1, childrena2.size());
        Node folderb2 = session2.getChildNode(root2, "bar", false);
        List<Node> childrenb2 = session2.getChildren(folderb2, null, false);
        assertEquals(0, childrenb2.size());

        // in first session, move between folders
        session1.move(doc1, folderb1, null);
        session1.save();

        // in second session, check children count
        session2.save(); // process invalidations (non-transactional)
        childrena2 = session2.getChildren(foldera2, null, false);
        assertEquals(0, childrena2.size());
        childrenb2 = session2.getChildren(folderb2, null, false);
        assertEquals(1, childrenb2.size());
    }

    public void testCrossSessionChildrenInvalidationCopy() throws Exception {
        // in first session, create base folders and doc
        Session session1 = repository.getConnection();
        Node root1 = session1.getRootNode();
        Node foldera1 = session1.addChildNode(root1, "foo", null, "TestDoc",
                false);
        Node folderb1 = session1.addChildNode(root1, "bar", null, "TestDoc",
                false);
        Node doc1 = session1.addChildNode(foldera1, "gee", null, "TestDoc",
                false);
        session1.save();

        // in second session, retrieve folders and check children
        Session session2 = repository.getConnection();
        Node root2 = session2.getRootNode();
        Node foldera2 = session2.getChildNode(root2, "foo", false);
        List<Node> childrena2 = session2.getChildren(foldera2, null, false);
        assertEquals(1, childrena2.size());
        Node folderb2 = session2.getChildNode(root2, "bar", false);
        List<Node> childrenb2 = session2.getChildren(folderb2, null, false);
        assertEquals(0, childrenb2.size());

        // in first session, copy between folders
        session1.copy(doc1, folderb1, null);
        session1.save();

        // in second session, check children count
        session2.save(); // process invalidations (non-transactional)
        childrena2 = session2.getChildren(foldera2, null, false);
        assertEquals(1, childrena2.size());
        childrenb2 = session2.getChildren(folderb2, null, false);
        assertEquals(1, childrenb2.size());
    }

    public void testClustering() throws Exception {
        if (this instanceof TestSQLBackendNet
                || this instanceof ITSQLBackendNet) {
            return;
        }
        if (!DatabaseHelper.DATABASE.supportsClustering()) {
            System.out.println("Skipping clustering test for unsupported database: "
                    + DatabaseHelper.DATABASE.getClass().getName());
            return;
        }

        repository.close();
        // get two clustered repositories
        long DELAY = 500; // ms
        repository = newRepository(DELAY, false);
        repository2 = newRepository(DELAY, false);

        Session session1 = repository.getConnection();
        // session1 creates root node and does a save
        // which resets invalidation timeout
        Session session2 = repository2.getConnection();
        session2.save(); // save resets invalidations timeout

        // in session1, create base folder
        Node root1 = session1.getRootNode();
        Node folder1 = session1.addChildNode(root1, "foo", null, "TestDoc",
                false);
        SimpleProperty title1 = folder1.getSimpleProperty("tst:title");
        session1.save();

        // in session2, retrieve folder and check children
        Node root2 = session2.getRootNode();
        Node folder2 = session2.getChildNode(root2, "foo", false);
        SimpleProperty title2 = folder2.getSimpleProperty("tst:title");
        session2.getChildren(folder2, null, false);

        // in session1, add document
        session1.addChildNode(folder1, "gee", null, "TestDoc", false);
        session1.save();

        // in session2, try to get document
        // immediate check, invalidation delay means not done yet
        session2.save();
        Node doc2 = session2.getChildNode(folder2, "gee", false);
        assertNull(doc2);
        Thread.sleep(DELAY + 1); // wait invalidation delay
        session2.save(); // process invalidations (non-transactional)
        doc2 = session2.getChildNode(folder2, "gee", false);
        assertNotNull(doc2);

        // in session1 change title
        title1.setValue("yo");
        assertNull(title2.getString());
        // save session1 (queues its invalidations to others)
        session1.save();
        // session2 has not saved (committed) yet, so still unmodified
        assertNull(title2.getString());
        // immediate check, invalidation delay means not done yet
        session2.save();
        assertNull(title2.getString());
        Thread.sleep(DELAY + 1); // wait invalidation delay
        session2.save();
        // after commit, invalidations have been processed
        assertEquals("yo", title2.getString());

        // written properties aren't shared
        title1.setValue("mama");
        title2.setValue("glop");
        session1.save();
        assertEquals("mama", title1.getString());
        assertEquals("glop", title2.getString());
        session2.save(); // and notifies invalidations
        // in non-transaction mode, session1 has not processed
        // its invalidations yet, call save() to process them artificially
        Thread.sleep(DELAY + 1); // wait invalidation delay
        session1.save();
        // session2 save wins
        assertEquals("glop", title1.getString());
        assertEquals("glop", title2.getString());
    }

    public void testRollback() throws Exception {
        Session session = repository.getConnection();
        XAResource xaresource = ((SessionImpl) session).getXAResource();
        Node root = session.getRootNode();
        Node nodea = session.addChildNode(root, "foo", null, "TestDoc", false);
        nodea.setSimpleProperty("tst:title", "old");
        assertEquals("old", nodea.getSimpleProperty("tst:title").getString());
        session.save();

        /*
         * rollback before save (underlying XAResource saw no updates)
         */
        Xid xid = new XidImpl("1");
        xaresource.start(xid, XAResource.TMNOFLAGS);
        nodea = session.getNodeByPath("/foo", null);
        nodea.setSimpleProperty("tst:title", "new");
        xaresource.end(xid, XAResource.TMSUCCESS);
        xaresource.prepare(xid);
        xaresource.rollback(xid);
        nodea = session.getNodeByPath("/foo", null);
        assertEquals("old", nodea.getSimpleProperty("tst:title").getString());

        /*
         * rollback after save (underlying XAResource does a rollback too)
         */
        xid = new XidImpl("2");
        xaresource.start(xid, XAResource.TMNOFLAGS);
        nodea = session.getNodeByPath("/foo", null);
        nodea.setSimpleProperty("tst:title", "new");
        session.save();
        xaresource.end(xid, XAResource.TMSUCCESS);
        xaresource.prepare(xid);
        xaresource.rollback(xid);
        nodea = session.getNodeByPath("/foo", null);
        assertEquals("old", nodea.getSimpleProperty("tst:title").getString());
    }

    public void testSaveOnCommit() throws Exception {
        Session session = repository.getConnection(); // init
        session.save();

        XAResource xaresource = ((SessionImpl) session).getXAResource();

        // first transaction
        Xid xid = new XidImpl("1");
        xaresource.start(xid, XAResource.TMNOFLAGS);
        Node root = session.getRootNode();
        assertNotNull(root);
        session.addChildNode(root, "foo", null, "TestDoc", false);
        // let end do an implicit save
        xaresource.end(xid, XAResource.TMSUCCESS);
        xaresource.prepare(xid);
        xaresource.commit(xid, false);

        // should have saved, clearing caches should be harmless
        ((SessionImpl) session).clearCaches();

        // second transaction
        xid = new XidImpl("2");
        xaresource.start(xid, XAResource.TMNOFLAGS);
        Node foo = session.getNodeByPath("/foo", null);
        assertNotNull(foo);
        xaresource.end(xid, XAResource.TMSUCCESS);
        int outcome = xaresource.prepare(xid);
        if (outcome == XAResource.XA_OK) {
            // Derby doesn't allow rollback if prepare returned XA_RDONLY
            xaresource.rollback(xid);
        }
    }

    protected List<String> getNames(List<Node> nodes) {
        List<String> names = new ArrayList<String>(nodes.size());
        for (Node node : nodes) {
            names.add(node.getName());
        }
        return names;
    }

    public void testOrdered() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node fold = session.addChildNode(root, "fold", null, "OFolder", false);
        Node doca = session.addChildNode(fold, "a", null, "TestDoc", false);
        Node docb = session.addChildNode(fold, "b", null, "TestDoc", false);
        Node docc = session.addChildNode(fold, "c", null, "TestDoc", false);
        Node docd = session.addChildNode(fold, "d", null, "TestDoc", false);
        Node doce = session.addChildNode(fold, "e", null, "TestDoc", false);
        session.save();
        // check order
        List<Node> children = session.getChildren(fold, null, false);
        assertEquals(Arrays.asList("a", "b", "c", "d", "e"), getNames(children));

        // reorder self
        session.orderBefore(fold, docb, docb);
        children = session.getChildren(fold, null, false);
        assertEquals(Arrays.asList("a", "b", "c", "d", "e"), getNames(children));
        // reorder up
        session.orderBefore(fold, docd, docb);
        children = session.getChildren(fold, null, false);
        assertEquals(Arrays.asList("a", "d", "b", "c", "e"), getNames(children));
        // reorder first
        session.orderBefore(fold, docc, doca);
        children = session.getChildren(fold, null, false);
        assertEquals(Arrays.asList("c", "a", "d", "b", "e"), getNames(children));
        // reorder last
        session.orderBefore(fold, docd, null);
        children = session.getChildren(fold, null, false);
        assertEquals(Arrays.asList("c", "a", "b", "e", "d"), getNames(children));
        // reorder down
        session.orderBefore(fold, doca, docd);
        children = session.getChildren(fold, null, false);
        assertEquals(Arrays.asList("c", "b", "e", "a", "d"), getNames(children));
    }

    public void testMove() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node foldera = session.addChildNode(root, "folder_a", null, "TestDoc",
                false);
        Serializable prevId = foldera.getId();
        Node nodea = session.addChildNode(foldera, "node_a", null, "TestDoc",
                false);
        Node nodeac = session.addChildNode(nodea, "node_a_complex", null,
                "TestDoc", true);
        assertEquals("/folder_a/node_a/node_a_complex", session.getPath(nodeac));
        Node folderb = session.addChildNode(root, "folder_b", null, "TestDoc",
                false);
        session.addChildNode(folderb, "node_b", null, "TestDoc", false);
        session.save();

        // cannot move under itself
        try {
            session.move(foldera, nodea, "abc");
            fail();
        } catch (StorageException e) {
            // ok
        }

        // cannot move to name that already exists
        try {
            session.move(foldera, folderb, "node_b");
            fail();
        } catch (StorageException e) {
            // ok
        }

        // do normal move
        Node node = session.move(foldera, folderb, "yo");
        assertEquals(prevId, node.getId());
        assertEquals("yo", node.getName());
        assertEquals("/folder_b/yo", session.getPath(node));
        assertEquals("/folder_b/yo/node_a/node_a_complex",
                session.getPath(nodeac));

        // move higher is allowed
        node = session.move(node, root, "underr");
        assertEquals(prevId, node.getId());
        assertEquals("underr", node.getName());
        assertEquals("/underr", session.getPath(node));
        assertEquals("/underr/node_a/node_a_complex", session.getPath(nodeac));

        session.save();
    }

    /*
     * Test that lots of moves don't break internal datastructures.
     */
    public void testMoveMany() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        ArrayList<Node> nodes = new ArrayList<Node>();
        nodes.add(root);
        Random rnd = new Random(123456);
        List<String[]> graph = new ArrayList<String[]>();
        for (int i = 0; i < 200; i++) {
            // create a node under a random node
            Node parent = nodes.get((int) Math.floor(rnd.nextFloat()
                    * nodes.size()));
            Node child = session.addChildNode(parent, "child" + i, null,
                    "TestDoc", false);
            nodes.add(child);
            // update graph
            addEdge(graph, parent.getId().toString(), child.getId().toString());
            if ((i % 5) == 0) {
                // move a random node under a random parent
                int ip, ic;
                Node p, c;
                String pid, cid;
                do {
                    ip = (int) Math.floor(rnd.nextFloat() * nodes.size());
                    ic = (int) Math.floor(rnd.nextFloat() * nodes.size());
                    p = nodes.get(ip);
                    c = nodes.get(ic);
                    pid = p.getId().toString();
                    cid = c.getId().toString();
                    if (isUnder(graph, cid, pid)) {
                        // check we have an error for this move
                        try {
                            session.move(c, p, c.getName());
                            fail("shouldn't be able to move");
                        } catch (Exception e) {
                            // ok
                        }
                        ic = 0; // try again
                    }
                } while (ic == 0 || ip == ic);
                String oldpid = c.getParentId().toString();
                session.move(c, p, c.getName());
                removeEdge(graph, oldpid, cid);
                addEdge(graph, pid, cid);
            }
        }
        session.save();

        // dumpGraph(graph);
        // dumpDescendants(buildDescendants(graph, root.getId().toString()));
    }

    private static void addEdge(List<String[]> graph, String p, String c) {
        graph.add(new String[] { p, c });
    }

    private static void removeEdge(List<String[]> graph, String p, String c) {
        for (String[] edge : graph) {
            if (edge[0].equals(p) && edge[1].equals(c)) {
                graph.remove(edge);
                return;
            }
        }
        throw new IllegalArgumentException(String.format("No edge %s -> %s", p,
                c));
    }

    private static boolean isUnder(List<String[]> graph, String p, String c) {
        if (p.equals(c)) {
            return true;
        }
        Set<String> under = new HashSet<String>();
        under.add(p);
        int oldSize = 0;
        // inefficient algorithm but for tests it's ok
        while (under.size() != oldSize) {
            oldSize = under.size();
            Set<String> add = new HashSet<String>();
            for (String n : under) {
                for (String[] edge : graph) {
                    if (edge[0].equals(n)) {
                        String cc = edge[1];
                        if (c.equals(cc)) {
                            return true;
                        }
                        add.add(cc);
                    }
                }
            }
            under.addAll(add);
        }
        return false;
    }

    private static Map<String, Set<String>> buildDescendants(
            List<String[]> graph, String root) {
        Map<String, Set<String>> ancestors = new HashMap<String, Set<String>>();
        Map<String, Set<String>> descendants = new HashMap<String, Set<String>>();
        // create all sets, for clearer code later
        for (String[] edge : graph) {
            for (String n : edge) {
                if (!ancestors.containsKey(n)) {
                    ancestors.put(n, new HashSet<String>());
                }
                if (!descendants.containsKey(n)) {
                    descendants.put(n, new HashSet<String>());
                }
            }
        }
        // traverse from root
        LinkedList<String> todo = new LinkedList<String>();
        todo.add(root);
        do {
            String p = todo.removeFirst();
            for (String[] edge : graph) {
                if (edge[0].equals(p)) {
                    // found a child
                    String c = edge[1];
                    todo.add(c);
                    // child's ancestors
                    Set<String> cans = ancestors.get(c);
                    cans.addAll(ancestors.get(p));
                    cans.add(p);
                    // all ancestors have it as descendant
                    for (String pp : cans) {
                        descendants.get(pp).add(c);
                    }
                }
            }
        } while (!todo.isEmpty());
        return descendants;
    }

    // dump in dot format, for graphviz
    private static void dumpGraph(List<String[]> graph) {
        for (String[] edge : graph) {
            System.out.println("\t" + edge[0] + " -> " + edge[1] + ";");
        }
    }

    private static void dumpDescendants(Map<String, Set<String>> descendants) {
        for (Entry<String, Set<String>> e : descendants.entrySet()) {
            String p = e.getKey();
            for (String c : e.getValue()) {
                System.out.println(String.format("%s %s", p, c));
            }
        }
    }

    public void testCopy() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node foldera = session.addChildNode(root, "folder_a", null, "TestDoc",
                false);
        Serializable prevFolderaId = foldera.getId();
        Node nodea = session.addChildNode(foldera, "node_a", null, "TestDoc",
                false);
        Serializable prevNodeaId = nodea.getId();
        Node nodeac = session.addChildNode(nodea, "node_a_complex", null,
                "TestDoc", true);
        Node nodead = session.addChildNode(nodea, "node_a_duo", null, "duo",
                true);
        Serializable prevNodeacId = nodeac.getId();
        nodea.setSimpleProperty("tst:title", "hello world");
        nodea.setCollectionProperty("tst:subjects", new String[] { "a", "b",
                "c" });
        nodea.setSimpleProperty("ecm:lifeCycleState", "foostate"); // misc table
        assertEquals("/folder_a/node_a/node_a_complex", session.getPath(nodeac));
        Node folderb = session.addChildNode(root, "folder_b", null, "TestDoc",
                false);
        session.addChildNode(folderb, "node_b", null, "TestDoc", false);
        Node folderc = session.addChildNode(root, "folder_c", null, "TestDoc",
                false);
        session.save();

        // cannot copy under itself
        try {
            session.copy(foldera, nodea, "abc");
            fail();
        } catch (StorageException e) {
            // ok
        }

        // cannot copy to name that already exists
        try {
            session.copy(foldera, folderb, "node_b");
            fail();
        } catch (StorageException e) {
            // ok
        }

        // do normal copy
        Node foldera2 = session.copy(foldera, folderb, "yo");
        // one children was known (complete), check it was invalidated
        Node n = session.getChildNode(folderb, "yo", false);
        assertNotNull(n);
        assertEquals(foldera2.getId(), n.getId());
        assertNotSame(prevFolderaId, foldera2.getId());
        assertEquals("yo", foldera2.getName());
        assertEquals("/folder_b/yo", session.getPath(foldera2));
        Node nodea2 = session.getChildNode(foldera2, "node_a", false);
        assertNotSame(prevNodeaId, nodea2.getId());
        assertEquals("hello world",
                nodea2.getSimpleProperty("tst:title").getString());
        assertEquals("foostate",
                nodea2.getSimpleProperty("ecm:lifeCycleState").getString());
        // check that the collection copy is different from the original
        String[] subjectsa2 = nodea2.getCollectionProperty("tst:subjects").getStrings();
        nodea.setCollectionProperty("tst:subjects", new String[] { "foo" });
        String[] subjectsa = nodea.getCollectionProperty("tst:subjects").getStrings();
        assertEquals(Arrays.asList("foo"), Arrays.asList(subjectsa));
        assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(subjectsa2));
        // complex children are there too
        Node nodeac2 = session.getChildNode(nodea2, "node_a_complex", true);
        assertNotNull(nodeac2);
        assertNotSame(prevNodeacId, nodeac2.getId());

        // copy to a folder that we know has no children
        // checks proper Children invalidation
        session.copy(nodea, folderc, "hm");
        Node nodea3 = session.getChildNode(folderc, "hm", false);
        assertNotNull(nodea3);

        session.save();
    }

    public void testVersioning() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node foldera = session.addChildNode(root, "folder_a", null, "TestDoc",
                false);
        Node nodea = session.addChildNode(foldera, "node_a", null, "TestDoc",
                false);
        Node nodeac = session.addChildNode(nodea, "node_a_complex", null,
                "TestDoc", true);
        nodea.setSimpleProperty("tst:title", "hello world");
        nodea.setCollectionProperty("tst:subjects", new String[] { "a", "b",
                "c" });
        // nodea.setSingleProperty("ecm:majorVersion", Long.valueOf(1));
        // nodea.setSingleProperty("ecm:minorVersion", Long.valueOf(0));
        session.save();
        Serializable nodeacId = nodeac.getId();

        /*
         * Check in.
         */
        Node version = session.checkIn(nodea, "foolab", "bardesc");
        assertNotNull(version);
        assertNotSame(version.getId(), nodea.getId());
        // doc is now checked in
        assertEquals(Boolean.TRUE,
                nodea.getSimpleProperty("ecm:isCheckedIn").getValue());
        assertEquals(version.getId(),
                nodea.getSimpleProperty("ecm:baseVersion").getString());
        // the version info
        assertEquals("node_a", version.getName()); // keeps name
        assertNull(session.getParentNode(version));
        assertEquals("hello world",
                version.getSimpleProperty("tst:title").getString());
        assertNull(version.getSimpleProperty("ecm:baseVersion").getString());
        assertNull(version.getSimpleProperty("ecm:isCheckedIn").getValue());
        assertEquals(nodea.getId(),
                version.getSimpleProperty("ecm:versionableId").getString());
        // assertEquals(Long.valueOf(1), version.getSimpleProperty(
        // "ecm:majorVersion").getLong());
        // assertEquals(Long.valueOf(0), version.getSimpleProperty(
        // "ecm:minorVersion").getLong());
        assertNotNull(version.getSimpleProperty("ecm:versionCreated").getValue());
        assertEquals("foolab",
                version.getSimpleProperty("ecm:versionLabel").getValue());
        assertEquals("bardesc",
                version.getSimpleProperty("ecm:versionDescription").getValue());
        // the version child (complex prop)
        Node nodeacv = session.getChildNode(version, "node_a_complex", true);
        assertNotNull(nodeacv);
        assertNotSame(nodeacId, nodeacv.getId());

        /*
         * Check out.
         */
        session.checkOut(nodea);
        assertEquals(Boolean.FALSE,
                nodea.getSimpleProperty("ecm:isCheckedIn").getValue());
        assertEquals(version.getId(),
                nodea.getSimpleProperty("ecm:baseVersion").getString());
        nodea.setSimpleProperty("tst:title", "blorp");
        nodea.setCollectionProperty("tst:subjects", new String[] { "x", "y" });
        Node nodeac2 = session.getChildNode(nodea, "node_a_complex", true);
        nodeac2.setSimpleProperty("tst:title", "comp");
        session.save();

        /*
         * Restore.
         */
        session.restore(nodea, version);
        assertEquals("hello world",
                nodea.getSimpleProperty("tst:title").getString());
        assertEquals(
                Arrays.asList("a", "b", "c"),
                Arrays.asList(nodea.getCollectionProperty("tst:subjects").getStrings()));
        Node nodeac3 = session.getChildNode(nodea, "node_a_complex", true);
        assertNotNull(nodeac3);
        SimpleProperty sp = nodeac3.getSimpleProperty("tst:title");
        assertNotNull(sp);
        assertNull(sp.getString());
    }

    public void testProxies() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node foldera = session.addChildNode(root, "foldera", null, "TestDoc",
                false);
        Node nodea = session.addChildNode(foldera, "nodea", null, "TestDoc",
                false);
        Node folderb = session.addChildNode(root, "folderb", null, "TestDoc",
                false);

        /*
         * Check in.
         */
        Node version = session.checkIn(nodea, "v1", "");
        assertNotNull(version);
        session.checkOut(nodea);
        Node version2 = session.checkIn(nodea, "v2", "");
        /*
         * Make proxy (by hand).
         */
        Node proxy = session.addProxy(version.getId(), nodea.getId(), folderb,
                "proxy1", null);
        session.save();
        assertNotSame(version.getId(), proxy.getId());
        assertNotSame(nodea.getId(), proxy.getId());
        assertEquals("/folderb/proxy1", session.getPath(proxy));
        assertEquals(folderb.getId(), session.getParentNode(proxy).getId());
        /*
         * Searches.
         */
        // from versionable
        List<Node> proxies = session.getProxies(nodea, null);
        assertEquals(1, proxies.size());
        assertEquals(proxy, proxies.get(0));
        proxies = session.getProxies(nodea, folderb);
        assertEquals(1, proxies.size());
        proxies = session.getProxies(nodea, foldera);
        assertEquals(0, proxies.size());
        // from version
        proxies = session.getProxies(version, null);
        assertEquals(1, proxies.size());
        assertEquals(proxy, proxies.get(0));
        proxies = session.getProxies(version, folderb);
        assertEquals(1, proxies.size());
        proxies = session.getProxies(version, foldera);
        assertEquals(0, proxies.size());
        // from other version (which has no proxy)
        proxies = session.getProxies(version2, null);
        assertEquals(0, proxies.size());
        // from proxy
        proxies = session.getProxies(proxy, null);
        assertEquals(1, proxies.size());
        assertEquals(proxy, proxies.get(0));
        proxies = session.getProxies(proxy, folderb);
        assertEquals(1, proxies.size());
        proxies = session.getProxies(proxy, foldera);
        assertEquals(0, proxies.size());
    }

    public void testDelete() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node nodea = session.addChildNode(root, "foo", null, "TestDoc", false);
        nodea.setSimpleProperty("tst:title", "foo");
        Node nodeb = session.addChildNode(nodea, "bar", null, "TestDoc", false);
        nodeb.setSimpleProperty("tst:title", "bar");
        Node nodec = session.addChildNode(nodeb, "gee", null, "TestDoc", false);
        nodec.setSimpleProperty("tst:title", "gee");
        session.save();
        // delete foo after having modified some of the deleted children
        nodea.setSimpleProperty("tst:title", "foo2");
        nodeb.setSimpleProperty("tst:title", "bar2");
        nodec.setSimpleProperty("tst:title", "gee2");
        session.removeNode(nodea);
        session.save();
    }

    public void testBulkFetch() throws Exception {
        Session session = repository.getConnection();

        // check computed prefetch info
        Model model = ((SessionImpl) session).getModel();
        assertEquals(
                new HashSet<String>(Arrays.asList("testschema", "tst:subjects",
                        "tst:bignotes", "tst:tags", //
                        "acls", "versions", "misc")),
                model.getTypePrefetchedFragments("TestDoc"));
        assertEquals(new HashSet<String>(Arrays.asList("testschema2", //
                "acls", "versions", "misc")),
                model.getTypePrefetchedFragments("TestDoc2"));
        assertEquals(new HashSet<String>(Arrays.asList("tst:subjects", //
                "acls", "versions", "misc")),
                model.getTypePrefetchedFragments("TestDoc3"));

        Node root = session.getRootNode();

        Node node1 = session.addChildNode(root, "n1", null, "TestDoc", false);
        node1.setSimpleProperty("tst:title", "one");
        node1.setCollectionProperty("tst:subjects", new String[] { "a", "b" });
        node1.setCollectionProperty("tst:tags", new String[] { "foo" });
        node1.setSimpleProperty("tst:count", Long.valueOf(123));
        node1.setSimpleProperty("tst:rate", Double.valueOf(3.14));
        CollectionProperty aclProp = node1.getCollectionProperty(Model.ACL_PROP);
        ACLRow acl = new ACLRow(1, "test", true, "Write", "steve", null);
        aclProp.setValue(new ACLRow[] { acl });

        Node node2 = session.addChildNode(root, "n2", null, "TestDoc2", false);
        node2.setSimpleProperty("tst2:title", "two");
        aclProp = node2.getCollectionProperty(Model.ACL_PROP);
        acl = new ACLRow(0, "test", true, "Read", null, "Members");
        aclProp.setValue(new ACLRow[] { acl });

        session.save();
        session.close();
        session = repository.getConnection();

        List<Node> nodes = session.getNodesByIds(Arrays.asList(node1.getId(),
                node2.getId()), null);

        assertEquals(2, nodes.size());
        node1 = nodes.get(0);
        node2 = nodes.get(1);
        if (node1.getName().equals("n2")) {
            // swap
            Node n = node1;
            node1 = node2;
            node2 = n;
        }
        assertEquals(
                Arrays.asList("a", "b"),
                Arrays.asList(node1.getCollectionProperty("tst:subjects").getStrings()));
        assertEquals(
                Arrays.asList("foo"),
                Arrays.asList(node1.getCollectionProperty("tst:tags").getStrings()));
        aclProp = node1.getCollectionProperty(Model.ACL_PROP);
        ACLRow[] acls = (ACLRow[]) aclProp.getValue();
        assertEquals(1, acls.length);
        assertEquals("Write", acls[0].permission);

        assertEquals("two", node2.getSimpleProperty("tst2:title").getString());
        aclProp = node2.getCollectionProperty(Model.ACL_PROP);
        acls = (ACLRow[]) aclProp.getValue();
        assertEquals(1, acls.length);
        assertEquals("Read", acls[0].permission);
    }

    public void testBulkFetchProxies() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();

        Node node0 = session.addChildNode(root, "n0", null, "TestDoc", false);
        node0.setSimpleProperty("tst:title", "zero");
        Node node1 = session.addChildNode(root, "n1", null, "TestDoc", false);
        node1.setSimpleProperty("tst:title", "one");
        Node node2 = session.addChildNode(root, "n2", null, "TestDoc", false);
        node2.setSimpleProperty("tst:title", "two");
        Node version1 = session.checkIn(node1, "v1", "");
        Node version2 = session.checkIn(node2, "v2", "");
        Node proxy1 = session.addProxy(version1.getId(), node1.getId(), root,
                "proxy1", null);
        Node proxy2 = session.addProxy(version2.getId(), node2.getId(), root,
                "proxy2", null);
        session.save();

        session.close();
        session = repository.getConnection();

        @SuppressWarnings("unused")
        List<Node> nodes = session.getNodesByIds(Arrays.asList(node0.getId(),
                proxy1.getId(), proxy2.getId()), null);

        // check logs by hand to see that data fragments are bulk fetched
    }

    public void testBulkFetchMany() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node1 = session.addChildNode(root, "n1", null, "TestDoc", false);
        Node node2 = session.addChildNode(root, "n2", null, "TestDoc2", false);
        session.save();

        // another session
        session.close();
        session = repository.getConnection();

        List<Serializable> ids = new ArrayList<Serializable>();
        ids.add(node2.getId());
        ids.add(node1.getId());
        int size = 2000; // > dialect.getMaximumArgsForIn()
        for (int i = 0; i < size; i++) {
            ids.add("nosuchid-" + i);
        }
        List<Node> nodes = session.getNodesByIds(ids, null);
        assertEquals(2 + size, nodes.size());
        assertEquals(node2.getId(), nodes.get(0).getId());
        assertEquals(node1.getId(), nodes.get(1).getId());
        for (int i = 0; i < size; i++) {
            assertNull(nodes.get(2 + i));
        }
    }

    public void testFulltext() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        node.setSimpleProperty("tst:title", "hello world");
        node = session.addChildNode(root, "bar", null, "TestDoc", false);
        node.setSimpleProperty("tst:title", "barbar");
        session.save();
        DatabaseHelper.DATABASE.sleepForFulltext();

        // Note that MySQL is buggy and doesn't return answers on "hello", doh!
        PartialList<Serializable> res;
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:fulltext = 'world'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());
        res = session.query(
                "SELECT * FROM TestDoc WHERE NOT (ecm:fulltext = 'world')",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());
        // Test multiple fulltext
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:fulltext = 'world' OR  ecm:fulltext = 'barbar'",
                QueryFilter.EMPTY, false);
        assertEquals(2, res.list.size());
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:fulltext = 'world' AND  ecm:fulltext = 'barbar'",
                QueryFilter.EMPTY, false);
        assertEquals(0, res.list.size());
    }

    public void testFulltextDisabled() throws Exception {
        if (this instanceof TestSQLBackendNet
                || this instanceof ITSQLBackendNet) {
            return;
        }
        // reconfigure repository with fulltext disabled
        repository.close();
        boolean fulltextDisabled = true;
        repository = newRepository(-1, fulltextDisabled);

        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        node.setSimpleProperty("tst:title", "hello world");
        session.save();
        try {
            session.query("SELECT * FROM TestDoc WHERE ecm:fulltext = 'world'",
                    QueryFilter.EMPTY, false);
            fail("Expected fulltext to be disabled and throw an exception");
        } catch (StorageException e) {
            if (!e.getMessage().contains("disabled")) {
                fail("Expected fulltext to be disabled, got: " + e);
            }
            // ok
        }
    }

    public void testFulltextUpgrade() throws Exception {
        if (!DatabaseHelper.DATABASE.supportsMultipleFulltextIndexes()) {
            System.out.println("Skipping multi-fulltext test for unsupported database: "
                    + DatabaseHelper.DATABASE.getClass().getName());
            return;
        }

        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        node.setSimpleProperty("tst:title", "hello world");
        session.save();
        repository.close();

        // reopen repository on same database,
        // with custom indexing config
        RepositoryDescriptor descriptor = newDescriptor(-1, false);
        List<FulltextIndexDescriptor> ftis = new LinkedList<FulltextIndexDescriptor>();
        descriptor.fulltextIndexes = ftis;
        FulltextIndexDescriptor fti = new FulltextIndexDescriptor(); // default
        ftis.add(fti);
        fti = new FulltextIndexDescriptor();
        fti.name = "title";
        fti.fields = Collections.singleton("tst:title");
        ftis.add(fti);
        repository = new RepositoryImpl(descriptor);

        // check new values can be written
        session = repository.getConnection();
        root = session.getRootNode();
        node = session.getChildNode(root, "foo", false);
        assertNotNull(node);
        node.setSimpleProperty("tst:title", "one two three testing");
        session.save();
        DatabaseHelper.DATABASE.sleepForFulltext();

        // check fulltext search works
        PartialList<Serializable> res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:fulltext = 'testing'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());

        if (!DatabaseHelper.DATABASE.supportsMultipleFulltextIndexes()) {
            System.out.println("Skipping multi-fulltext test for unsupported database: "
                    + DatabaseHelper.DATABASE.getClass().getName());
            return;
        }
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:fulltext.tst:title = 'testing'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());
    }

    public void testRelation() throws Exception {
        PartialList<Serializable> res;

        Session session = repository.getConnection();
        Node rel1 = session.addChildNode(null, "rel", null, "Relation", false);
        rel1.setSimpleProperty("relation:source", "123");
        rel1.setSimpleProperty("relation:target", "456");
        Node rel2 = session.addChildNode(null, "rel", null, "Relation2", false);
        rel2.setSimpleProperty("relation:source", "123");
        rel2.setSimpleProperty("relation:target", "789");
        rel2.setSimpleProperty("tst:title", "yo");
        session.save();

        res = session.query(
                "SELECT * FROM Document WHERE relation:source = '123'",
                QueryFilter.EMPTY, false);
        assertEquals(0, res.list.size()); // Relation is not a Document
        res = session.query(
                "SELECT * FROM Relation WHERE relation:source = '123'",
                QueryFilter.EMPTY, false);
        assertEquals(2, res.list.size());
        res = session.query("SELECT * FROM Relation2", QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());
        res = session.query("SELECT * FROM Relation2 WHERE tst:title = 'yo'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());

        // remove
        session.removeNode(rel1);
        session.save();
        res = session.query(
                "SELECT * FROM Relation WHERE relation:source = '123'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());
    }

    public void testTagsUpgrade() throws Exception {
        if (this instanceof TestSQLBackendNet
                || this instanceof ITSQLBackendNet) {
            return;
        }
        JDBCMapper.testProps.put(JDBCMapper.TEST_UPGRADE, Boolean.TRUE);
        try {
            Session session = repository.getConnection();

            PartialList<Serializable> res;

            res = session.query("SELECT * FROM Tag WHERE ecm:isProxy = 0",
                    QueryFilter.EMPTY, false);
            assertEquals(2, res.list.size());
            String tagId = "11111111-2222-3333-4444-555555555555";
            Serializable tagId1 = res.list.get(0);
            Serializable tagId2 = res.list.get(1);
            if (tagId.equals(tagId2)) {
                // swap
                Serializable t = tagId1;
                tagId1 = tagId2;
                tagId2 = t;
            }
            assertEquals(tagId, tagId1);
            Node tag1 = session.getNodeById(tagId1);
            assertEquals("mytag",
                    tag1.getSimpleProperty("tag:label").getString());
            assertEquals("mytag",
                    tag1.getSimpleProperty("dc:title").getString());
            assertEquals("Administrator",
                    tag1.getSimpleProperty("dc:creator").getString());
            assertEquals("mytag", tag1.getName());

            Node tag2 = session.getNodeById(tagId2);
            assertEquals("othertag",
                    tag2.getSimpleProperty("tag:label").getString());
            assertEquals("othertag",
                    tag2.getSimpleProperty("dc:title").getString());
            assertEquals("Administrator",
                    tag2.getSimpleProperty("dc:creator").getString());
            assertEquals("othertag", tag2.getName());

            res = session.query("SELECT * FROM Tagging", QueryFilter.EMPTY,
                    false);
            assertEquals(1, res.list.size());
            Serializable taggingId = res.list.get(0);
            Node tagging = session.getNodeById(taggingId);
            assertEquals("dddddddd-dddd-dddd-dddd-dddddddddddd",
                    tagging.getSimpleProperty("relation:source").getValue());
            assertEquals(tagId,
                    tagging.getSimpleProperty("relation:target").getValue());
            assertEquals("mytag",
                    tagging.getSimpleProperty("dc:title").getString());
            assertEquals("Administrator",
                    tagging.getSimpleProperty("dc:creator").getString());
            assertEquals("mytag", tagging.getName());
            assertNotNull(tagging.getSimpleProperty("dc:created").getValue());

            // hidden tags root is gone
            Node tags = session.getNodeByPath("/tags", null);
            assertNull(tags);
        } finally {
            JDBCMapper.testProps.clear();
        }
    }

    protected static boolean isLatestVersion(Node node) throws Exception {
        Boolean b = (Boolean) node.getSimpleProperty(
                Model.VERSION_IS_LATEST_PROP).getValue();
        return b.booleanValue();
    }

    protected static boolean isLatestMajorVersion(Node node) throws Exception {
        Boolean b = (Boolean) node.getSimpleProperty(
                Model.VERSION_IS_LATEST_MAJOR_PROP).getValue();
        return b.booleanValue();
    }

    protected static String getVersionLabel(Node node) throws Exception {
        return (String) node.getSimpleProperty(Model.VERSION_LABEL_PROP).getValue();
    }

    public void testVersionsUpgrade() throws Exception {
        if (this instanceof TestSQLBackendNet
                || this instanceof ITSQLBackendNet) {
            return;
        }
        JDBCMapper.testProps.put(JDBCMapper.TEST_UPGRADE, Boolean.TRUE);
        JDBCMapper.testProps.put(JDBCMapper.TEST_UPGRADE_VERSIONS, Boolean.TRUE);
        try {
            Node ver;
            Session session = repository.getConnection();

            // check normal doc is not a version
            Node doc = session.getNodeById("dddddddd-dddd-dddd-dddd-dddddddddddd");
            assertFalse(doc.isVersion());

            // v 1
            ver = session.getNodeById("11111111-0000-0000-2222-000000000000");
            assertTrue(ver.isVersion());
            assertFalse(isLatestVersion(ver));
            assertFalse(isLatestMajorVersion(ver));
            assertEquals("0.1", getVersionLabel(ver));

            // v 2
            ver = session.getNodeById("11111111-0000-0000-2222-000000000001");
            assertTrue(ver.isVersion());
            assertFalse(isLatestVersion(ver));
            assertTrue(isLatestMajorVersion(ver));
            assertEquals("1.0", getVersionLabel(ver));

            // v 3
            ver = session.getNodeById("11111111-0000-0000-2222-000000000002");
            assertTrue(ver.isVersion());
            assertTrue(isLatestVersion(ver));
            assertFalse(isLatestMajorVersion(ver));
            assertEquals("1.1", getVersionLabel(ver));

            // v 4 other doc
            ver = session.getNodeById("11111111-0000-0000-3333-000000000001");
            assertTrue(ver.isVersion());
            assertTrue(isLatestVersion(ver));
            assertTrue(isLatestMajorVersion(ver));
            assertEquals("1.0", getVersionLabel(ver));

        } finally {
            JDBCMapper.testProps.clear();
        }
    }

    // SPL : remove since dc_contributors removal
//    public void testLastContributorUpgrade() throws StorageException {
//        if (this instanceof TestSQLBackendNet
//                || this instanceof ITSQLBackendNet) {
//            return;
//        }
//        JDBCMapper.testProps.put(JDBCMapper.TEST_UPGRADE, Boolean.TRUE);
//        JDBCMapper.testProps.put(JDBCMapper.TEST_UPGRADE_LAST_CONTRIBUTOR,
//                Boolean.TRUE);
//
//        try {
//            Node ver;
//            Session session = repository.getConnection();
//
//            ver = session.getNodeById("12121212-dddd-dddd-dddd-000000000000");
//            assertNotNull(ver);
//            assertEquals("mynddoc", ver.getName());
//            assertEquals("Administrator",
//                    ver.getSimpleProperty("dc:creator").getString());
//            assertEquals("Administrator",
//                    ver.getSimpleProperty("dc:lastContributor").getString());
//
//            ver = session.getNodeById("12121212-dddd-dddd-dddd-000000000001");
//            assertNotNull(ver);
//            assertEquals("myrddoc", ver.getName());
//            assertEquals("Administrator",
//                    ver.getSimpleProperty("dc:creator").getString());
//            assertEquals("FakeOne",
//                    ver.getSimpleProperty("dc:lastContributor").getString());
//        } finally {
//            JDBCMapper.testProps.clear();
//        }
//    }

    public void testMixinAPI() throws Exception {
        PartialList<Serializable> res;
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);

        assertFalse(node.hasMixinType("Aged"));
        assertFalse(node.hasMixinType("Orderable"));
        assertEquals(0, node.getMixinTypes().length);
        session.save();
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:mixinType = 'Aged'",
                QueryFilter.EMPTY, false);
        assertEquals(0, res.list.size());
        res = session.query(
                "SELECT * FROM Document WHERE ecm:mixinType <> 'Aged'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());

        assertTrue(node.addMixinType("Aged"));
        session.save();
        assertTrue(node.hasMixinType("Aged"));
        assertEquals(1, node.getMixinTypes().length);
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:mixinType = 'Aged'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:mixinType <> 'Aged'",
                QueryFilter.EMPTY, false);
        assertEquals(0, res.list.size());

        assertFalse(node.addMixinType("Aged"));
        assertEquals(1, node.getMixinTypes().length);

        assertTrue(node.addMixinType("Orderable"));
        assertTrue(node.hasMixinType("Aged"));
        assertTrue(node.hasMixinType("Orderable"));
        assertEquals(2, node.getMixinTypes().length);

        try {
            node.addMixinType("nosuchmixin");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
        assertEquals(2, node.getMixinTypes().length);

        assertTrue(node.removeMixinType("Aged"));
        session.save();
        assertFalse(node.hasMixinType("Aged"));
        assertTrue(node.hasMixinType("Orderable"));
        assertEquals(1, node.getMixinTypes().length);
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:mixinType = 'Aged'",
                QueryFilter.EMPTY, false);
        assertEquals(0, res.list.size());
        res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:mixinType <> 'Aged'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());

        assertFalse(node.removeMixinType("Aged"));
        assertEquals(1, node.getMixinTypes().length);

        assertFalse(node.removeMixinType("nosuchmixin"));
        assertEquals(1, node.getMixinTypes().length);

        assertTrue(node.removeMixinType("Orderable"));
        assertFalse(node.hasMixinType("Aged"));
        assertFalse(node.hasMixinType("Orderable"));
        assertEquals(0, node.getMixinTypes().length);
    }

    public void testMixinIncludedInPrimaryType() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "DocWithAge", false);

        node.setSimpleProperty("age:age", "123");
        assertEquals("123", node.getSimpleProperty("age:age").getValue());
        session.save();

        // another session
        session.close();
        session = repository.getConnection();
        root = session.getRootNode();
        node = session.getNodeById(node.getId());
        assertEquals("123", node.getSimpleProperty("age:age").getValue());

        // API on doc whose type has a mixin (facet)
        assertEquals(0, node.getMixinTypes().length); // instance mixins
        assertEquals(Collections.singleton("Aged"), node.getAllMixinTypes());
        assertTrue(node.hasMixinType("Aged"));
        assertFalse(node.addMixinType("Aged"));
        assertFalse(node.removeMixinType("Aged"));

    }

    public void testMixinAddRemove() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        session.save();

        // mixin not there
        try {
            node.getSimpleProperty("age:age");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }

        // add
        node.addMixinType("Aged");
        SimpleProperty p = node.getSimpleProperty("age:age");
        assertNotNull(p);
        p.setValue("123");
        session.save();

        // remove
        node.removeMixinType("Aged");
        session.save();

        // mixin not there anymore
        try {
            node.getSimpleProperty("age:age");
            fail();
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    // mixin on doc with same schema in primary type does no harm
    public void testMixinAddRemove2() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "DocWithAge", false);

        node.setSimpleProperty("age:age", "456");
        session.save();

        node.addMixinType("Aged");
        SimpleProperty p = node.getSimpleProperty("age:age");
        assertEquals("456", p.getValue());

        node.removeMixinType("Aged");
        p = node.getSimpleProperty("age:age");
        assertEquals("456", p.getValue());
        session.save();
    }

    public void testMixinCopy() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        node.addMixinType("Aged");
        node.setSimpleProperty("age:age", "123");
        session.save();

        // copy the doc
        Node copy = session.copy(node, root, "foo2");
        SimpleProperty p = copy.getSimpleProperty("age:age");
        assertEquals("123", p.getValue());
    }

    public void testMixinFulltext() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        node.addMixinType("Aged");
        node.setSimpleProperty("age:age", "barbar");
        session.save();
        DatabaseHelper.DATABASE.sleepForFulltext();

        PartialList<Serializable> res = session.query(
                "SELECT * FROM TestDoc WHERE ecm:fulltext = 'barbar'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());
    }

    public void testMixinQueryContent() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        node.addMixinType("Aged");
        node.setSimpleProperty("age:age", "barbar");
        session.save();

        PartialList<Serializable> res = session.query(
                "SELECT * FROM TestDoc WHERE age:age = 'barbar'",
                QueryFilter.EMPTY, false);
        assertEquals(1, res.list.size());
    }

    public void testLocking() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        Serializable nodeId = node.getId();
        assertNull(session.getLock(nodeId));
        session.save();

        session.close();
        session = repository.getConnection();
        node = session.getNodeById(nodeId);

        Lock lock = session.setLock(nodeId, new Lock("bob", null));
        assertNull(lock);
        assertNotNull(session.getLock(nodeId));

        lock = session.setLock(nodeId, new Lock("john", null));
        assertEquals("bob", lock.getOwner());

        lock = session.removeLock(nodeId, "steve", false);
        assertEquals("bob", lock.getOwner());
        assertTrue(lock.getFailed());
        assertNotNull(session.getLock(nodeId));

        lock = session.removeLock(nodeId, null, false);
        assertEquals("bob", lock.getOwner());
        assertFalse(lock.getFailed());
        assertNull(session.getLock(nodeId));

        lock = session.removeLock(nodeId, null, false);
        assertNull(lock);
    }

    public void testLockingParallel() throws Throwable {
        Serializable nodeId = createNode();
        runParallelLocking(nodeId, repository, repository);
    }

    // TODO disabled as there still are problems in cluster mode
    // due to invalidations not really being synchronous
    public void testLockingParallelClustered() throws Throwable {
        if (this instanceof TestSQLBackendNet
                || this instanceof ITSQLBackendNet) {
            return;
        }
        if (!DatabaseHelper.DATABASE.supportsClustering()) {
            System.out.println("Skipping clustered locking test for unsupported database: "
                    + DatabaseHelper.DATABASE.getClass().getName());
            return;
        }

        Serializable nodeId = createNode();

        // get two clustered repositories
        repository.close();
        long DELAY = 50; // ms
        repository = newRepository(DELAY, false);
        repository2 = newRepository(DELAY, false);

        runParallelLocking(nodeId, repository, repository2);
    }

    protected Serializable createNode() throws Exception {
        Session session = repository.getConnection();
        Node root = session.getRootNode();
        Node node = session.addChildNode(root, "foo", null, "TestDoc", false);
        session.save();
        Serializable nodeId = node.getId();
        session.close();
        return nodeId;
    }

    protected static void runParallelLocking(Serializable nodeId,
            Repository repository1, Repository repository2) throws Throwable {
        CyclicBarrier barrier = new CyclicBarrier(2);
        CountDownLatch firstReady = new CountDownLatch(1);
        long TIME = 1000; // ms
        LockingJob r1 = new LockingJob(repository1, "t1-", nodeId, TIME,
                firstReady, barrier);
        LockingJob r2 = new LockingJob(repository2, "t2-", nodeId, TIME, null,
                barrier);
        Thread t1 = new Thread(r1, "t1");
        Thread t2 = new Thread(r2, "t2");
        t1.start();
        firstReady.await();
        t2.start();

        t1.join();
        t2.join();
        if (r1.throwable != null) {
            throw r1.throwable;
        }
        if (r2.throwable != null) {
            throw r2.throwable;
        }
        int count = r1.count + r2.count;
        log.warn("Parallel locks per second: " + count);
    }

    protected static class LockingJob implements Runnable {

        protected final Repository repository;

        protected final String namePrefix;

        protected final Serializable nodeId;

        protected final long waitMillis;

        public final CountDownLatch ready;

        public final CyclicBarrier barrier;

        public Throwable throwable;

        public int count;

        public LockingJob(Repository repository, String namePrefix,
                Serializable nodeId, long waitMillis, CountDownLatch ready,
                CyclicBarrier barrier) {
            this.repository = repository;
            this.namePrefix = namePrefix;
            this.nodeId = nodeId;
            this.waitMillis = waitMillis;
            this.ready = ready;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            try {
                doHeavyLockingJob();
            } catch (Throwable t) {
                t.printStackTrace();
                throwable = t;
            }
        }

        protected void doHeavyLockingJob() throws Exception {
            Session session = repository.getConnection();
            if (ready != null) {
                ready.countDown();
            }
            barrier.await();
            // System.err.println(namePrefix + " starting");
            long start = System.currentTimeMillis();
            do {
                String name = namePrefix + count++;

                // lock
                while (true) {
                    Lock lock = session.setLock(nodeId, new Lock(name, null));
                    if (lock == null) {
                        break;
                    }
                    // System.err.println(name + " waiting, already locked by "
                    // + lock.getOwner());
                }
                // System.err.println(name + " locked");

                // unlock
                Lock lock = session.removeLock(nodeId, null, false);
                assertNotNull("got no lock, expected " + name, lock);
                assertEquals(name, lock.getOwner());
                // System.err.println(name + " unlocked");
            } while (System.currentTimeMillis() - start < waitMillis);
            session.close();
        }
    }

    public void testLocksUpgrade() throws Exception {
        if (this instanceof TestSQLBackendNet
                || this instanceof ITSQLBackendNet) {
            return;
        }
        JDBCMapper.testProps.put(JDBCMapper.TEST_UPGRADE, Boolean.TRUE);
        JDBCMapper.testProps.put(JDBCMapper.TEST_UPGRADE_LOCKS, Boolean.TRUE);
        try {
            doTestLocksUpgrade();
        } finally {
            JDBCMapper.testProps.clear();
        }
    }

    protected void doTestLocksUpgrade() throws Exception {
        Session session = repository.getConnection();
        String id;
        Lock lock;

        // check lock has been upgraded from 'bob:Jan 26, 2011'
        id = "dddddddd-dddd-dddd-dddd-dddddddddddd";
        lock = session.getLock(id);
        assertNotNull(lock);
        assertEquals("bob", lock.getOwner());
        Calendar expected = new GregorianCalendar(2011, Calendar.JANUARY, 26,
                0, 0, 0);
        assertEquals(expected, lock.getCreated());

        // old lock was nulled after unlock
        id = "11111111-2222-3333-4444-555555555555";
        lock = session.getLock(id);
        assertNull(lock);
    }

}
