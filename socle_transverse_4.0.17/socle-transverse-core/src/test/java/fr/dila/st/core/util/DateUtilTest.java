package fr.dila.st.core.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Test;

public class DateUtilTest {

    @Test
    public void testXmlGregorianCalendarToCalendarWithNullCalendar() {
        assertThat(DateUtil.xmlGregorianCalendarToCalendar(null)).isNull();
    }

    @Test
    public void testXmlGregorianCalendarToDateWithNullCalendar() {
        assertThat(DateUtil.xmlGregorianCalendarToDate(null)).isNull();
    }

    @Test
    public void testDateToXmlGregorianCalendar() {
        // Given
        Date date = SolonDateConverter.DATE_SLASH.parseToDate("18/05/2021");

        // When
        XMLGregorianCalendar calendar = DateUtil.dateToXmlGregorianCalendar(date);

        // Then
        assertThat(calendar.getDay()).isEqualTo(18);
        assertThat(calendar.getMonth()).isEqualTo(5);
        assertThat(calendar.getYear()).isEqualTo(2021);
    }

    @Test
    public void testDateToXmlGregorianCalendarWithNullDate() {
        assertThat(DateUtil.dateToXmlGregorianCalendar(null)).isNull();
    }

    @Test
    public void testToCalendar() {
        // Given
        Date date = SolonDateConverter.DATE_SLASH.parseToDate("18/05/2021");

        // When
        Calendar calendar = DateUtil.toCalendar(date);

        // Then
        assertThat(calendar.get(Calendar.DAY_OF_MONTH)).isEqualTo(18);
        assertThat(calendar.get(Calendar.MONTH)).isEqualTo(Calendar.MAY);
        assertThat(calendar.get(Calendar.YEAR)).isEqualTo(2021);
    }

    @Test
    public void testToCalendarWithNullDate() {
        assertThat(DateUtil.toCalendar(null)).isNull();
    }

    @Test
    public void testAddMonthsToNow() {
        LocalDate now = LocalDate.now();
        int monthsToAdd = 6;

        Calendar cal = DateUtil.addMonthsToNow(monthsToAdd);

        assertThat(cal.get(Calendar.MONTH)).isEqualTo(now.plusMonths(monthsToAdd).getMonth().ordinal());
    }

    @Test
    public void testRemoveMonthsToNow() {
        LocalDate now = LocalDate.now();
        int monthsToRemove = 6;

        Calendar cal = DateUtil.removeMonthsToNow(monthsToRemove);

        assertThat(cal.get(Calendar.MONTH)).isEqualTo(now.minusMonths(monthsToRemove).getMonth().ordinal());
    }

    @Test
    public void testListCalendarToGregorianCalendar() {
        // Given
        Calendar calendar1 = DateUtil.toCalendarFromNotNullDate(
            SolonDateConverter.DATE_SLASH.parseToDate("18/05/2021")
        );
        Calendar calendar2 = DateUtil.toCalendarFromNotNullDate(
            SolonDateConverter.DATE_SLASH.parseToDate("21/05/2021")
        );
        Calendar calendar3 = null;

        // When
        List<XMLGregorianCalendar> calendars = DateUtil.listCalendarToGregorianCalendar(
            Arrays.asList(calendar1, calendar2, calendar3)
        );

        // Then
        assertThat(calendars)
            .containsExactly(
                DateUtil.calendarToXMLGregorianCalendar(calendar1),
                DateUtil.calendarToXMLGregorianCalendar(calendar2)
            );
    }

    @Test
    public void testListCalendarToGregorianCalendarWithNullDate() {
        assertThat(DateUtil.listCalendarToGregorianCalendar(null)).isNull();
    }

    @Test
    public void testFormatDDMMYYYYSlash() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.APRIL, 18);
        Date date = calendar.getTime();

        assertThat(SolonDateConverter.DATE_SLASH.format(date)).isEqualTo("18/04/2021");
    }

    @Test
    public void testGetDateFormatDDMMMMYYYYSpaces() {
        assertThat(SolonDateConverter.DATE_SPACES.simpleDateFormat())
            .isEqualTo(new SimpleDateFormat("dd MMMM yyyy", Locale.FRENCH));
    }

    @Test
    public void testXmlGregorianCalendarToDate() throws DatatypeConfigurationException {
        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();
        Date expected = cal.getTime();

        // When
        Date result = null;
        try {
            result = DateUtil.xmlGregorianCalendarToDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(cal));
        } catch (DatatypeConfigurationException e) {
            fail(e.getMessage());
        }

        // Then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testXmlGregorianCalendarToCalendar() throws DatatypeConfigurationException {
        GregorianCalendar givenCal = (GregorianCalendar) GregorianCalendar.getInstance();

        // When
        Calendar result = null;
        try {
            result =
                DateUtil.xmlGregorianCalendarToCalendar(
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(givenCal)
                );
        } catch (DatatypeConfigurationException e) {
            fail(e.getMessage());
        }

        // Then
        assertThat(result.get(Calendar.DAY_OF_MONTH)).isEqualTo(givenCal.get(Calendar.DAY_OF_MONTH));
        assertThat(result.get(Calendar.MONTH)).isEqualTo(givenCal.get(Calendar.MONTH));
        assertThat(result.get(Calendar.YEAR)).isEqualTo(givenCal.get(Calendar.YEAR));
    }
}
