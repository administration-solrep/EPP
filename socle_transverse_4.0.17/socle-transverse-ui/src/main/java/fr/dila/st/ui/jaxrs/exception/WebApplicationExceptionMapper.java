package fr.dila.st.ui.jaxrs.exception;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.th.ThEngineService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper
    extends AbstractExceptionMapper
    implements ExceptionMapper<WebApplicationException> {
    private static final STLogger LOGGER = STLogFactory.getLog(WebApplicationExceptionMapper.class);

    public WebApplicationExceptionMapper(@Context HttpServletRequest request) {
        super(request);
    }

    public WebApplicationExceptionMapper(@Context HttpServletRequest request, ThEngineService engineService) {
        super(request, engineService);
    }

    @Override
    public Response toResponse(final WebApplicationException exception) {
        logException(exception, LOGGER);
        Response response = exception.getResponse();
        return buildResponse(exception, response.getStatus(), response);
    }
}
