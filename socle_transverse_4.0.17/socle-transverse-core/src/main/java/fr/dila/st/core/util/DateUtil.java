package fr.dila.st.core.util;

import static java.time.Instant.now;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Classe utilitaire sur les dates.
 *
 * @author jgomez
 * @author jtremeaux
 * @author sly
 */
public final class DateUtil {

    private DateUtil() {
        // utility class
    }

    /**
     * Convertit une date joda en une chaîne de caractère utilisable dans les requêtes NXQL.
     *
     * @param date la date à convertir
     * @return Chaîne formatée
     */
    public static String convert(final DateTime date) {
        String dateStr = SolonDateConverter.DATE_DASH_REVERSE.format(date.toDate());

        return "'" + dateStr + "'";
    }

    /**
     * Convertit un {@link Calendar} en {@link XMLGregorianCalendar}.
     */
    public static XMLGregorianCalendar calendarToXMLGregorianCalendar(Calendar date) {
        if (date == null) {
            return null;
        }

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(date.getTime());
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new NuxeoException(e);
        }
    }

    /**
     * Convertit une {@link Date} en {@link GregorianCalendar}.
     */
    public static Calendar dateToGregorianCalendar(Date date) {
        if (date == null) {
            return null;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        return calendar;
    }

    /**
     * Convertit un {@link Instant} en {@link GregorianCalendar}.
     */
    public static Calendar instantToGregorianCalendar(Instant instant) {
        if (instant == null) {
            return null;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(instant.toEpochMilli());

        return calendar;
    }

    /**
     * Convertit un {@link Calendar} en {@link Instant}.
     */
    public static Instant gregorianCalendarToInstant(Calendar calendar) {
        return calendar == null ? null : calendar.toInstant();
    }

    /**
     * Convertit une {@link LocalDate} en {@link GregorianCalendar}.
     */
    public static Calendar localDateToGregorianCalendar(LocalDate date) {
        return date == null ? null : instantToGregorianCalendar(localDateToInstant(date));
    }

    /**
     * Convertit un {@link Calendar} en {@link LocalDate}.
     */
    public static LocalDate gregorianCalendarToLocalDate(Calendar calendar) {
        return calendar == null
            ? null
            : gregorianCalendarToInstant(calendar).atZone(calendar.getTimeZone().toZoneId()).toLocalDate();
    }

    /**
     * Convertit une {@link LocalDate} en {@link Date}.
     */
    public static Date localDateToDate(LocalDate date) {
        return date == null ? null : Date.from(localDateToInstant(date));
    }

    /**
     * Convertit une {@link LocalDate} au type {@link Instant}.
     */
    private static Instant localDateToInstant(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    /**
     * Convertit une string en {@link XMLGregorianCalendar}.
     */
    public static XMLGregorianCalendar stringToXMLGregorianCalendar(String date) {
        try {
            return date == null ? null : DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
        } catch (DatatypeConfigurationException e) {
            throw new NuxeoException(e);
        }
    }

    /**
     * Convertisseur {@link XMLGregorianCalendar} => {@link Calendar}
     */
    public static Calendar xmlGregorianCalendarToCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar == null ? null : xmlGregorianCalendar.toGregorianCalendar();
    }

    /**
     * Convertisseur {@link XMLGregorianCalendar} => {@link Date}
     */
    public static Date xmlGregorianCalendarToDate(XMLGregorianCalendar xmlGregorianCalendar) {
        return xmlGregorianCalendar == null ? null : xmlGregorianCalendar.toGregorianCalendar().getTime();
    }

    /**
     * Convertisseur {@link Date} => {@link XMLGregorianCalendar}
     */
    public static XMLGregorianCalendar dateToXmlGregorianCalendar(Date date) {
        return date == null ? null : calendarToXMLGregorianCalendar(DateUtils.toCalendar(date));
    }

    /**
     * Retourne true si les deux Calendar définissent le même jour, ou que les deux sont nuls.
     *
     * @param calendar1
     * @param calendar2
     * @return
     */
    public static boolean isSameDay(Calendar calendar1, Calendar calendar2) {
        if (calendar1 == null && calendar2 != null || calendar1 != null && calendar2 == null) {
            return false;
        }

        if (calendar1 == null && calendar2 == null) {
            return true;
        }

        return DateUtils.isSameDay(calendar1, calendar2);
    }

    /**
     * Used to clone an external date
     *
     * @param externalDate the external Date
     * @return a copy of date
     */
    public static Date copyDate(Date externalDate) {
        return externalDate == null ? null : (Date) externalDate.clone();
    }

    /**
     * Retourne une liste de date au format XMLGregorianCalendar depuis une liste de date au format calendar
     */
    public static List<XMLGregorianCalendar> listCalendarToGregorianCalendar(List<Calendar> listDate) {
        if (listDate == null) {
            return null;
        }

        return listDate
            .stream()
            .filter(Objects::nonNull)
            .map(DateUtil::calendarToXMLGregorianCalendar)
            .collect(Collectors.toList());
    }

    public static void setDateToBeginingOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public static void setDateToEndOfDay(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }

    public static String getDuration(Instant from) {
        return Duration.between(from, now()).toString().substring(2).toLowerCase();
    }

    public static Calendar toCalendar(Date date) {
        return date == null ? null : DateUtils.toCalendar(date);
    }

    public static Calendar toCalendarFromNotNullDate(Date date) {
        return DateUtils.toCalendar(date);
    }

    public static Date toDate(Calendar cal) {
        return cal == null ? null : cal.getTime();
    }

    public static Calendar addMonthsToNow(int months) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, months);
        return cal;
    }

    public static Calendar removeMonthsToNow(int months) {
        return addMonthsToNow(months * -1);
    }

    public static String convertFromTimestamp(final String dateToParse) {
        return SolonDateConverter.DATETIME_SLASH_SECOND_COLON.format(
            SolonDateConverter.DATETIME_DASH_REVERSE_MILLI_COLON.parseToDate(dateToParse)
        );
    }

    /**
     * Convertit une date au format dd/MM/yyyy en yyyy-MM-dd
     */
    public static String convertToReverseDate(String dateAsString) {
        Date date = SolonDateConverter.DATE_SLASH.parseToDateOrNull(dateAsString);
        return date == null ? null : SolonDateConverter.DATE_DASH_REVERSE.format(date);
    }
}
