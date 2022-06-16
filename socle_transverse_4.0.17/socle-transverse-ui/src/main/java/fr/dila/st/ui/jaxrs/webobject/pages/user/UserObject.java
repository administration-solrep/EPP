package fr.dila.st.ui.jaxrs.webobject.pages.user;

import static fr.dila.st.ui.services.STUIServiceLocator.getSTUserManagerUIService;

import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliUser")
public class UserObject extends AbstractUserObject {

    @POST
    @Path("password/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePassword(@FormParam("password") String newPassword) {
        String userId = context.getSession().getPrincipal().getName();

        return doUpdatePassword(newPassword, userId);
    }

    @POST
    @Path("password/updateForUser")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePasswordForUser(
        @FormParam("password") String newPassword,
        @FormParam("userId") String userId
    ) {
        if (StringUtils.isBlank(userId)) {
            throw new STValidationException("form.validation.username.notblank");
        }

        return doUpdatePassword(newPassword, userId);
    }

    private Response doUpdatePassword(String newPassword, String userId) {
        init(userId);

        context.putInContextData(STContextDataKey.NEW_USER_PASSWORD, newPassword);
        getSTUserManagerUIService().updatePassword(context);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("password/reset")
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(@FormParam("userId") String userId, @FormParam("email") String email) {
        context.putInContextData(STContextDataKey.USER_ID, userId);
        context.putInContextData(STContextDataKey.USER_EMAIL, email);
        getSTUserManagerUIService().askResetPassword(context);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
