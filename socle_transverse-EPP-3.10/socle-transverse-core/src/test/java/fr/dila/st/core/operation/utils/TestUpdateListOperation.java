package fr.dila.st.core.operation.utils;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.InvalidChainException;
import org.nuxeo.ecm.automation.OperationChain;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.OperationParameters;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.PropertyUtil;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({ "org.nuxeo.ecm.automation.core", "org.nuxeo.ecm.platform.web.common", "org.nuxeo.ecm.platform.login",
		"fr.dila.st.core" })
@LocalDeploy("fr.dila.st.core:OSGI-INF/test-core-type-contrib.xml")
public class TestUpdateListOperation {

	@Inject
	AutomationService								service;

	@Inject
	CoreSession										session;

	protected DocumentModel							docTest;

	protected String								listTestSchema				= "list-test";
	protected String								listTestPrefix				= "lst";
	protected String								namesProperty				= "lst:names";
	protected String								birthdatesProperty			= "lst:birthdates";
	protected String								agesProperty				= "lst:ages";
	protected String								familiesInfosProperty		= "lst:familiesInfos";
	protected String								dupuisFamilyProperty		= "lst:dupuisFamily";
	protected String								dupuisDatesProperty			= "lst:dupuisDates";
	protected String								strInfosKey					= "itemStrList";
	protected String								dateInfosKey				= "itemDateArray";
	protected String								dupuisCharactersProperty	= "lst:dupuisCharacters";
	protected String								characterNameKey			= "name";
	protected String								characterAgeKey				= "age";
	protected String								characterDateKey			= "birthdate";

	protected ArrayList<String>						dupuisNames					= new ArrayList<String>();
	protected ArrayList<Calendar>					dupuisDates					= new ArrayList<Calendar>();
	protected ArrayList<Long>						dupuisAges					= new ArrayList<Long>();
	protected HashMap<String, Serializable>			mapFamiliesInfos			= new HashMap<String, Serializable>();
	protected ArrayList<HashMap<String, Object>>	charactersList				= new ArrayList<HashMap<String, Object>>();

