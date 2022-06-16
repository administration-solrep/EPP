package org.nuxeo.ecm.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.schema.PrefetchInfo;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

/**
 * @since 11.1
 */
@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
public class TestPrefetchSW {

    @Inject
    protected CoreSession session;

    @Inject
    protected TransactionalFeature txFeature;

    
    @Test
    public void testDocumentModelPrefetchWithTable() {
        DocumentModel doc = session.createDocumentModel("/", "foo", "File");
        doc.setPropertyValue("dc:title", "foo/title");
        doc.setPropertyValue("dc:description", "foo/description");
        doc.setPropertyValue("common:icon", "foo/icon"); // not prefetched by default
        doc.setPropertyValue("uid:uid", "foo/uid"); // not prefetched by default
        doc = session.createDocument(doc);

        // make sure we are in a new clean transaction
        txFeature.nextTransaction();

        doc = session.getDocument(doc.getRef());
        doc.detach(false);
        
        assertEquals("foo/title", doc.getPropertyValue("dc:title"));
        assertEquals("foo/description", doc.getPropertyValue("dc:description"));
        assertNull(doc.getPropertyValue("common:icon"));
        assertNull(doc.getPropertyValue("uid:uid"));
        
        // make sure we are in a new clean transaction
        txFeature.nextTransaction();
        
        PrefetchInfo prefetchInfo = new PrefetchInfo("common,uid");
        
        DocumentModelList documentModelList = session.getDocuments(Collections.singletonList(doc.getId()), prefetchInfo);
        assertEquals(1, documentModelList.size());
        doc = documentModelList.get(0);
        doc.detach(false);

        assertNull(doc.getPropertyValue("dc:title"));
        assertNull(doc.getPropertyValue("dc:description"));
        assertEquals("foo/icon", doc.getPropertyValue("common:icon"));
        assertEquals("foo/uid", doc.getPropertyValue("uid:uid"));
    }

    

    @Test
    @Deploy("org.nuxeo.ecm.core.test.tests:OSGI-INF/test-repo-core-types-contrib.xml")
    public void testDocumentModelPrefetchWithField2() {
    	String[] subjects = new String[] {"foo/subject1", "foo/subject2", "foo/subject3"};
        DocumentModel doc = session.createDocumentModel("/", "foo", "MyDocType");
        doc.setPropertyValue("dc:title", "foo/title");
        doc.setPropertyValue("dc:description", "foo/description");
        doc.setPropertyValue("dc:subjects", subjects);
        doc.setPropertyValue("book:price", "1234"); // not prefetched
        doc.setPropertyValue("book:title", "book/title"); // not prefetched
        doc = session.createDocument(doc);
        
        // make sure we are in a new clean transaction
        txFeature.nextTransaction();

        doc = session.getDocument(doc.getRef());
        doc.detach(false);
        // use default prefetch
        assertEquals("foo/title", doc.getPropertyValue("dc:title"));
        assertArrayEquals(subjects,  (String[]) doc.getPropertyValue("dc:subjects"));
        assertNull(doc.getPropertyValue("book:title"));
        assertNull(doc.getPropertyValue("book:price"));
        
        // make sure we are in a new clean transaction
        txFeature.nextTransaction();

        // use custom prefetch
        PrefetchInfo prefetchInfo = new PrefetchInfo("book.title");
        DocumentModelList documentModelList = session.getDocuments(Collections.singletonList(doc.getId()), prefetchInfo);
        assertEquals(1,documentModelList.size());
                doc = documentModelList.get(0);
        doc.detach(false);

        assertNull(doc.getPropertyValue("dc:title"));
        assertNull(doc.getPropertyValue("dc:subjects"));
        assertEquals("book/title", doc.getPropertyValue("book:title"));
        assertNull(doc.getPropertyValue("book:price"));
        
     // use custom prefetch
        prefetchInfo = new PrefetchInfo("book.title,dc:subjects");
        documentModelList = session.getDocuments(Collections.singletonList(doc.getId()), prefetchInfo);
        assertEquals(1,documentModelList.size());
                doc = documentModelList.get(0);
        doc.detach(false);

        assertNull(doc.getPropertyValue("dc:title"));
        assertArrayEquals(subjects,  (String[]) doc.getPropertyValue("dc:subjects"));
        assertEquals("book/title", doc.getPropertyValue("book:title"));
        assertNull(doc.getPropertyValue("book:price"));
    }

    @Test
    @Deploy("org.nuxeo.ecm.core.test.tests:OSGI-INF/test-repo-core-types-contrib.xml")
    public void testDocumentModelPrefetchWithComplexFields() {
        List<Map<String, Serializable>> complexList = Arrays.asList(TestSQLRepositoryProperties.map("foo", "foo1", "bar", "bar1"));
        Map<String, Serializable> complex = TestSQLRepositoryProperties.map("foo", "foo1", "bar", null);
        
        
        DocumentModel doc = session.createDocumentModel("/", "foo", "MyDocType2");
        
        doc.setPropertyValue("cpxl:complexList", (Serializable) complexList);
        doc.setPropertyValue("cpx:complex", (Serializable) complex);
        doc = session.createDocument(doc);
        session.save();
        
        assertEquals(complexList, doc.getPropertyValue("cpxl:complexList"));
        assertEquals(complex, doc.getPropertyValue("cpx:complex"));
        
        // make sure we are in a new clean transaction
        txFeature.nextTransaction();
        
        doc = session.getDocument(doc.getRef());
        doc.detach(false);
        assertEquals(new ArrayList<>(), doc.getPropertyValue("cpxl:complexList"));
        assertNull(doc.getPropertyValue("cpx:complex/foo"));
 
        List<String> listIds = Collections.singletonList(doc.getId());
        
        
        
        // Without custom prefetch, nothing is found !
        DocumentModelList documentModelList = session.getDocuments(listIds, new PrefetchInfo("complex,complexList"));
        assertEquals(1,documentModelList.size());
                doc = documentModelList.get(0);
        doc.detach(false);
        
        assertEquals(complexList, doc.getPropertyValue("cpxl:complexList"));
        assertEquals(complex, doc.getPropertyValue("cpx:complex"));
        
        
        // make sure we are in a new clean transaction
        txFeature.nextTransaction();
        
        documentModelList = session.getDocuments(listIds, new PrefetchInfo("cpxl:complexList/*/foo,cpx:complex/foo"));
        assertEquals(1,documentModelList.size());
                doc = documentModelList.get(0);
        doc.detach(false);
        
        assertEquals("foo1", doc.getPropertyValue("cpxl:complexList/0/foo"));
        assertEquals("foo1", doc.getPropertyValue("cpx:complex/foo"));
        assertNull(doc.getPropertyValue("cpx:complex/bar"));


    }

    
    
}
