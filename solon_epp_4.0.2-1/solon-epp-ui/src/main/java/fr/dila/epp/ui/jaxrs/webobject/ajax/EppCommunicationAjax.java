package fr.dila.epp.ui.jaxrs.webobject.ajax;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.ACCEPTER;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_MESSAGE;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.DESTINATAIRE;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.SKIP_LOCK;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.EMETTEUR;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.TYPE_EVENEMENT;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static fr.dila.st.ui.enums.STContextDataKey.TRANSMETTRE_PAR_MEL_FORM;

import com.google.gson.Gson;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.solonepp.api.domain.message.Message;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.bean.TransmettreParMelForm;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "CommunicationAjax")
public class EppCommunicationAjax extends SolonWebObject {
    private static final String ID_MESSAGE = "idMessage";
    private static final String PARAM_SKIP_LOCK = "skipLock";

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("publier")
    public Response doPublier(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);

        SolonEppUIServiceLocator.getEvenementUIService().publierEvenement(context);

        putQueueInUserSession();
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("supprimer")
    public Response doSupprimer(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);

        SolonEppUIServiceLocator.getEvenementActionsUIService().supprimerEvenement(context);

        putQueueInUserSession();
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("annuler")
    public Response doAnnulerCommunication(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);

        SolonEppUIServiceLocator.getEvenementActionsUIService().annulerEvenement(context);

        putQueueInUserSession();
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("traiter")
    public Response doTraiter(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);

        SolonEppUIServiceLocator.getEvenementActionsUIService().traiterEvenement(context);

        putQueueInUserSession();
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("enCoursDeTraitement")
    public Response doEnCoursDeTraitement(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);

        SolonEppUIServiceLocator.getEvenementActionsUIService().enCoursTraitementEvenement(context);

        putQueueInUserSession();
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("accuserReception")
    public Response doAccuserReception(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);

        SolonEppUIServiceLocator.getEvenementActionsUIService().accuserReceptionVersion(context);

        putQueueInUserSession();
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("accepter")
    public Response doAccepter(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);
        context.putInContextData(ACCEPTER, true);

        SolonEppUIServiceLocator.getEvenementActionsUIService().validerVersion(context);

        putQueueInUserSession();
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("rejeter")
    public Response doRejeter(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);
        context.putInContextData(ACCEPTER, false);

        SolonEppUIServiceLocator.getEvenementActionsUIService().validerVersion(context);

        putQueueInUserSession();
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("abandonner")
    public Response doAbandonner(@FormParam(ID_MESSAGE) String idMessage) {
        context.putInContextData(ID, idMessage);

        SolonEppUIServiceLocator.getEvenementActionsUIService().abandonnerVersion(context);
        putQueueInUserSession();

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("envoyerMel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response envoyerMel(@SwBeanParam TransmettreParMelForm transmettreParMelForm) {
        context.putInContextData(TRANSMETTRE_PAR_MEL_FORM, transmettreParMelForm);
        SolonEppUIServiceLocator.getEvenementActionsUIService().transmettreParMelEnvoyer(context);
        return getJsonResponseWithMessagesInSessionIfSuccess();
    }

    @POST
    @Path("changerDestinataireCopie")
    @Produces(MediaType.APPLICATION_JSON)
    public Response changerDestinataireCopie(
        @FormParam("typeEvenement") String typeEvenement,
        @FormParam("emetteur") String emetteur,
        @FormParam("destinataire") String destinataire
    ) {
        context.putInContextData(TYPE_EVENEMENT, typeEvenement);
        context.putInContextData(EMETTEUR, emetteur);
        context.putInContextData(DESTINATAIRE, destinataire);
        SelectValueDTO destinataireCopie = SolonEppUIServiceLocator
            .getMetadonneesUIService()
            .getDestinataireCopie(context);

        Map<String, Object> result = new HashMap<>();
        result.put("destinataireCopieId", destinataireCopie.getId());
        result.put("destinataireCopieLabel", destinataireCopie.getLabel());

        Gson gson = new Gson();
        String json = gson.toJson(result);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), json).build();
    }

    private void putQueueInUserSession() {
        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("verifier/verrou")
    public Response doCheckLock(@FormParam(ID_MESSAGE) String idMessage) {
        String lockMessage = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(idMessage)) {
            DocumentModel messageDoc = context.getSession().getDocument(new IdRef(idMessage));
            Message message = messageDoc.getAdapter(Message.class);
            context.putInContextData(CURRENT_MESSAGE, message);

            lockMessage = SolonEppUIServiceLocator.getEvenementUIService().checkLockMessage(context);
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), lockMessage).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deverrouiller/force")
    public Response doForceUnlock(@FormParam(ID_MESSAGE) String idMessage, @FormParam(PARAM_SKIP_LOCK) Boolean skipLock) {
        context.putInContextData(ID, idMessage);
        context.putInContextData(SKIP_LOCK, skipLock);

        SolonEppUIServiceLocator.getEvenementUIService().forceUnlockCurrentMessage(context);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("deverrouiller")
    public Response doUnlock(@FormParam(ID_MESSAGE) String idMessage) {
        if (StringUtils.isNotBlank(idMessage)) {
            DocumentModel messageDoc = context.getSession().getDocument(new IdRef(idMessage));
            Message message = messageDoc.getAdapter(Message.class);
            context.putInContextData(CURRENT_MESSAGE, message);

            SolonEppUIServiceLocator.getEvenementUIService().unlockCurrentMessage(context);
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
