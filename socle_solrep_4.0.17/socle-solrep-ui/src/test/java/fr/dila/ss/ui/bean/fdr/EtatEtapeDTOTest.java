package fr.dila.ss.ui.bean.fdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class EtatEtapeDTOTest {

    @Test
    public void testSimpleConstructor() {
        EtatEtapeDTO dto = new EtatEtapeDTO();

        assertNull(dto.getLabel());
        assertNull(dto.getIcon());
    }

    @Test
    public void testConstructorWithArgs() {
        EtatEtapeDTO dto = new EtatEtapeDTO("monLabel", "monIcon");

        assertEquals("monLabel", dto.getLabel());
        assertEquals("monIcon", dto.getIcon());
    }

    @Test
    public void testSetter() {
        EtatEtapeDTO dto = new EtatEtapeDTO();

        assertNull(dto.getLabel());
        assertNull(dto.getIcon());

        dto.setLabel("monLabel");
        assertEquals("monLabel", dto.getLabel());

        dto.setIcon("monIcon");
        assertEquals("monIcon", dto.getIcon());
    }
}
