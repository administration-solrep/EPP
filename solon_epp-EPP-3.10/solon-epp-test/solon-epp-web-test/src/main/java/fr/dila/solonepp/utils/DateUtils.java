package fr.dila.solonepp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Classe utilitaire sur les dates.
 * 
 * @author asatre
 */
public class DateUtils {

    private static final SimpleDateFormat FORMAT_DDMMYYYY_HHMM = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final SimpleDateFormat FORMAT_DDMMYYYY = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Convertit une date au format dd/MM/yyyy HH:mm.
     * 
     * @param calendar Calendar
     * @return Date formatée
     */
    public static String formatWithHour(final Calendar calendar) {
        if (calendar != null) {
            return FORMAT_DDMMYYYY_HHMM.format(calendar.getTime());
        }
        return null;
    }

    /**
     * Convertit une date au format dd/MM/yyyy
     * 
     * @param calendar Calendar
     * @return Date formatée
     */
    public static String format(final Calendar calendar) {
        if (calendar != null) {
            return FORMAT_DDMMYYYY.format(calendar.getTime());
        }
        return null;
    }

}
