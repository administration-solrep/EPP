package fr.sword.naiad.commons.xml.util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public final class DateUtil {

    private DateUtil() {
        // default private constructor
    }

    public static XMLGregorianCalendar calendarToXMLGregorianCalendar(final Calendar date) {
        if (date == null) {
            return null;
        }

        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date.getTime());
        XMLGregorianCalendar xmlDate;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            return xmlDate;
        } catch (final DatatypeConfigurationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static Calendar xmlGregorianCalendarToCalendar(final XMLGregorianCalendar date) {
        if (date == null) {
            return null;
        }

        final Calendar cal = Calendar.getInstance();
        cal.setTime(date.toGregorianCalendar().getTime());
        return cal;
    }
}
