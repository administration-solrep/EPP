package fr.sword.naiad.nuxeo.commons.core.util;



import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({ "fr.sword.naiad.nuxeo.commons.core" })
@LocalDeploy({"fr.sword.naiad.nuxeo.commons.core:OSGI-INF/test-doc-type.xml"})
public class DublincorePropertyUtilTest {

	private static final String DOCTEST_TYPE = "DocTest";
	
	@Inject
	private CoreSession session;
	
	@Test
	public void testCommonProperties() throws NuxeoException{
		DocumentModel doc = session.createDocumentModel(DOCTEST_TYPE);
		Assert.assertNotNull(doc);
		
		final String title = "un titre";
		
		PropertyUtil.setProperty(doc,  CommonSchemaConstant.SCHEMA_DUBLINCORE, CommonSchemaConstant.PROP_DUBLINCORE_TITLE, title);
		Assert.assertEquals(title, DublincorePropertyUtil.getTitle(doc));
		
		final String description = "une description";
		doc.setPropertyValue(CommonSchemaConstant.PREFIX_DUBLICORE + ':' + CommonSchemaConstant.PROP_DUBLINCORE_DESCRIPTION, description);
		Assert.assertEquals(description, DublincorePropertyUtil.getDescription(doc));
		
		
		final String title2 = "un second titre";
		DublincorePropertyUtil.setTitle(doc, title2);
		Assert.assertEquals(title2, DublincorePropertyUtil.getTitle(doc));
		
		final String description2 = "une seconde description";
		DublincorePropertyUtil.setDescription(doc, description2);
		Assert.assertEquals(description2, DublincorePropertyUtil.getDescription(doc));
	}
	
	@Test
	public void testSubjectProperty() throws NuxeoException {
		DocumentModel doc = session.createDocumentModel(DOCTEST_TYPE);		
		Assert.assertNotNull(doc);
		doc = session.createDocument(doc);
		DocumentRef docRef = doc.getRef();
		
		String[] subjectsRetrieved = DublincorePropertyUtil.getSubjects(doc);
		Assert.assertEquals(0, subjectsRetrieved.length);
		
		String[] subjects = new String[]{"s1", "s2", "s3"};		
		DublincorePropertyUtil.setSubjects(doc, subjects);
		doc = session.saveDocument(doc);		
		session.save();
		
		doc = session.getDocument(docRef);
		subjectsRetrieved = DublincorePropertyUtil.getSubjects(doc); 
		Assert.assertEquals(subjects.length, subjectsRetrieved.length);
		for(int i = 0; i < subjects.length; ++i){
			Assert.assertEquals(subjects[i], subjectsRetrieved[i]);
		}
	}
	
	
}
