package fr.dila.epp.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import fr.dila.epp.ui.th.bean.MessageListForm;
import fr.dila.st.ui.bean.ColonneInfo;
import fr.dila.st.ui.bean.IColonneInfo;
import java.util.List;
import org.junit.Test;

public class MessageListTest {

    @Test
    public void testConstructor() {
        MessageList dto = new MessageList();

        assertNotNull(dto.getListe());
        assertNotNull(dto.getListeColones());
        assertEquals(new Integer(0), dto.getNbTotal());
    }

    @Test
    public void testSetter() {
        MessageList dto = new MessageList();
        assertNotNull(dto.getListe());
        assertNotNull(dto.getListeColones());
        assertEquals(new Integer(0), dto.getNbTotal());

        dto.setListe(Lists.newArrayList(new EppMessageDTO(), new EppMessageDTO()));
        assertNotNull(dto.getListe());
        assertEquals(2, dto.getListe().size());

        dto.setNbTotal(11);
        assertEquals(new Integer(11), dto.getNbTotal());

        assertNotNull(dto.getListeColones(new MessageListForm()));
    }

    @Test
    public void getIHMColonnes() {
        MessageList dto = new MessageList();
        List<ColonneInfo> lstColonnes = dto.getListeColones();
        assertNotNull(lstColonnes);
        assertTrue(lstColonnes.isEmpty());

        MessageListForm form = new MessageListForm();
        lstColonnes = dto.getListeColones(form);
        assertNotNull(lstColonnes);

        assertEquals(10, lstColonnes.size());
        IColonneInfo col = lstColonnes.get(1);
        assertNotNull(col);
        assertEquals("corbeille.communication.table.header.iddossier", col.getLabel());
        assertTrue(col.isVisible());
        assertTrue(col.isSortable());
        assertEquals("idDossierTri", col.getSortName());
        assertEquals("idDossierTriHeader", col.getSortId());
    }
}
