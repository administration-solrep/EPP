package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwRegex;

public class SwRegexValidator extends AbstractRegexValidator<SwRegex> {

    @Override
    public void initialize(SwRegex constraintAnnotation) {
        regex = constraintAnnotation.value();
    }
}
