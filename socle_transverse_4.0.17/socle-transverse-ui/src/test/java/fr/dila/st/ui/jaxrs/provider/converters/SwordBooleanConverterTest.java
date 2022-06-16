package fr.dila.st.ui.jaxrs.provider.converters;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;

public class SwordBooleanConverterTest {

    @Test
    public void testConverterDefaultValue() {
        SwordBooleanConverter converter = new SwordBooleanConverter(null);
        Boolean converted = converter.convert(Boolean.class, null);
        assertNull(converted);

        converter = new SwordBooleanConverter(true);
        converted = converter.convert(Boolean.class, null);
        assertTrue(converted);

        converter = new SwordBooleanConverter(false);
        converted = converter.convert(Boolean.class, null);
        assertFalse(converted);
    }

    @Test(expected = ConversionException.class)
    public void testConverterHandleUnknownValue() {
        SwordBooleanConverter converter = new SwordBooleanConverter(null);

        converter.convert(Boolean.class, "toto");
    }

    @Test
    public void testConverterBooleanValue() {
        SwordBooleanConverter converter = new SwordBooleanConverter(true);

        Boolean converted = converter.convert(Boolean.class, "yes");
        assertTrue(converted);
        converted = converter.convert(Boolean.TYPE, "no");
        assertFalse(converted);
        converted = converter.convert(Boolean.class, "y");
        assertTrue(converted);
        converted = converter.convert(Boolean.TYPE, "n");
        assertFalse(converted);
        converted = converter.convert(Boolean.class, "true");
        assertTrue(converted);
        converted = converter.convert(Boolean.TYPE, "false");
        assertFalse(converted);
        converted = converter.convert(Boolean.class, "1");
        assertTrue(converted);
        converted = converter.convert(Boolean.TYPE, "0");
        assertFalse(converted);
        converted = converter.convert(Boolean.class, "on");
        assertTrue(converted);
        converted = converter.convert(Boolean.TYPE, "off");
        assertFalse(converted);
    }
}
