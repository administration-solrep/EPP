package fr.dila.st.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.time.DateUtils;
import org.joda.time.DateTime;
import org.nuxeo.ecm.core.api.ClientException;

/**
 * Classe utilitaire sur les dates.
 * 
 * @author jgomez
 * @author jtremeaux
 * @author sly
 */
public final class DateUtil {

	private static final String	FORMAT_YYYY_MM_DD	= "yyyy-MM-dd";

	/**
	 * utility class
	 */
	private DateUtil() {
		// do nothing
	}

	/**
	 * Convertit une date joda en une chaîne de caractère utilisable dans les requêtes NXQL.
	 * 
	 * @param date
	 *            la date à convertir
	 * @return Chaîne formatée
	 */
	public static String convert(final DateTime date) {
		SimpleDateFormat isoDate = new SimpleDateFormat(FORMAT_YYYY_MM_DD, Locale.FRENCH);
		String dateStr = isoDate.format(date.toDate());

		return "'" + dateStr + "'";
	}

	/**
	 * Convertit une date au format dd-MM-yyyy.
	 * 
	 * @param date
	 *            Date
	 * @return Date formatée
	 */
	public static String formatDDMMYYYY(final Calendar calendar) {

		return formatDDMMYYYY(calendar.getTime());
	}

	/**
	 * Convertit une date au format dd-MM-yyyy.
	 * 
	 * @param date
	 *            Date
	 * @return Date formatée
	 */
	public static String formatDDMMYYYY(final Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.FRENCH);
		return format.format(date);
	}

	/**
	 * Convertit une date au format dd/MM/yyyy.
	 * 
	 * @param date
	 *            Date
	 * @return Date formatée
	 */
	public static String formatDDMMYYYYSlash(final Calendar calendar) {
		return formatDDMMYYYYSlash(calendar.getTime());
	}

	/**
	 * Convertit une date au format dd/MM/yyyy.
	 * 
	 * @param date
	 *            Date
	 * @return Date formatée
	 */
	public static String formatDDMMYYYYSlash(final Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
		return format.format(date);
	}

	/**
	 * Convertit un objet de type Calendar au type XMLGregorianCalendar.
	 * 
	 * @param date
	 *            Date de type Calendar
	 * @return Date de type XMLGregorianCalendar
	 */
	public static XMLGregorianCalendar calendarToXMLGregorianCalendar(Calendar date) {
		if (date == null) {
			return null;
		}

		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date.getTime());
		XMLGregorianCalendar xmlDate;
		try {
			xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
			return xmlDate;
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Convertit une date au type GregorianCalendar.
	 * 
	 * @param date
	 *            Date
	 * @return Date de type GregorianCalendar
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
	 * Convertit une date au format string xs:date au type XMLGregorianCalendar.
	 * 
	 * @param date
	 *            Date au format string xs:date
	 * @return Date de type XMLGregorianCalendar
	 */
	public static XMLGregorianCalendar stringToXMLGregorianCalendar(String date) {
		if (date == null) {
			return null;
		}

		XMLGregorianCalendar xmlDate;
		try {
			xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
			return xmlDate;
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

	public static String formatForClient(final Date date) {
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd MMMMM yyyy", Locale.FRENCH);
			return format.format(date);
		}
		return null;
	}

	/**
	 * Convertit une date au format dd/MM/yyyy HH:mm.
	 * 
	 * @param date
	 *            Date
	 * @return Date formatée
	 */
	public static String formatWithHour(final Date date) {
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm ", Locale.FRENCH);
			return format.format(date);
		}
		return null;
	}

	/**
	 * Convertit une date au format dd/MM/yyyy à HH:mm.
	 * 
	 * @param date
	 *            Date
	 * @return Date formatée
	 */
	public static String formatddmmyyyyahhmm(final Date date) {
		if (date != null) {
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy à HH:mm ", Locale.FRENCH);
			return format.format(date);
		}
		return null;
	}

	/**
	 * Convertit une string au format dd/MM/yyyy HH:mm en date
	 * 
	 * @param date
	 *            String au format dd/MM/yyyy HH:mm
	 * @return Calendar
	 * @throws ClientException
	 */
	public static Calendar parseWithHour(final String dateStr) throws ClientException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRENCH);
		Date date;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			throw new ClientException("Impossible de parser au format dd/MM/yyyy HH:mm la date " + dateStr, e);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * Convertit une string au format dd/MM/yyyy HH:mm:ss en date
	 * 
	 * @param date
	 *            String au format dd/MM/yyyy HH:mm:ss
	 * @return Calendar
	 * @throws ClientException
	 */
	public static Calendar parseWithSec(final String dateStr) throws ClientException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRENCH);
		Date date;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			throw new ClientException("Impossible de parser au format dd/MM/yyyy HH:mm:ss la date " + dateStr, e);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * Convertit une date au format yyyy-MM-dd
	 * 
	 * @param date
	 *            Date
	 * @return la chaîne de caractère
	 */
	public static String formatYYYYMMdd(Date date) {
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_YYYY_MM_DD, Locale.FRENCH);
		return format.format(date);
	}

	/**
	 * Convertit une string en date
	 * 
	 * @param dateToParse
	 *            String au format dd/MM/yyyy
	 * @return
	 * @throws ClientException
	 */
	public static Calendar parse(final String dateToParse) throws ClientException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
		Date date;
		try {
			date = sdf.parse(dateToParse);
		} catch (ParseException e) {
			throw new ClientException("Impossible de parser au format dd/MM/yyyy la date " + dateToParse, e);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}

	/**
	 * Convertisseur {@link XMLGregorianCalendar} => {@link Calendar}
	 * 
	 * @param xmlGregorianCalendar
	 * @return
	 */
	public static Calendar xmlGregorianCalendarToCalendar(XMLGregorianCalendar xmlGregorianCalendar) {
		Calendar cal = null;
		if (xmlGregorianCalendar != null) {
			cal = xmlGregorianCalendar.toGregorianCalendar();
		}
		return cal;
	}

	/**
	 * Convertisseur {@link XMLGregorianCalendar} => {@link Date}
	 * 
	 * @param xmlGregorianCalendar
	 * @return
	 */
	public static Date xmlGregorianCalendarToDate(XMLGregorianCalendar xmlGregorianCalendar) {
		Date date = null;
		if (xmlGregorianCalendar != null) {
			date = xmlGregorianCalendar.toGregorianCalendar().getTime();
		}
		return date;
	}

	/**
	 * Convertisseur {@link Date} => {@link XMLGregorianCalendar}
	 * 
	 * @param xmlGregorianCalendar
	 * @return
	 */
	public static XMLGregorianCalendar dateToXmlGregorianCalendar(Date date) {
		XMLGregorianCalendar xmlGregorianCalendar = null;
		if (date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			xmlGregorianCalendar = calendarToXMLGregorianCalendar(cal);
		}
		return xmlGregorianCalendar;
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
	 * si le mois ou la date du mois est plus petit que 10 on ajoute un 0 avant
	 * 
	 * @param partie
	 *            de la date
	 * @return une partie de la date avec 2 caractères
	 */
	public static String adjustDatePart(int part) {
		String strPart = Integer.toString(part);
		if (part < 10) {
			return "0" + strPart;
		}
		return strPart;
	}

	/**
	 * Used to clone an external date
	 * 
	 * @param externalDate
	 *            the external Date
	 * @return a copy of date
	 */
	public static Date copyDate(Date externalDate) {
		return externalDate == null ? null : (Date) externalDate.clone();
	}

	/**
	 * Used to format date with a Frensh LOCALE
	 * 
	 * @param format
	 *            the format
	 * @return a SimpleDateFormat instance
	 */
	public static SimpleDateFormat simpleDateFormat(String format) {
		return new SimpleDateFormat(format, Locale.FRENCH);
	}

	/**
	 * Retourne une liste de date au format XMLGregorianCalendar depuis une liste de date au format calendar
	 * 
	 * @param listDate
	 * @return
	 */
	public static List<XMLGregorianCalendar> listCalendarToGregorianCalendar(List<Calendar> listDate) {
		if (listDate == null) {
			return null;
		}
		List<XMLGregorianCalendar> listRetour = new ArrayList<XMLGregorianCalendar>();
		for (Calendar date : listDate) {
			if (date != null) {
				listRetour.add(calendarToXMLGregorianCalendar(date));
			}
		}

		return listRetour;
	}

	/**
	 * Convertit unedate au format ISO8601 (ex: 2018-06-14T20:41:48Z).
	 * 
	 * @see https://en.wikipedia.org/wiki/ISO_8601
	 */
	public static String formatDatetimeISO8601(Date date) {
		if (date == null) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return format.format(date);
	}

	/**
	 * Convertit une date au format MMMM yyyy.
	 * 
	 * @param date
	 *            Date
	 * @return Date formatée
	 */
	public static String formatMMMMYYYY(final Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", Locale.FRENCH);
		return format.format(date);
	}
}
