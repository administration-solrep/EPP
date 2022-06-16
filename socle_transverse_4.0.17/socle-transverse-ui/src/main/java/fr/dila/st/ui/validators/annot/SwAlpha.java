package fr.dila.st.ui.validators.annot;

import fr.dila.st.ui.validators.SwAlphaValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@SwConstraint(validatedBy = SwAlphaValidator.class)
public @interface SwAlpha {
}
