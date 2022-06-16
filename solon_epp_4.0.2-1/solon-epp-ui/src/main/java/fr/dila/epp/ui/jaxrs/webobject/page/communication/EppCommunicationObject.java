package fr.dila.epp.ui.jaxrs.webobject.page.communication;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.COMMUNICATION_METADONNEES_MAP;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.MODE_CREATION;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.PUBLIER;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.LAYOUT_MODE_COMPLETER;
import static fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl.LAYOUT_MODE_RECTIFIER;
import static fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl.IS_EDIT_MODE;
import static fr.dila.st.ui.enums.STContextDataKey.ID;

import com.google.gson.Gson;
import fr.dila.epp.ui.enumeration.EppActionCategory;
import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.th.model.EppCorbeilleTemplate;
import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.DetailCommunication;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.bean.TransmettreParMelForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliCommunication")
public class EppCommunicationObject extends SolonWebObject {
    private static final String COMMUNICATION_CREATE_TEMPLATE = "pages/communication/create";
    private static final String STRING_COLON_STRING = "%s : %s";

    @GET
    public ThTemplate doHome() {
        ThTemplate template = getMyTemplate();
        template.setName("pages/espaceTravailHome");
        context.removeNavigationContextTitle();
        template.setContext(context);

        return template;
    }

    @GET
    @Path("creerAlerte")
    public ThTemplate creerAlerte(@QueryParam("idMessagePrecedent") String idMessagePrecedent) {
        context.putInContextData(ID, idMessagePrecedent);
        String typeAlerteSuccessive = SolonEppUIServiceLocator
            .getEvenementActionsUIService()
            .getTypeAlerteSuccessive(context);
        return creerCommunication(typeAlerteSuccessive, idMessagePrecedent);
    }

