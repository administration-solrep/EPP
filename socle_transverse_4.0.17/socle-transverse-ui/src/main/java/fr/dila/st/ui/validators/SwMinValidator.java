package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwMin;

public class SwMinValidator implements SwValidator<SwMin> {
    private long minValue;

    public SwMinValidator() {
        super();
    }

    @Override
    public void initialize(SwMin constraintAnnotation) {
        minValue = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(String value) {
        return value == null || Long.parseLong(value) >= minValue;
    }
}
