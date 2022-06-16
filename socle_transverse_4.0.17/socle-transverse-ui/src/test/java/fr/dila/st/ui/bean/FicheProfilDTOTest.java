package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class FicheProfilDTOTest {

    @Test
    public void testConstructor() {
        FicheProfilDTO ficheProfil = new FicheProfilDTO();
        assertNotNull(ficheProfil);
        assertNull(ficheProfil.getId());
        assertNull(ficheProfil.getLabel());
        assertNotNull(ficheProfil.getFonctions());
        assertNotNull(ficheProfil.getLstColonnes());
        assertEquals(true, ficheProfil.getFonctions().isEmpty());
        assertEquals(true, ficheProfil.getLstColonnes().isEmpty());
    }

    @Test
    public void testSetter() {
        FicheProfilDTO ficheProfil = new FicheProfilDTO();

        ficheProfil.setId("2");
        ficheProfil.setLabel("profil");
        assertNotNull(ficheProfil.getId());
        assertEquals("2", ficheProfil.getId());
        assertNotNull(ficheProfil.getLabel());
        assertEquals("profil", ficheProfil.getLabel());

        List<SelectValueDTO> fonctions = new ArrayList<>();
        fonctions.add(new SelectValueDTO("1", "Administrateur"));
        ficheProfil.setFonctions(fonctions);
        assertNotNull(ficheProfil.getFonctions());
        assertEquals(1, ficheProfil.getFonctions().size());
        assertEquals("1", ficheProfil.getFonctions().get(0).getId());
        assertEquals("Administrateur", ficheProfil.getFonctions().get(0).getLabel());

        List<ColonneInfo> lstColonnes = new ArrayList<>();
        lstColonnes.add(new ColonneInfo("colonne", true, true, false, true));
        ficheProfil.setLstColonnes(lstColonnes);
        assertNotNull(ficheProfil.getLstColonnes());
        assertEquals(1, ficheProfil.getLstColonnes().size());
        assertEquals("colonne", ficheProfil.getLstColonnes().get(0).getLabel());
        assertEquals(true, ficheProfil.getLstColonnes().get(0).isSortable());
        assertEquals(true, ficheProfil.getLstColonnes().get(0).isVisible());
    }
}
