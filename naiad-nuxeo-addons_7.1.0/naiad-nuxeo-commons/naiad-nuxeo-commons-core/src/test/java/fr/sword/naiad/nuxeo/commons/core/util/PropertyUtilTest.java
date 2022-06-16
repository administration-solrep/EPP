package fr.sword.naiad.nuxeo.commons.core.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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
public class PropertyUtilTest {

	private static final String DOCTEST_TYPE = "DocTest";
	private static final String DOCTEST_SCHEMA = "schemaTest";
	
	private static final String PROP_STRING = "a_string";
	private static final String PROP_LONG = "a_long";
	private static final String PROP_DATE = "a_date";
	private static final String PROP_BOOL = "a_bool";
	private static final String PROP_INT = "a_int";
	
	private static final String PROP_STRINGLIST = "a_stringlist";
	private static final String PROP_TYPE_COMPLEXE = "a_typeComplexe";
	private static final String PROP_TYPE_COMPLEXE_LIST = "a_typeComplexeList";
	private static final String PROP_TYPE_COMPLEXE_FIELD1 = "a_string";
	private static final String PROP_TYPE_COMPLEXE_FIELD2 = "a_date";
	
	
	private static final String VALUE_STRING = "une valeur string";
	private static final Long VALUE_LONG = 1L;
	private static final Calendar VALUE_DATE = Calendar.getInstance();
	private static final Boolean VALUE_BOOL = Boolean.TRUE;
	private static final Integer VALUE_INT = 50;
	
	private static final class ComplexeType {
		private final String a_string;
		private final Calendar a_date;
		public ComplexeType(String a_string, Calendar a_date){
			this.a_string = a_string;
			this.a_date = a_date;
		}
		public String getA_string() {
			return a_string;
		}
		public Calendar getA_date() {
			return a_date;
		}
		
	}
	
	@Inject
	private CoreSession session;
	
	@Test
	public void testSimpleTypes() throws NuxeoException {
		DocumentModel doc = session.createDocumentModel(DOCTEST_TYPE);
		
		doc.setProperty(DOCTEST_SCHEMA, PROP_STRING, VALUE_STRING);
		Assert.assertNotNull(doc.getProperty(DOCTEST_SCHEMA, PROP_STRING));
		doc.setProperty(DOCTEST_SCHEMA, PROP_LONG, VALUE_LONG);
		doc.setProperty(DOCTEST_SCHEMA, PROP_DATE, VALUE_DATE);		
		doc.setProperty(DOCTEST_SCHEMA, PROP_INT, VALUE_INT);
		
		String strval = PropertyUtil.getStringProperty(doc, DOCTEST_SCHEMA, PROP_STRING);
		Assert.assertEquals(VALUE_STRING, strval);
		
		Long longval = PropertyUtil.getLongProperty(doc, DOCTEST_SCHEMA, PROP_LONG);
		Assert.assertEquals(VALUE_LONG, longval);
		
		Calendar dateval = PropertyUtil.getCalendarProperty(doc, DOCTEST_SCHEMA, PROP_DATE);
		Assert.assertEquals(VALUE_DATE, dateval);
		
		// valeur booleenne non positionné
		Assert.assertNull(doc.getProperty(DOCTEST_SCHEMA, PROP_BOOL));		
		Boolean boolval = PropertyUtil.getBooleanProperty(doc, DOCTEST_SCHEMA, PROP_BOOL);
		Assert.assertEquals(Boolean.FALSE, boolval);
		
		doc.setProperty(DOCTEST_SCHEMA, PROP_BOOL, VALUE_BOOL);
		boolval = PropertyUtil.getBooleanProperty(doc, DOCTEST_SCHEMA, PROP_BOOL);
		Assert.assertEquals(VALUE_BOOL, boolval);
		
				
	}
	
	@Test
	public void testStringList() throws NuxeoException {
		
		final String STR1 = "str1";
		final String STR2 = "str2";
		final String STR3 = "str3";
		
		final List<String> strList = new ArrayList<String>();
		strList.add(STR1);
		strList.add(STR2);
		strList.add(STR3);
		
		DocumentModel doc = session.createDocumentModel(DOCTEST_TYPE);
		doc.setProperty(DOCTEST_SCHEMA, PROP_STRINGLIST, strList);
		
		List<String> strListVal = PropertyUtil.getStringListProperty(doc, DOCTEST_SCHEMA, PROP_STRINGLIST);
		Assert.assertEquals(strList, strListVal);
	}
	
	
	@Test
	public void testTypeComplexe() throws NuxeoException {
		final Map<String, Serializable> complexe = new HashMap<String, Serializable>();		
		complexe.put(PROP_TYPE_COMPLEXE_FIELD1, VALUE_STRING);
		complexe.put(PROP_TYPE_COMPLEXE_FIELD2, VALUE_DATE);
		
		final List<Map<String, Serializable>> complexeList = new ArrayList<Map<String,Serializable>>();
		complexeList.add(complexe);
		
		DocumentModel doc = session.createDocumentModel(DOCTEST_TYPE);
		doc.setProperty(DOCTEST_SCHEMA, PROP_TYPE_COMPLEXE_LIST, complexeList);		
		List<Map<String, Serializable>> complexeListVal = PropertyUtil.getMapStringSerializableListProperty(doc, DOCTEST_SCHEMA, PROP_TYPE_COMPLEXE_LIST);		
		Assert.assertEquals(complexeList, complexeListVal);
		
		doc.setProperty(DOCTEST_SCHEMA, PROP_TYPE_COMPLEXE, complexe);		
		Map<String, Serializable> complexeVal = PropertyUtil.getMapStringSerializableProperty(doc, DOCTEST_SCHEMA, PROP_TYPE_COMPLEXE);		
		Assert.assertEquals(complexe, complexeVal);
		
		// test mapper
		
		ComplexeType complexeTypeVal = PropertyUtil.getComplexeProperty(doc, DOCTEST_SCHEMA, PROP_TYPE_COMPLEXE, new PropertyUtil.ComplexeTypeMapper<ComplexeType>() {
			public ComplexeType doMapping(Map<String, Serializable> data){
				return new ComplexeType((String)data.get(PROP_TYPE_COMPLEXE_FIELD1), (Calendar)data.get(PROP_TYPE_COMPLEXE_FIELD2));
			}			
		});
		Assert.assertNotNull(complexeTypeVal);
		Assert.assertEquals(VALUE_STRING, complexeTypeVal.a_string);
		Assert.assertEquals(VALUE_DATE, complexeTypeVal.a_date);

		List<ComplexeType> complexeTypeListVal = PropertyUtil.getComplexePropertyList(doc, DOCTEST_SCHEMA, PROP_TYPE_COMPLEXE_LIST, new PropertyUtil.ComplexeTypeMapper<ComplexeType>() {
			public ComplexeType doMapping(Map<String, Serializable> data){
				return new ComplexeType((String)data.get(PROP_TYPE_COMPLEXE_FIELD1), (Calendar)data.get(PROP_TYPE_COMPLEXE_FIELD2));
			}			
		});
		Assert.assertNotNull(complexeTypeListVal);
		Assert.assertEquals(1, complexeTypeListVal.size());
		Assert.assertEquals(VALUE_STRING, complexeTypeListVal.get(0).a_string);
		Assert.assertEquals(VALUE_DATE, complexeTypeListVal.get(0).a_date);

	}
	
}
