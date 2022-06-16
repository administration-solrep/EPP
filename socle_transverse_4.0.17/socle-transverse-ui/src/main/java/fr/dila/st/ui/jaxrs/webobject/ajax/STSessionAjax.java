package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "SessionAjax")
public class STSessionAjax extends SolonWebObject {

    public STSessionAjax() {}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("update")
    public Response updateSession() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
