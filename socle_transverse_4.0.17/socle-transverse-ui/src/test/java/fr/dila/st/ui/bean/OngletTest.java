package fr.dila.st.ui.bean;

import static org.junit.Assert.*;

import org.junit.Test;

public class OngletTest {

    @Test
    public void testConstructor() {
        Onglet onglet = new Onglet();
        assertEquals("", onglet.getContent());
        assertEquals(false, onglet.getIsActif());
        assertNull(onglet.getFragmentFile());
        assertNull(onglet.getFragmentName());
        assertNull(onglet.getId());
        assertNull(onglet.getLabel());
        assertNull(onglet.getScript());
    }

    @Test
    public void testSetters() {
        Onglet onglet = new Onglet();
        assertEquals("", onglet.getContent());
        assertEquals(false, onglet.getIsActif());
        assertNull(onglet.getFragmentFile());
        assertNull(onglet.getFragmentName());
        assertNull(onglet.getId());
        assertNull(onglet.getLabel());
        assertNull(onglet.getScript());

        onglet.setContent("test");
        assertEquals("test", onglet.getContent());

        onglet.setIsActif(true);
        assertEquals(true, onglet.getIsActif());

        onglet.setFragmentFile("fragment");
        assertEquals("fragment", onglet.getFragmentFile());

        onglet.setFragmentName("fragmentName");
        assertEquals("fragmentName", onglet.getFragmentName());

        onglet.setId("monId");
        assertEquals("monId", onglet.getId());

        onglet.setLabel("label");
        assertEquals("label", onglet.getLabel());

        onglet.setScript("monScript");
        assertEquals("monScript", onglet.getScript());
    }
}
