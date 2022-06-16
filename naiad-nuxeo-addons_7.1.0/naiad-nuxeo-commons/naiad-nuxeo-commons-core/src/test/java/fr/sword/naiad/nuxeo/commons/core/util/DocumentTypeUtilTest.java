package fr.sword.naiad.nuxeo.commons.core.util;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({ "fr.sword.naiad.nuxeo.commons.core" })
@LocalDeploy({"fr.sword.naiad.nuxeo.commons.core:OSGI-INF/test-doc-type.xml"})
public class DocumentTypeUtilTest {
	private static final String DOCUMENT = "Document";

	private static final String DOCTEST1 = "DocTest";

	private static final String DOCTEST2 = "DocTest2";

	private static final String DOCTEST3 = "DocTest3";

	private static final String DOCTEST4 = "DocTest4";

	private static final Set<String> DOCUMENT_TYPE_HIERARCHY = new HashSet<>();
	static {
		DOCUMENT_TYPE_HIERARCHY.add(DOCUMENT);
	}

	private static final Set<String> DOCTEST1_TYPE_HIERARCHY = new HashSet<>();
	static {
		DOCTEST1_TYPE_HIERARCHY.add(DOCUMENT);
		DOCTEST1_TYPE_HIERARCHY.add(DOCTEST1);
	}

	private static final Set<String> DOCTEST2_TYPE_HIERARCHY = new HashSet<>();
	static {
		DOCTEST2_TYPE_HIERARCHY.add(DOCUMENT);
		DOCTEST2_TYPE_HIERARCHY.add(DOCTEST1);
		DOCTEST2_TYPE_HIERARCHY.add(DOCTEST2);
	}

	private static final Set<String> DOCTEST3_TYPE_HIERARCHY = new HashSet<>();
	static {
		DOCTEST3_TYPE_HIERARCHY.add(DOCUMENT);
		DOCTEST3_TYPE_HIERARCHY.add(DOCTEST1);
		DOCTEST3_TYPE_HIERARCHY.add(DOCTEST2);
		DOCTEST3_TYPE_HIERARCHY.add(DOCTEST3);
	}

	private static final Set<String> DOCTEST4_TYPE_HIERARCHY = new HashSet<>();
	static {
		DOCTEST4_TYPE_HIERARCHY.add(DOCUMENT);
		DOCTEST4_TYPE_HIERARCHY.add(DOCTEST1);
		DOCTEST4_TYPE_HIERARCHY.add(DOCTEST4);
	}

	@Inject
	private CoreSession session;

	private Map<String, DocumentModel> documents = new HashMap<>();

	public DocumentTypeUtilTest() {
		// do nothing
	}
	
	@Before
	public void init() throws NuxeoException {
		documents.put("doc", session.createDocument(session.createDocumentModel("/", "doc", DOCUMENT)));
		documents.put("doc1", session.createDocument(session.createDocumentModel("/", "doc1", DOCTEST1)));
		documents.put("doc2", session.createDocument(session.createDocumentModel("/", "doc2", DOCTEST2)));
		documents.put("doc3", session.createDocument(session.createDocumentModel("/", "doc3", DOCTEST3)));
		documents.put("doc4", session.createDocument(session.createDocumentModel("/", "doc4", DOCTEST4)));
	}

	@Test
	public void typeHierarchyTest() {
		Set<String> documentTypeHierarchy = DocumentTypeUtil.typeHierarchy(documents.get("doc"));
		Set<String> documentTypeHierarchy1 = DocumentTypeUtil.typeHierarchy(documents.get("doc1"));
		Set<String> documentTypeHierarchy2 = DocumentTypeUtil.typeHierarchy(documents.get("doc2"));
		Set<String> documentTypeHierarchy3 = DocumentTypeUtil.typeHierarchy(documents.get("doc3"));
		Set<String> documentTypeHierarchy4 = DocumentTypeUtil.typeHierarchy(documents.get("doc4"));

		assertNotNull("Null Hierarchy", documentTypeHierarchy);
		assertNotNull("Null Hierarchy", documentTypeHierarchy1);
		assertNotNull("Null Hierarchy", documentTypeHierarchy2);
		assertNotNull("Null Hierarchy", documentTypeHierarchy3);
		assertNotNull("Null Hierarchy", documentTypeHierarchy4);

		assertEquals("Wrong number of elements", DOCUMENT_TYPE_HIERARCHY.size(), documentTypeHierarchy.size());
		assertEquals("Wrong number of elements", DOCTEST1_TYPE_HIERARCHY.size(), documentTypeHierarchy1.size());
		assertEquals("Wrong number of elements", DOCTEST2_TYPE_HIERARCHY.size(), documentTypeHierarchy2.size());
		assertEquals("Wrong number of elements", DOCTEST3_TYPE_HIERARCHY.size(), documentTypeHierarchy3.size());
		assertEquals("Wrong number of elements", DOCTEST4_TYPE_HIERARCHY.size(), documentTypeHierarchy4.size());

		assertTrue("Wrong elements", documentTypeHierarchy.containsAll(DOCUMENT_TYPE_HIERARCHY));
		assertTrue("Wrong elements", documentTypeHierarchy1.containsAll(DOCTEST1_TYPE_HIERARCHY));
		assertTrue("Wrong elements", documentTypeHierarchy2.containsAll(DOCTEST2_TYPE_HIERARCHY));
		assertTrue("Wrong elements", documentTypeHierarchy3.containsAll(DOCTEST3_TYPE_HIERARCHY));
		assertTrue("Wrong elements", documentTypeHierarchy4.containsAll(DOCTEST4_TYPE_HIERARCHY));
	}

