package fr.sword.naiad.nuxeo.commons.core.util;

import java.util.Calendar;

/**
 * Classe utilitaire manipulant le type Calendar.
 *
 * @author fmh
 */
public final class CalendarUtil {
	/**
	 * Constructeur privé.
	 */
	private CalendarUtil() {
		// Classe utilitaire
	}

	/**
	 * Récupère la date du jour à minuit (date sans les heures, minutes, secondes, millisecondes).
	 * 
	 * @return Date du jour à minuit.
	 */
	public static Calendar getTodayDate() {
		Calendar today = Calendar.getInstance();
		clearTime(today);
		return today;
	}

	/**
	 * Réinitialise les champs heures, minutes, secondes et millisecondes d'une date.
	 * 
	 * @param date
	 *            Date à modifier.
	 */
	public static void clearTime(Calendar date) {
		date.set(Calendar.HOUR_OF_DAY, date.getActualMinimum(Calendar.HOUR_OF_DAY));
		date.set(Calendar.MINUTE, date.getActualMinimum(Calendar.MINUTE));
		date.set(Calendar.SECOND, date.getActualMinimum(Calendar.SECOND));
		date.set(Calendar.MILLISECOND, date.getActualMinimum(Calendar.MILLISECOND));
	}
}
