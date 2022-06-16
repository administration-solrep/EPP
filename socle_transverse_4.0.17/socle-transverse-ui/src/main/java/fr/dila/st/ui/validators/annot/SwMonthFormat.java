package fr.dila.st.ui.validators.annot;

import fr.dila.st.ui.validators.SwMonthFormatValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@SwConstraint(validatedBy = SwMonthFormatValidator.class)
public @interface SwMonthFormat {
}
