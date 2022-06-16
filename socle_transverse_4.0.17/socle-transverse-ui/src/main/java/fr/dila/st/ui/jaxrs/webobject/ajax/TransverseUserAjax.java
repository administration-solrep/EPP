package fr.dila.st.ui.jaxrs.webobject.ajax;

import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static fr.dila.st.ui.services.STUIServiceLocator.getSTUserManagerUIService;

import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "TransverseUserAjax")
public class TransverseUserAjax extends SolonWebObject {
    private static final String USER_PARAM = "id";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/delete")
    public Response deleteUser(@PathParam(USER_PARAM) String userId) {
        context.setCurrentDocument(getUserManager().getUserModel(userId));
        if (!getSTUserManagerUIService().getAllowDeleteUser(context)) {
            throw new STAuthorizationException("action suppression utilisateur " + userId);
        }

        getSTUserManagerUIService().deleteUser(context);
        return getJsonResponseWithMessagesInSessionIfSuccess();
    }
}
