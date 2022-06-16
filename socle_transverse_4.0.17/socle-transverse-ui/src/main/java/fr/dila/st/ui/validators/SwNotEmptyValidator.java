package fr.dila.st.ui.validators;

import fr.dila.st.ui.validators.annot.SwNotEmpty;
import org.apache.commons.lang3.StringUtils;

public class SwNotEmptyValidator implements SwValidator<SwNotEmpty> {

    @Override
    public void initialize(SwNotEmpty constraintAnnotation) {}

    @Override
    public boolean isValid(String value) {
        return StringUtils.isNotBlank(value);
    }
}
