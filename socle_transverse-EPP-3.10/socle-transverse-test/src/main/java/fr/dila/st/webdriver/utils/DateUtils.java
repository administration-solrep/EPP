package fr.dila.st.webdriver.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Des méthodes utilitaires pour le formatage des dates
 * 
 * @author jgomez
 * 
 */
public class DateUtils {

	private static final String	DATE_FORMAT		= "dd/MM/yyyy";

	private static final String	DATETIME_FORMAT	= "dd/MM/yyyy HH:mm";

	// Private constructeur
	protected DateUtils() {
	}

	public static String formatDate(Date date) {
		String formattedDate = new SimpleDateFormat(DATE_FORMAT).format(date);
		return formattedDate;
	}

	public static String formatDatetime(Date date) {
		String formattedDate = new SimpleDateFormat(DATETIME_FORMAT).format(date);
		return formattedDate;
	}
}
