package fr.dila.st.ui.jaxrs.exception;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import com.sun.jersey.api.container.ContainerException;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.ThEngineService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.nuxeo.ecm.core.api.NuxeoException;

@Provider
public class ContainerExceptionMapper extends AbstractExceptionMapper implements ExceptionMapper<ContainerException> {
    private static final STLogger LOGGER = STLogFactory.getLog(ContainerExceptionMapper.class);

    public ContainerExceptionMapper(@Context HttpServletRequest request) {
        super(request);
    }

    public ContainerExceptionMapper(@Context HttpServletRequest request, ThEngineService engineService) {
        super(request, engineService);
    }

    @Override
    public Response toResponse(ContainerException exception) {
        logException(exception, LOGGER);

        Response response;

        if (exception.getCause() instanceof NuxeoException) {
            NuxeoException cause = ((NuxeoException) exception.getCause());
            response = buildResponse(cause.getMessage(), cause.getStatusCode(), null);
        } else {
            response = buildResponse(ResourceHelper.getString("request.bad.param"), BAD_REQUEST.getStatusCode(), null);
        }

        return response;
    }
}
