package fr.dila.epp.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Lists;
import fr.dila.st.ui.bean.WidgetDTO;
import org.junit.Test;

public class DetailDossierTest {

    @Test
    public void testConstructor() {
        DetailDossier dto = new DetailDossier();
        assertNotNull(dto.getLstWidgets());
    }

    @Test
    public void testSetter() {
        DetailDossier dto = new DetailDossier();
        assertNotNull(dto.getLstWidgets());

        dto.setLstWidgets(Lists.newArrayList(new WidgetDTO(), new WidgetDTO()));
        assertNotNull(dto.getLstWidgets());
        assertEquals(2, dto.getLstWidgets().size());
    }
}
