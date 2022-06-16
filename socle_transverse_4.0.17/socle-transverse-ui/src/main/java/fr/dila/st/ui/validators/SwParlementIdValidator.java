package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwParlementId;

public class SwParlementIdValidator extends AbstractRegexValidator<SwParlementId> {

    public SwParlementIdValidator() {
        super();
    }

    @Override
    public void initialize(SwParlementId constraintAnnotation) {
        regex = constraintAnnotation.value();
    }
}
