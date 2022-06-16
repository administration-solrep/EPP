package fr.dila.st.ui.th.annot;

import fr.dila.st.ui.services.FragmentService;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Repeatable(FragmentContainer.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Fragment {
    Class<? extends FragmentService> service();

    String templateFile();

    String template();

    int order() default 0;
}
