package fr.dila.ss.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.st.ui.bean.TreeElementDTO;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class MailboxListDTOTest {

    @Test
    public void testConstructor() {
        MailboxListDTO dto = new MailboxListDTO();
        assertNotNull(dto);
        assertNotNull(dto.getChilds());
        assertEquals(0, dto.getChilds().size());
        assertEquals("", dto.getSelectionPoste());
        assertEquals("", dto.getSelectionUser());
    }

    @Test
    public void testSetter() {
        MailboxListDTO dto = new MailboxListDTO();

        dto.setChilds(null);
        assertNull(dto.getChilds());

        List<TreeElementDTO> liste = new ArrayList<>();
        liste.add(new TreeElementDTO());
        liste.add(new TreeElementDTO());
        dto.setChilds(liste);
        assertNotNull(dto.getChilds());
        assertEquals(2, dto.getChilds().size());

        dto.setModeTri(TypeRegroupement.PAR_POSTE);
        assertEquals(TypeRegroupement.PAR_POSTE, dto.getModeTri());

        dto.setSelectionPoste("selectionPosteTest");
        assertEquals("selectionPosteTest", dto.getSelectionPoste());

        dto.setSelectionUser("selectionUserTest");
        assertEquals("selectionUserTest", dto.getSelectionUser());
    }
}
