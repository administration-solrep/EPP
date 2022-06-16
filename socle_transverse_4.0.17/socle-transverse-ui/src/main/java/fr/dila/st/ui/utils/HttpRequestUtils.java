package fr.dila.st.ui.utils;

import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.ContainerRequest;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class HttpRequestUtils {

    /**
     * utility class
     */
    private HttpRequestUtils() {
        // do nothing
    }

    public static Annotation getJaxRSAnnotation(Annotation[] annots) {
        List<Class<? extends Annotation>> acceptedAnnotation = Arrays.asList(
            QueryParam.class,
            FormParam.class,
            PathParam.class
        );

        return Arrays
            .stream(annots)
            .filter(a -> acceptedAnnotation.contains(a.annotationType()))
            .findFirst()
            .orElse(null);
    }

    public static List<String> getValuesFromForm(ContainerRequest container, FormParam myAnnot) {
        Form form = (Form) container.getProperties().get("com.sun.jersey.api.representation.form");
        return form.get(myAnnot.value());
    }

    public static List<String> getValuesFromQuery(ContainerRequest container, QueryParam myAnnot) {
        return Optional
            .ofNullable(container.getQueryParameters())
            .map(parameter -> parameter.get(myAnnot.value()))
            .orElse(null);
    }
}