    @GET
    @Path("creation")
    public ThTemplate creerCommunication(
        @QueryParam("typeEvenement") String typeEvenement,
        @QueryParam("idMessagePrecedent") String idMessagePrecedent
    ) {
        template.setName(COMMUNICATION_CREATE_TEMPLATE);
        template.setContext(context);

        context.putInContextData(EppContextDataKey.TYPE_EVENEMENT, typeEvenement);
        context.putInContextData(ID, idMessagePrecedent);
        SolonEppUIServiceLocator.getEvenementUIService().creerEvenement(context);

        String title = String.format(
            STRING_COLON_STRING,
            ResourceHelper.getString(
                idMessagePrecedent == null ? "creation.communication.title" : "creation.communication.successive.title"
            ),
            ObjectHelper.requireNonNullElse(
                SolonEppServiceLocator.getEvenementTypeService().getEvenementType(typeEvenement).getLabel(),
                typeEvenement
            )
        );
        context.setNavigationContextTitle(
            new Breadcrumb(
                "Création",
                StringUtils.join(
                    "/communication/creation?typeEvenement=",
                    typeEvenement,
                    StringUtils.isNotBlank(idMessagePrecedent) ? "&idMessagePrecedent=" + idMessagePrecedent : "",
                    "#main_content"
                ),
                Breadcrumb.SUBTITLE_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );

        context.putInContextData(IS_EDIT_MODE, true);

        List<Action> mainActions = context.getActions(
            typeEvenement.startsWith("ALERTE")
                ? EppActionCategory.MAIN_COMMUNICATION_CREER_ALERTE
                : EppActionCategory.MAIN_COMMUNICATION_CREER
        );

        template.setData(
            getTemplateData(
                context,
                idMessagePrecedent,
                title,
                SolonEppConstant.VERSION_ACTION_CREER_EVENEMENT,
                typeEvenement,
                mainActions
            )
        );

        return template;
    }

    @GET
    @Path("modification")
    public ThTemplate modifierCommunication(@QueryParam("id") String id) {
        template.setName(COMMUNICATION_CREATE_TEMPLATE);
        template.setContext(context);

        context.putInContextData(ID, id);
        SolonEppUIServiceLocator.getEvenementUIService().modifierEvenement(context);

        String typeEvenement = context.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();
        String title = String.format(
            STRING_COLON_STRING,
            ResourceHelper.getString("modification.communication.title"),
            ObjectHelper.requireNonNullElse(
                SolonEppServiceLocator.getEvenementTypeService().getEvenementType(typeEvenement).getLabel(),
                typeEvenement
            )
        );
        context.setNavigationContextTitle(
            new Breadcrumb(
                "Modification",
                "/communication/modification?id=" + id,
                Breadcrumb.SUBTITLE_ORDER + 2,
                context.getWebcontext().getRequest()
            )
        );

        context.putInContextData(IS_EDIT_MODE, true);

        List<Action> mainActions = context.getActions(EppActionCategory.MAIN_COMMUNICATION_EDIT);

        template.setData(
            getTemplateData(context, id, title, SolonEppConstant.VERSION_ACTION_MODIFIER, typeEvenement, mainActions)
        );

        return template;
    }

    @GET
    @Path("completer")
    public ThTemplate completerCommunication(@QueryParam("id") String id) {
        return completerRectifier(id, LAYOUT_MODE_COMPLETER);
    }

    @GET
    @Path("rectifier")
    public ThTemplate rectifierCommunication(@QueryParam("id") String id) {
        return completerRectifier(id, LAYOUT_MODE_RECTIFIER);
    }

    private ThTemplate completerRectifier(String id, String typeAction) {
        template.setName(COMMUNICATION_CREATE_TEMPLATE);
        template.setContext(context);

        context.putInContextData(ID, id);
        context.putInContextData(MODE_CREATION, typeAction);
        SolonEppUIServiceLocator.getEvenementUIService().consulterEvenement(context);

        String typeEvenement = context.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();
        String title = String.format(
            STRING_COLON_STRING,
            ResourceHelper.getString(typeAction + ".communication.title"),
            ObjectHelper.requireNonNullElse(
                SolonEppServiceLocator.getEvenementTypeService().getEvenementType(typeEvenement).getLabel(),
                typeEvenement
            )
        );
        context.setNavigationContextTitle(
            new Breadcrumb(
                ResourceHelper.getString("action.button.label." + typeAction),
                "/communication/" + typeAction + "?id=" + id,
                Breadcrumb.SUBTITLE_ORDER + 2,
                context.getWebcontext().getRequest()
            )
        );

        context.putInContextData(IS_EDIT_MODE, true);

        List<Action> mainActions = context.getActions(EppActionCategory.MAIN_COMMUNICATION_EDIT);

        template.setData(
            getTemplateData(
                context,
                id,
                title,
                LAYOUT_MODE_COMPLETER.equals(typeAction)
                    ? SolonEppConstant.VERSION_ACTION_COMPLETER
                    : SolonEppConstant.VERSION_ACTION_RECTIFIER,
                typeEvenement,
                mainActions
            )
        );

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("enregistrement")
    public Response enregistrerCommunication(
        @FormParam("communication") String json,
        @FormParam("idMessage") String idMessage,
        @FormParam("publier") Boolean publier,
        @FormParam("typeAction") String typeAction,
        @FormParam("typeEvenement") String typeEvenement
    ) {
        context.putInContextData(ID, idMessage);
        Gson gson = new Gson();
        context.putInContextData(COMMUNICATION_METADONNEES_MAP, gson.fromJson(json, Map.class));
        context.putInContextData(PUBLIER, publier);
        String idMessageForRedirect = idMessage;

        if (SolonEppConstant.VERSION_ACTION_CREER_EVENEMENT.equals(typeAction)) {
            context.putInContextData(EppContextDataKey.TYPE_EVENEMENT, typeEvenement);
            idMessageForRedirect = SolonEppUIServiceLocator.getEvenementUIService().saveCreerEvenement(context);
        } else if (SolonEppConstant.VERSION_ACTION_MODIFIER.equals(typeAction)) {
            SolonEppUIServiceLocator.getEvenementUIService().saveModifierEvenement(context);
        } else {
            context.putInContextData(MODE_CREATION, typeAction);
            SolonEppUIServiceLocator.getEvenementUIService().saveCompleterRectifierEvenement(context);
        }

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        Map<String, String> retourSave = new HashMap<>();
        retourSave.put("idMessage", idMessageForRedirect);
        retourSave.put("app", "epp");
        String jsonRetour = gson.toJson(retourSave);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), jsonRetour).build();
    }

