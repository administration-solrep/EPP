package fr.dila.st.ui.jaxrs.filters;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.jersey.spi.container.ResourceFilter;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.jaxrs.exception.NuxeoExceptionMapper;
import fr.dila.st.ui.utils.HttpRequestUtils;
import fr.dila.st.ui.utils.ValidatorUtils;
import fr.dila.st.ui.validators.annot.SwRequired;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ext.Provider;
import org.apache.commons.collections4.CollectionUtils;

/**
 * Read and validate data
 * @author SLE
 */
@Provider
public class ValidationFilter implements ResourceFilter, ContainerRequestFilter, ContainerResponseFilter {
    private static final STLogger LOGGER = STLogFactory.getLog(NuxeoExceptionMapper.class);
    private static final String REQUEST_BAD_PARAM_ERROR_MESSAGE = "request.bad.param";

    private Method method;

    public ValidationFilter() {}

    public ValidationFilter(Method method) {
        this.method = method;
    }

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        for (Annotation[] annots : method.getParameterAnnotations()) {
            Annotation jaxrsAnnot = HttpRequestUtils.getJaxRSAnnotation(annots);
            if (ValidatorUtils.isParameterWithConstraint(annots) && jaxrsAnnot != null) {
                List<String> values = new ArrayList<>();
                String paramName = "";
                if (jaxrsAnnot.annotationType().equals(FormParam.class)) {
                    FormParam annot = (FormParam) jaxrsAnnot;
                    paramName = annot.value();
                    values = HttpRequestUtils.getValuesFromForm(containerRequest, annot);
                } else if (jaxrsAnnot.annotationType().equals(QueryParam.class)) {
                    QueryParam annot = (QueryParam) jaxrsAnnot;
                    paramName = annot.value();
                    values = HttpRequestUtils.getValuesFromQuery(containerRequest, annot);
                }

                if (CollectionUtils.isEmpty(values)) {
                    for (Annotation myannot : annots) {
                        if (myannot.annotationType().equals(SwRequired.class)) {
                            LOGGER.error(
                                STLogEnumImpl.LOG_EXCEPTION_TEC,
                                ResourceHelper.getString(
                                    "back.validation.filter.error.required.parameter",
                                    paramName,
                                    method.getName()
                                )
                            );
                            throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE);
                        }
                    }
                } else {
                    if (!areValidValues(values, annots)) {
                        LOGGER.error(
                            STLogEnumImpl.LOG_EXCEPTION_TEC,
                            ResourceHelper.getString(
                                "back.validation.filter.error.invalid.parameter",
                                paramName,
                                method.getName()
                            )
                        );
                        throw new STValidationException(REQUEST_BAD_PARAM_ERROR_MESSAGE);
                    }
                }
            }
        }
        return containerRequest;
    }

    private static boolean areValidValues(List<String> values, Annotation[] annots) {
        return values.stream().allMatch(value -> ValidatorUtils.isValid(annots, value));
    }

    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
        // Do something with the outgoing response here
        return containerResponse;
    }

    @Override
    public ContainerRequestFilter getRequestFilter() {
        return this;
    }

    @Override
    public ContainerResponseFilter getResponseFilter() {
        return this;
    }
}
