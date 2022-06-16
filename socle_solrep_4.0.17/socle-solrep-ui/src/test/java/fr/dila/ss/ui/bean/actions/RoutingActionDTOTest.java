package fr.dila.ss.ui.bean.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RoutingActionDTOTest {

    @Test
    public void testConstructor() {
        RoutingActionDTO dto = new RoutingActionDTO();
        assertEquals(false, dto.getHasRelatedRoute());
    }

    @Test
    public void testSetters() {
        RoutingActionDTO dto = new RoutingActionDTO();
        assertEquals(false, dto.getHasRelatedRoute());

        dto.setHasRelatedRoute(true);
        assertEquals(true, dto.getHasRelatedRoute());
    }
}
