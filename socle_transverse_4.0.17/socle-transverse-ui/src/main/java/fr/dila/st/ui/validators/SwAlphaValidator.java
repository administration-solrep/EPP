package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwRegex;
import org.apache.commons.lang3.StringUtils;

public class SwAlphaValidator implements SwValidator<SwRegex> {

    public SwAlphaValidator() {
        super();
    }

    @Override
    public void initialize(SwRegex constraintAnnotation) {
        // Nothing to do
    }

    @Override
    public boolean isValid(String value) {
        return StringUtils.isEmpty(value) || StringUtils.isAlpha(value);
    }
}
