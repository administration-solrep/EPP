package fr.dila.ss.core.util;

import fr.dila.st.core.util.DateUtil;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.Calendar;
import org.apache.commons.lang3.StringUtils;

public class NXQLUtils {

    /**
     * Convertit une date au format dd/MM/yyyy en Timestamp pour NXQL
     *
     * @param value
     * @param endOfDay
     *            true si le timestamp doit correspondre à la fin de la journée
     * @return
     */
    public static String convertToTimestamp(String value, boolean endOfDay) {
        Calendar cal = SolonDateConverter.DATE_SLASH.parseToCalendar(value);
        if (endOfDay) {
            DateUtil.setDateToEndOfDay(cal);
        }
        return String.format(
            "TIMESTAMP '%s'",
            SolonDateConverter.DATETIME_DASH_REVERSE_T_SECOND_COLON_Z.format(cal, true)
        );
    }

    /**
     * Convertit un timestamp NXQL au format dd/MM/yyyy
     *
     * @param value
     * @return
     */
    public static String parseTimeStamp(String value) {
        return SolonDateConverter.DATE_SLASH.format(
            SolonDateConverter.DATETIME_DASH_REVERSE_T_MILLI_Z.parseToCalendar(
                StringUtils.strip(value.replace("TIMESTAMP ", StringUtils.EMPTY), "'")
            )
        );
    }
}
