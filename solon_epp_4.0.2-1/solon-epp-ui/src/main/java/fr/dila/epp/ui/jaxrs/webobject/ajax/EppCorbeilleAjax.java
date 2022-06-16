package fr.dila.epp.ui.jaxrs.webobject.ajax;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.MESSAGE_LIST_FORM;
import static fr.dila.epp.ui.enumeration.EppContextDataKey.RAPID_SEARCH_FORM;
import static fr.dila.epp.ui.services.impl.EppCorbeilleMenuServiceImpl.ACTIVE_KEY;
import static fr.dila.st.ui.enums.STContextDataKey.ID;

import com.google.gson.Gson;
import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.bean.RapidSearchDTO;
import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.epp.ui.th.bean.MessageListForm;
import fr.dila.epp.ui.th.bean.RapidSearchForm;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.MapUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "EppCorbeilleAjax")
public class EppCorbeilleAjax extends SolonWebObject {

    @GET
    public ThTemplate doHome(@QueryParam("id") String corbeilleId) {
        return doSearchCommunication(corbeilleId, null);
    }

    @POST
    @Path("search")
    public ThTemplate doSearchCommunication(String id, @FormParam("search") String jsonSearch) {
        template.setContext(context);
        Gson gson = new Gson();
        Map<String, Object> mapSearch = jsonSearch != null ? gson.fromJson(jsonSearch, Map.class) : new HashMap<>();

        String corbeilleId = MapUtils.isNotEmpty(mapSearch) ? (String) mapSearch.get("idCorbeille") : id;

        if (jsonSearch != null) {
            UserSessionHelper.putUserSessionParameter(context, EppContextDataKey.JSON_SEARCH + corbeilleId, jsonSearch);
        } else {
            if (
                UserSessionHelper.getUserSessionParameter(
                    context,
                    EppContextDataKey.JSON_SEARCH + corbeilleId,
                    String.class
                ) !=
                null
            ) {
                mapSearch =
                    gson.fromJson(
                        UserSessionHelper.getUserSessionParameter(
                            context,
                            EppContextDataKey.JSON_SEARCH + corbeilleId,
                            String.class
                        ),
                        Map.class
                    );
            }
        }

        MessageListForm msgform = new MessageListForm(jsonSearch);
        RapidSearchForm rapidSearchForm = new RapidSearchForm(jsonSearch);

        context.putInContextData(ID, corbeilleId);
        context.putInContextData(MESSAGE_LIST_FORM, msgform);
        context.putInContextData(RAPID_SEARCH_FORM, rapidSearchForm);
        context.putInContextData(EppContextDataKey.MAP_SEARCH, mapSearch != null ? mapSearch : new HashMap<>());

        MessageList result = SolonEppUIServiceLocator.getMessageListUIService().getMessageListForCorbeille(context);

        context.setNavigationContextTitle(
            new Breadcrumb(
                result.getTitre(),
                "/corbeille/consulter?id=" + corbeilleId,
                Breadcrumb.TITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();
            template.setData(map);
        }

        // On renseigne la corbeille sélectionnée pour qu'elle soit surlignée
        UserSessionHelper.putUserSessionParameter(context, ACTIVE_KEY, corbeilleId);

        RapidSearchDTO rapidSearchDTO = new RapidSearchDTO();
        rapidSearchDTO.setLstTypeCommunication(
            SolonEppActionsServiceLocator.getEvenementTypeActionService().getEvenementTypeList()
        );
        rapidSearchDTO.setLstInstitution(SolonEppUIServiceLocator.getSelectValueUIService().getAllInstitutions());

        template.getData().put(STTemplateConstants.RESULT_LIST, result);
        template.getData().put(STTemplateConstants.LST_COLONNES, result.getListeColones(msgform));
        template.getData().put(STTemplateConstants.RESULT_FORM, msgform);
        template.getData().put(STTemplateConstants.DATA_URL, "/corbeille/consulter");
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, "/ajax/corbeille/search");
        template.getData().put(STTemplateConstants.RAPID_SEARCH, rapidSearchDTO);
        template.getData().put(STTemplateConstants.CORBEILLE, corbeilleId);
        template.getData().put(STTemplateConstants.TITRE, result.getTitre());
        template.getData().put(STTemplateConstants.RESULT_VISIBLE, mapSearch != null);

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("reinitialiser")
    public Response doReinitialiser(@FormParam("idCorbeille") String idCorbeille) {
        UserSessionHelper.putUserSessionParameter(context, EppContextDataKey.JSON_SEARCH + idCorbeille, null);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        ThTemplate myTemplate = new AjaxLayoutThTemplate("fragments/table/tableCommunications", getMyContext());
        myTemplate.setData(new HashMap<>());
        return myTemplate;
    }
}
