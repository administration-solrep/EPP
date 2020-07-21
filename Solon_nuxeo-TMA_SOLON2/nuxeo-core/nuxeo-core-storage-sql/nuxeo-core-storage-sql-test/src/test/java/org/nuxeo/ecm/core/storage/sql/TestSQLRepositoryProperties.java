/*
 * Copyright (c) 2006-2011 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     <a href="mailto:at@nuxeo.com">Anahide Tchertchian</a>
 *
 * $Id$
 */

package org.nuxeo.ecm.core.storage.sql;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolderAdapterService;
import org.nuxeo.ecm.core.api.externalblob.ExternalBlobAdapter;
import org.nuxeo.ecm.core.api.externalblob.FileSystemExternalBlobAdapter;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.api.model.impl.primitives.ExternalBlobProperty;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.schema.types.Field;
import org.nuxeo.ecm.core.schema.types.ListType;
import org.nuxeo.ecm.core.schema.types.Type;
import org.nuxeo.runtime.api.Framework;

/**
 * @author Anahide Tchertchian
 *
 */
@SuppressWarnings("unchecked")
public class TestSQLRepositoryProperties extends SQLRepositoryTestCase {

    DocumentModel doc;

    public TestSQLRepositoryProperties(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        deployContrib("org.nuxeo.ecm.core.storage.sql.test.tests",
                "OSGI-INF/test-repo-core-types-contrib.xml");

        // deploy specific adapter for testing external blobs: files are stored
        // in temporary directory
        deployContrib("org.nuxeo.ecm.core.storage.sql.test.tests",
                "OSGI-INF/test-externalblob-adapters-contrib.xml");
        // set container to temp directory here in case that depends on the OS
        // or machine configuration and add funny characters to avoid problems
        // due to xml parsing
        BlobHolderAdapterService service = Framework.getService(BlobHolderAdapterService.class);
        assertNotNull(service);
        ExternalBlobAdapter adapter = service.getExternalBlobAdapterForPrefix("fs");
        Map<String, String> props = new HashMap<String, String>();
        props.put(FileSystemExternalBlobAdapter.CONTAINER_PROPERTY_NAME, "\n"
                + System.getProperty("java.io.tmpdir") + " ");
        adapter.setProperties(props);

        openSession();
        doc = session.createDocumentModel("TestDocument");
        doc.setPathInfo("/", "doc");
        doc = session.createDocument(doc);
    }

    protected File createTempFile() throws Exception {
        File file = File.createTempFile("testExternalBlob", ".txt");
        FileWriter fstream = new FileWriter(file);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Hello External Blob");
        out.close();
        file.deleteOnExit();
        return file;
    }

    // NXP-2467
    public void testCreationWithDefaultPrefetch() throws Exception {
        DocumentModel doc = session.createDocumentModel("TestDocumentWithDefaultPrefetch");
        doc.setPathInfo("/", "docwithDefaultPrefetch");
        session.createDocument(doc);
    }

    public void testStringArray() throws Exception {
        assertNotNull(doc.getPropertyValue("tp:stringArray"));
        String[] values = { "foo", "bar" };
        doc.setPropertyValue("tp:stringArray", values);
        doc = session.saveDocument(doc);
        assertTrue(Arrays.equals(values,
                (Object[]) doc.getPropertyValue("tp:stringArray")));
    }

    // NXP-2454
    public void testDateArray() throws Exception {
        assertNotNull(doc.getPropertyValue("tp:dateArray"));
        Calendar cal = Calendar.getInstance();
        cal.set(2008, 6, 10);
        Calendar[] values = { cal };
        doc.setPropertyValue("tp:dateArray", values);
        doc = session.saveDocument(doc);
        // currently returning long[] instead of Calendar[]
        assertTrue(Arrays.equals(values,
                (Object[]) doc.getPropertyValue("tp:dateArray")));
    }

