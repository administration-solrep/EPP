package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BreadcrumbTest {

    @Test
    public void testConstructor() {
        Breadcrumb item = new Breadcrumb("test", "url", 0);
        assertEquals("test", item.getKey());
        assertEquals("url", item.getUrl());
        assertEquals(new Integer(0), item.getOrder());
    }

    @Test
    public void testSetters() {
        Breadcrumb item = new Breadcrumb("test", "url", 0);
        assertEquals("test", item.getKey());
        assertEquals("url", item.getUrl());
        assertEquals(new Integer(0), item.getOrder());

        item.setKey("1");
        assertEquals("1", item.getKey());

        item.setUrl("url 2");
        assertEquals("url 2", item.getUrl());

        item.setOrder(2);
        assertEquals(new Integer(2), item.getOrder());
    }

    @Test
    public void testEquality() {
        Breadcrumb item = null;
        assertEquals(null, item);

        item = new Breadcrumb("test", "test", 0);
        assertEquals(new Breadcrumb("test", "test", 0), item);
        assertEquals(new Breadcrumb("test", null, 0), item);
        assertEquals((new Breadcrumb("test", "test", 0)).hashCode(), item.hashCode());
        assertEquals((new Breadcrumb("test", null, 0)).hashCode(), item.hashCode());

        assertNotEquals(new Breadcrumb("test 2", null, 0), item);
        assertNotEquals(new Breadcrumb("test", null, 2), item);
        assertNotEquals((new Breadcrumb("test 2", null, 0)).hashCode(), item.hashCode());
        assertNotEquals((new Breadcrumb("test", null, 2)).hashCode(), item.hashCode());

        item = new Breadcrumb(null, "test", null);
        assertEquals(new Breadcrumb(null, null, null), item);
        assertNotEquals(new Breadcrumb("test", null, null), item);
        assertNotEquals(new Breadcrumb(null, null, 0), item);
    }

    @Test
    public void testIsThisMenu() {
        Breadcrumb item = new Breadcrumb("test", "url", 0);
        assertTrue(item.isThisMenu("test"));
        assertFalse(item.isThisMenu("test 2"));
        assertFalse(item.isThisMenu(null));
    }

    @Test
    public void testMustBeShortened() {
        Breadcrumb item = new Breadcrumb("test", "url", 0);
        assertFalse(item.mustBeShortened());

        item.setKey("Test super long supérieur à trente caractères");
        assertTrue(item.mustBeShortened());

        item.setKey(null);
        assertFalse(item.mustBeShortened());
    }
}