	@Before
	public void setUp() throws Exception {
		docTest = session.createDocumentModel("/", "src", "list-test");
		// Init families
		dupuisNames.add("Spirou");
		dupuisNames.add("Spip");
		dupuisNames.add("Fantasio");

		// Init ages
		dupuisAges.add(new Long(25));
		dupuisAges.add(new Long(7));
		dupuisAges.add(new Long(30));

		// Init dates
		Calendar date1 = Calendar.getInstance();
		Calendar date2 = Calendar.getInstance();
		Calendar date3 = Calendar.getInstance();
		date1.add(Calendar.YEAR, 1);
		date2.add(Calendar.MONTH, 1);
		date3.add(Calendar.DAY_OF_YEAR, 1);
		dupuisDates.add(date1);
		dupuisDates.add(date2);
		dupuisDates.add(date3);

		// Init array of String
		docTest.setPropertyValue(namesProperty, dupuisNames);
		// Init array of dates
		docTest.setPropertyValue(birthdatesProperty, dupuisDates);
		// Init array of int
		docTest.setPropertyValue(agesProperty, dupuisAges);
		// Init list of string
		docTest.setPropertyValue(dupuisFamilyProperty, dupuisNames);
		// Init list of date
		docTest.setPropertyValue(dupuisDatesProperty, dupuisDates);

		// Init map
		mapFamiliesInfos.put(strInfosKey, dupuisNames);
		mapFamiliesInfos.put(dateInfosKey, dupuisDates.toArray());
		docTest.setPropertyValue(familiesInfosProperty, mapFamiliesInfos);

		// Init list of string and date
		HashMap<String, Object> character1 = new HashMap<String, Object>();
		character1.put(characterNameKey, dupuisNames.get(0));
		character1.put(characterDateKey, dupuisDates.get(0));
		character1.put(characterAgeKey, dupuisAges.get(0));
		HashMap<String, Object> character2 = new HashMap<String, Object>();
		character2.put(characterNameKey, dupuisNames.get(1));
		character2.put(characterDateKey, dupuisDates.get(1));
		character2.put(characterAgeKey, dupuisAges.get(1));
		HashMap<String, Object> character3 = new HashMap<String, Object>();
		character3.put(characterNameKey, dupuisNames.get(2));
		character3.put(characterDateKey, dupuisDates.get(2));
		character3.put(characterAgeKey, dupuisAges.get(2));
		charactersList.add(character1);
		charactersList.add(character2);
		charactersList.add(character3);

		docTest.setPropertyValue(dupuisCharactersProperty, charactersList);

		docTest = session.createDocument(docTest);
		session.save();
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void assertListEquals(List<? extends Object> expected, List<? extends Object> actual) {
		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++) {
			if (expected.get(i) instanceof Map) {
				assertMapEquals((Map) expected.get(i), (Map) actual.get(i));
			} else if (expected.get(i) instanceof List) {
				assertListEquals((List) expected.get(i), (List) actual.get(i));
			} else if (expected.get(i).getClass().isArray()) {
				assertArrayEquals((Object[]) expected.get(i), (Object[]) actual.get(i));
			} else {
				assertEquals(expected.get(i), actual.get(i));
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void assertMapEquals(Map<Object, Object> expected, Map<Object, Object> actual) {
		// Check size
		assertEquals(expected.size(), actual.size());
		for (Entry<Object, Object> entry : expected.entrySet()) {
			assertTrue(actual.containsKey(entry.getKey()));
			Object value = entry.getValue();
			if (value != null) {
				if (value instanceof List) {
					assertListEquals((List) value, (List) actual.get(entry.getKey()));
				} else if (value instanceof Map) {
					assertMapEquals((Map) value, (Map) actual.get(entry.getKey()));
				} else if (value.getClass().isArray()) {
					assertArrayEquals((Object[]) value, (Object[]) actual.get(entry.getKey()));
				} else {
					assertEquals(value, actual.get(entry.getKey()));
				}
			} else {
				assertNull(actual.get(entry.getKey()));
			}
		}
	}

	protected String initKeyValue(String[] keyValue) {
		return keyValue[0] + UpdateListOperation.KEY_VALUE_SEPARATOR + keyValue[1];
	}

	protected String initMapEntries(String... entries) {
		StringBuilder result = new StringBuilder();
		result.append(UpdateListOperation.MAP_INIT_STR);
		for (int i = 0; i < entries.length - 1; i++) {
			result.append(entries[i]).append(UpdateListOperation.MAP_SEPARATOR);
		}
		result.append(entries[entries.length - 1]).append(UpdateListOperation.MAP_END_STR);
		return result.toString();
	}

	protected String initListEntries(String... entries) {
		StringBuilder result = new StringBuilder();
		result.append(UpdateListOperation.LIST_INIT_STR);
		for (int i = 0; i < entries.length - 1; i++) {
			result.append(entries[i]).append(UpdateListOperation.LIST_SEPARATOR);
		}
		result.append(entries[entries.length - 1]).append(UpdateListOperation.LIST_END_STR);
		return result.toString();
	}

	@Test
	public void testWrongParameters() throws Exception {
		assertNotNull(service);
		assertNotNull(session);
		OperationContext ctx = new OperationContext(session);

		OperationChain chain = new OperationChain("testChain");
		OperationParameters parameters = chain.add(UpdateListOperation.ID);
		parameters.set("id", "").set("property", "").set("mode", "");
		// None parameters
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_PARAM_EXC, exc.getMessage());
		}

		// One parameter : id
		parameters.set("id", docTest.getId());
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_PARAM_EXC, exc.getMessage());
		}

