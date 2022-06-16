package fr.dila.st.ui.validators;

import java.lang.annotation.Annotation;

public interface SwValidator<T extends Annotation> {
    void initialize(T constraintAnnotation);

    boolean isValid(String value);
}
