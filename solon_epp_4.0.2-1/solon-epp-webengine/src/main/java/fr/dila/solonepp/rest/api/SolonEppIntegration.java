package fr.dila.solonepp.rest.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebResourceNotFoundException;
import org.nuxeo.ecm.webengine.model.exceptions.WebSecurityException;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

/**
 * Racine du module des Web Services pour SOLON EPP.
 *
 * @author jtremeaux
 */
@Path("solonepp")
@WebObject(type = "SolonEppIntegration")
public class SolonEppIntegration extends ModuleRoot {
    protected NuxeoPrincipalImpl principal;

    @GET
    @Produces("text/html;charset=UTF-8")
    public Object doGet() {
        return getView("index");
    }

    @Path(WSEpp.SERVICE_NAME)
    public WSEppImpl getWSEpp() {
        return (WSEppImpl) newObject(WSEpp.SERVICE_NAME);
    }

    @Path(WSEvenement.SERVICE_NAME)
    public WSEvenementImpl getWSEvenement() {
        return (WSEvenementImpl) newObject(WSEvenement.SERVICE_NAME);
    }

    /**
     * Handle Errors
     *
     * @see org.nuxeo.ecm.webengine.model.impl.ModuleRoot#handleError(javax.ws.rs.WebApplicationException)
     */
    @Override
    public Object handleError(final Throwable t) {
        if (t instanceof WebSecurityException) {
            final String message = "Vous n'avez pas les droits nécessaire pour accéder à cette resource";
            return Response.status(401).entity(message).type("text/xml").build();
        } else if (t instanceof WebResourceNotFoundException) {
            final String message = "Resource inexistante";
            return Response.status(404).entity(message).type("text/xml").build();
        } else {
            return Response.status(404).entity(t.getMessage()).type("text/xml").build();
        }
    }
}
