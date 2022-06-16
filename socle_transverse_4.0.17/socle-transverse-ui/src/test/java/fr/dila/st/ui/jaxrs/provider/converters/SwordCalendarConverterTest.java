package fr.dila.st.ui.jaxrs.provider.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;

public class SwordCalendarConverterTest {

    @Test
    public void testConverterDefaultValue() {
        SwordCalendarConverter converter = new SwordCalendarConverter();
        Calendar converted = converter.convert(Calendar.class, null);
        assertNull(converted);
        converted = converter.convert(Calendar.class, "");
        assertNull(converted);
        converted = converter.convert(Calendar.class, " ");
        assertNull(converted);
    }

    @Test(expected = ConversionException.class)
    public void testConverterHandleUnknownValue() {
        SwordCalendarConverter converter = new SwordCalendarConverter();

        converter.convert(Calendar.class, "toto");
    }

    @Test
    public void testConverterToValue() {
        SwordCalendarConverter converter = new SwordCalendarConverter();
        Calendar expected = new GregorianCalendar(2020, 0, 1);

        Date actualDate = converter.convert(Date.class, "01/01/2020");
        assertEquals(expected.getTime(), actualDate);

        Calendar actualCal = converter.convert(Calendar.class, "01/01/2020");
        assertEquals(expected, actualCal);

        GregorianCalendar actualGregCal = converter.convert(GregorianCalendar.class, "01/01/2020");
        assertEquals(expected, actualGregCal);
    }
}
