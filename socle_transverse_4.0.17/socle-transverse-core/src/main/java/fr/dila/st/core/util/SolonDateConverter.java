package fr.dila.st.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Formalisme des noms des éléments de l'enum :
 * - DATE|DATETIME selon qu'on s'arrête à la précision jour ou plus bas (exception : MONTH_YEAR)
 * - Séparateur des éléments de la date ou NOSEP
 * - REVERSE si on affiche dans l'ordre année/mois/jour
 * - Séparateur s'il y a un élément qui sépare la date de l'heure
 * - MINUTE|SECOND|MILLI pour indiquer le degré de précision dans l'affichage de l'heure
 * - Séparateur principal
 * - Elément final s'il y en a un.
 *
 */
public enum SolonDateConverter {
    /**
     * MMMM yyyy -> décembre 2010
     */
    MONTH_YEAR("MMMM yyyy"),
    /**
     * yyyy-MM -> 2010-12
     */
    YEAR_MONTH("yyyy-MM"),
    /**
     * dd MMMM yyyy -> 31 décembre 2010
     */
    DATE_SPACES("dd MMMM yyyy"),
    /**
     * ddMMyyyy -> 31122010
     */
    DATE_NOSEP("ddMMyyyy"),
    /**
     * yyyy-MM-dd -> 2010-12-31
     */
    DATE_DASH_REVERSE("yyyy-MM-dd"),
    /**
     * dd-MM-yyyy -> 31-12-2010
     */
    DATE_DASH("dd-MM-yyyy"),
    /**
     * dd/MM/yyyy -> 31/12/2010
     */
    DATE_SLASH("dd/MM/yyyy"),
    /**
     * yyyy/MM/dd -> 2010/12/31
     */
    DATE_SLASH_REVERSE("yyyy/MM/dd"),
    /**
     * dd/MM/yyyy HH:mm -> 31/12/2010 12:34
     */
    DATETIME_SLASH_MINUTE_COLON("dd/MM/yyyy HH:mm"),
    /**
     * dd/MM/yyyy à HH:mm -> 31/12/2010 à 12:34
     */
    DATETIME_SLASH_A_MINUTE_COLON("dd/MM/yyyy à HH:mm"),
    /**
     * dd-MM-yyyy-HH-mm -> 31-12-2010-12-34
     */
    DATETIME_DASH_MINUTE_DASH("dd-MM-yyyy-HH-mm"),

    /**
     * dd-MM-yyyy à HH:mm -> 31-12-2010 à 12:34
     */
    DATETIME_DASH_A_MINUTE_COLON("dd-MM-yyyy 'à' HH:mm"),
    /**
     * dd/MM/yyyy HH:mm:ss -> 31/12/2010 12:34:56
     */
    DATETIME_SLASH_SECOND_COLON("dd/MM/yyyy HH:mm:ss"),
    /**
     * yyyy-MM-dd HH:mm:ss -> 2010-12-31 12:34:56
     */
    DATETIME_DASH_REVERSE_SECOND_COLON("yyyy-MM-dd HH:mm:ss"),
    /**
     * dd_MM_yyyy_HH_mm_ss -> 31_12_2010_12_34_56
     */
    DATETIME_UNDER_SECOND_UNDER("dd_MM_yyyy_HH_mm_ss"),
    /**
     * dd_MM_yyyy_HH_mm_ss -> 31_12_2010_12_34_56
     */
    DATETIME_UNDER_REVERSE_SECOND_UNDER("yyyy_MM_dd_HH_mm_ss"),
    /**
     * yyyy-MM-dd'T'HH:mm:ss'Z' -> 2010-12-31T12:34:56Z
     *
     * format ISO8601 (ex: 2018-06-14T20:41:48Z).
     *
     * @see <a href=
     *      "https://en.wikipedia.org/wiki/ISO_8601">https://en.wikipedia.org/wiki/ISO_8601</a>
     */
    DATETIME_DASH_REVERSE_T_SECOND_COLON_Z("yyyy-MM-dd'T'HH:mm:ss'Z'"),
    /**
     * yyyy-MM-dd_HHmmss -> 2010-12-31_123456
     */
    DATETIME_DASH_REVERSE_SECOND_NOSEP("yyyy-MM-dd_HHmmss"),
    /**
     * yyyy-MM-dd-HH-mm-ss -> 2010-12-31-12-34-56
     */
    DATETIME_DASH_REVERSE_SECOND_DASH("yyyy-MM-dd-HH-mm-ss"),
    /**
     * yyyy-MM-dd'T'HH:mm-ss -> 2010-12-31T12:34-56
     */
    DATETIME_DASH_REVERSE_T_SECOND_COLON("yyyy-MM-dd'T'HH:mm-ss"),
    /**
     * yyyy-MM-dd HH:mm:ss.SSS -> 2010-12-31 12:34:56.789
     */
    DATETIME_DASH_REVERSE_MILLI_COLON("yyyy-MM-dd HH:mm:ss.SSS"),
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSS'Z' -> 2010-12-31T12:34:56.789Z
     */
    DATETIME_DASH_REVERSE_T_MILLI_Z("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private final String pattern;

    // Java 8 formatter is thread safe
    private static final Map<SolonDateConverter, DateTimeFormatter> LAZY_FORMATTER_MAP = MapUtils.lazyMap(
        new EnumMap<>(SolonDateConverter.class),
        sdc -> DateTimeFormatter.ofPattern(sdc.getPattern(), Locale.FRENCH)
    );

    SolonDateConverter(String pattern) {
        this.pattern = pattern;
    }

    public static SolonDateConverter getClientConverter() {
        return DATE_SPACES;
    }

    private DateTimeFormatter getFormatter() {
        return LAZY_FORMATTER_MAP.get(this);
    }

    public String getPattern() {
        return pattern;
    }

    public SimpleDateFormat simpleDateFormat() {
        return new SimpleDateFormat(pattern, Locale.FRENCH);
    }

    public String format(Temporal localDateToFormat) {
        return localDateToFormat == null ? null : getFormatter().format(localDateToFormat);
    }

    public String format(Date dateToFormat, boolean withTimeZone) {
        return dateToFormat == null ? null : formatNonNullSafe(dateToFormat, withTimeZone);
    }

    public String format(Calendar calToFormat, boolean withTimeZone) {
        return calToFormat == null ? null : formatNonNullSafe(calToFormat.getTime(), withTimeZone);
    }

    private String formatNonNullSafe(Date date, boolean withTimeZone) {
        SimpleDateFormat sdf = simpleDateFormat();
        if (withTimeZone) {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        }
        return sdf.format(date);
    }

    public String format(Calendar cal) {
        return format(cal, false);
    }

    public String format(Date date) {
        return format(date, false);
    }

    public String formatNow() {
        return format(LocalDateTime.now());
    }

    public Date parseToDate(String dateToParse) {
        SimpleDateFormat sdf = simpleDateFormat();
        try {
            return sdf.parse(dateToParse);
        } catch (ParseException e) {
            throw new NuxeoException("Impossible de parser au format " + pattern + " la date " + dateToParse, e);
        }
    }

    public Calendar parseToCalendar(String dateToParse) {
        Date date = parseToDate(dateToParse);
        return DateUtil.dateToGregorianCalendar(date);
    }

    public Calendar parseToCalendarOrNull(String dateToParse) {
        return StringUtils.isNotBlank(dateToParse) ? parseToCalendar(dateToParse) : null;
    }

    public Date parseToDateOrNull(String dateStr) {
        return StringUtils.isNotBlank(dateStr) ? parseToDate(dateStr) : null;
    }
}
