package fr.dila.st.ui.validators.annot;

import fr.dila.st.ui.validators.SwLengthValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@SwConstraint(validatedBy = SwLengthValidator.class)
public @interface SwLength {
    int min() default 0;

    int max() default Integer.MAX_VALUE;
}
