package fr.dila.st.ui.bean;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TreeElementDTOTest {

    @Test
    public void testConstructor() {
        TreeElementDTO element = new TreeElementDTO();
        assertNotNull(element);
        assertNotNull(element.getChilds());
        assertEquals(0, element.getChilds().size());
        assertNull(element.getLabel());
        assertNull(element.getKey());
        assertNull(element.getCompleteKey());
        assertNull(element.getAction());
        assertNull(element.getLink());
        assertEquals(false, element.getIsOpen());
        assertEquals(false, element.getIsLastLevel());
        assertEquals(true, element.getIsBold());
    }

    @Test
    public void testSetter() {
        TreeElementDTO element = new TreeElementDTO();
        assertNotNull(element);
        assertNotNull(element.getChilds());
        assertEquals(0, element.getChilds().size());
        assertNull(element.getLabel());
        assertNull(element.getKey());
        assertNull(element.getCompleteKey());
        assertNull(element.getAction());
        assertNull(element.getLink());
        assertEquals(false, element.getIsOpen());
        assertEquals(false, element.getIsLastLevel());
        assertEquals(true, element.getIsBold());

        element.setChilds(null);
        assertNull(element.getChilds());

        List<TreeElementDTO> liste = new ArrayList<>();
        liste.add(new TreeElementDTO());
        liste.add(new TreeElementDTO());
        element.setChilds(liste);
        assertNotNull(element.getChilds());
        assertEquals(2, element.getChilds().size());

        element.setLabel("TestLabelKey");
        assertEquals("TestLabelKey", element.getLabel());

        element.setKey("TestKey");
        assertEquals("TestKey", element.getKey());

        element.setCompleteKey("TestCompleteKey");
        assertEquals("TestCompleteKey", element.getCompleteKey());

        element.setAction("TestActionKey");
        assertEquals("TestActionKey", element.getAction());

        element.setLink("TestLink");
        assertEquals("TestLink", element.getLink());

        element.setIsOpen(true);
        assertEquals(true, element.getIsOpen());

        element.setIsLastLevel(true);
        assertEquals(true, element.getIsLastLevel());

        element.setIsBold(false);
        assertEquals(false, element.getIsBold());
    }
}
