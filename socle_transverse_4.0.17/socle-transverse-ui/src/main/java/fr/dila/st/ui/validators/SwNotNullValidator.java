package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwRequired;

public class SwNotNullValidator implements SwValidator<SwRequired> {

    @Override
    public void initialize(SwRequired constraintAnnotation) {}

    @Override
    public boolean isValid(String value) {
        return value != null;
    }
}
