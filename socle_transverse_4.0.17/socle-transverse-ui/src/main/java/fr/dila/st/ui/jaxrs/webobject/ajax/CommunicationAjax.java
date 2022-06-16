package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "CommunicationAjax")
public class CommunicationAjax extends SolonWebObject {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("enCoursDeTraitement")
    public Response doEnCoursDeTraitement() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("traiter")
    public Response doTraiter() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("transmettre")
    public Response doTransmettre() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("alerter")
    public Response doAlerter() {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("supprimer")
    public Response doSupprimer(@FormParam("dossierId") String dossierId) {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("annuler")
    public Response doAnnulerCommunication(@FormParam("dossierId") String dossierId) {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("rectifier")
    public Response doRectifier(@FormParam("dossierId") String dossierId) {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("completer")
    public Response doCompleter(@FormParam("dossierId") String dossierId) {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("accepter")
    public Response doAccepter(@FormParam("dossierId") String dossierId) {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("rejeter")
    public Response doRejeter(@FormParam("dossierId") String dossierId) {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("abandonner")
    public Response doAbandonner(@FormParam("dossierId") String dossierId) {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("publier")
    public Response doPublier(@FormParam("dossierId") String dossierId) {
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
