package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwPathRefName;

public class SwPathRefNameValidator extends AbstractRegexValidator<SwPathRefName> {

    public SwPathRefNameValidator() {
        super();
    }

    @Override
    public void initialize(SwPathRefName constraintAnnotation) {
        regex = constraintAnnotation.value();
    }
}
