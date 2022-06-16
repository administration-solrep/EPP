package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class OngletConteneurTest {

    @Test
    public void testConstructor() {
        OngletConteneur conteneur = new OngletConteneur();
        assertNull(conteneur.getName());
        assertNull(conteneur.getOnglets());
        assertFalse(conteneur.isCurrentTabAllowed());
    }

    @Test
    public void testSetters() {
        OngletConteneur conteneur = new OngletConteneur();
        assertNull(conteneur.getName());
        assertNull(conteneur.getOnglets());
        assertFalse(conteneur.isCurrentTabAllowed());

        conteneur.setName("conteneur");
        assertEquals("conteneur", conteneur.getName());

        conteneur.setCurrentTabAllowed(true);
        assertTrue(conteneur.isCurrentTabAllowed());

        List<Onglet> onglets = new ArrayList<>();
        onglets.add(new Onglet());
        conteneur.setOnglets(onglets);
        assertNotNull(conteneur.getOnglets());
        assertEquals(1, conteneur.getOnglets().size());
    }
}
