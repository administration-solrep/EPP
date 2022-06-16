package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwYearFormat;
import java.time.Year;

public class SwYearFormatValidator implements SwValidator<SwYearFormat> {
    private int min;
    private int max;

    @Override
    public void initialize(SwYearFormat constraintAnnotation) {
        boolean maxCurrentYear = constraintAnnotation.maxCurrentYear();
        min = constraintAnnotation.minYear();
        max = maxCurrentYear ? Year.now().getValue() : constraintAnnotation.maxYear();
    }

    @Override
    public boolean isValid(String value) {
        if (value == null) {
            return true;
        }
        try {
            int val = Integer.parseInt(value);

            return min <= val && val <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
