package fr.dila.ss.ui.bean.fdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;

public class FdrTableDTOTest {

    @Test
    public void testConstructor() {
        FdrTableDTO dto = new FdrTableDTO();

        assertNotNull(dto.getLines());
        assertTrue(CollectionUtils.isEmpty(dto.getLines()));
        assertEquals(new Integer(0), dto.getTotalNbLevel());
    }

    @Test
    public void testSetter() {
        FdrTableDTO dto = new FdrTableDTO();

        assertNotNull(dto.getLines());
        assertTrue(CollectionUtils.isEmpty(dto.getLines()));
        assertEquals(new Integer(0), dto.getTotalNbLevel());

        List<EtapeDTO> list = new ArrayList<>();
        EtapeDTO etape = new EtapeDTO();
        etape.setId("idEtape");
        list.add(etape);
        dto.setLines(list);
        assertEquals(1, dto.getLines().size());
        assertEquals("idEtape", dto.getLines().get(0).getId());

        dto.setTotalNbLevel(5);
        assertEquals(new Integer(5), dto.getTotalNbLevel());
    }
}
