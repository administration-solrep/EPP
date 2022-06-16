package fr.dila.st.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.google.common.collect.ImmutableList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.nuxeo.ecm.core.api.NuxeoException;

public class SolonDateConverterTest {
    private static List<CalendarAndLocalTime> datesToTest = ImmutableList.of(
        new CalendarAndLocalTime(1984, Calendar.MARCH, 15, 18, 28, 11, 123),
        new CalendarAndLocalTime(2021, Calendar.MAY, 18, 12, 34, 56, 789),
        new CalendarAndLocalTime(1999, Calendar.DECEMBER, 31, 23, 59, 59, 999),
        new CalendarAndLocalTime(2000, Calendar.JANUARY, 1, 0, 0, 0, 0)
    );

    private static class CalendarAndLocalTime {
        private final Calendar calendar;
        private final LocalDateTime localDateTime;

        CalendarAndLocalTime(int year, int month, int day, int hour, int minute, int second, int millisecond) {
            Calendar cal = new GregorianCalendar();
            cal.set(year, month, day, hour, minute, second);
            cal.set(Calendar.MILLISECOND, millisecond);

            calendar = cal;
            localDateTime =
                LocalDateTime.of(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.SECOND),
                    cal.get(Calendar.MILLISECOND) * 1000000
                );
        }

        Calendar getCalendar() {
            return calendar;
        }

        LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        Date getDate() {
            return calendar.getTime();
        }
    }

    private void testAllConverters(Consumer<SolonDateConverter> converterTester) {
        Stream.of(SolonDateConverter.values()).forEach(converterTester);
    }

    private void testAllConvertersAndDates(BiConsumer<SolonDateConverter, CalendarAndLocalTime> converterTester) {
        Stream
            .of(SolonDateConverter.values())
            .forEach(t -> datesToTest.forEach(date -> converterTester.accept(t, date)));
    }

    private int getPrecision(SolonDateConverter converter) {
        String pattern = converter.getPattern();
        List<Pair<Character, Integer>> symbols = ImmutableList.of(
            Pair.of('S', Calendar.MILLISECOND),
            Pair.of('s', Calendar.SECOND),
            Pair.of('m', Calendar.MINUTE),
            Pair.of('H', Calendar.HOUR),
            Pair.of('d', Calendar.DAY_OF_MONTH),
            Pair.of('M', Calendar.MONTH),
            Pair.of('y', Calendar.YEAR)
        );

        return symbols
            .stream()
            .filter(pair -> pattern.indexOf(pair.getLeft()) > -1)
            .findFirst()
            .map(Pair::getRight)
            .map(Integer.class::cast)
            .orElse(Calendar.YEAR);
    }

    @Test
    public void testFormat() {
        // For all known formats :
        testAllConverters(
            converter -> {
                // Check that format() returns something that can be parsed back to a date
                String actual = converter.formatNow();
                String pattern = converter.getPattern();

                SimpleDateFormat sdf = getSimpleDateFormat(pattern);

                try {
                    assertThat(sdf.parse(actual)).isBeforeOrEqualTo(new Date());
                } catch (ParseException e) {
                    fail(pattern + " -> " + e.getMessage() + " should be " + sdf.format(new Date()));
                }
            }
        );
    }

    @Test
    public void testFormatDate() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String actual = converter.format(date.getDate());

                assertCalendarsAreSimilar(converter, date, actual, false);
            }
        );
    }

    @Test
    public void testFormatCalendar() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String actual = converter.format(date.getCalendar());

                assertCalendarsAreSimilar(converter, date, actual, false);
            }
        );
    }

    @Test
    public void testFormatCalendarWithNullValue() {
        testAllConverters(converter -> assertThat(converter.format((Calendar) null)).isNull());
    }

    @Test
    public void testFormatCalendarWithTimeZone() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String actualNoTimezone = converter.format(date.getCalendar(), false);

                assertCalendarsAreSimilar(converter, date, actualNoTimezone, false);

                String actualWithTimezone = converter.format(date.getCalendar(), true);

                assertCalendarsAreSimilar(converter, date, actualWithTimezone, true);
            }
        );
    }

    @Test
    public void testFormatDateWithTimeZone() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String actualNoTimezone = converter.format(date.getDate(), false);

                assertCalendarsAreSimilar(converter, date, actualNoTimezone, false);

                String actualWithTimezone = converter.format(date.getDate(), true);

                assertCalendarsAreSimilar(converter, date, actualWithTimezone, true);
            }
        );
    }

    @Test
    public void testFormatTemporal() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String actual = converter.format(date.getLocalDateTime());

                assertCalendarsAreSimilar(converter, date, actual, false);
            }
        );
    }

    @Test
    public void testParseToDate() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String pattern = converter.getPattern();
                SimpleDateFormat sdf = getSimpleDateFormat(pattern);
                String given = sdf.format(date.getDate());

                Date actual = converter.parseToDate(given);

                Calendar cal = Calendar.getInstance();
                cal.setTime(actual);

                checkCalendarFields(converter, date.getCalendar(), cal);
            }
        );
    }

    @Test
    public void testParseToDateWithUnparsable() {
        testAllConverters(
            converter ->
                Assertions
                    .assertThatThrownBy(() -> converter.parseToDate("unparsable"))
                    .isInstanceOf(NuxeoException.class)
                    .hasMessageContaining("Impossible de parser au format")
        );
    }

    @Test
    public void testParseToDateOrNull() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String pattern = converter.getPattern();
                SimpleDateFormat sdf = getSimpleDateFormat(pattern);
                String given = sdf.format(date.getDate());

                Date actual = converter.parseToDateOrNull(given);

                Calendar cal = Calendar.getInstance();
                cal.setTime(actual);

                checkCalendarFields(converter, date.getCalendar(), cal);
            }
        );

        testAllConverters(
            converter -> {
                assertThat(converter.parseToDateOrNull("")).isNull();
            }
        );
        testAllConverters(
            converter -> {
                assertThat(converter.parseToDateOrNull(null)).isNull();
            }
        );
    }

    @Test
    public void testParseToCalendarOrNull() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String pattern = converter.getPattern();
                SimpleDateFormat sdf = getSimpleDateFormat(pattern);
                String given = sdf.format(date.getDate());

                Calendar actual = converter.parseToCalendarOrNull(given);

                checkCalendarFields(converter, date.getCalendar(), actual);
            }
        );

        testAllConverters(
            converter -> {
                assertThat(converter.parseToDateOrNull("")).isNull();
            }
        );
        testAllConverters(
            converter -> {
                assertThat(converter.parseToDateOrNull(null)).isNull();
            }
        );
    }

    @Test
    public void testParseToCalendar() {
        testAllConvertersAndDates(
            (converter, date) -> {
                String pattern = converter.getPattern();
                SimpleDateFormat sdf = getSimpleDateFormat(pattern);
                String given = sdf.format(date.getDate());

                Calendar actual = converter.parseToCalendar(given);

                checkCalendarFields(converter, date.getCalendar(), actual);
            }
        );
    }

    private SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.FRENCH);
    }

    @Test
    public void testFormatForClient() {
        Date date1 = SolonDateConverter.DATE_SLASH.parseToDate("26/09/2021");
        Date date2 = SolonDateConverter.DATE_SLASH.parseToDate("03/12/2021");

        String expectedFormat1 = "26 septembre 2021";
        String expectedFormat2 = "03 décembre 2021";

        assertThat(SolonDateConverter.getClientConverter().format(date1)).isEqualTo(expectedFormat1);
        assertThat(SolonDateConverter.getClientConverter().format(date2)).isEqualTo(expectedFormat2);
    }

    private void checkCalendarFields(SolonDateConverter converter, Calendar expectedCal, Calendar actual) {
        for (int field = 0; field <= getPrecision(converter); field++) {
            int got = actual.get(field);
            int expected = expectedCal.get(field);
            assertThat(got)
                .withFailMessage(
                    "Format %s returned date %s: expected %s, got %s",
                    converter.getPattern(),
                    actual,
                    expected,
                    got
                )
                .isEqualTo(expected);
        }
    }

    private void assertCalendarsAreSimilar(
        SolonDateConverter converter,
        CalendarAndLocalTime date,
        String actual,
        boolean withTimezone
    ) {
        String pattern = converter.getPattern();
        SimpleDateFormat sdf = getSimpleDateFormat(pattern);
        try {
            Calendar actualCal = Calendar.getInstance();

            actualCal.setTime(sdf.parse(actual));

            Calendar gmtCal = (Calendar) date.getCalendar().clone();
            if (withTimezone) {
                gmtCal.setTimeZone(TimeZone.getTimeZone("GMT"));
            }
            checkCalendarFields(converter, gmtCal, actualCal);
        } catch (ParseException e) {
            fail(pattern + " -> " + e.getMessage() + " should be " + sdf.format(new Date()));
        }
    }

    private void assertOneFormatIsFoundByItsPattern(SolonDateConverter sdf) {
        assertThat(findByPattern(sdf.getPattern())).contains(sdf);
    }

    @Test
    public void testFindFormatByPattern() {
        Stream.of(SolonDateConverter.values()).forEach(this::assertOneFormatIsFoundByItsPattern);

        assertThat(findByPattern("unknown pattern")).isEmpty();
    }

    private static Optional<SolonDateConverter> findByPattern(String pattern) {
        return Stream.of(SolonDateConverter.values()).filter(sdf -> sdf.getPattern().equals(pattern)).findFirst();
    }
}
