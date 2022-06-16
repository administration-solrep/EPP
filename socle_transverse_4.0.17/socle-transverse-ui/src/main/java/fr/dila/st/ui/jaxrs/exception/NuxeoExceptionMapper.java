package fr.dila.st.ui.jaxrs.exception;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Exception mapper of {@link NuxeoException}.
 * @author olejacques
 */
@Provider
public class NuxeoExceptionMapper extends AbstractExceptionMapper implements ExceptionMapper<NuxeoException> {
    private static final STLogger LOGGER = STLogFactory.getLog(NuxeoExceptionMapper.class);

    public NuxeoExceptionMapper(@Context HttpServletRequest request) {
        super(request);
    }

    @Override
    public Response toResponse(final NuxeoException exception) {
        logException(exception, LOGGER);
        int status = exception.getStatusCode();
        return buildResponse(exception, status, null);
    }
}
