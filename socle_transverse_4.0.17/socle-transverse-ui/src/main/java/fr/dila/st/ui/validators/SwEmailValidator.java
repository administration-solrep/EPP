package fr.dila.st.ui.validators;

import fr.dila.st.core.util.StringHelper;
import fr.dila.st.ui.validators.annot.SwEmail;
import org.apache.commons.lang3.StringUtils;

public class SwEmailValidator implements SwValidator<SwEmail> {

    public SwEmailValidator() {
        super();
    }

    @Override
    public void initialize(SwEmail constraintAnnotation) {
        // Nothing to do
    }

    @Override
    public boolean isValid(String value) {
        return StringUtils.isBlank(value) || StringHelper.isEmail(value);
    }
}
