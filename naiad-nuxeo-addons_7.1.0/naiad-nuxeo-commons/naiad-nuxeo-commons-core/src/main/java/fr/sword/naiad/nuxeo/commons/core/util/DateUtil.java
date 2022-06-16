package fr.sword.naiad.nuxeo.commons.core.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Classe utilitaire de gestion des dates (conversions vers les formats Nuxeo).
 * 
 * @author fmh
 */
public final class DateUtil {
	private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Constructeur privé.
	 */
	private DateUtil() {
		// Classe utilitaire
	}

	/**
	 * Formate une date au format yyyy-MM-dd HH:mm:ss.
	 * 
	 * @param date
	 *            Date à formater.
	 * @return Date formatée.
	 */
	public static String formatNXQLDate(Calendar date) {
		return formatNXQLDate(date.getTime());
	}

	/**
	 * Formate une date au format yyyy-MM-dd HH:mm:ss.
	 * 
	 * @param date
	 *            Date à formater.
	 * @return Date formatée.
	 */
	public static String formatNXQLDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault());
		return sdf.format(date);
	}
}
