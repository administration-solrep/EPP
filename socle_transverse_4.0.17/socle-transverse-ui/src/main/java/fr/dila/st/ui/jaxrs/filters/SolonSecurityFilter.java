package fr.dila.st.ui.jaxrs.filters;

import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.utils.XssSanitizerUtils;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.owasp.esapi.errors.IntrusionException;

@Provider
public class SolonSecurityFilter implements ContainerRequestFilter {
    private static final STLogger LOGGER = STLogFactory.getLog(SolonSecurityFilter.class);

    private static final List<String> EXCLUDE_KEY = Arrays.asList("referer");

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        // Clean the query strings
        cleanParams(request.getQueryParameters());

        // Clean the headers
        cleanParams(request.getRequestHeaders());

        // Clean the cookies
        cleanParams(request.getCookieNameValueMap());

        // Clean the Form param
        cleanParams(request.getFormParameters());

        if (
            request.getProperties() != null &&
            request.getProperties().get("com.sun.jersey.api.representation.form") != null
        ) {
            // Clean the Form param
            cleanParams((Form) request.getProperties().get("com.sun.jersey.api.representation.form"));
        }

        // Return the cleansed request
        return request;
    }

    /**
     * Apply the XSS filter to the parameters
     * @param parameters
     */
    private static void cleanParams(MultivaluedMap<String, String> parameters) {
        parameters
            .entrySet()
            .stream()
            .filter(entry -> !EXCLUDE_KEY.contains(entry.getKey()))
            .forEach(
                entry ->
                    parameters.put(
                        entry.getKey(),
                        entry
                            .getValue()
                            .stream()
                            .map(value -> cleanValue(entry.getKey(), value))
                            .collect(Collectors.toList())
                    )
            );
    }

    private static String cleanValue(String key, String value) {
        String cleanValue = XssSanitizerUtils.stripXSS(value);
        try {
            if (!cleanValue.equals(XssSanitizerUtils.ESAPI_ENCODER.canonicalize(value))) {
                // Valeur a risque on log et renvoie une exception
                LOGGER.warn(STLogEnumImpl.LOG_EXCEPTION_TEC, String.format("Valeur a risque %s : %s", key, value));
                throw new STValidationException("request.security.error");
            }
        } catch (IntrusionException exception) {
            throw new STValidationException("request.security.error", exception);
        }
        return value;
    }
}
