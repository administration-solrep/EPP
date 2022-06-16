package fr.dila.ss.ui.jaxrs.webobject.ajax.etape;

import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.ui.bean.EditionEtapeFdrDTO;
import fr.dila.ss.ui.bean.FdrDTO;
import fr.dila.ss.ui.bean.fdr.CreationEtapeDTO;
import fr.dila.ss.ui.bean.fdr.EtapeDTO;
import fr.dila.ss.ui.bean.fdr.FdrTableDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.enums.AlertType;
import fr.dila.st.ui.th.model.AjaxJSONLayoutThTemplate;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.ControllerUtils;
import fr.dila.st.ui.validators.annot.SwId;
import fr.dila.st.ui.validators.annot.SwLength;
import java.io.IOException;
import java.util.Collections;
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
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxEtape")
public class SSEtapeAjax extends SolonWebObject {
    protected static final String FDR_ID = "fdrId";
    protected static final String STATUS_INDEX_KEY = "statusIndex";
    protected static final String DTO_KEY = "dto";
    public static final String DOSSIER_LINK_ID = "dossierLinkId";
    public static final String ID_BRANCH = "idBranch";
    public static final String COPIED_STEP_SESSION_KEY = "copiedSteps";
    public static final String MAP_KEY_LINE = "line";
    public static final String MAP_KEY_UNIQUE_ID = "uniqueId";

    @Path("note")
    public Object doNote() {
        return newObject("AjaxNoteEtape");
    }

    @GET
    @Path("ajouterEtape")
    public ThTemplate ajouterEtape(
        @SwLength(max = 13) @QueryParam("uniqueId") Long uniqueId,
        @SwId @QueryParam(FDR_ID) String fdrId
    ) {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/fdr/etapeFdr", context);
        Map<String, Object> map = buildAjouterEtapeMap(uniqueId.toString());
        template.setData(map);

        return template;
    }

    protected List<SelectValueDTO> getTypeEtapeAjout() {
        // Méthode surchargé pour pouvoir filtrer des type d'étape lors de l'ajout/modification
        return SSUIServiceLocator.getSSSelectValueUIService().getRoutingTaskTypes();
    }

    @Path("charger")
    @POST
    public ThTemplate chargerEtape(@FormParam("stepId") String stepId) {
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/fdr/etapeFdr");
        template.setContext(context);

        context.setCurrentDocument(stepId);

        if (
            context.getCurrentDocument() != null && context.getCurrentDocument().getAdapter(SSRouteStep.class) != null
        ) {
            context.putInContextData(
                STContextDataKey.ID,
                context
                    .getCurrentDocument()
                    .getAdapter(SSRouteStep.class)
                    .getFeuilleRoute(context.getSession())
                    .getDocument()
                    .getId()
            );
        }

        EtapeDTO etapeDto = SSUIServiceLocator.getSSFeuilleRouteUIService().getEtapeDTO(context);

        if (StringUtils.isNotEmpty(etapeDto.getPosteId())) {
            etapeDto.setMapPoste(
                Collections.singletonMap(
                    etapeDto.getPosteId(),
                    STServiceLocator
                        .getOrganigrammeService()
                        .getOrganigrammeNodeById(etapeDto.getPosteId(), OrganigrammeType.POSTE)
                        .getLabel()
                )
            );
        }

        Map<String, Object> map = new HashMap<>();

        map.put(SSTemplateConstants.TYPE_ETAPE, getTypeEtapeAjout());
        map.put(MAP_KEY_LINE, etapeDto);
        map.put(MAP_KEY_UNIQUE_ID, "editStep");
        map.put(SSTemplateConstants.PROFIL, context.getWebcontext().getPrincipal().getGroups());
        template.setData(map);

        return template;
    }

