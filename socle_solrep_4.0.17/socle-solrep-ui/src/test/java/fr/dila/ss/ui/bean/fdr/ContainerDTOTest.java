package fr.dila.ss.ui.bean.fdr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.nuxeo.ecm.platform.actions.Action;

public class ContainerDTOTest {

    @Test
    public void testConstructor() {
        ContainerDTO dto = new ContainerDTO();
        assertNull(dto.getId());
        assertNull(dto.getParent());
        assertFalse(dto.getIsParallel());
        assertNull(dto.getLevel());
        assertNull(dto.getPosition());
        assertEquals(new Integer(0), dto.getNbChilds());
        assertNotNull(dto.getActions());
        assertTrue(CollectionUtils.isEmpty(dto.getActions()));
        assertNull(dto.getDepth());
    }

    @Test
    public void testSetter() {
        ContainerDTO dto = new ContainerDTO();
        assertNull(dto.getId());
        assertNull(dto.getParent());
        assertFalse(dto.getIsParallel());
        assertNull(dto.getLevel());
        assertNull(dto.getPosition());
        assertEquals(new Integer(0), dto.getNbChilds());
        assertNotNull(dto.getActions());
        assertTrue(CollectionUtils.isEmpty(dto.getActions()));
        assertNull(dto.getDepth());

        dto.setId("monId");
        assertEquals("monId", dto.getId());

        ContainerDTO parent = new ContainerDTO();
        parent.setId("parentId");
        dto.setParent(parent);
        assertNotNull(dto.getParent());
        assertEquals("parentId", dto.getParent().getId());

        dto.setIsParallel(true);
        assertTrue(dto.getIsParallel());

        dto.setLevel(2);
        assertEquals(new Integer(2), dto.getLevel());

        dto.setPosition(7);
        assertEquals(new Integer(7), dto.getPosition());

        dto.setNbChilds(16);
        assertEquals(new Integer(16), dto.getNbChilds());
        List<Action> list = new ArrayList<>();

        Action action = new Action();
        action.setLabel("monAction");
        list.add(action);
        dto.setActions(list);
        assertEquals(1, dto.getActions().size());
        assertEquals("monAction", dto.getActions().get(0).getLabel());
        dto.setDepth(1);

        assertEquals(new Integer(1), dto.getDepth());
    }
}
