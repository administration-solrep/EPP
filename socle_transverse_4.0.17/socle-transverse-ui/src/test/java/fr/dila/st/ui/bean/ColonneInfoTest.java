package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ColonneInfoTest {

    @Test
    public void testConstructor() {
        IColonneInfo item = new ColonneInfo("test.label", true, true, false, true);
        assertEquals("test.label", item.getLabel());
        assertTrue(item.isSortable());
        assertTrue(item.isVisible());
        assertFalse(item.isInverseSort());

        assertNull(item.getSortId());
        assertNull(item.getSortName());
        assertNull(item.getSortValue());

        item = new ColonneInfo("test.label", true, true, true, true);
        assertEquals("test.label", item.getLabel());
        assertTrue(item.isSortable());
        assertTrue(item.isVisible());
        assertTrue(item.isInverseSort());

        assertNull(item.getSortId());
        assertNull(item.getSortName());
        assertNull(item.getSortValue());
    }

    @Test
    public void testSetters() {
        ColonneInfo item = new ColonneInfo("test.label", true, true, false, true);
        assertEquals("test.label", item.getLabel());
        assertTrue(item.isSortable());
        assertTrue(item.isVisible());

        assertNull(item.getSortId());
        assertNull(item.getSortName());
        assertNull(item.getSortValue());

        item.setSortId("1");
        assertEquals("1", item.getSortId());

        item.setSortName("test");
        assertEquals("test", item.getSortName());

        item.setSortValue("asc");
        assertEquals("asc", item.getSortValue());
    }
}
