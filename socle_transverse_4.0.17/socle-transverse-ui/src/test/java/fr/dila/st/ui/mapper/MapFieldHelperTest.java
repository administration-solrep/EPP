package fr.dila.st.ui.mapper;

import static org.junit.Assert.assertEquals;

import fr.dila.st.ui.annot.NxProp;
import java.util.List;
import org.junit.Test;

public class MapFieldHelperTest {

    @SuppressWarnings("unused")
    public static class MyBean {
        @NxProp(xpath = "dc:title", docType = "File")
        private String field1;

        private String field2;

        @NxProp(xpath = "dc:description", docType = "File")
        private String field3;
    }

    @SuppressWarnings("unused")
    public static class MyBean2 {
        private String field1;
    }

    public static class MyBean3 extends MyBean {
        @NxProp(xpath = "ecm:uuid", docType = "File")
        private String fieldId;
    }

    @Test
    public void testExtractField() {
        List<MapField> fields;

        fields = MapFieldHelper.extractMapFields(MyBean2.class);
        assertEquals(0, fields.size());

        fields = MapFieldHelper.extractMapFields(MyBean.class);
        assertEquals(2, fields.size());

        fields = MapFieldHelper.extractMapFields(MyBean3.class);
        assertEquals(3, fields.size());
    }
}