		// Two parameters : id and property
		parameters.set("property", namesProperty);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_PARAM_EXC, exc.getMessage());
		}

		// Three parameters but wrong id
		parameters.set("mode", UpdateListOperation.REMOVEALL_MODE);
		String idBidon = "UID-comple-tement-bidon-1234";
		parameters.set("id", idBidon);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.DOC_NOT_FOUND_FOR_ID + idBidon, exc.getMessage());
		}

		// Three parameters, id is good, but mode isn't
		String modeBidon = "thiswillnotwork";
		parameters.set("mode", modeBidon);
		parameters.set("id", docTest.getId());
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.getModeException(modeBidon), exc.getMessage());
		}

		// Three parameters, mode is remove but no data
		parameters.set("mode", UpdateListOperation.REMOVE_MODE);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATA_PARAM_EXC, exc.getMessage());
		}

		// Three parameters, mode is add but no data
		parameters.set("mode", UpdateListOperation.ADD_MODE);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATA_PARAM_EXC, exc.getMessage());
		}

		// Four parameters, but property isn't valid
		String propertyBidon = "dc:thisisnotinthecurrentschema";
		parameters.set("data", "contributor 4");
		parameters.set("property", propertyBidon);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.CANT_GET_PROPERTY_EXC + propertyBidon, exc.getMessage());
		}
	}

	@Test
	public void testUpdateArrayStringOperation() throws InvalidChainException, OperationException, Exception {
		// check init
		assertNotNull(service);
		assertNotNull(session);
		String property = namesProperty;
		String[] names = (String[]) docTest.getPropertyValue(property);

		// Check contributors
		assertArrayEquals(dupuisNames.toArray(), names);

		OperationContext ctx = new OperationContext(session);

		OperationChain chain = new OperationChain("testChain");
		OperationParameters parameters = chain.add(UpdateListOperation.ID);
		String newMember = "Gaston";
		dupuisNames.add(newMember);
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.ADD_MODE)
				.set("data", newMember);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = (String[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisNames.toArray(), names);

		// remove second member
		String secondMember = dupuisNames.remove(1);
		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", secondMember);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = (String[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisNames.toArray(), names);

		// remove Unknown contributor should leave array unchanged
		String unknownContributor = "Tintin";
		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", unknownContributor);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = (String[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisNames.toArray(), names);

		// remove all
		dupuisNames.clear();
		parameters.set("mode", UpdateListOperation.REMOVEALL_MODE);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = (String[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisNames.toArray(), names);

		// add second member previously removed
		dupuisNames.add(secondMember);
		// First, without datatype
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", secondMember);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATATYPE_EXC + property, exc.getMessage());
		}
		// then, add datatype
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", secondMember).set("dataType", "String");
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = (String[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisNames.toArray(), names);
	}

	@Test
	public void testUpdateListStringOperation() throws InvalidChainException, OperationException, Exception {
		// check init
		assertNotNull(service);
		assertNotNull(session);
		String property = dupuisFamilyProperty;
		List<String> names = PropertyUtil.getStringListProperty(docTest, listTestSchema, property);

		// Check family
		assertListEquals(dupuisNames, names);

		OperationContext ctx = new OperationContext(session);

		OperationChain chain = new OperationChain("testChain");
		OperationParameters parameters = chain.add(UpdateListOperation.ID);
		String gaston = "Gaston";
		dupuisNames.add(gaston);
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.ADD_MODE)
				.set("data", gaston);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = PropertyUtil.getStringListProperty(docTest, listTestSchema, property);
		// Check
		assertListEquals(dupuisNames, names);

		// remove second member
		String secondMember = dupuisNames.remove(1);
		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", secondMember);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = PropertyUtil.getStringListProperty(docTest, listTestSchema, property);
		assertListEquals(dupuisNames, names);

		// remove Unknown member should leave list unchanged
		String unknownMember = "Tintin";
		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", unknownMember);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = PropertyUtil.getStringListProperty(docTest, listTestSchema, property);
		assertListEquals(dupuisNames, names);

		// remove all members
		dupuisNames.clear();
		parameters.set("mode", UpdateListOperation.REMOVEALL_MODE);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = PropertyUtil.getStringListProperty(docTest, listTestSchema, property);
		assertListEquals(dupuisNames, names);

		// add second member previously removed
		dupuisNames.add(secondMember);
		// first without dataType
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", secondMember);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATATYPE_EXC + property, exc.getMessage());
		}
		// then, add dataType
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", secondMember).set("dataType", "String");
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		names = PropertyUtil.getStringListProperty(docTest, listTestSchema, property);
		assertListEquals(dupuisNames, names);
	}

	@Test
	public void testUpdateArrayDateOperation() throws InvalidChainException, OperationException, Exception {
		// check init
		assertNotNull(service);
		assertNotNull(session);

		// Check dates
		String property = birthdatesProperty;
		Calendar[] dates = (Calendar[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisDates.toArray(), dates);

		OperationContext ctx = new OperationContext(session);

		OperationChain chain = new OperationChain("testChain");
		OperationParameters parameters = chain.add(UpdateListOperation.ID);
		Calendar date4 = Calendar.getInstance();
		String date4Str = DateUtil.formatWithHour(date4.getTime());
		date4 = DateUtil.parseWithHour(date4Str);
		dupuisDates.add(date4);
		// Add one date to the end - Caution : adding a date only with dd/MM/yyyy
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.ADD_MODE)
				.set("data", date4Str);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (Calendar[]) docTest.getPropertyValue(property);
		// Check dates
		assertArrayEquals(dupuisDates.toArray(), dates);

		// remove one date
		Calendar dateToRemove = dupuisDates.remove(1);
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.REMOVE_MODE)
				.set("data", DateUtil.formatWithHour(dateToRemove.getTime()));
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (Calendar[]) docTest.getPropertyValue(property);
		// Check dates
		assertArrayEquals(dupuisDates.toArray(), dates);

		// remove unknown date should leave array unchanged
		Calendar dateUnknown = Calendar.getInstance();
		dateUnknown.add(Calendar.YEAR, -999);
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.REMOVE_MODE)
				.set("data", DateUtil.formatWithHour(dateUnknown.getTime()));
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (Calendar[]) docTest.getPropertyValue(property);
		// Check dates
		assertArrayEquals(dupuisDates.toArray(), dates);

		// Remove all dates
		dupuisDates.clear();
		parameters.set("mode", UpdateListOperation.REMOVEALL_MODE);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (Calendar[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisDates.toArray(), dates);

		// add date previously removed
		date4Str = DateUtil.formatWithHour(dateToRemove.getTime());
		date4 = DateUtil.parseWithHour(date4Str);
		dupuisDates.add(date4);
		// first without datatype
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data",
				DateUtil.formatWithHour(dateToRemove.getTime()));
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATATYPE_EXC + property, exc.getMessage());
		}
		// then add datatype property
		parameters.set("mode", UpdateListOperation.ADD_MODE)
				.set("data", DateUtil.formatWithHour(dateToRemove.getTime())).set("dataType", "Calendar");
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (Calendar[]) docTest.getPropertyValue(property);
		// Check dates
		assertArrayEquals(dupuisDates.toArray(), dates);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateListDateOperation() throws InvalidChainException, OperationException, Exception {
		// check init
		assertNotNull(service);
		assertNotNull(session);

		// Check dates
		String property = dupuisDatesProperty;
		List<Calendar> dates = (List<Calendar>) docTest.getPropertyValue(property);
		assertListEquals(dupuisDates, dates);

		OperationContext ctx = new OperationContext(session);

		OperationChain chain = new OperationChain("testChain");
		OperationParameters parameters = chain.add(UpdateListOperation.ID);
		Calendar date4 = Calendar.getInstance();
		String date4Str = DateUtil.formatWithHour(date4.getTime());
		date4 = DateUtil.parseWithHour(date4Str);
		dupuisDates.add(date4);
		// Add one date to the end - Caution : adding a date only with dd/MM/yyyy
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.ADD_MODE)
				.set("data", date4Str);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (List<Calendar>) docTest.getPropertyValue(property);
		// Check dates
		assertListEquals(dupuisDates, dates);

		// remove one date
		Calendar dateToRemove = dupuisDates.remove(1);
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.REMOVE_MODE)
				.set("data", DateUtil.formatWithHour(dateToRemove.getTime()));
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (List<Calendar>) docTest.getPropertyValue(property);
		// Check dates
		assertListEquals(dupuisDates, dates);

		// remove unknown date should leave array unchanged
		Calendar dateUnknown = Calendar.getInstance();
		dateUnknown.add(Calendar.YEAR, -999);
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.REMOVE_MODE)
				.set("data", DateUtil.formatWithHour(dateUnknown.getTime()));
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (List<Calendar>) docTest.getPropertyValue(property);
		// Check dates
		assertListEquals(dupuisDates, dates);

		// Remove all dates
		dupuisDates.clear();
		parameters.set("mode", UpdateListOperation.REMOVEALL_MODE);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (List<Calendar>) docTest.getPropertyValue(property);
		assertListEquals(dupuisDates, dates);

		// add date previously removed
		dupuisDates.add(DateUtil.parseWithHour(DateUtil.formatWithHour(dateToRemove.getTime())));
		// first, without dataType
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data",
				DateUtil.formatWithHour(dateToRemove.getTime()));
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATATYPE_EXC + property, exc.getMessage());
		}
		// then, add datatype
		parameters.set("mode", UpdateListOperation.ADD_MODE)
				.set("data", DateUtil.formatWithHour(dateToRemove.getTime())).set("dataType", "Calendar");
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		dates = (List<Calendar>) docTest.getPropertyValue(property);
		// Check dates
		assertListEquals(dupuisDates, dates);
	}

	@Test
	public void testUpdateArrayIntOperation() throws InvalidChainException, OperationException, Exception {
		// check init
		assertNotNull(service);
		assertNotNull(session);
		String property = agesProperty;
		Long[] ages = (Long[]) docTest.getPropertyValue(property);
		// Check ages
		assertArrayEquals(dupuisAges.toArray(), ages);

		OperationContext ctx = new OperationContext(session);
		OperationChain chain = new OperationChain("testChain");
		OperationParameters parameters = chain.add(UpdateListOperation.ID);
		Long newAge = new Long(55);
		dupuisAges.add(newAge);
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.ADD_MODE)
				.set("data", newAge.toString());
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		ages = (Long[]) docTest.getPropertyValue(property);
		// Check ages
		assertArrayEquals(dupuisAges.toArray(), ages);

		// remove second age
		Long secondAge = dupuisAges.remove(1);
		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", secondAge.toString());
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		ages = (Long[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisAges.toArray(), ages);

		// remove Unknown age should leave array unchanged
		Long unknownAge = new Long(-55);
		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", unknownAge.toString());
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		ages = (Long[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisAges.toArray(), ages);

		// remove all
		dupuisAges.clear();
		parameters.set("mode", UpdateListOperation.REMOVEALL_MODE);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		ages = (Long[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisAges.toArray(), ages);

		// remove age previously removed
		dupuisAges.add(secondAge);
		// First, without dataType
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", secondAge.toString());
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATATYPE_EXC + property, exc.getMessage());
		}
		// then, add datatype
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", secondAge.toString()).set("dataType", "Long");
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		ages = (Long[]) docTest.getPropertyValue(property);
		assertArrayEquals(dupuisAges.toArray(), ages);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testUpdateTypeComplexWithListAndArrayOperation() throws InvalidChainException, OperationException,
			Exception {
		// check init
		assertNotNull(service);
		assertNotNull(session);
		String property = familiesInfosProperty;
		HashMap<String, Object> familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		OperationContext ctx = new OperationContext(session);
		OperationChain chain = new OperationChain("testChain");
		OperationParameters parameters = chain.add(UpdateListOperation.ID);
		// new family member
		String gaston = initMapEntries(initKeyValue(new String[] { strInfosKey, initListEntries("Gaston") }));
		dupuisNames.add("Gaston");
		mapFamiliesInfos.put(strInfosKey, dupuisNames);
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.ADD_MODE)
				.set("data", gaston);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		// new date
		Calendar dateGaston = Calendar.getInstance();
		dateGaston.add(Calendar.HOUR, 5);
		String dateStrGaston = DateUtil.formatWithHour(dateGaston.getTime());
		String parameterDateGaston = initMapEntries(initKeyValue(new String[] { dateInfosKey,
				initListEntries(dateStrGaston) }));
		parameters.set("data", parameterDateGaston);
		dateGaston = DateUtil.parseWithHour(dateStrGaston);
		dupuisDates.add(dateGaston);
		mapFamiliesInfos.put(dateInfosKey, dupuisDates.toArray());
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		// remove second member
		String secondMember = dupuisNames.remove(1);
		mapFamiliesInfos.put(strInfosKey, dupuisNames);
		String secondMemberParameter = initMapEntries(initKeyValue(new String[] { strInfosKey,
				initListEntries(secondMember) }));
		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", secondMemberParameter);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		// remove one date
		Calendar dateToRemove = dupuisDates.remove(1);
		mapFamiliesInfos.put(dateInfosKey, dupuisDates.toArray());
		String dateStrToRemove = DateUtil.formatWithHour(dateToRemove.getTime());
		String parameterDateToRemove = initMapEntries(initKeyValue(new String[] { dateInfosKey,
				initListEntries(dateStrToRemove) }));
		parameters.set("data", parameterDateToRemove);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		// remove unknown member
		String unknownMember = initMapEntries(initKeyValue(new String[] { strInfosKey, initListEntries("Tintin") }));
		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", unknownMember);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		// remove unknown date
		Calendar dateUnknown = Calendar.getInstance();
		dateUnknown.add(Calendar.YEAR, -999);
		parameterDateToRemove = initMapEntries(initKeyValue(new String[] { dateInfosKey,
				initListEntries(DateUtil.formatWithHour(dateUnknown.getTime())) }));
		parameters.set("id", docTest.getId()).set("property", property).set("mode", UpdateListOperation.REMOVE_MODE)
				.set("data", parameterDateToRemove);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		// Remove all
		mapFamiliesInfos.put(strInfosKey, new ArrayList<String>());
		mapFamiliesInfos.put(dateInfosKey, new Object[0]);
		parameters.set("mode", UpdateListOperation.REMOVEALL_MODE);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		// add member previously removed
		dupuisNames.clear();
		dupuisNames.add(secondMember);
		mapFamiliesInfos.put(strInfosKey, dupuisNames);
		// first, without dataType
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", secondMemberParameter);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATATYPE_EXC + property, exc.getMessage());
		}
		// then, add datatype
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", secondMemberParameter)
				.set("dataType", initMapEntries(initKeyValue(new String[] { "String", initListEntries("String") })));
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

		// add date previously removed
		dupuisDates.clear();
		dupuisDates.add(DateUtil.parseWithHour(DateUtil.formatWithHour(dateToRemove.getTime())));
		mapFamiliesInfos.put(dateInfosKey, dupuisDates.toArray());
		parameterDateToRemove = initMapEntries(initKeyValue(new String[] { dateInfosKey,
				initListEntries(DateUtil.formatWithHour(dateToRemove.getTime())) }));
		// first without datatype
		parameters.set("data", parameterDateToRemove).set("dataType", "");
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATATYPE_EXC + property, exc.getMessage());
		}
		// then add datatype
		parameters.set("data", parameterDateToRemove).set("dataType",
				initMapEntries(initKeyValue(new String[] { "String", initListEntries("Calendar") })));
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		familiesInfos = (HashMap<String, Object>) docTest.getPropertyValue(property);
		// Check
		assertMapEquals((Map) mapFamiliesInfos, (Map) familiesInfos);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateListStrDateMapOperation() throws InvalidChainException, OperationException, Exception {
		// check init
		assertNotNull(service);
		assertNotNull(session);
		String property = dupuisCharactersProperty;
		ArrayList<HashMap<String, Object>> charactersDoc = (ArrayList<HashMap<String, Object>>) docTest
				.getPropertyValue(property);
		assertListEquals(charactersList, charactersDoc);

		OperationContext ctx = new OperationContext(session);
		OperationChain chain = new OperationChain("testChain");
		OperationParameters parameters = chain.add(UpdateListOperation.ID);
		parameters.set("id", docTest.getId()).set("property", property);

		HashMap<String, Object> character4 = new HashMap<String, Object>();
		Calendar dateGaston = Calendar.getInstance();
		String dateStrGaston = DateUtil.formatWithHour(dateGaston.getTime());
		dateGaston = DateUtil.parseWithHour(dateStrGaston);
		character4.put(characterNameKey, "Gaston");
		character4.put(characterDateKey, dateGaston);
		character4.put(characterAgeKey, new Long(26));
		charactersList.add(character4);
		String parameter = initMapEntries(initKeyValue(new String[] { characterNameKey, "Gaston" }),
				initKeyValue(new String[] { characterAgeKey, "26" }), initKeyValue(new String[] { characterDateKey,
						dateStrGaston }));

		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", parameter);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		charactersDoc = (ArrayList<HashMap<String, Object>>) docTest.getPropertyValue(property);
		// Check
		assertListEquals(charactersList, charactersDoc);

		// Remove second character
		HashMap<String, Object> characterRemoved = charactersList.remove(1);
		parameter = initMapEntries(
				initKeyValue(new String[] { characterNameKey, (String) characterRemoved.get(characterNameKey) }),
				initKeyValue(new String[] { characterAgeKey, characterRemoved.get(characterAgeKey).toString() }),
				initKeyValue(new String[] { characterDateKey,
						DateUtil.formatWithHour(((Calendar) characterRemoved.get(characterDateKey)).getTime()) }));

		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", parameter);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		charactersDoc = (ArrayList<HashMap<String, Object>>) docTest.getPropertyValue(property);
		// Check
		assertListEquals(charactersList, charactersDoc);

		// Remove unknown character
		Calendar dateUnknown = Calendar.getInstance();
		dateUnknown.add(Calendar.YEAR, -999);
		parameter = initMapEntries(initKeyValue(new String[] { characterNameKey, "Tintin" }),
				initKeyValue(new String[] { characterAgeKey, "24" }), initKeyValue(new String[] { characterDateKey,
						DateUtil.formatWithHour((dateUnknown.getTime())) }));

		parameters.set("mode", UpdateListOperation.REMOVE_MODE).set("data", parameter);
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		charactersDoc = (ArrayList<HashMap<String, Object>>) docTest.getPropertyValue(property);
		// Check
		assertListEquals(charactersList, charactersDoc);

		// Remove all character
		charactersList.clear();
		parameters.set("mode", UpdateListOperation.REMOVEALL_MODE).set("data", "");
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		charactersDoc = (ArrayList<HashMap<String, Object>>) docTest.getPropertyValue(property);
		// Check
		assertListEquals(charactersList, charactersDoc);

		// Add character previously removed
		// Nothing in the property
		characterRemoved.put(characterDateKey,
				DateUtil.parse(DateUtil.formatWithHour(((Calendar) characterRemoved.get(characterDateKey)).getTime())));
		charactersList.add(characterRemoved);
		parameter = initMapEntries(
				initKeyValue(new String[] { characterNameKey, (String) characterRemoved.get(characterNameKey) }),
				initKeyValue(new String[] { characterAgeKey, characterRemoved.get(characterAgeKey).toString() }),
				initKeyValue(new String[] { characterDateKey,
						DateUtil.formatWithHour(((Calendar) characterRemoved.get(characterDateKey)).getTime()) }));
		// first without datatype
		parameters.set("mode", UpdateListOperation.ADD_MODE).set("data", parameter);
		try {
			service.run(ctx, chain);
		} catch (OperationException exc) {
			assertEquals(UpdateListOperation.EMPTY_DATATYPE_EXC + property, exc.getMessage());
		}
		// then add datatype
		parameters
				.set("mode", UpdateListOperation.ADD_MODE)
				.set("data", parameter)
				.set("dataType",
						initMapEntries(initKeyValue(new String[] { "String", "String" }), initKeyValue(new String[] {
								"String", "Long" }), initKeyValue(new String[] { "String", "Calendar" })));
		service.run(ctx, chain);
		docTest.refresh(DocumentModel.REFRESH_ALL, new String[] { listTestSchema });
		charactersDoc = (ArrayList<HashMap<String, Object>>) docTest.getPropertyValue(property);
		// Check
		assertListEquals(charactersList, charactersDoc);
	}
}
