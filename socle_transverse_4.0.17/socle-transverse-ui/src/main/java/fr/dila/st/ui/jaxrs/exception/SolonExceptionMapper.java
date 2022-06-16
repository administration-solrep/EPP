package fr.dila.st.ui.jaxrs.exception;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.ui.th.ThEngineService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class SolonExceptionMapper extends AbstractExceptionMapper implements ExceptionMapper<Exception> {
    private static final STLogger LOGGER = STLogFactory.getLog(SolonExceptionMapper.class);

    public SolonExceptionMapper(@Context HttpServletRequest request) {
        super(request);
    }

    public SolonExceptionMapper(@Context HttpServletRequest request, ThEngineService engineService) {
        super(request, engineService);
    }

    @Override
    public Response toResponse(final Exception exception) {
        logException(exception, LOGGER);
        return buildResponse(exception, DEFAULT_STATUS.getStatusCode(), null);
    }
}
