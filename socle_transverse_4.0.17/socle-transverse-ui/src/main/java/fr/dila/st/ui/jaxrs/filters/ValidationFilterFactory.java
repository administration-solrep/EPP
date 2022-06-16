package fr.dila.st.ui.jaxrs.filters;

import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import fr.dila.st.ui.utils.ValidatorUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationFilterFactory implements ResourceFilterFactory {
    @Context
    private SecurityContext sc;

    @Override
    public List<ResourceFilter> create(AbstractMethod am) {
        List<ResourceFilter> validationFilters = null;
        if (
            Arrays
                .stream(am.getMethod().getParameters())
                .anyMatch(p -> ValidatorUtils.isParameterWithConstraint(p.getAnnotations()))
        ) {
            validationFilters = Collections.singletonList(new ValidationFilter(am.getMethod()));
        }
        return validationFilters;
    }
}
