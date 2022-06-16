package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxAlertes")
public class SSSuiviAlerteAjax extends SolonWebObject {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("suspendre")
    public Response suspendAlerte(@FormParam("id") String id) {
        context.setCurrentDocument(id);

        String alertId = SSActionsServiceLocator.getAlertActionService().suspend(context);

        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), alertId).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("activer")
    public Response activateAlerte(@FormParam("id") String id) {
        context.setCurrentDocument(id);

        String alertId = SSActionsServiceLocator.getAlertActionService().activate(context);

        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), alertId).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("supprimer")
    public Response deleteAlerte(@FormParam("id") String id) {
        context.setCurrentDocument(id);

        SSActionsServiceLocator.getAlertActionService().delete(context);

        // Comme je recharge la page si pas d'erreur je mets en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
