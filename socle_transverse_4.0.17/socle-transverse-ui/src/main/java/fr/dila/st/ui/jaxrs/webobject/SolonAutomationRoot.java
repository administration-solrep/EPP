package fr.dila.st.ui.jaxrs.webobject;

import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;
import static org.nuxeo.ecm.webengine.WebEngine.getActiveContext;

import java.io.IOException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

@Path("/execoperation")
@WebObject(type = "SolonAutomationRoot")
@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
public class SolonAutomationRoot extends ModuleRoot {

    /**
     * Execute an operation without parameters
     * <p>
     * For Administrator only
     *
     * @return {@link Response}
     * @throws OperationException
     * @throws IOException
     */
    @POST
    @Path("{id}")
    public Response execOperation(@PathParam("id") String id) throws OperationException {
        CoreSession session = getActiveContext().getCoreSession();
        if (!session.getPrincipal().isAdministrator()) {
            return Response.status(Status.FORBIDDEN).entity("Seul Administrator peut exécuter une opération").build();
        }

        OperationContext opCtx = new OperationContext(session);
        getRequiredService(AutomationService.class).run(opCtx, id);

        return Response.ok().build();
    }
}
