package fr.dila.ss.ui.jaxrs.webobject.ajax.etape;

import fr.dila.ss.ui.bean.fdr.NoteEtapeFormDTO;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxNoteEtape")
public class SSNoteEtapeAjax extends SolonWebObject {
    public static final String DOSSIERS_ID = "idDossiers";
    public static final String COMMENT = "commentContent";

    private static final String ADD_NOTE_SUCCESS_MESSAGE = "fdr.toast.add.note.success";

    public SSNoteEtapeAjax() {
        super();
    }

    @Path("creer")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response create(@SwBeanParam NoteEtapeFormDTO noteEtapeFormDTO) {
        context.setCurrentDocument(noteEtapeFormDTO.getStepId());
        context.putInContextData(SSContextDataKey.NOTE_ETAPE_FORM, noteEtapeFormDTO);

        SSUIServiceLocator.getSSCommentManagerUIService().addComment(context);
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString(ADD_NOTE_SUCCESS_MESSAGE));

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("creerEnCours")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response createCurrentStep(@SwBeanParam NoteEtapeFormDTO noteEtapeFormDTO) {
        context.setCurrentDocument(noteEtapeFormDTO.getDossierId());
        context.putInContextData(SSContextDataKey.NOTE_ETAPE_FORM, noteEtapeFormDTO);

        SSUIServiceLocator.getSSCommentManagerUIService().addCommentRunningStep(context);
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString(ADD_NOTE_SUCCESS_MESSAGE));

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("modifier")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response edit(@SwBeanParam NoteEtapeFormDTO noteEtapeFormDTO) {
        context.setCurrentDocument(noteEtapeFormDTO.getStepId());
        context.putInContextData(SSContextDataKey.NOTE_ETAPE_FORM, noteEtapeFormDTO);

        SSUIServiceLocator.getSSCommentManagerUIService().updateComment(context);
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString("fdr.toast.edit.note.success"));

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("supprimer")
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response remove(@SwBeanParam NoteEtapeFormDTO noteEtapeFormDTO) {
        context.setCurrentDocument(noteEtapeFormDTO.getStepId());
        context.putInContextData(SSContextDataKey.COMMENT_ID, noteEtapeFormDTO.getCommentId());

        SSUIServiceLocator.getSSCommentManagerUIService().deleteComment(context);
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString("fdr.toast.remove.note.success"));

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("charger")
    @POST
    public ThTemplate loadNote(@FormParam("noteId") String noteId) {
        NoteEtapeFormDTO dto = Optional
            .ofNullable(noteId)
            .map(
                id -> {
                    context.setCurrentDocument(noteId);
                    return SSActionsServiceLocator.getRouteStepNoteActionService().getNoteEtape(context);
                }
            )
            .orElseGet(NoteEtapeFormDTO::new);

        ThTemplate template = new AjaxLayoutThTemplate("fragments/fdr/addEditNoteEtapeContent", context);
        Map<String, Object> map = new HashMap<>();
        map.put(SSTemplateConstants.NOTE_DTO, dto);
        template.setData(map);
        return template;
    }

    @Path("creerNotePartagee")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSharedNote(
        @FormParam("idEtapes[]") List<String> idEtapes,
        @FormParam("commentContent") String commentContent
    ) {
        context.putInContextData(SSContextDataKey.ROUTE_STEP_IDS, idEtapes);
        context.putInContextData(SSContextDataKey.COMMENT_CONTENT, commentContent);

        SSUIServiceLocator.getSSCommentManagerUIService().addSharedComment(context);
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString(ADD_NOTE_SUCCESS_MESSAGE));

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Path("chargerEtapesEnCours")
    @POST
    public ThTemplate loadCurrentSteps(
        @FormParam("idDossiers[]") List<String> idDossiers,
        @FormParam("routingTaskTypes[]") List<String> routingTaskTypes
    ) {
        List<String> uniqueRoutingTaskTypes = new ArrayList<>(new LinkedHashSet<>(routingTaskTypes));
        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/fdr/modaleInfo");

        Map<String, Object> map = new HashMap<>();
        map.put(DOSSIERS_ID, idDossiers);
        map.put("routingTaskTypes", uniqueRoutingTaskTypes);
        template.setData(map);

        template.setContext(context);

        return template;
    }

    @Path("creerNotesMultiDossiers")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMulti(@SwBeanParam NoteEtapeFormDTO noteEtapeFormDTO) {
        context.putInContextData(SSContextDataKey.NOTE_ETAPE_FORM, noteEtapeFormDTO);

        SSUIServiceLocator.getSSCommentManagerUIService().addCommentFromDossierLinks(context);

        context.getMessageQueue().addToastSuccess(ResourceHelper.getString(ADD_NOTE_SUCCESS_MESSAGE));

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