    @Path("saveEtape")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveEtape(
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId,
        @SwBeanParam CreationEtapeDTO creationEtapeDTO
    ) {
        checkRightSaveEtape(dossierLinkId, creationEtapeDTO);

        SSUIServiceLocator.getSSFeuilleRouteUIService().addEtapes(context);

        handleErrors();

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("supprimerBrancheOuEtape")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response supprimerBrancheOuEtape(@FormParam(ID_BRANCH) String id) {
        context.setCurrentDocument(id);
        if (SSActionsServiceLocator.getFeuilleRouteActionService().checkRightDeleteBranchOrStep(context)) {
            SSActionsServiceLocator.getDocumentRoutingActionService().removeStep(context);
        } else {
            throw new STAuthorizationException("/admin/fdr/etape/supprimerBrancheOuEtape");
        }

        // Comme je recharge la page si pas d'erreur je met en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            context.getMessageQueue().addToastSuccess(ResourceHelper.getString("fdr.toast.remove.step.success"));
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("moveStep")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response moveStep(@FormParam("stepId") String stepId, @FormParam("direction") String direction) {
        context.setCurrentDocument(stepId);
        context.putInContextData(SSContextDataKey.DIRECTION_MOVE_STEP, direction);

        if (SSActionsServiceLocator.getFeuilleRouteActionService().checkRightMoveStep(context)) {
            SSActionsServiceLocator.getDocumentRoutingActionService().moveRouteElement(context);
        } else {
            throw new STAuthorizationException("/admin/fdr/etape/moveStep");
        }

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("editer")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response editerEtape(
        @FormParam(DOSSIER_LINK_ID) String dossierLinkId,
        @SwBeanParam EditionEtapeFdrDTO editStep
    )
        throws IOException {
        ThTemplate template = checkRightEditerEtape(dossierLinkId, editStep);

        EtapeDTO etapeDto = SSUIServiceLocator.getSSFeuilleRouteUIService().saveEtape(context);

        setDataTemplate(editStep, template, etapeDto);

        return new JsonResponse(
            SolonStatus.OK,
            context.getMessageQueue(),
            ControllerUtils.renderHtmlFromTemplate(template)
        )
        .build();
    }

    protected void setDataTemplate(EditionEtapeFdrDTO editStep, ThTemplate template, EtapeDTO etapeDto) {
        etapeDto.setId(editStep.getStepId());

        FdrTableDTO fdrTableDto = new FdrTableDTO();
        FdrDTO fdrDto = new FdrDTO();
        fdrTableDto.setTotalNbLevel(editStep.getTotalNbLevel());
        fdrDto.setTable(fdrTableDto);
        if (editStep.getIsModele()) {
            fdrDto.buildColonnesFdrModele();
        } else {
            fdrDto.buildColonnesFdr();
        }
        template.getData().put(MAP_KEY_LINE, etapeDto);
        template.getData().put(DTO_KEY, fdrDto);
        template.getData().put(STATUS_INDEX_KEY, editStep.getLineIndex());
    }

    @Path("copier")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response copySteps(@FormParam("stepIds[]") List<String> stepIds) {
        UserSessionHelper.putUserSessionParameter(context, COPIED_STEP_SESSION_KEY, stepIds);

        context
            .getMessageQueue()
            .addMessageToQueue(ResourceHelper.getString("fdr.etapes.copiees"), AlertType.TOAST_INFO);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @SuppressWarnings("unchecked")
    @Path("coller")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response pasteSteps(
        @FormParam("rootDocId") String rootDocId,
        @FormParam("lineId") String lineId,
        @FormParam("before") Boolean before
    ) {
        List<String> stepIds = UserSessionHelper.getUserSessionParameter(context, COPIED_STEP_SESSION_KEY, List.class);
        context.putInContextData(SSContextDataKey.ROUTE_STEP_IDS, stepIds);
        context.putInContextData(SSContextDataKey.ID_ETAPE, lineId);
        context.putInContextData(SSContextDataKey.ADD_BEFORE, before);

        int pastedSteps = 0;
        if (SSActionsServiceLocator.getFeuilleRouteActionService().checkRightPasteStep(context)) {
            // id du dossier ou du modele de fdr
            context.setCurrentDocument(rootDocId);
            if (BooleanUtils.isTrue(before)) {
                pastedSteps = SSActionsServiceLocator.getDocumentRoutingActionService().pasteBefore(context);
            } else {
                pastedSteps = SSActionsServiceLocator.getDocumentRoutingActionService().pasteAfter(context);
            }
        }

        if (pastedSteps > 0) {
            context
                .getMessageQueue()
                .addToastSuccess(ResourceHelper.getString("feuilleRoute.etape.action.paste.success", pastedSteps));
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    protected void checkRightSaveEtape(String dossierLinkId, CreationEtapeDTO creationEtapeDTO) {
        context.putInContextData(SSContextDataKey.CREATION_ETAPE_DTO, creationEtapeDTO);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);
        if (!SSActionsServiceLocator.getFeuilleRouteActionService().checkRightSaveEtape(context)) {
            throw new STAuthorizationException("/admin/fdr/etape/saveEtape");
        }
    }

    protected ThTemplate checkRightEditerEtape(String dossierLinkId, EditionEtapeFdrDTO editStep) {
        ThTemplate template = new AjaxJSONLayoutThTemplate();
        template.setContext(context);
        template.setName("fragments/fdr/colInfoEtapeFdr");

        context.putInContextData(SSContextDataKey.EDITION_ETAPE_FDR_DTO, editStep);
        context.putInContextData(STContextDataKey.CURRENT_DOSSIER_LINK, dossierLinkId);

        if (!SSActionsServiceLocator.getFeuilleRouteActionService().checkRightUpdateStep(context)) {
            throw new STAuthorizationException("/admin/fdr/etape/editer");
        }
        return template;
    }

    protected void handleErrors() {
        // Comme je recharge la page si pas d'erreur je met en session les
        // messages pour les afficher au rechargement
        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }
    }

    protected Map<String, Object> buildAjouterEtapeMap(String uniqueId) {
        Map<String, Object> map = new HashMap<>();
        map.put(MAP_KEY_UNIQUE_ID, uniqueId);
        map.put(SSTemplateConstants.TYPE_ETAPE, getTypeEtapeAjout());
        map.put("btnDeleteVisible", true);
        map.put(SSTemplateConstants.PROFIL, context.getWebcontext().getPrincipal().getGroups());
        return map;
    }
}
