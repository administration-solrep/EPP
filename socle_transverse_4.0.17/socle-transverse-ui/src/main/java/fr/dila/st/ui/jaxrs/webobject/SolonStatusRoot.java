package fr.dila.st.ui.jaxrs.webobject;

import static fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum.KO;
import static fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum.OK;
import static fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil.getRequiredService;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.services.StatusService;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;

@Path("/statut")
@WebObject(type = "SolonStatusRoot")
@Produces({ MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON })
public class SolonStatusRoot extends ModuleRoot {

    @GET
    public String getGeneralStatus() {
        final Map<String, Object> status = getRequiredService(StatusService.class).getStatus();
        if (
            status
                .values()
                .stream()
                .filter(ResultInfo.class::isInstance)
                .map(ResultInfo.class::cast)
                .anyMatch(v -> KO.equals(v.getStatut()))
        ) {
            return KO.toString();
        }

        return OK.toString();
    }

    @GET
    @Path("full")
    public Object getStatus() {
        return redirect(WebEngine.getActiveContext().getBasePath() + "/status");
    }
}
