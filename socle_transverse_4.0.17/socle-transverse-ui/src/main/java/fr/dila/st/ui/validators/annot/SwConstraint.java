package fr.dila.st.ui.validators.annot;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import fr.dila.st.ui.validators.SwValidator;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface SwConstraint {
    Class<? extends SwValidator<?>> validatedBy();
}
