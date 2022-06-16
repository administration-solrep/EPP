package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class TreeDTOTest {

    @Test
    public void testConstructor() {
        TreeDTO<TreeElementDTO> arbre = new TreeDTO<>();
        assertNotNull(arbre);
        assertNotNull(arbre.getChilds());
        assertEquals(0, arbre.getChilds().size());
    }

    @Test
    public void testSetter() {
        TreeDTO<TreeElementDTO> arbre = new TreeDTO<>();
        assertNotNull(arbre);
        assertNotNull(arbre.getChilds());
        assertEquals(0, arbre.getChilds().size());

        arbre.setChilds(null);
        assertNull(arbre.getChilds());

        List<TreeElementDTO> liste = new ArrayList<>();
        liste.add(new TreeElementDTO());
        liste.add(new TreeElementDTO());
        arbre.setChilds(liste);
        assertNotNull(arbre.getChilds());
        assertEquals(2, arbre.getChilds().size());
    }
}
