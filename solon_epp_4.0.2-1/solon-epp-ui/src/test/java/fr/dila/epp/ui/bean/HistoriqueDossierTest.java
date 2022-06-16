package fr.dila.epp.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Lists;
import fr.dila.st.ui.bean.DossierHistoriqueEPP;
import fr.dila.st.ui.bean.MessageVersion;
import org.junit.Test;

public class HistoriqueDossierTest {

    @Test
    public void testConstructor() {
        DossierHistoriqueEPP dto = new DossierHistoriqueEPP();
        assertNotNull(dto.getLstVersions());
    }

    @Test
    public void testSetter() {
        DossierHistoriqueEPP dto = new DossierHistoriqueEPP();
        assertNotNull(dto.getLstVersions());

        dto.setLstVersions(Lists.newArrayList(new MessageVersion(), new MessageVersion()));
        assertNotNull(dto.getLstVersions());
        assertEquals(2, dto.getLstVersions().size());
    }
}
