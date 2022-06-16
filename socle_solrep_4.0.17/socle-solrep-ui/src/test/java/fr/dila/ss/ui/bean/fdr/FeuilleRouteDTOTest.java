package fr.dila.ss.ui.bean.fdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import fr.dila.ss.ui.bean.fdr.FeuilleRouteDTO;
import org.junit.Test;

public class FeuilleRouteDTOTest {

    @Test
    public void testConstructor() {
        FeuilleRouteDTO dto = new FeuilleRouteDTO();
        assertNull(dto.getId());
        assertNull(dto.getDocIdForSelection());
        assertNull(dto.getEtat());
        assertNull(dto.getIntitule());
        assertNull(dto.getAuteur());
        assertNull(dto.getDerniereModif());
        assertNull(dto.getlock());
        assertEquals("FeuilleRoute", dto.getType());

        dto = new FeuilleRouteDTO("id", "etat", "intitule", "ministere", "auteur", "derniereModif", false, "lockOwner");
        assertEquals("id", dto.getId());
        assertEquals("id", dto.getDocIdForSelection());
        assertEquals("etat", dto.getEtat());
        assertEquals("intitule", dto.getIntitule());
        assertEquals("auteur", dto.getAuteur());
        assertEquals("derniereModif", dto.getDerniereModif());
        assertEquals("FeuilleRoute", dto.getType());
        assertFalse(dto.getlock());
    }

    @Test
    public void testSetters() {
        FeuilleRouteDTO dto = new FeuilleRouteDTO();
        assertNull(dto.getId());
        assertNull(dto.getDocIdForSelection());
        assertNull(dto.getEtat());
        assertNull(dto.getIntitule());
        assertNull(dto.getAuteur());
        assertNull(dto.getDerniereModif());
        assertNull(dto.getlock());
        assertEquals("FeuilleRoute", dto.getType());

        dto.setId("id");
        assertEquals("id", dto.getId());

        dto.setDocIdForSelection("docIdForSelection");
        assertEquals("docIdForSelection", dto.getDocIdForSelection());

        dto.setEtat("etat");
        assertEquals("etat", dto.getEtat());

        dto.setIntitule("intitule");
        assertEquals("intitule", dto.getIntitule());

        dto.setAuteur("auteur");
        assertEquals("auteur", dto.getAuteur());

        dto.setDerniereModif("derniereModif");
        assertEquals("derniereModif", dto.getDerniereModif());

        dto.setLock(true);
        assertTrue(dto.getlock());

        dto.setLockOwner("lockOwner");
        assertEquals("lockOwner", dto.getlockOwner());
    }
}