    // NXP-2454
    public void testIntArray() throws Exception {
        assertNotNull(doc.getPropertyValue("tp:intArray"));
        Long[] values = { 1L, 2L, 3L };
        doc.setPropertyValue("tp:intArray", values);
        doc = session.saveDocument(doc);
        // currently returning long[], maybe this is the wanted behaviour (?)
        assertTrue(Arrays.equals(values,
                (Object[]) doc.getPropertyValue("tp:intArray")));
    }

    public void testComplexList() throws Exception {
        // not null on list
        assertTrue(doc.getPropertyValue("tp:complexList") instanceof List);
        assertEquals(0, ((List) doc.getPropertyValue("tp:complexList")).size());
        ArrayList<Map<String, Serializable>> values = new ArrayList<Map<String, Serializable>>();
        Map<String, Serializable> item = new HashMap<String, Serializable>();
        item.put("string", "foo");
        Calendar cal = Calendar.getInstance();
        cal.set(2008, 6, 10);
        item.put("date", cal);
        item.put("int", 3L);
        values.add(item);
        doc.setPropertyValue("tp:complexList", values);
        doc = session.saveDocument(doc);

        Serializable actual = doc.getPropertyValue("tp:complexList");
        assertTrue(actual instanceof List);
        assertEquals(1, ((List) actual).size());
        assertEquals(item, ((List) actual).get(0));
    }

    // NXP-912
    public void testNewBlob() throws Exception {
        // simple
        Object value = null;
        SchemaManager tm = Framework.getService(SchemaManager.class);
        Field field = tm.getField("tp:fileList");
        Type type = field.getType();
        Type itemType = ((ListType) type).getFieldType();
        value = itemType.newInstance();
        assertNull(value);

        // complex
        field = tm.getField("tp:fileComplexList");
        type = field.getType();
        itemType = ((ListType) type).getFieldType();
        Map<String, Serializable> map = (Map) itemType.newInstance();
        assertEquals(2, map.size());
        assertTrue(map.containsKey("filename"));
        assertTrue(map.containsKey("blob"));
        assertNull(map.get("filename"));
        assertNull(map.get("blob"));
    }

    // NXP-2468
    public void testBlobListValue() throws Exception {
        // not null on list
        assertTrue(doc.getPropertyValue("tp:fileList") instanceof List);
        assertEquals(0, ((List) doc.getPropertyValue("tp:fileList")).size());
        ArrayList<Blob> values = new ArrayList<Blob>();
        StringBlob blob = new StringBlob("My content");
        values.add(blob);
        doc.setPropertyValue("tp:fileList", values);
        doc = session.saveDocument(doc);

        Serializable actual = doc.getPropertyValue("tp:fileList");
        assertTrue(actual instanceof List);

        List<Blob> blobs = (List) actual;
        assertEquals(1, blobs.size());
        assertNotNull(blobs.get(0));
    }

    // NXP-2301
    public void testSubBlobValue() throws Exception {
        // not null on list
        assertTrue(doc.getPropertyValue("tp:fileComplexList") instanceof List);
        assertEquals(0,
                ((List) doc.getPropertyValue("tp:fileComplexList")).size());
        ArrayList<Map<String, Serializable>> values = new ArrayList<Map<String, Serializable>>();
        Map<String, Serializable> item = new HashMap<String, Serializable>();
        StringBlob blob = new StringBlob("My content");
        item.put("blob", blob);
        item.put("filename", "My filename");
        values.add(item);
        doc.setPropertyValue("tp:fileComplexList", values);
        doc = session.saveDocument(doc);

        Object actual = doc.getPropertyValue("tp:fileComplexList");
        assertTrue(actual instanceof List);
        List<Map<String, Serializable>> items = (List) actual;
        assertEquals(1, items.size());
        assertNotNull(items.get(0));
        Map<String, Serializable> actualItem = items.get(0);
        assertEquals("My filename", actualItem.get("filename"));
        assertTrue(actualItem.get("blob") instanceof Blob);

        Object actualBlob = doc.getProperty("tp:fileComplexList/0/blob").getValue(
                Blob.class);
        assertTrue(actualBlob instanceof Blob);
    }

