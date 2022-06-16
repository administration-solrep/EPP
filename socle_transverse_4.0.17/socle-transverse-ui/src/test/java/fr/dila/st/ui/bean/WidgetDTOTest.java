package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import fr.dila.st.core.requete.recherchechamp.Parametre;
import org.junit.Test;

public class WidgetDTOTest {

    @Test
    public void testConstructor() {
        WidgetDTO dto = new WidgetDTO();
        assertNull(dto.getLabel());
        assertNull(dto.getTypeChamp());
        assertNotNull(dto.getParametres());
        assertNull(dto.getName());
        assertFalse(dto.isModifiedInCurVersion());
        assertNull(dto.getSingleFileName());
        assertNull(dto.getPjTitle());
        assertNotNull(dto.getLstFileNames());
    }

    @Test
    public void testSetters() {
        WidgetDTO dto = new WidgetDTO();
        assertNull(dto.getLabel());
        assertNull(dto.getTypeChamp());
        assertNotNull(dto.getParametres());
        assertNull(dto.getName());
        assertFalse(dto.isModifiedInCurVersion());
        assertNull(dto.getSingleFileName());
        assertNull(dto.getPjTitle());
        assertNotNull(dto.getLstFileNames());

        dto.setName("name");
        assertEquals("name", dto.getName());

        dto.setLabel("label");
        assertEquals("label", dto.getLabel());

        dto.setTypeChamp("file");
        assertEquals("file", dto.getTypeChamp());

        dto.setParametres(
            Lists.newArrayList(new Parametre("param", "paramvalue"), new Parametre("param2", "paramvalue2"))
        );
        assertEquals(2, dto.getParametres().size());

        dto.setModifiedInCurVersion(true);
        assertTrue(dto.isModifiedInCurVersion());

        dto.setSingleFileName("fileName");
        assertEquals("fileName", dto.getSingleFileName());

        dto.setPjTitle("fileTitle");
        assertEquals("fileTitle", dto.getPjTitle());

        dto.setLstFileNames(Lists.newArrayList("file", "file2", "file3"));
        assertEquals(3, dto.getLstFileNames().size());
    }
}
