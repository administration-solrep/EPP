package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwMonthFormat;

public class SwMonthFormatValidator implements SwValidator<SwMonthFormat> {
    private static final Integer MONTH_COUNT = 12;

    @Override
    public void initialize(SwMonthFormat constraintAnnotation) {}

    @Override
    public boolean isValid(String value) {
        if (value == null) {
            return true;
        }
        try {
            int val = Integer.parseInt(value);
            return val >= 1 && val <= MONTH_COUNT;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
