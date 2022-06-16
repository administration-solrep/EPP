package fr.dila.st.ui.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.Lists;
import org.junit.Test;

public class DetailCommunicationTest {

    @Test
    public void testConstructor() {
        DetailCommunication dto = new DetailCommunication();
        assertNotNull(dto.getLstWidgets());
        assertNotNull(dto.getLstComSuccessives());
    }

    @Test
    public void testSetter() {
        DetailCommunication dto = new DetailCommunication();
        assertNotNull(dto.getLstWidgets());
        assertNotNull(dto.getLstComSuccessives());

        dto.setLstWidgets(Lists.newArrayList(new WidgetDTO(), new WidgetDTO()));
        assertNotNull(dto.getLstWidgets());
        assertEquals(2, dto.getLstWidgets().size());

        dto.setLstComSuccessives(Lists.newArrayList(new SelectValueDTO(), new SelectValueDTO()));
        assertNotNull(dto.getLstComSuccessives());
        assertEquals(2, dto.getLstComSuccessives().size());
    }
}