	@Test
	public void hasTypeTest() {
		DocumentModel document = documents.get("doc");
		DocumentModel document1 = documents.get("doc1");
		DocumentModel document2 = documents.get("doc2");
		DocumentModel document3 = documents.get("doc3");
		DocumentModel document4 = documents.get("doc4");

		assertTrue("Missing type", DocumentTypeUtil.hasType(document, DOCUMENT));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document, DOCTEST1));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document, DOCTEST2));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document, DOCTEST3));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document, DOCTEST4));

		assertTrue("Missing type", DocumentTypeUtil.hasType(document1, DOCUMENT));
		assertTrue("Missing type", DocumentTypeUtil.hasType(document1, DOCTEST1));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document1, DOCTEST2));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document1, DOCTEST3));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document1, DOCTEST4));

		assertTrue("Missing type", DocumentTypeUtil.hasType(document2, DOCUMENT));
		assertTrue("Missing type", DocumentTypeUtil.hasType(document2, DOCTEST1));
		assertTrue("Missing type", DocumentTypeUtil.hasType(document2, DOCTEST2));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document2, DOCTEST3));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document2, DOCTEST4));

		assertTrue("Missing type", DocumentTypeUtil.hasType(document3, DOCUMENT));
		assertTrue("Missing type", DocumentTypeUtil.hasType(document3, DOCTEST1));
		assertTrue("Missing type", DocumentTypeUtil.hasType(document3, DOCTEST2));
		assertTrue("Missing type", DocumentTypeUtil.hasType(document3, DOCTEST3));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document3, DOCTEST4));

		assertTrue("Missing type", DocumentTypeUtil.hasType(document4, DOCUMENT));
		assertTrue("Missing type", DocumentTypeUtil.hasType(document4, DOCTEST1));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document4, DOCTEST2));
		assertFalse("Type not missing", DocumentTypeUtil.hasType(document4, DOCTEST3));
		assertTrue("Missing type", DocumentTypeUtil.hasType(document4, DOCTEST4));
	}

	@Test
	public void hasOneTypeTest() {
		DocumentModel document = documents.get("doc");

		assertTrue("Missing type", DocumentTypeUtil.hasOneType(document, Arrays.asList(DOCUMENT)));
		assertTrue("Missing type", DocumentTypeUtil.hasOneType(document, Arrays.asList(DOCUMENT, DOCTEST1)));
		assertTrue("Missing type", DocumentTypeUtil.hasOneType(document, Arrays.asList(DOCUMENT, DOCTEST1, DOCTEST2, DOCTEST3, DOCTEST4)));
		assertFalse("Type not missing", DocumentTypeUtil.hasOneType(document, Arrays.asList(DOCTEST1)));
		assertFalse("Type not missing", DocumentTypeUtil.hasOneType(document, Arrays.asList(DOCTEST1, DOCTEST2, DOCTEST3, DOCTEST4)));
	}
	
	@Test
	public void listTypeTest(){
		
		DocumentModel document = documents.get("doc");
		List<String> types = Arrays.asList(new String[]{DOCUMENT});
		assertEquals(types, DocumentTypeUtil.orderedTypeHierarchy(document));
		
		document = documents.get("doc3");
		types = Arrays.asList(new String[]{DOCTEST3, DOCTEST2, DOCTEST1, DOCUMENT});
		assertEquals(types, DocumentTypeUtil.orderedTypeHierarchy(document));		

		document = documents.get("doc4");
		types = Arrays.asList(new String[]{DOCTEST4, DOCTEST1, DOCUMENT});
		assertEquals(types, DocumentTypeUtil.orderedTypeHierarchy(document));
	}
}
