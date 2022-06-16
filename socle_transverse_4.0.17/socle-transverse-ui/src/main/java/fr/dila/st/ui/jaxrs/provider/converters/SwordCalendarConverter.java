package fr.dila.st.ui.jaxrs.provider.converters;

import fr.dila.st.core.util.SolonDateConverter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.apache.commons.beanutils.converters.DateTimeConverter;

public class SwordCalendarConverter extends DateTimeConverter {

    @Override
    protected Class<?> getDefaultType() {
        return Calendar.class;
    }

    @Override
    protected String convertToString(Object value) throws Throwable {
        if (value instanceof Calendar) {
            return SolonDateConverter.DATE_SLASH.format((Calendar) value);
        } else if (value instanceof Date) {
            return SolonDateConverter.DATE_SLASH.format((Date) value);
        } else {
            return super.convertToString(value);
        }
    }

    @Override
    protected <T> T convertToType(Class<T> targetType, Object value) throws Exception {
        if (value instanceof String) {
            if (Calendar.class.equals(targetType) || GregorianCalendar.class.equals(targetType)) {
                return targetType.cast(SolonDateConverter.DATE_SLASH.parseToCalendarOrNull((String) value));
            } else if (Date.class.equals(targetType)) {
                return targetType.cast(SolonDateConverter.DATE_SLASH.parseToDateOrNull((String) value));
            }
        }

        T converted = super.convertToType(targetType, value);
        if (converted instanceof Calendar) {
            //On reset lenient pour garantir l'égalité en ignorant sa valeur
            ((Calendar) converted).setLenient(true);
        }
        return converted;
    }

    @Override
    protected <T> T handleMissing(Class<T> type) {
        return null;
    }
}
