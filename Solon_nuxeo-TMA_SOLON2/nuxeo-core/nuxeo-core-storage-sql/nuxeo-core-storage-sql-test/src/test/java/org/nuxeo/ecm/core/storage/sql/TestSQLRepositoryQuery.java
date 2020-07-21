/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dragos Mihalache
 *     Florent Guillaume
 */

package org.nuxeo.ecm.core.storage.sql;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;
import org.nuxeo.ecm.core.api.impl.FacetFilter;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.impl.ACLImpl;
import org.nuxeo.ecm.core.api.security.impl.ACPImpl;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.schema.FacetNames;
import org.nuxeo.runtime.api.Framework;

/**
 * @author Dragos Mihalache
 * @author Florent Guillaume
 * @author Benjamin Jalon
 */
public class TestSQLRepositoryQuery extends SQLRepositoryTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        deployContrib("org.nuxeo.ecm.core.storage.sql.test.tests",
                "OSGI-INF/testquery-core-types-contrib.xml");
        deployContrib("org.nuxeo.ecm.core.storage.sql.test.tests",
                "OSGI-INF/test-repo-core-types-contrib-2.xml");
        openSession();
    }

    // ---------------------------------------

    protected Calendar getCalendar(int year, int month, int day, int hours,
            int minutes, int seconds) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"));
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // 0-based
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, seconds);
        return cal;
    }

    /**
     * Creates the following structure of documents:
     *
     * <pre>
     *  root (UUID_1)
     *  |- testfolder1 (UUID_2)
     *  |  |- testfile1 (UUID_3) (content UUID_4)
     *  |  |- testfile2 (UUID_5) (content UUID_6)
     *  |  \- testfile3 (Note) (UUID_7) (trans UUID_8 stylesheet UUID_9)
     *  \- tesfolder2 (UUID_10)
     *     \- testfolder3 (UUID_11)
     *        \- testfile4 (UUID_12) (content UUID_13)
     * </pre>
     */
    protected void createDocs() throws Exception {
        DocumentModel folder1 = new DocumentModelImpl("/", "testfolder1",
                "Folder");
        folder1.setPropertyValue("dc:title", "testfolder1_Title");
        folder1 = session.createDocument(folder1);

        DocumentModel file1 = new DocumentModelImpl("/testfolder1",
                "testfile1", "Fileq");
        file1.setPropertyValue("dc:title", "testfile1_Title");
        file1.setPropertyValue("dc:description", "testfile1_description");
        String content = "Some caf\u00e9 in a restaurant.\nDrink!.\n";
        String filename = "testfile.txt";
        ByteArrayBlob blob1 = new ByteArrayBlob(content.getBytes("UTF-8"),
                "text/plain");
        file1.setPropertyValue("content", blob1);
        file1.setPropertyValue("filename", filename);
        Calendar cal1 = getCalendar(2007, 3, 1, 12, 0, 0);
        file1.setPropertyValue("dc:created", cal1);
        file1.setPropertyValue("dc:coverage", "football");
        file1.setPropertyValue("tl:subjects", new String[] { "foo", "gee/moo" });
        file1.setPropertyValue("uid", "uid123");
        file1 = session.createDocument(file1);

        DocumentModel file2 = new DocumentModelImpl("/testfolder1",
                "testfile2", "Fileq");
        file2.setPropertyValue("dc:title", "testfile2_Title");
        file2.setPropertyValue("dc:description", "testfile2_DESCRIPTION2");
        Calendar cal2 = getCalendar(2007, 4, 1, 12, 0, 0);
        file2.setPropertyValue("dc:created", cal2);
        file2.setPropertyValue("tl:contributors",
                new String[] { "bob", "pete" });
        file2.setPropertyValue("dc:coverage", "foo/bar");
        file2 = session.createDocument(file2);

        DocumentModel file3 = new DocumentModelImpl("/testfolder1",
                "testfile3", "Noteq");
        file3.setPropertyValue("dc:title", "testfile3_Title");
        file3.setPropertyValue("dc:description",
                "testfile3_desc1 testfile3_desc2,  testfile3_desc3");
        file3.setPropertyValue("tl:contributors",
                new String[] { "bob", "john" });
        file3 = session.createDocument(file3);

        DocumentModel folder2 = new DocumentModelImpl("/", "testfolder2",
                "Folder");
        folder2 = session.createDocument(folder2);

        DocumentModel folder3 = new DocumentModelImpl("/testfolder2",
                "testfolder3", "Folder");
        folder3 = session.createDocument(folder3);

        // create file 4
        DocumentModel file4 = new DocumentModelImpl("/testfolder2/testfolder3",
                "testfile4", "Fileq");
        // title without space or _ for Oracle fulltext searchability
        // (testFulltextProxy)
        file4.setPropertyValue("dc:title", "testfile4Title");
        file4.setPropertyValue("dc:description", "testfile4_DESCRIPTION4");
        file4 = session.createDocument(file4);

        session.save();
    }

    /**
     * Publishes testfile4 to testfolder1:
     * <p>
     * version (UUID_14, content UUID_15)
     * <p>
     * proxy (UUID_16)
     */
    protected DocumentModel publishDoc() throws Exception {
        DocumentModel doc = session.getDocument(new PathRef(
                "/testfolder2/testfolder3/testfile4"));
        DocumentModel sec = session.getDocument(new PathRef("/testfolder1"));
        DocumentModel proxy = session.publishDocument(doc, sec);
        session.save();
        DocumentModelList proxies = session.getProxies(doc.getRef(),
                sec.getRef());
        assertEquals(1, proxies.size());
        return proxy;
    }

    // from TestAPI

    public void testQueryBasic() throws Exception {
        DocumentModelList dml;
        createDocs();

        dml = session.query("SELECT * FROM File");
        assertEquals(3, dml.size());
        for (DocumentModel dm : dml) {
            assertEquals("Fileq", dm.getType());
        }

        dml = session.query("SELECT * FROM Note");
        assertEquals(1, dml.size());
        for (DocumentModel dm : dml) {
            assertEquals("Noteq", dm.getType());
        }

        dml = session.query("SELECT * FROM Folder");
        assertEquals(3, dml.size());
        for (DocumentModel dm : dml) {
            assertEquals("Folder", dm.getType());
        }

        dml = session.query("SELECT * FROM Document");
        assertEquals(7, dml.size());

        dml = session.query("SELECT * FROM File");
        assertEquals(3, dml.size());

        dml = session.query("SELECT * FROM File WHERE dc:title = 'testfile1_Title'");
        assertEquals(1, dml.size());

        dml = session.query("SELECT * FROM File WHERE NOT dc:title = 'testfile1_Title'");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM Document WHERE dc:title = 'testfile1_Title'");
        assertEquals(1, dml.size());

        dml = session.query("SELECT * FROM File WHERE dc:title = 'testfile1_Title' OR dc:title = 'testfile2_Title'");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM File WHERE dc:title = 'testfolder1_Title'");
        assertEquals(0, dml.size());

        dml = session.query("SELECT * FROM File WHERE filename = 'testfile.txt'");
        assertEquals(1, dml.size());

        dml = session.query("SELECT * FROM Note WHERE dc:title = 'testfile3_Title'");
        assertEquals(1, dml.size());

        // property in a schema with no prefix
        dml = session.query("SELECT * FROM Document WHERE uid = 'uid123'");
        assertEquals(1, dml.size());
        // compat syntax for old search service:
        dml = session.query("SELECT * FROM Document WHERE uid:uid = 'uid123'");
        assertEquals(1, dml.size());

        // this needs an actual LEFT OUTER JOIN
        dml = session.query("SELECT * FROM Document WHERE filename = 'testfile.txt' OR dc:title = 'testfile3_Title'");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM Document WHERE filename = 'testfile.txt' OR tl:contributors = 'bob'");
        assertEquals(3, dml.size());

        dml = session.query("SELECT * FROM Document WHERE dc:created BETWEEN DATE '2007-01-01' AND DATE '2008-01-01'");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM Document WHERE dc:created BETWEEN DATE '2007-03-15' AND DATE '2008-01-01'");
        assertEquals(1, dml.size());

        // early detection of conflicting types for VCS
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType = 'foo'");
        assertEquals(0, dml.size());
    }

    public void testQueryBasic2() throws Exception {
        // Documents without creation date don't match any DATE query
        // 2 documents with creation date
        createDocs();
        DocumentModelList dml;

        if (database == DatabaseDerby.INSTANCE) {
            // Derby 10.5.3.0 has bugs with LEFT JOIN and NOT BETWEEN
            // http://issues.apache.org/jira/browse/DERBY-4388
            return;
        }

        dml = session.query("SELECT * FROM Document WHERE dc:created NOT BETWEEN DATE '2007-01-01' AND DATE '2008-01-01'");
        assertEquals(0, dml.size()); // 2 Documents match the BETWEEN query

        dml = session.query("SELECT * FROM Document WHERE dc:created NOT BETWEEN DATE '2007-03-15' AND DATE '2008-01-01'");
        assertEquals(1, dml.size()); // 1 Document matches the BETWEEN query

        dml = session.query("SELECT * FROM Document WHERE dc:created NOT BETWEEN DATE '2009-03-15' AND DATE '2009-01-01'");
        assertEquals(2, dml.size()); // 0 Document matches the BETWEEN query

        dml = session.query("SELECT * FROM Document WHERE dc:title ILIKE 'test%'");
        assertEquals(5, dml.size());

        dml = session.query("SELECT * FROM Document WHERE dc:title ILIKE 'Test%'");
        assertEquals(5, dml.size());

        dml = session.query("SELECT * FROM Document WHERE dc:title NOT ILIKE 'foo%'");
        assertEquals(5, dml.size());

        dml = session.query("SELECT * FROM Document WHERE dc:title NOT ILIKE 'Foo%'");
        assertEquals(5, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:subjects ILIKE '%oo'");
        assertEquals(1, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:subjects NOT ILIKE '%oo'");
        assertEquals(6, dml.size());
    }

    public void testQueryMultiple() throws Exception {
        DocumentModelList dml;
        createDocs();

        dml = session.query("SELECT * FROM File WHERE tl:contributors = 'pete'");
        assertEquals(1, dml.size());

        dml = session.query("SELECT * FROM File WHERE tl:contributors = 'bob'");
        assertEquals(1, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors = 'bob'");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors IN ('bob', 'pete')");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors IN ('bob', 'john')");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors NOT IN ('bob', 'pete')");
        assertEquals(5, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors NOT IN ('bob', 'john')");
        assertEquals(5, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors LIKE 'pe%'");
        assertEquals(1, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors LIKE 'bo%'");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors LIKE '%o%'");
        assertEquals(2, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:subjects LIKE '%oo%'");
        assertEquals(1, dml.size());

        dml = session.query("SELECT * FROM File WHERE tl:subjects NOT LIKE '%oo%'");
        assertEquals(2, dml.size());
    }

    public void testQueryNegativeMultiple() throws Exception {
        DocumentModelList dml;
        createDocs();
        dml = session.query("SELECT * FROM Document WHERE tl:contributors <> 'pete'");
        assertEquals(6, dml.size());

        dml = session.query("SELECT * FROM Document WHERE tl:contributors <> 'blah'");
        assertEquals(7, dml.size());

        dml = session.query("SELECT * FROM File WHERE tl:contributors <> 'blah' AND ecm:isProxy = 0");
        assertEquals(3, dml.size());

        dml = session.query("SELECT * FROM Document WHERE ecm:mixinType = 'Versionable' AND ecm:mixinType <> 'Downloadable'");
        assertEquals(1, dml.size()); // 1 note
    }

    public void testQueryAfterEdit() throws Exception {
        DocumentModel root = session.getRootDocument();

        String fname1 = "file1";
        DocumentModel childFile1 = new DocumentModelImpl(
                root.getPathAsString(), fname1, "File");

        DocumentModel[] childDocs = new DocumentModel[1];
        childDocs[0] = childFile1;

        DocumentModel[] returnedChildDocs = session.createDocument(childDocs);
        assertEquals(1, returnedChildDocs.length);

        childFile1 = returnedChildDocs[0];
        childFile1.setProperty("file", "filename", "f1");

        // add a blob
        String s = "<html><head/><body>La la la!</body></html>";
        Blob blob = new ByteArrayBlob(s.getBytes("UTF-8"), "text/html");
        childFile1.setProperty("file", "content", blob);

        session.saveDocument(childFile1);
        session.save();

        DocumentModelList list;
        DocumentModel docModel;

        list = session.query("SELECT * FROM Document");
        assertEquals(1, list.size());
        docModel = list.get(0);

        // read the properties
        docModel.getProperty("dublincore", "title");

        // XXX: FIXME: OG the following throws a class cast exception since the
        // get property returns an HashMap instance instead of a LazyBlob when
        // the tests are all run together:

        Blob blob2 = (Blob) docModel.getProperty("file", "content");
        assertEquals(s.length(), blob2.getLength()); // only ascii chars
        assertEquals("text/html", blob2.getMimeType());

        // edit the title without touching the blob
        docModel.setProperty("dublincore", "title", "edited title");
        docModel.setProperty("dublincore", "description", "edited description");
        session.saveDocument(docModel);
        session.save();

        list = session.query("SELECT * FROM Document");
        assertEquals(1, list.size());
        docModel = list.get(0);

        session.removeDocument(docModel.getRef());
    }

    public void testOrderBy() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        sql = "SELECT * FROM Document WHERE dc:title LIKE 'testfile%' ORDER BY dc:description";
        dml = session.query(sql);
        assertEquals(4, dml.size());
        assertEquals("testfile1_description",
                dml.get(0).getPropertyValue("dc:description"));

        sql = "SELECT * FROM Document WHERE dc:title LIKE 'testfile%' ORDER BY dc:description DESC";
        dml = session.query(sql);
        assertEquals(4, dml.size());
        assertEquals("testfile4_DESCRIPTION4",
                dml.get(0).getPropertyValue("dc:description"));
    }

    public void testOrderBySeveralColumns() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        // avoid null dc:coverage, null sort first/last is db-dependent
        sql = "SELECT * FROM File "
                + " WHERE dc:title in ('testfile1_Title', 'testfile2_Title')"
                + " ORDER BY dc:title, dc:coverage";
        dml = session.query(sql);
        assertEquals(2, dml.size());
        assertEquals("testfile1", dml.get(0).getName());
        assertEquals("testfile2", dml.get(1).getName());

        // swap columns
        sql = "SELECT * FROM File "
                + " WHERE dc:title in ('testfile1_Title', 'testfile2_Title')"
                + " ORDER BY dc:coverage, dc:title";
        dml = session.query(sql);
        assertEquals(2, dml.size());
        assertEquals("testfile2", dml.get(0).getName());
        assertEquals("testfile1", dml.get(1).getName());
    }

    public void testOrderBySameColumns() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        sql = "SELECT * FROM File "
                + " WHERE dc:title in ('testfile1_Title', 'testfile2_Title')"
                + " ORDER BY dc:title, dc:title";
        dml = session.query(sql);
        assertEquals(2, dml.size());
        assertEquals("testfile1", dml.get(0).getName());
        assertEquals("testfile2", dml.get(1).getName());

        sql = "SELECT * FROM File "
                + " WHERE dc:title in ('testfile1_Title', 'testfile2_Title')"
                + " ORDER BY dc:title DESC, dc:title";
        dml = session.query(sql);
        assertEquals(2, dml.size());
        assertEquals("testfile2", dml.get(0).getName());
        assertEquals("testfile1", dml.get(1).getName());
    }

    public void testOrderByPath() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        sql = "SELECT * FROM Document ORDER BY ecm:path";
        dml = session.query(sql);
        assertEquals(7, dml.size());
        assertEquals("/testfolder1", dml.get(0).getPathAsString());
        assertEquals("/testfolder1/testfile1", dml.get(1).getPathAsString());
        assertEquals("/testfolder1/testfile2", dml.get(2).getPathAsString());
        assertEquals("/testfolder1/testfile3", dml.get(3).getPathAsString());
        assertEquals("/testfolder2", dml.get(4).getPathAsString());
        assertEquals("/testfolder2/testfolder3", dml.get(5).getPathAsString());
        assertEquals("/testfolder2/testfolder3/testfile4",
                dml.get(6).getPathAsString());

        sql = "SELECT * FROM Document ORDER BY ecm:path DESC";
        dml = session.query(sql);
        assertEquals(7, dml.size());
        assertEquals("/testfolder2/testfolder3/testfile4",
                dml.get(0).getPathAsString());
        assertEquals("/testfolder1", dml.get(6).getPathAsString());

        // then with batching

        sql = "SELECT * FROM Document ORDER BY ecm:path";
        dml = session.query(sql, null, 2, 3, false);
        assertEquals(2, dml.size());
        assertEquals("/testfolder1/testfile3", dml.get(0).getPathAsString());
        assertEquals("/testfolder2", dml.get(1).getPathAsString());
    }

    public void testOrderByPos() throws Exception {
        DocumentModelList dml;

        DocumentModel ofolder = new DocumentModelImpl("/", "ofolder",
                "OrderedFolder");
        ofolder = session.createDocument(ofolder);
        DocumentModel file1 = new DocumentModelImpl("/ofolder", "testfile1",
                "File");
        file1 = session.createDocument(file1);
        DocumentModel file2 = new DocumentModelImpl("/ofolder", "testfile2",
                "File");
        file2 = session.createDocument(file2);
        DocumentModel file3 = new DocumentModelImpl("/ofolder", "testfile3",
                "File");
        file3 = session.createDocument(file3);
        session.save();

        String sql = String.format(
                "SELECT * FROM Document WHERE ecm:parentId = '%s' ORDER BY ecm:pos",
                ofolder.getId());
        String sqldesc = sql + " DESC";

        dml = session.query(sql);
        assertEquals(3, dml.size());
        assertEquals(file1.getId(), dml.get(0).getId());
        assertEquals(file2.getId(), dml.get(1).getId());
        assertEquals(file3.getId(), dml.get(2).getId());

        dml = session.query(sqldesc);
        assertEquals(file3.getId(), dml.get(0).getId());
        assertEquals(file2.getId(), dml.get(1).getId());
        assertEquals(file1.getId(), dml.get(2).getId());

        session.orderBefore(ofolder.getRef(), "testfile3", "testfile2");
        session.save();

        dml = session.query(sql);
        assertEquals(file1.getId(), dml.get(0).getId());
        assertEquals(file3.getId(), dml.get(1).getId());
        assertEquals(file2.getId(), dml.get(2).getId());

        dml = session.query(sqldesc);
        assertEquals(file2.getId(), dml.get(0).getId());
        assertEquals(file3.getId(), dml.get(1).getId());
        assertEquals(file1.getId(), dml.get(2).getId());

        // test ecm:pos as a field
        sql = "SELECT * FROM Document WHERE ecm:pos = 1";
        dml = session.query(sql);
        assertEquals(1, dml.size());
        assertEquals(file3.getId(), dml.iterator().next().getId());
    }

    public void testBatching() throws Exception {
        doBatching(true);
    }

    public void doBatching(boolean checkNames) throws Exception {
        DocumentModelList dml;
        createDocs();

        String sql = "SELECT * FROM Document ORDER BY ecm:name";

        dml = session.query(sql);
        assertEquals(7, dml.size());
        assertEquals(7, dml.totalSize());
        if (checkNames) {
            assertEquals("testfile1", dml.get(0).getName());
            assertEquals("testfile2", dml.get(1).getName());
            assertEquals("testfile3", dml.get(2).getName());
            assertEquals("testfile4", dml.get(3).getName());
            assertEquals("testfolder1", dml.get(4).getName());
            assertEquals("testfolder2", dml.get(5).getName());
            assertEquals("testfolder3", dml.get(6).getName());
        }

        dml = session.query(sql, null, 99, 0, true);
        assertEquals(7, dml.size());
        assertEquals(7, dml.totalSize());
        if (checkNames) {
            assertEquals("testfile1", dml.get(0).getName());
            assertEquals("testfolder3", dml.get(6).getName());
        }

        dml = session.query(sql, null, 7, 0, true);
        assertEquals(7, dml.size());
        assertEquals(7, dml.totalSize());
        if (checkNames) {
            assertEquals("testfile1", dml.get(0).getName());
            assertEquals("testfolder3", dml.get(6).getName());
        }

        dml = session.query(sql, null, 6, 0, true);
        assertEquals(6, dml.size());
        assertEquals(7, dml.totalSize());
        if (checkNames) {
            assertEquals("testfile1", dml.get(0).getName());
            assertEquals("testfolder2", dml.get(5).getName());
        }

        dml = session.query(sql, null, 6, 1, true);
        assertEquals(6, dml.size());
        assertEquals(7, dml.totalSize());
        if (checkNames) {
            assertEquals("testfile2", dml.get(0).getName());
            assertEquals("testfolder3", dml.get(5).getName());
        }

        dml = session.query(sql, null, 99, 3, true);
        assertEquals(4, dml.size());
        assertEquals(7, dml.totalSize());
        if (checkNames) {
            assertEquals("testfile4", dml.get(0).getName());
            assertEquals("testfolder3", dml.get(3).getName());
        }

        dml = session.query(sql, null, 99, 50, true);
        assertEquals(0, dml.size());
        assertEquals(7, dml.totalSize());
    }

    // from TestSQLWithPath
    public void testEcmPathEqual() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        sql = "SELECT * FROM document WHERE ecm:path = '/testfolder1'";
        dml = session.query(sql);
        assertEquals(1, dml.size());

        // trailing slash accepted
        sql = "SELECT * FROM document WHERE ecm:path = '/testfolder1/'";
        dml = session.query(sql);
        assertEquals(1, dml.size());

        sql = "SELECT * FROM document WHERE ecm:path <> '/testfolder1'";
        dml = session.query(sql);
        assertEquals(6, dml.size());
    }

    public void testStartsWith() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        sql = "SELECT * FROM document WHERE ecm:path STARTSWITH '/'";
        dml = session.query(sql);
        assertEquals(7, dml.size());

        sql = "SELECT * FROM document WHERE ecm:path STARTSWITH '/nothere/'";
        dml = session.query(sql);
        assertEquals(0, dml.size());

        sql = "SELECT * FROM document WHERE ecm:path STARTSWITH '/testfolder1/'";
        dml = session.query(sql);
        assertEquals(3, dml.size());

        sql = "SELECT * FROM document WHERE ecm:path STARTSWITH '/testfolder2/'";
        dml = session.query(sql);
        assertEquals(2, dml.size());

        sql = "SELECT * FROM document WHERE dc:title='testfile1_Title' AND ecm:path STARTSWITH '/'";
        dml = session.query(sql);
        assertEquals(1, dml.size());

        sql = "SELECT * FROM document WHERE dc:title LIKE 'testfile%' AND ecm:path STARTSWITH '/'";
        dml = session.query(sql);
        assertEquals(4, dml.size());

    }

    public void testStartsWithMove() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        // move folder2 into folder1
        session.move(new PathRef("/testfolder2/"),
                new PathRef("/testfolder1/"), null);
        session.save();

        sql = "SELECT * FROM document WHERE ecm:path STARTSWITH '/testfolder1/'";
        dml = session.query(sql);
        assertEquals(6, dml.size());

        sql = "SELECT * FROM document WHERE ecm:path STARTSWITH '/testfolder1/testfolder2/'";
        dml = session.query(sql);
        assertEquals(2, dml.size());

        sql = "SELECT * FROM document WHERE ecm:path STARTSWITH '/testfolder2/'";
        dml = session.query(sql);
        assertEquals(0, dml.size());
    }

    public void testStartsWithNonPath() throws Exception {
        String sql;
        createDocs();

        sql = "SELECT * FROM Document WHERE dc:coverage STARTSWITH 'foo'";
        assertEquals(1, session.query(sql).size());

        sql = "SELECT * FROM Document WHERE dc:coverage STARTSWITH 'foo/bar'";
        assertEquals(1, session.query(sql).size());

        sql = "SELECT * FROM Document WHERE dc:coverage STARTSWITH 'foo/bar/baz'";
        assertEquals(0, session.query(sql).size());

        sql = "SELECT * FROM Document WHERE tl:subjects STARTSWITH 'foo'";
        assertEquals(1, session.query(sql).size());

        sql = "SELECT * FROM Document WHERE tl:subjects STARTSWITH 'gee'";
        assertEquals(1, session.query(sql).size());

        sql = "SELECT * FROM Document WHERE tl:subjects STARTSWITH 'gee/moo'";
        assertEquals(1, session.query(sql).size());

        sql = "SELECT * FROM Document WHERE tl:subjects STARTSWITH 'gee/moo/blah'";
        assertEquals(0, session.query(sql).size());
    }

    public void testReindexEditedDocument() throws Exception {
        String sql = "SELECT * FROM document WHERE dc:title LIKE 'testfile1_Ti%'";
        DocumentModelList dml;
        createDocs();

        dml = session.query(sql);
        assertEquals(1, dml.size());

        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));

        // edit file1
        file1.setPropertyValue("dc:description", "testfile1_description");
        file1.setPropertyValue("content", null);
        session.saveDocument(file1);
        session.save();

        // rerunning the same query
        dml = session.query(sql);
        assertEquals(1, dml.size());

        // edit the title
        file1.setPropertyValue("dc:title", "testfile1_ModifiedTitle");
        session.saveDocument(file1);
        session.save();

        // rerun the same query
        dml = session.query(sql);
        assertEquals(0, dml.size());

        // editithe title
        file1.setPropertyValue("dc:description", "Yet another description");
        session.saveDocument(file1);
        session.save();

        // adjust the query to the new title
        sql = "SELECT * FROM document WHERE dc:title LIKE 'testfile1_Mo%'";
        dml = session.query(sql);
        assertEquals(1, dml.size());
    }

    // from TestSQLWithDate

    public void testDate() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        sql = "SELECT * FROM document WHERE dc:created >= DATE '2007-01-01'";
        dml = session.query(sql);
        assertEquals(2, dml.size());

        sql = "SELECT * FROM document WHERE dc:created >= DATE '2007-03-15'";
        dml = session.query(sql);
        assertEquals(1, dml.size());

        sql = "SELECT * FROM document WHERE dc:created >= DATE '2007-05-01'";
        dml = session.query(sql);
        assertEquals(0, dml.size());

        sql = "SELECT * FROM document WHERE dc:created >= TIMESTAMP '2007-03-15 00:00:00'";
        dml = session.query(sql);
        assertEquals(1, dml.size());

        sql = "SELECT * FROM document WHERE dc:created >= DATE '2007-02-15' AND dc:created <= DATE '2007-03-15'";
        dml = session.query(sql);
        assertEquals(1, dml.size());
    }

    // other tests

    public void testBoolean() throws Exception {
        String sql;
        DocumentModelList dml;
        createDocs();

        sql = "SELECT * FROM document WHERE my:boolean = 1";
        dml = session.query(sql);
        assertEquals(0, dml.size());
        sql = "SELECT * FROM document WHERE my:boolean = 0";
        dml = session.query(sql);
        assertEquals(0, dml.size());

        DocumentModel doc = new DocumentModelImpl("/testfolder1", "mydoc",
                "MyDocType");
        doc.setPropertyValue("my:boolean", Boolean.TRUE);
        doc = session.createDocument(doc);
        session.save();

        sql = "SELECT * FROM document WHERE my:boolean = 1";
        dml = session.query(sql);
        assertEquals(1, dml.size());
        sql = "SELECT * FROM document WHERE my:boolean = 0";
        dml = session.query(sql);
        assertEquals(0, dml.size());

        doc.setPropertyValue("my:boolean", Boolean.FALSE);
        session.saveDocument(doc);
        session.save();

        sql = "SELECT * FROM document WHERE my:boolean = 1";
        dml = session.query(sql);
        assertEquals(0, dml.size());
        sql = "SELECT * FROM document WHERE my:boolean = 0";
        dml = session.query(sql);
        assertEquals(1, dml.size());
    }

    public void testQueryWithSecurity() throws Exception {
        createDocs();
        DocumentModel root = session.getRootDocument();
        ACP acp = new ACPImpl();
        ACL acl = new ACLImpl();
        acl.add(new ACE("Administrator", "Everything", true));
        acl.add(new ACE("bob", "Browse", true));
        acp.addACL(acl);
        root.setACP(acp, true);
        DocumentModel folder1 = session.getDocument(new PathRef("/testfolder1"));
        acp = new ACPImpl();
        acl = new ACLImpl();
        acl.add(new ACE("bob", "Browse", false));
        acp.addACL(acl);
        folder1.setACP(acp, true);
        session.save();
        closeSession();
        session = openSessionAs("bob");

        DocumentModelList dml = session.query("SELECT * FROM Document");
        assertEquals(3, dml.size());
    }

    public void testQueryWithSecurityAndFulltext() throws Exception {
        createDocs();
        closeSession();
        session = openSessionAs("bob");
        // this failed with ORA-00918 on Oracle (NXP-5410)
        session.query("SELECT * FROM Document WHERE ecm:fulltext = 'world'");
        // we don't care about the answer, just that the query executes
    }

    public void testSecurityManagerBasic() throws Exception {
        doTestSecurityManager("OSGI-INF/security-policy-contrib.xml");
    }

    public void testSecurityManagerWithTransformer() throws Exception {
        doTestSecurityManager("OSGI-INF/security-policy2-contrib.xml");
    }

    public void doTestSecurityManager(String contrib) throws Exception {
        createDocs();
        DocumentModelList dml;

        dml = session.query("SELECT * FROM Document");
        assertEquals(7, dml.size());
        assertEquals(7, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 0, true);
        assertEquals(2, dml.size());
        assertEquals(7, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 5, true);
        assertEquals(2, dml.size());
        assertEquals(7, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 6, true);
        assertEquals(1, dml.size());
        assertEquals(7, dml.totalSize());

        // now add a security policy hiding docs of type File
        deployContrib("org.nuxeo.ecm.core.storage.sql.test.tests", contrib);

        dml = session.query("SELECT * FROM Document");
        assertEquals(4, dml.size());
        assertEquals(4, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 0, true);
        assertEquals(2, dml.size());
        assertEquals(4, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 2, true);
        assertEquals(2, dml.size());
        assertEquals(4, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 3, true);
        assertEquals(1, dml.size());
        assertEquals(4, dml.totalSize());

        // add an ACL as well
        DocumentModel root = session.getRootDocument();
        ACP acp = new ACPImpl();
        ACL acl = new ACLImpl();
        acl.add(new ACE("Administrator", "Everything", true));
        acl.add(new ACE("bob", "Browse", true));
        acp.addACL(acl);
        root.setACP(acp, true);
        DocumentModel folder1 = session.getDocument(new PathRef(
                "/testfolder2/testfolder3"));
        acp = new ACPImpl();
        acl = new ACLImpl();
        acl.add(new ACE("bob", "Browse", false));
        acp.addACL(acl);
        folder1.setACP(acp, true);
        session.save();
        closeSession();
        session = openSessionAs("bob");

        dml = session.query("SELECT * FROM Document");
        assertEquals(3, dml.size());
        assertEquals(3, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 0, true);
        assertEquals(2, dml.size());
        assertEquals(3, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 1, true);
        assertEquals(2, dml.size());
        assertEquals(3, dml.totalSize());
        dml = session.query("SELECT * FROM Document", null, 2, 2, true);
        assertEquals(1, dml.size());
        assertEquals(3, dml.totalSize());
    }

    private static void assertIdSet(DocumentModelList dml, String... ids) {
        Collection<String> expected = new HashSet<String>(Arrays.asList(ids));
        Collection<String> actual = new HashSet<String>();
        for (DocumentModel d : dml) {
            actual.add(d.getId());
        }
        assertEquals(expected, actual);
    }

    public void testQueryWithProxies() throws Exception {
        createDocs();
        DocumentModel proxy = publishDoc();

        DocumentModel doc = session.getDocument(new PathRef(
                "/testfolder2/testfolder3/testfile4"));
        String docId = doc.getId();
        String proxyId = proxy.getId();
        String versionId = proxy.getSourceId();
        assertNotSame(docId, proxyId);
        assertNotNull(versionId);
        assertNotSame(docId, versionId);
        assertNotSame(proxyId, versionId);

        DocumentModelList dml;
        Filter filter;

        // queries must return proxies *and versions*
        dml = session.query("SELECT * FROM Document WHERE dc:title = 'testfile4Title'");
        assertIdSet(dml, docId, proxyId, versionId);

        dml = session.query("SELECT * FROM Document WHERE ecm:isProxy = 1");
        assertIdSet(dml, proxyId);
        dml = session.query("SELECT * FROM Document WHERE ecm:isProxy <> 0");
        assertIdSet(dml, proxyId);

        dml = session.query("SELECT * FROM Document WHERE ecm:isProxy = 0");
        assertEquals(8, dml.size()); // 7 folder/docs, 1 version
        dml = session.query("SELECT * FROM Document WHERE ecm:isProxy <> 1");
        assertEquals(8, dml.size());

        dml = session.query("SELECT * FROM Document WHERE ecm:isCheckedInVersion = 1");
        assertIdSet(dml, versionId);

        dml = session.query("SELECT * FROM Document WHERE ecm:isCheckedInVersion = 0");
        assertEquals(8, dml.size()); // 7 folder/docs, 1 proxy

        dml = session.query("SELECT * FROM Document WHERE ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0");
        assertEquals(7, dml.size()); // 7 folder/docs

        // filter out proxies explicitely, keeps live and version
        dml = session.query("SELECT * FROM Document WHERE dc:title = 'testfile4Title' AND ecm:isProxy = 0");
        assertIdSet(dml, docId, versionId);

        // only keep proxies
        dml = session.query("SELECT * FROM Document WHERE dc:title = 'testfile4Title' AND ecm:isProxy = 1");
        assertIdSet(dml, proxyId);

        // only keep versions
        dml = session.query("SELECT * FROM Document WHERE dc:title = 'testfile4Title' AND ecm:isCheckedInVersion = 1");
        assertIdSet(dml, versionId);


        // "deep" isProxy
        dml = session.query("SELECT * FROM Document WHERE (dc:title = 'blah' OR ecm:isProxy = 1)");
        assertIdSet(dml, proxyId);
        dml = session.query("SELECT * FROM Document WHERE ecm:isProxy = 0 AND (dc:title = 'testfile1_Title' OR ecm:isProxy = 1)");
        assertEquals(1, dml.size());

        // proxy query with order by
        dml = session.query("SELECT * FROM Document WHERE dc:title = 'testfile4Title' ORDER BY dc:title");
        assertIdSet(dml, docId, proxyId, versionId);
        dml = session.query("SELECT * FROM File WHERE dc:title = 'testfile4Title' ORDER BY dc:description");
        assertIdSet(dml, docId, proxyId, versionId);
    }

    public void testQueryPaging() throws Exception {
        createDocs();
        DocumentModelList whole = session.query("SELECT * FROM Document ORDER BY dc:modified, ecm:uuid");
        assertTrue(whole.size() >= 2);
        DocumentModelList firstPage = session.query(
                "SELECT * from Document ORDER BY dc:modified, ecm:uuid", null,
                1, 0, false);
        assertEquals(1, firstPage.size());
        assertEquals(whole.get(0).getId(), firstPage.get(0).getId());
        DocumentModelList secondPage = session.query(
                "SELECT * from Document ORDER BY dc:modified, ecm:uuid", null,
                1, 1, false);
        assertEquals(1, secondPage.size());
        assertEquals(whole.get(1).getId(), secondPage.get(0).getId());
    }

    public void testQuerySpecialFields() throws Exception {
        // ecm:isProxy and ecm:isCheckedInVersion are already tested in
        // testQueryWithProxies

        // ecm:path already tested in testStartsWith

        createDocs();
        DocumentModel proxy = publishDoc();
        DocumentModel version = session.getDocument(new IdRef(
                proxy.getSourceId()));

        DocumentModelList dml;
        DocumentModel folder1 = session.getDocument(new PathRef("/testfolder1"));
        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));
        DocumentModel file2 = session.getDocument(new PathRef(
                "/testfolder1/testfile2"));
        DocumentModel file3 = session.getDocument(new PathRef(
                "/testfolder1/testfile3"));
        DocumentModel file4 = session.getDocument(new PathRef(
                "/testfolder2/testfolder3/testfile4"));
        file1.setLock();
        session.save();

        /*
         * ecm:uuid
         */
        dml = session.query(String.format(
                "SELECT * FROM Document WHERE ecm:uuid = '%s'", file1.getId()));
        assertIdSet(dml, file1.getId());
        dml = session.query(String.format(
                "SELECT * FROM Document WHERE ecm:uuid = '%s'", proxy.getId()));
        assertIdSet(dml, proxy.getId());

        /*
         * ecm:name
         */
        dml = session.query(String.format(
                "SELECT * FROM Document WHERE ecm:name = '%s'", file1.getName()));
        assertIdSet(dml, file1.getId());
        // Disabled, version and proxies names don't need to be identical
        // dml = session.query(String.format(
        // "SELECT * FROM Document WHERE ecm:name = '%s'", file4.getName()));
        // assertIdSet(dml, file4.getId(), proxy.getId(), version.getId());

        /*
         * ecm:parentId
         */
        dml = session.query(String.format(
                "SELECT * FROM Document WHERE ecm:parentId = '%s'",
                folder1.getId()));
        assertIdSet(dml, file1.getId(), file2.getId(), file3.getId(),
                proxy.getId());

        /*
         * ecm:primaryType
         */
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType = 'Folder'");
        assertEquals(3, dml.size());
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType <> 'Folder'");
        assertEquals(6, dml.size()); // 3 files, 1 note, 1 proxy, 1 version
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType = 'Note'");
        assertEquals(0, dml.size());
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType = 'Noteq'");
        assertEquals(1, dml.size());
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType = 'File'");
        assertEquals(0, dml.size()); // 3 files, 1 proxy, 1 version
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType = 'Fileq'");
        assertEquals(5, dml.size()); // 3 files, 1 proxy, 1 version
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType IN ('Folder', 'Noteq')");
        assertEquals(4, dml.size());
        dml = session.query("SELECT * FROM Document WHERE ecm:primaryType NOT IN ('Folder', 'Noteq')");
        assertEquals(5, dml.size()); // 3 files, 1 proxy, 1 version

        /*
         * ecm:mixinType
         */

        dml = session.query("SELECT * FROM Document WHERE ecm:mixinType = 'Folderish'");
        assertEquals(3, dml.size());
        dml = session.query("SELECT * FROM Document WHERE ecm:mixinType = 'Downloadable'");
        assertEquals(5, dml.size()); // 3 files, 1 proxy, 1 version
        dml = session.query("SELECT * FROM Document WHERE ecm:mixinType = 'Versionable'");
        assertEquals(6, dml.size()); // 1 note, 3 files, 1 proxy, 1 version
        dml = session.query("SELECT * FROM Document WHERE ecm:mixinType IN ('Folderish', 'Downloadable')");
        assertEquals(8, dml.size()); // 3 folders, 3 files, 1 proxy, 1 version
        dml = session.query("SELECT * FROM Document WHERE ecm:mixinType NOT IN ('Folderish', 'Downloadable')");
        assertEquals(1, dml.size()); // 1 note
        // same with facet
        FacetFilter filter;
        filter = new FacetFilter(FacetNames.FOLDERISH, true);
        dml = session.query("SELECT * FROM Document ", filter);
        assertEquals(3, dml.size());
        filter = new FacetFilter(FacetNames.FOLDERISH, false);
        dml = session.query("SELECT * FROM Document ", filter);
        assertEquals(6, dml.size());
        filter = new FacetFilter(FacetNames.DOWNLOADABLE, true);
        dml = session.query("SELECT * FROM Document ", filter);
        assertEquals(5, dml.size()); // 3 files, 1 proxy, 1 version
        filter = new FacetFilter(FacetNames.DOWNLOADABLE, false);
        dml = session.query("SELECT * FROM Document ", filter);
        assertEquals(4, dml.size());
        filter = new FacetFilter(FacetNames.VERSIONABLE, true);
        dml = session.query("SELECT * FROM Document ", filter);
        assertEquals(6, dml.size()); // 1 note, 3 files, 1 proxy, 1 version
        filter = new FacetFilter(FacetNames.VERSIONABLE, false);
        dml = session.query("SELECT * FROM Document ", filter);
        assertEquals(3, dml.size());

        /*
         * ecm:currentLifeCycleState
         */
        dml = session.query("SELECT * FROM Document WHERE ecm:currentLifeCycleState = 'project'");
        // 3 folders, 1 note, 3 files, 1 proxy, 1 version
        assertEquals(9, dml.size());

        /*
         * ecm:versionLabel
         */
        dml = session.query("SELECT * FROM Document WHERE ecm:versionLabel = '0.1'");
        assertIdSet(dml, version.getId());

        /*
         * ecm:lock (deprecated, uses ecm:lockOwner actually)
         */
        dml = session.query("SELECT * FROM Document WHERE ecm:lock <> '_'");
        assertIdSet(dml, file1.getId());
        dml = session.query("SELECT * FROM Document ORDER BY ecm:lock");
        assertEquals(9, dml.size());

        /*
         * ecm:lockOwner
         */
        // don't use a '' here for Oracle, for which '' IS NULL
        dml = session.query("SELECT * FROM Document WHERE ecm:lockOwner <> '_'");
        assertIdSet(dml, file1.getId());
        dml = session.query("SELECT * FROM Document ORDER BY ecm:lockOwner");
        assertEquals(9, dml.size());

        /*
         * ecm:lockCreated
         */
        dml = session.query("SELECT * FROM Document ORDER BY ecm:lockCreated");
        assertEquals(9, dml.size());

        // ecm:fulltext tested below
    }

    public void testEmptyLifecycle() throws Exception {
        DocumentModelList dml;
        createDocs();
        String sql = "SELECT * FROM Document WHERE ecm:currentLifeCycleState <> 'deleted'";

        dml = session.query(sql);
        assertEquals(7, dml.size());

        // create a doc with no lifecycle associated
        DocumentModel doc = new DocumentModelImpl("/testfolder1", "mydoc",
                "MyDocType");
        doc = session.createDocument(doc);
        session.save();
        assertEquals("undefined", doc.getCurrentLifeCycleState());
        dml = session.query(sql);
        assertEquals(8, dml.size());
    }

    /**
     * Wait a bit to give time to the asynchronous fulltext extractor.
     * <p>
     * Subclassed for MS SQL Server which is itself asynchronous when indexing
     * fulltext.
     */
    protected void sleepForFulltext() {
        Framework.getLocalService(EventService.class).waitForAsyncCompletion();
        database.sleepForFulltext();
    }

    public void testFulltext() throws Exception {
        createDocs();
        sleepForFulltext();
        String query, nquery;
        DocumentModelList dml;
        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));
        DocumentModel file2 = session.getDocument(new PathRef(
                "/testfolder1/testfile2"));
        DocumentModel file3 = session.getDocument(new PathRef(
                "/testfolder1/testfile3"));
        DocumentModel file4 = session.getDocument(new PathRef(
                "/testfolder2/testfolder3/testfile4"));

        // query
        query = "SELECT * FROM File WHERE ecm:fulltext = 'world'";
        dml = session.query(query);
        assertEquals(0, dml.size());

        // negative query
        nquery = "SELECT * FROM File WHERE NOT(ecm:fulltext = 'world')";
        dml = session.query(nquery);
        assertIdSet(dml, file1.getId(), file2.getId(), file4.getId());

        file1.setProperty("dublincore", "title", "hello world");
        session.saveDocument(file1);
        session.save();
        sleepForFulltext();

        // query
        dml = session.query(query);
        assertIdSet(dml, file1.getId());

        // negative query
        dml = session.query(nquery);
        assertIdSet(dml, file2.getId(), file4.getId());

        file2.setProperty("dublincore", "description", "the world is my oyster");
        session.saveDocument(file2);
        session.save();
        sleepForFulltext();

        // query
        dml = session.query(query);
        assertIdSet(dml, file1.getId(), file2.getId());

        // negative query
        dml = session.query(nquery);
        assertIdSet(dml, file4.getId());

        file3.setProperty("dublincore", "title", "brave new world");
        session.saveDocument(file3);
        session.save();
        sleepForFulltext();

        // query
        dml = session.query(query);
        assertIdSet(dml, file1.getId(), file2.getId()); // file3 is a Note

        // negative query
        dml = session.query(nquery);
        assertIdSet(dml, file4.getId());

        query = "SELECT * FROM Note WHERE ecm:fulltext = 'world'";
        dml = session.query(query);
        assertIdSet(dml, file3.getId());

        query = "SELECT * FROM Document WHERE ecm:fulltext = 'world' "
                + "AND tl:contributors = 'pete'";
        sleepForFulltext();
        dml = session.query(query);
        assertIdSet(dml, file2.getId());

        // multi-valued field
        query = "SELECT * FROM Document WHERE ecm:fulltext = 'bzzt'";
        sleepForFulltext();
        dml = session.query(query);
        assertEquals(0, dml.size());
        file1.setPropertyValue("tl:subjects", new String[] { "bzzt" });
        session.saveDocument(file1);
        session.save();
        sleepForFulltext();
        query = "SELECT * FROM Document WHERE ecm:fulltext = 'bzzt'";
        dml = session.query(query);
        assertIdSet(dml, file1.getId());
    }

    public void testFulltextProxy() throws Exception {
        createDocs();
        sleepForFulltext();

        String query;
        DocumentModelList dml;

        DocumentModel doc = session.getDocument(new PathRef(
                "/testfolder2/testfolder3/testfile4"));
        String docId = doc.getId();

        query = "SELECT * FROM Document WHERE ecm:fulltext = 'testfile4Title'";
        dml = session.query(query);
        assertIdSet(dml, docId);

        // publish doc
        DocumentModel proxy = publishDoc();
        String proxyId = proxy.getId();
        String versionId = proxy.getSourceId();
        sleepForFulltext();

        // query must return also proxies and versions
        dml = session.query(query);
        assertIdSet(dml, docId, proxyId, versionId);

        // remove proxy
        session.removeDocument(proxy.getRef());
        session.save();

        // leaves live doc and version
        dml = session.query(query);
        assertIdSet(dml, docId, versionId);

        // remove live doc
        session.removeDocument(doc.getRef());
        session.save();

        // version gone as well
        dml = session.query(query);
        assertTrue(dml.isEmpty());
    }

    public void testFulltextExpressionSyntax() throws Exception {
        createDocs();
        sleepForFulltext();
        String query;
        DocumentModelList dml;
        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));

        file1.setProperty("dublincore", "title", "the world is my oyster");
        session.saveDocument(file1);
        session.save();
        sleepForFulltext();

        query = "SELECT * FROM File WHERE ecm:fulltext = 'pete'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'world'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = '+world'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'oyster'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'kangaroo'"; // absent
        dml = session.query(query);
        assertEquals(0, dml.size());

        // implicit AND

        query = "SELECT * FROM File WHERE ecm:fulltext = 'world oyster'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'world +oyster'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'world kangaroo'";
        dml = session.query(query);
        assertEquals(0, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'kangaroo oyster'";
        dml = session.query(query);
        assertEquals(0, dml.size());

        // NOT

        query = "SELECT * FROM File WHERE ecm:fulltext = 'world -oyster'";
        dml = session.query(query);
        assertEquals(0, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = '-world oyster'";
        dml = session.query(query);
        assertEquals(0, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'world -kangaroo'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'world -kangaroo -smurf'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = '-world kangaroo'";
        dml = session.query(query);
        assertEquals(0, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'kangaroo -oyster'";
        dml = session.query(query);
        assertEquals(0, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = '-kangaroo oyster'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        // OR

        query = "SELECT * FROM File WHERE ecm:fulltext = 'pete OR world'";
        dml = session.query(query);
        assertEquals(2, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'pete OR world smurf'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'pete OR world -smurf'";
        dml = session.query(query);
        assertEquals(2, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = '-smurf world OR pete'";
        dml = session.query(query);
        assertEquals(2, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'pete OR world oyster'";
        dml = session.query(query);
        assertEquals(2, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = 'pete OR world -oyster'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = '-oyster world OR pete'";
        dml = session.query(query);
        assertEquals(1, dml.size());
    }

    public void testFulltextExpressionPhrase() throws Exception {
        if (database instanceof DatabasePostgreSQL) {
            System.out.println("Skipping fulltext phrase test for unsupported database: "
                    + database.getClass().getName());
            return;
        }
        String query;
        DocumentModelList dml;

        DocumentModel file1 = new DocumentModelImpl("/", "testfile1", "File");
        file1.setPropertyValue("dc:title",
                "you can learn international law easily");
        file1 = session.createDocument(file1);
        session.save();
        sleepForFulltext();

        query = "SELECT * FROM File WHERE ecm:fulltext = '\"international law\"'";
        dml = session.query(query);
        assertEquals(1, dml.size());

        query = "SELECT * FROM File WHERE ecm:fulltext = '\"learn law\"'";
        dml = session.query(query);
        assertEquals(0, dml.size());
    }

    public void testFulltextSecondary() throws Exception {
        if (!database.supportsMultipleFulltextIndexes()) {
            System.out.println("Skipping multi-fulltext test for unsupported database: "
                    + database.getClass().getName());
            return;
        }
        createDocs();
        String query;
        DocumentModelList dml;
        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));
        DocumentModel file2 = session.getDocument(new PathRef(
                "/testfolder1/testfile2"));
        DocumentModel file3 = session.getDocument(new PathRef(
                "/testfolder1/testfile3"));

        file1.setProperty("dublincore", "title", "hello world");
        session.saveDocument(file1);
        file2.setProperty("dublincore", "description", "the world is my oyster");
        session.saveDocument(file2);
        file3.setProperty("dublincore", "title", "brave new world");
        session.saveDocument(file3);
        session.save();
        sleepForFulltext();

        // check main fulltext index
        query = "SELECT * FROM Document WHERE ecm:fulltext = 'world'";
        dml = session.query(query);
        assertIdSet(dml, file1.getId(), file2.getId(), file3.getId());
        // check secondary fulltext index, just for title field
        query = "SELECT * FROM Document WHERE ecm:fulltext_title = 'world'";
        dml = session.query(query);
        assertIdSet(dml, file1.getId(), file3.getId()); // file2 has it in descr

        // field-based fulltext
        // index exists
        query = "SELECT * FROM Document WHERE ecm:fulltext.dc:title = 'brave'";
        dml = session.query(query);
        assertIdSet(dml, file3.getId());
        // no index exists
        query = "SELECT * FROM Document WHERE ecm:fulltext.dc:description = 'oyster'";
        dml = session.query(query);
        assertIdSet(dml, file2.getId());
        query = "SELECT * FROM Document WHERE ecm:fulltext.dc:description = 'world OYSTER'";
        dml = session.query(query);
        assertIdSet(dml, file2.getId());
    }

    public void testFulltextBlob() throws Exception {
        deployBundle("org.nuxeo.ecm.core.convert.api");
        deployBundle("org.nuxeo.ecm.core.convert");
        deployBundle("org.nuxeo.ecm.core.convert.plugins");

        createDocs();
        sleepForFulltext();
        String query;
        DocumentModelList dml;
        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));
        query = "SELECT * FROM File WHERE ecm:isProxy = 0 AND ecm:fulltext = 'restaurant'";
        dml = session.query(query);
        assertIdSet(dml, file1.getId());
        query = "SELECT * FROM File WHERE ecm:isProxy = 1 AND ecm:fulltext = 'restaurant'";
        dml = session.query(query);
        assertEquals(0, dml.size());
        query = "SELECT * FROM File WHERE ecm:fulltext = 'restaurant'";
        dml = session.query(query);
        assertIdSet(dml, file1.getId());
        // check text extraction with '\0' in it
        String content = "Text with a \0 in it";
        ByteArrayBlob blob1 = new ByteArrayBlob(content.getBytes("UTF-8"),
                "text/plain");
        file1.setPropertyValue("content", blob1);
        session.saveDocument(file1);
        session.save();
        sleepForFulltext();
    }

    public void testFullTextCopy() throws Exception {
        createDocs();
        String query;
        DocumentModelList dml;
        DocumentModel folder1 = session.getDocument(new PathRef("/testfolder1"));
        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));
        file1.setProperty("dublincore", "title", "hello world");
        session.saveDocument(file1);
        session.save();
        sleepForFulltext();

        query = "SELECT * FROM File WHERE ecm:fulltext = 'world'";

        dml = session.query(query);
        assertIdSet(dml, file1.getId());

        // copy
        DocumentModel copy = session.copy(file1.getRef(), folder1.getRef(),
                "file1Copy");
        // the save is needed to update the read acls
        session.save();
        sleepForFulltext();

        dml = session.query(query);
        assertIdSet(dml, file1.getId(), copy.getId());
    }

    public void testOrderByAndDistinct() throws Exception {
        createDocs();

        String query;
        DocumentModelList dml;

        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));
        file1.setProperty("dublincore", "title", "hello world 1");

        session.saveDocument(file1);
        session.save();

        sleepForFulltext();

        query = "SELECT * FROM File Where dc:title = 'hello world 1' ORDER BY ecm:currentLifeCycleState";

        dml = session.query(query);

        assertIdSet(dml, file1.getId());
        query = "SELECT * FROM File Where dc:title = 'hello world 1' ORDER BY ecm:versionLabel";

        dml = session.query(query);
        assertIdSet(dml, file1.getId());
    }

    public void testQueryIterable() throws Exception {
        createDocs();

        IterableQueryResult res = session.queryAndFetch("SELECT * FROM File",
                "NXQL");
        List<Map<String, Serializable>> l = new LinkedList<Map<String, Serializable>>();
        for (Map<String, Serializable> x : res) {
            l.add(x);
        }
        assertEquals(3, l.size());
        res.close();

        // cursor behavior
        res = session.queryAndFetch("SELECT * FROM File", "NXQL");
        Iterator<Map<String, Serializable>> it = res.iterator();
        assertEquals(0, res.pos());
        it.next();
        assertEquals(1, res.pos());
        assertEquals(3, res.size());
        assertEquals(1, res.pos());
        assertTrue(it.hasNext());
        assertEquals(1, res.pos());
        it.next();
        assertEquals(2, res.pos());
        assertTrue(it.hasNext());
        assertEquals(2, res.pos());
        it.next();
        assertEquals(3, res.pos());
        assertFalse(it.hasNext());
        assertEquals(3, res.pos());

        res.skipTo(1);
        assertEquals(3, res.size());
        assertTrue(it.hasNext());
        assertEquals(1, res.pos());
        it.next();
        assertEquals(2, res.pos());
        res.close();

        // checking size when at end
        res = session.queryAndFetch("SELECT * FROM File", "NXQL");
        it = res.iterator();
        it.next();
        it.next();
        it.next();
        assertFalse(it.hasNext());
        assertEquals(3, res.size());
        res.close();

        // size when query returns nothing
        res = session.queryAndFetch(
                "SELECT * FROM File WHERE dc:title = 'zzz'", "NXQL");
        it = res.iterator();
        assertFalse(it.hasNext());
        assertEquals(0, res.size());
        res.close();
    }

    public void testQueryIterableWithTransformer() throws Exception {
        createDocs();
        IterableQueryResult res;

        res = session.queryAndFetch("SELECT * FROM Document", "NXQL");
        assertEquals(7, res.size());
        res.close();

        // NoFile2SecurityPolicy
        deployContrib("org.nuxeo.ecm.core.storage.sql.test.tests",
                "OSGI-INF/security-policy2-contrib.xml");

        res = session.queryAndFetch("SELECT * FROM Document", "NXQL");
        assertEquals(4, res.size());
        res.close();
    }

    public void testQueryComplexTypeFiles() throws Exception {
        DocumentModel doc = new DocumentModelImpl("/", "myfile", "File");
        List<Object> files = new LinkedList<Object>();
        Map<String, Object> f = new HashMap<String, Object>();
        f.put("filename", "f1");
        files.add(f);
        doc.setProperty("files", "files", files);
        doc = session.createDocument(doc);
        session.save();

        DocumentModelList dml = session.query("SELECT * FROM File");
        assertEquals(1, dml.size());
        // with MySQL was logging:
        // ERROR Unknown document type: file
        // due to its case-insensitivity in = and IN tests...
        // and returning an empty query, cf SQLQueryResult.getDocumentModels
    }

    public void testSelectColumns() throws Exception {
        String query;
        IterableQueryResult res;
        Iterator<Map<String, Serializable>> it;
        Map<String, Serializable> map;

        createDocs();

        // check proper tables are joined even if not in FROM
        query = "SELECT ecm:uuid, dc:title FROM File";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(3, res.size());
        map = res.iterator().next();
        assertTrue(map.containsKey("dc:title"));
        assertTrue(map.containsKey(NXQL.ECM_UUID));
        res.close();

        // check with no proxies (no subselect)
        query = "SELECT ecm:uuid, dc:title FROM File where ecm:isProxy = 0";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(3, res.size());
        res.close();

        // check content
        query = "SELECT ecm:uuid, dc:title FROM File ORDER BY dc:title";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(3, res.size());
        it = res.iterator();
        map = it.next();
        assertEquals("testfile1_Title", map.get("dc:title"));
        map = it.next();
        assertEquals("testfile2_Title", map.get("dc:title"));
        map = it.next();
        assertEquals("testfile4Title", map.get("dc:title"));
        res.close();

        // check content with no proxies (simpler query with no UNION ALL)
        query = "SELECT ecm:uuid, dc:title FROM File WHERE ecm:isProxy = 0 ORDER BY dc:title";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(3, res.size());
        it = res.iterator();
        map = it.next();
        assertEquals("testfile1_Title", map.get("dc:title"));
        map = it.next();
        assertEquals("testfile2_Title", map.get("dc:title"));
        map = it.next();
        assertEquals("testfile4Title", map.get("dc:title"));
        res.close();
    }

    public void testSelectColumnsSameName() throws Exception {
        String query;
        IterableQueryResult res;
        Map<String, Serializable> map;

        // two fields with same key
        DocumentModel file = new DocumentModelImpl("/", "testfile", "File2");
        file.setPropertyValue("dc:title", "title1");
        file.setPropertyValue("tst2:title", "title2");
        file = session.createDocument(file);
        session.save();

        query = "SELECT tst2:title, dc:title FROM File WHERE dc:title = 'title1' AND ecm:isProxy = 0";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(1, res.size());
        map = res.iterator().next();
        assertEquals("title1", map.get("dc:title"));
        assertEquals("title2", map.get("tst2:title"));
        res.close();

        // now with proxies, which needs a subselect and re-selects columns
        query = "SELECT tst2:title, dc:title FROM File WHERE dc:title = 'title1' ORDER BY ecm:uuid";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(1, res.size());
        map = res.iterator().next();
        assertEquals("title1", map.get("dc:title"));
        assertEquals("title2", map.get("tst2:title"));
        res.close();

        // same without ORDER BY
        query = "SELECT tst2:title, dc:title FROM File WHERE dc:title = 'title1'";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(1, res.size());
        map = res.iterator().next();
        assertEquals("title1", map.get("dc:title"));
        assertEquals("title2", map.get("tst2:title"));
        res.close();
    }

    public void testSelectColumnsDistinct() throws Exception {
        String query;
        IterableQueryResult res;

        createDocs();

        query = "SELECT DISTINCT dc:title FROM File";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(3, res.size());
        res.close();

        // some parents are identical
        query = "SELECT DISTINCT ecm:parentId FROM File";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(2, res.size());
        res.close();

        // without column aliasing
        query = "SELECT DISTINCT ecm:parentId FROM File WHERE ecm:isProxy = 0";
        res = session.queryAndFetch(query, "NXQL");
        assertEquals(2, res.size());
        res.close();
    }

    // ----- timestamp tests -----

    protected Date setupDocTest() throws Exception {
        Date currentDate = new Date();
        DocumentModel testDocument = new DocumentModelImpl("/", "testfolder1",
                "Folder");
        testDocument.setPropertyValue("dc:title", "test");
        testDocument.setPropertyValue("dc:modified", currentDate);
        testDocument = session.createDocument(testDocument);
        session.save();
        return ((Calendar) testDocument.getPropertyValue("dc:modified")).getTime();
    }

    protected static Date addSecond(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, 1);
        return cal.getTime();
    }

    protected static String formatTimestamp(Date date) {
        return new SimpleDateFormat("'TIMESTAMP' ''yyyy-MM-dd HH:mm:ss.SSS''").format(date);
    }

    public void testEqualsTimeWithMilliseconds() throws Exception {
        Date currentDate = setupDocTest();
        String testQuery = String.format(
                "SELECT * FROM Folder WHERE dc:title = 'test' AND dc:modified = %s"
                        + " AND ecm:isProxy = 0", formatTimestamp(currentDate));
        DocumentModelList docs = session.query(testQuery);
        assertEquals(1, docs.size());
    }

    public void testLTTimeWithMilliseconds() throws Exception {
        Date currentDate = setupDocTest();
        // add a second to be sure that the document is found
        currentDate = addSecond(currentDate);
        String testQuery = String.format(
                "SELECT * FROM Folder WHERE dc:title = 'test' AND dc:modified < %s"
                        + " AND ecm:isProxy = 0", formatTimestamp(currentDate));
        DocumentModelList docs = session.query(testQuery);
        assertEquals(1, docs.size());
    }

    public void testQueryIsNull() throws Exception {
        DocumentModelList dml;
        createDocs();

        dml = session.query("SELECT * FROM File WHERE dc:title IS NOT NULL");
        assertEquals(3, dml.size());
        dml = session.query("SELECT * FROM File WHERE dc:title IS NULL");
        assertEquals(0, dml.size());

        DocumentModel file1 = session.getDocument(new PathRef(
                "/testfolder1/testfile1"));
        file1.setPropertyValue("dc:title", null);
        session.saveDocument(file1);
        session.save();

        dml = session.query("SELECT * FROM File WHERE dc:title IS NOT NULL");
        assertEquals(2, dml.size());
        dml = session.query("SELECT * FROM File WHERE dc:title IS NULL");
        assertEquals(1, dml.size());

        // we didn't write the uid schema for all files
        dml = session.query("SELECT * FROM File WHERE uid IS NOT NULL");
        assertEquals(1, dml.size());
        dml = session.query("SELECT * FROM File WHERE uid IS NULL");
        assertEquals(2, dml.size());
    }
    
    public void testMultilineQuery() throws Exception {
        DocumentModelList dml;
        createDocs();
        
        String query = "SELECT * \n 		FROM File \n      WHERE dc:title IS NOT NULL \n       ORDER BY ecm:path";
        dml = session.query(query);
        assertEquals(3, dml.size());
        
        query = "SELECT * \r\n        FROM File \r\n      WHERE dc:title IS NULL \r\n       ORDER BY ecm:path DESC";
        dml = session.query(query);
        assertEquals(0, dml.size());
    }

}
