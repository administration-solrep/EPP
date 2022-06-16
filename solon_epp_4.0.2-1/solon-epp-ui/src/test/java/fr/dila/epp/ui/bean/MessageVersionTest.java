package fr.dila.epp.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.google.common.collect.Lists;
import fr.dila.st.ui.bean.MessageVersion;
import org.junit.Test;

public class MessageVersionTest {

    @Test
    public void testConstructors() {
        MessageVersion dto = new MessageVersion();
        assertNull(dto.getCommunicationId());
        assertNull(dto.getCommunicationLabel());
        assertNull(dto.getCommunicationType());
        assertNotNull(dto.getLstChilds());
        assertEquals(false, dto.isCourant());
        assertEquals(false, dto.isAnnule());

        dto = new MessageVersion("id", "label", "type", true, true);
        assertEquals("id", dto.getCommunicationId());
        assertEquals("label", dto.getCommunicationLabel());
        assertEquals("type", dto.getCommunicationType());
        assertNotNull(dto.getLstChilds());
        assertEquals(true, dto.isCourant());
        assertEquals(true, dto.isAnnule());
    }

    @Test
    public void testSetters() {
        MessageVersion dto = new MessageVersion();
        assertNull(dto.getCommunicationId());
        assertNull(dto.getCommunicationLabel());
        assertNull(dto.getCommunicationType());
        assertNotNull(dto.getLstChilds());
        assertEquals(false, dto.isCourant());
        assertEquals(false, dto.isAnnule());

        dto.setCommunicationId("id");
        assertEquals("id", dto.getCommunicationId());

        dto.setCommunicationLabel("label");
        assertEquals("label", dto.getCommunicationLabel());

        dto.setCommunicationType("type");
        assertEquals("type", dto.getCommunicationType());

        dto.setLstChilds(Lists.newArrayList(new MessageVersion(), new MessageVersion(), new MessageVersion()));
        assertNotNull(dto.getLstChilds());
        assertEquals(3, dto.getLstChilds().size());

        dto.setCourant(true);
        assertEquals(true, dto.isCourant());

        dto.setAnnule(true);
        assertEquals(true, dto.isAnnule());
    }
}
