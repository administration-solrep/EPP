package fr.dila.st.ui.bean.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class STLockActionDTOTest {

    @Test
    public void testConstructor() {
        STLockActionDTO dto = new STLockActionDTO();
        assertEquals(false, dto.getCurrentDocIsLockActionnableByCurrentUser());
    }

    @Test
    public void testSetter() {
        STLockActionDTO dto = new STLockActionDTO();
        assertEquals(false, dto.getCurrentDocIsLockActionnableByCurrentUser());

        dto.setCurrentDocIsLockActionnableByCurrentUser(true);
        assertEquals(true, dto.getCurrentDocIsLockActionnableByCurrentUser());
    }
}
