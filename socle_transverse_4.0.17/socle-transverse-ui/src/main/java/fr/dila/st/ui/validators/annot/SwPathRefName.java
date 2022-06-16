package fr.dila.st.ui.validators.annot;

import fr.dila.st.ui.validators.SwPathRefNameValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validateur qui vérifie que le texte ne contient pas le caractère /
 */
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@SwConstraint(validatedBy = SwPathRefNameValidator.class)
public @interface SwPathRefName {
    String value() default "^[^\\/]+$";
}
