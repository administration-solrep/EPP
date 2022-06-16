package fr.dila.st.ui.th.annot;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Repeatable(IHM.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentContainer {
    Fragment[] value() default {};

    String name() default "";
}
