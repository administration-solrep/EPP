package fr.dila.st.ui.validators.annot;

import fr.dila.st.ui.validators.SwParlementIdValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@SwConstraint(validatedBy = SwParlementIdValidator.class)
public @interface SwParlementId {
    String value() default "^([A-Za-z0-9\\-_])+$";
}
