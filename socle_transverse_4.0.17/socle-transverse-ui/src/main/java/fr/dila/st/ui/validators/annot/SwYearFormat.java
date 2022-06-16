package fr.dila.st.ui.validators.annot;

import fr.dila.st.ui.validators.SwYearFormatValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@SwConstraint(validatedBy = SwYearFormatValidator.class)
public @interface SwYearFormat {
    int minYear() default 1500;

    int maxYear() default 2100;

    boolean maxCurrentYear() default false;
}
