package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class PageProfilDTOTest {

    @Test
    public void testConstructor() {
        PageProfilDTO ppd = new PageProfilDTO();
        assertNotNull(ppd);
    }

    @Test
    public void testSetter() {
        PageProfilDTO page = new PageProfilDTO();
        List<String> listPro = new ArrayList<>();
        listPro.add("profilName");

        assertNotNull(listPro);
        assertEquals(1, listPro.size());

        List<ColonneInfo> lstColonnes = new ArrayList<>();
        ColonneInfo colonne = new ColonneInfo("profil", true, true, false, true);
        assertNotNull(colonne);
        lstColonnes.add(colonne);

        assertEquals(1, lstColonnes.size());

        page.setProfils(listPro);
        page.setLstColonnes(lstColonnes);

        assertNotNull(page);
        assertNotNull(page.getProfils());
        assertNotNull(page.getLstColonnes());
    }
}
