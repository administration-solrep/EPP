package fr.dila.st.ui.bean.actions;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DossierLockActionDTOTest {

    @Test
    public void testConstructor() {
        DossierLockActionDTO dto = new DossierLockActionDTO();
        assertEquals(false, dto.getCanLockCurrentDossier());
    }

    @Test
    public void testSetter() {
        DossierLockActionDTO dto = new DossierLockActionDTO();
        assertEquals(false, dto.getCanLockCurrentDossier());

        dto.setCanLockCurrentDossier(true);
        assertEquals(true, dto.getCanLockCurrentDossier());
    }
}