    public void testComplexPropertyChain() throws Exception {
        Property p = doc.getProperty("tp:complexChain");
        assertTrue(p.getValue() instanceof Map);
        assertEquals(2, ((Map) p.getValue()).size());
        p.setValue("string", "test");
        Map<String, Serializable> map = new HashMap<String, Serializable>();
        map.put("string", "test2");
        p.setValue("complex", map);

        doc = session.saveDocument(doc);

        p = doc.getProperty("tp:complexChain");
        assertTrue(p.getValue() instanceof Map);
        assertEquals("test", p.getValue("string"));
        assertEquals("test2", p.getValue("complex/string"));
        p = p.get("complex");
        assertTrue(p.getValue() instanceof Map);
        assertEquals("test2", p.getValue("string"));
    }

    public void testComplexPropertySubValue() throws Exception {
        doc.setPropertyValue("tp:complex/string", "test");
        doc = session.saveDocument(doc);
        assertEquals("test", doc.getPropertyValue("tp:complex/string"));
    }

    // NXP-2318: i don't get what's supposed to be answered to these questions
    public void XXXtestArrayOrListProperties() throws Exception {
        Property prop = doc.getProperty("tp:stringArray");
        assertFalse(prop.isContainer());
        assertFalse(prop.isList());
        assertTrue(prop.isScalar());

        prop = doc.getProperty("tp:dateArray");
        assertFalse(prop.isContainer());
        assertFalse(prop.isList());
        assertTrue(prop.isScalar());

        prop = doc.getProperty("tp:intArray");
        assertFalse(prop.isContainer());
        assertFalse(prop.isList());
        assertTrue(prop.isScalar());

        prop = doc.getProperty("tp:complex");
        assertTrue(prop.isContainer());
        assertFalse(prop.isList());
        assertFalse(prop.isScalar());

        prop = doc.getProperty("tp:complexList");
        assertTrue(prop.isContainer());
        assertTrue(prop.isList());
        assertFalse(prop.isScalar());
    }

    public void testExternalBlobDocumentProperty() throws Exception {
        File file = createTempFile();
        HashMap<String, String> map = new HashMap<String, String>();
        String uri = String.format("fs:%s", file.getName());
        map.put(ExternalBlobProperty.URI, uri);
        map.put(ExternalBlobProperty.FILE_NAME, "hello.txt");
        doc.setPropertyValue("tp:externalcontent", map);
        doc = session.saveDocument(doc);

        Object blob = doc.getPropertyValue("tp:externalcontent");

        assertNotNull(blob);
        assertTrue(blob instanceof Blob);
        assertEquals("Hello External Blob", ((Blob) blob).getString());
        assertEquals("hello.txt", ((Blob) blob).getFilename());
        assertEquals("hello.txt",
                doc.getPropertyValue("tp:externalcontent/name"));
        assertEquals(uri, doc.getPropertyValue("tp:externalcontent/uri"));
    }

    // this time only set the uri
    public void testExternalBlobDocumentProperty2() throws Exception {
        File file = createTempFile();
        String uri = String.format("fs:%s", file.getName());
        doc.setPropertyValue("tp:externalcontent/uri", uri);
        doc = session.saveDocument(doc);

        Object blob = doc.getPropertyValue("tp:externalcontent");

        assertNotNull(blob);
        assertTrue(blob instanceof Blob);
        assertEquals("Hello External Blob", ((Blob) blob).getString());
        assertEquals(file.getName(), ((Blob) blob).getFilename());
        assertNull(doc.getPropertyValue("tp:externalcontent/name"));
        assertEquals(uri, doc.getPropertyValue("tp:externalcontent/uri"));
    }

