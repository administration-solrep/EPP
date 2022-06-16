package fr.dila.st.ui.validators;

import java.lang.annotation.Annotation;
import org.apache.commons.lang3.StringUtils;

public abstract class AbstractRegexValidator<T extends Annotation> implements SwValidator<T> {
    protected String regex;

    @Override
    public boolean isValid(String value) {
        return StringUtils.isEmpty(value) || value.matches(regex);
    }
}
