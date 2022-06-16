package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwLength;

public class SwLengthValidator implements SwValidator<SwLength> {
    private int min;
    private int max;

    public SwLengthValidator() {
        super();
    }

    @Override
    public void initialize(SwLength constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
        validateParameters();
    }

    @Override
    public boolean isValid(String value) {
        if (value == null) {
            return true;
        }
        int length = value.length();
        return length >= min && length <= max;
    }

    private void validateParameters() {
        if (min < 0) {
            throw new IllegalArgumentException("The min parameter cannot be negative.");
        }
        if (max < 0) {
            throw new IllegalArgumentException("The max parameter cannot be negative.");
        }
        if (max < min) {
            throw new IllegalArgumentException("The length cannot be negative.");
        }
    }
}
