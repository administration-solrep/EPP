package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class DropDownItemTest {

    @Test
    public void testConstructor() {
        DropDownItem item = new DropDownItem("url", "label");
        assertEquals("url", item.getUrl());
        assertEquals("label", item.getLabel());
        assertNull(item.getIcon());

        item = new DropDownItem("url", "label", "icon");
        assertEquals("url", item.getUrl());
        assertEquals("label", item.getLabel());
        assertEquals("icon", item.getIcon());
    }

    @Test
    public void testSetters() {
        DropDownItem item = new DropDownItem("url", "label");
        assertEquals("url", item.getUrl());
        assertEquals("label", item.getLabel());
        assertNull(item.getIcon());

        item.setIcon("icon");
        assertEquals("icon", item.getIcon());

        item.setUrl("url2");
        assertEquals("url2", item.getUrl());

        item.setLabel("label2");
        assertEquals("label2", item.getLabel());
    }
}
