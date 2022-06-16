package fr.dila.st.ui.validators.annot;

import fr.dila.st.ui.validators.SwIdValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@SwConstraint(validatedBy = SwIdValidator.class)
public @interface SwId {
    String value() default "[a-zA-Z0-9.\\-]+";
}