    // NXP-2468
    public void testExternalBlobListValue() throws Exception {
        // not null on list
        String propName = "tp:externalFileList";
        assertTrue(doc.getPropertyValue(propName) instanceof List);
        assertEquals(0, ((List) doc.getPropertyValue(propName)).size());

        File file = createTempFile();
        ArrayList<Map> values = new ArrayList<Map>();
        Map<String, String> map = new HashMap<String, String>();
        String uri = String.format("fs:%s", file.getName());
        map.put(ExternalBlobProperty.URI, uri);
        map.put(ExternalBlobProperty.FILE_NAME, "hello.txt");
        values.add(map);

        doc.setPropertyValue(propName, values);
        doc = session.saveDocument(doc);

        Serializable actual = doc.getPropertyValue(propName);
        assertTrue(actual instanceof List);
        List<Blob> blobs = (List) actual;
        assertEquals(1, blobs.size());
        assertNotNull(blobs.get(0));
        assertTrue(blobs.get(0) instanceof Blob);
        Blob actualBlob = blobs.get(0);
        assertEquals("Hello External Blob", actualBlob.getString());
        assertEquals("hello.txt", actualBlob.getFilename());
        assertEquals("hello.txt", doc.getPropertyValue(propName + "/0/name"));
        assertEquals(uri, doc.getPropertyValue(propName + "/0/uri"));
    }

    // NXP-2301
    public void testSubExternalBlobValue() throws Exception {
        String propName = "tp:externalFileComplexList";
        // not null on list
        assertTrue(doc.getPropertyValue(propName) instanceof List);
        assertEquals(0, ((List) doc.getPropertyValue(propName)).size());
        ArrayList<Map<String, Serializable>> values = new ArrayList<Map<String, Serializable>>();
        Map<String, Serializable> item = new HashMap<String, Serializable>();

        File file = createTempFile();
        HashMap<String, String> blobMap = new HashMap<String, String>();
        String uri = String.format("fs:%s", file.getName());
        blobMap.put(ExternalBlobProperty.URI, uri);
        blobMap.put(ExternalBlobProperty.FILE_NAME, "hello.txt");

        item.put("blob", blobMap);
        item.put("filename", "My filename");
        values.add(item);
        doc.setPropertyValue(propName, values);
        doc = session.saveDocument(doc);

        Object actual = doc.getPropertyValue(propName);
        assertTrue(actual instanceof List);
        List<Map<String, Serializable>> items = (List) actual;
        assertEquals(1, items.size());
        assertNotNull(items.get(0));
        Map<String, Serializable> actualItem = items.get(0);
        assertEquals("My filename", actualItem.get("filename"));
        assertTrue(actualItem.get("blob") instanceof Blob);

        Object actualBlob = doc.getProperty(propName + "/0/blob").getValue();
        assertTrue(actualBlob instanceof Blob);
        assertEquals("Hello External Blob", ((Blob) actualBlob).getString());
        assertEquals("hello.txt", ((Blob) actualBlob).getFilename());
        assertEquals("hello.txt", doc.getPropertyValue(propName
                + "/0/blob/name"));
        assertEquals(uri, doc.getPropertyValue(propName + "/0/blob/uri"));
    }

    public void testSaveComplexTwice() throws Exception {
        testComplexList();
        doc.setPropertyValue("tp:stringArray", new String[] {}); // dirty dp
        doc = session.saveDocument(doc); // rewrites complex list again
        session.save();
    }

    // not many tests, logs have to be looked at to confirm behavior
    public void testUpdateMinimalChanges() throws Exception {
        // populate some properties
        testStringArray();
        testDateArray();
        testComplexList();
        testBlobListValue();
        session.save();
        closeSession();
        openSession();
        // change just one of the collection properties
        doc.setPropertyValue("tp:stringArray", new String[] { "baz" });
        doc = session.saveDocument(doc);
        session.save();
        // check that the minimal number of updates are done in the db
    }

}
