package fr.dila.st.ui.annot;

import fr.dila.st.ui.mapper.MapDoc2BeanDefaultProcess;
import fr.dila.st.ui.mapper.MapDoc2BeanProcess;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface NxProp {
    enum Way {
        BEAN_TO_DOC,
        DOC_TO_BEAN,
        TWO_WAY
    }

    String xpath();

    String docType();

    Class<? extends MapDoc2BeanProcess> process() default MapDoc2BeanDefaultProcess.class;

    Way way() default Way.TWO_WAY;
}
