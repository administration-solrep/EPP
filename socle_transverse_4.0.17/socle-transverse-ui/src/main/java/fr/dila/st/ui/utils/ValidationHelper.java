package fr.dila.st.ui.utils;

import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

public class ValidationHelper {
    private static final Pattern EMAIL_VALIDATOR = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,7}$"
    );

    private ValidationHelper() {
        // Utility class
    }

    private static final String KEY_REQUIRED = "validation.field.required";

    public static void matchRegex(String fieldName, String field, String regexPattern) {
        if (!Pattern.compile(regexPattern).matcher(field).matches()) {
            throw new STValidationException("validation.field.regex", fieldName, regexPattern);
        }
    }

    public static void minSize(String fieldName, String field, int minSize) {
        if (StringUtils.isNotBlank(field) && field.trim().length() < minSize) {
            throw new STValidationException("validation.field.size.min", fieldName, minSize);
        }
    }

    public static void notNull(String fieldName, Object field) {
        if (field == null) {
            throw new STValidationException(KEY_REQUIRED, fieldName);
        }
    }

    public static void notEmpty(String fieldName, String field) {
        if (StringUtils.isEmpty(field)) {
            throw new STValidationException(KEY_REQUIRED, fieldName);
        }
    }

    public static void notEmpty(String fieldName, Collection<?> field) {
        if (CollectionUtils.isEmpty(field)) {
            throw new STValidationException(KEY_REQUIRED, fieldName);
        }
    }

    public static void notBlank(String fieldName, String field) {
        if (StringUtils.isBlank(field)) {
            throw new STValidationException(KEY_REQUIRED, fieldName);
        }
    }

    public static void date(String fieldName, String field) {
        if (StringUtils.isNotEmpty(field)) {
            try {
                SolonDateConverter.DATE_SLASH.parseToDate(field);
            } catch (NuxeoException e) {
                throw new STValidationException("validation.field.valid.date", fieldName);
            }
        }
    }

    public static void email(String fieldName, String field) {
        if (StringUtils.isNotEmpty(field) && EMAIL_VALIDATOR.matcher(field).matches()) {
            throw new STValidationException("validation.field.valid.email", fieldName);
        }
    }

    public static void future(String fieldName, String field, String referenceName, String reference) {
        if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(reference)) {
            try {
                Date fieldDate = SolonDateConverter.DATE_SLASH.parseToDate(field);
                Date referenceDate = SolonDateConverter.DATE_SLASH.parseToDate(reference);
                if (fieldDate.before(referenceDate)) {
                    throw new STValidationException("validation.field.future.date", fieldName, referenceName);
                }
            } catch (NuxeoException e) {
                // We test if the date is in the future, not valid
            }
        }
    }

    public static void past(String fieldName, String field, String referenceName, String reference) {
        if (StringUtils.isNotEmpty(field) && StringUtils.isNotEmpty(reference)) {
            try {
                Date fieldDate = SolonDateConverter.DATE_SLASH.parseToDate(field);
                Date referenceDate = SolonDateConverter.DATE_SLASH.parseToDate(reference);
                if (fieldDate.after(referenceDate)) {
                    throw new STValidationException("validation.field.past.date", fieldName, referenceName);
                }
            } catch (NuxeoException e) {
                // We test if the date is in the future, not valid
            }
        }
    }
}
