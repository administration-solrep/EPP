package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwId;

public class SwIdValidator extends AbstractRegexValidator<SwId> {

    public SwIdValidator() {
        super();
    }

    @Override
    public void initialize(SwId constraintAnnotation) {
        regex = constraintAnnotation.value();
    }
}
