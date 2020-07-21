package fr.dila.st.core.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test de la classe CollectionUtil.
 * 
 */
public class TestCollectionUtil extends TestCase {

	List<String>	listTest1	= new ArrayList<String>();
	List<String>	listTest2	= new ArrayList<String>();
	List<String>	listTest3	= new ArrayList<String>();
	String			str1		= "A : Une chaine";
	String			str2		= "B : pour des tests ";
	String			str3		= "C : pour dédé ";
	String			str4		= "D : pour manger";

	@Override
	public void setUp() throws Exception {
		listTest1.add(str2);
		listTest1.add(str1);
		listTest1.add(str3);
		listTest2.add(str3);
		listTest3.add(str4);
	}

	public void testAsSortedList() {
		// On vérifie que la liste est bien desordonnée :
		assertEquals(str2, listTest1.get(0));
		assertEquals(str1, listTest1.get(1));
		assertEquals(str3, listTest1.get(2));
		List<String> sortedList1 = CollectionUtil.asSortedList(listTest1);
		assertEquals(str1, sortedList1.get(0));
		assertEquals(str2, sortedList1.get(1));
		assertEquals(str3, sortedList1.get(2));
	}

	public void testContainsOneElement() {
		List<String> listNull = null;
		assertFalse(CollectionUtil.containsOneElement(listTest1, listNull));
		assertFalse(CollectionUtil.containsOneElement(listNull, listTest1));

		assertTrue(CollectionUtil.containsOneElement(listTest1, listTest2));
		assertFalse(CollectionUtil.containsOneElement(listTest2, listTest3));
	}
}