    @GET
    @Path("transmettreParMel")
    public ThTemplate transmettreParMel(@QueryParam("id") String id) {
        template.setName("fragments/components/transmettreParMel");
        template.setContext(context);

        context.putInContextData(ID, id);
        TransmettreParMelForm tpmForm = SolonEppUIServiceLocator
            .getEvenementActionsUIService()
            .getTransmettreParMelForm(context);

        context.setNavigationContextTitle(
            new Breadcrumb(
                ResourceHelper.getString("action.button.label.transmettreMail"),
                "/communication/transmettreParMel?id=" + id,
                Breadcrumb.SUBTITLE_ORDER + 2,
                context.getWebcontext().getRequest()
            )
        );

        List<Action> baseActions = context.getActions(EppActionCategory.BASE_COMMUNICATION_EDIT);
        List<Action> mainActions = context.getActions(EppActionCategory.MAIN_TRANSMETTRE_PAR_MEL);

        Map<String, Object> map = new HashMap<>();

        map.put(STTemplateConstants.BASE_ACTIONS, baseActions);
        map.put(STTemplateConstants.MAIN_ACTIONS, mainActions);
        map.put("transmettreParMelForm", tpmForm);

        if (context.getNavigationContext().size() > 1) {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            context.removeNavigationContextTitle();
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }

        template.setData(map);

        return template;
    }

    @POST
    @Path("buildBlocPJ")
    public ThTemplate buildBlocPJ(
        @FormParam("widget") String widget,
        @FormParam("widgetName") String widgetName,
        @FormParam("widgetLabel") String widgetLabel,
        @FormParam("multiValue") String multiValue
    ) {
        template = new AjaxLayoutThTemplate("fragments/components/bloc-ajout-piece-jointe", getMyContext());
        template.setData(new HashMap<>());

        template.getData().put("widget", widget);
        template.getData().put("widgetName", widgetName);
        template.getData().put("widgetLabel", StringUtils.removeEnd(widgetLabel, "(s)"));
        template.getData().put("multiValue", multiValue);
        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new EppCorbeilleTemplate();
    }

    private static Map<String, Object> getTemplateData(
        SpecificContext context,
        String id,
        String title,
        String typeAction,
        String typeEvenement,
        List<Action> mainActions
    ) {
        Map<String, Object> templateData = new HashMap<>();

        templateData.put(STTemplateConstants.ID, id);
        templateData.put(STTemplateConstants.TITRE, title);
        templateData.put(STTemplateConstants.TYPE_ACTION, typeAction);
        templateData.put(STTemplateConstants.TYPE_EVENEMENT, typeEvenement);
        DetailCommunication com = new DetailCommunication();
        com.setLstWidgets(SolonEppUIServiceLocator.getMetadonneesUIService().getWidgetListForCommunication(context));
        templateData.put(STTemplateConstants.LST_WIDGETS, com.getLstWidgets());
        List<Action> baseActions = context.getActions(EppActionCategory.BASE_COMMUNICATION_EDIT);
        templateData.put(STTemplateConstants.BASE_ACTIONS, baseActions);
        templateData.put(STTemplateConstants.MAIN_ACTIONS, mainActions);

        if (context.getNavigationContext().size() > 1) {
            templateData.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            context.removeNavigationContextTitle();
            templateData.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }

        return templateData;
    }
}
