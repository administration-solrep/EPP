package fr.dila.st.ui.jaxrs.webobject.ajax;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.impl.AbstractOrganigrammeManagerService;
import fr.dila.st.ui.services.impl.OrganigrammeTreeUIServiceImpl;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.json.JSONException;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "OrganigrammeAjax")
public class STOrganigrammeAjax extends SolonWebObject {
    protected static final String RESPONSE_FILE = "file";
    protected static final String RESPONSE_STATUS_OK = "OK";
    protected static final String RESPONSE_STATUS_KO = "KO";
    protected static final String RESPONSE_STATUS = "status";

    public STOrganigrammeAjax() {
        super();
    }

    @GET
    @Path("/selectarbre")
    public ThTemplate getSelectArbre(
        @QueryParam(STTemplateConstants.TYPE_SELECTION) List<String> typeSelection,
        @QueryParam(STTemplateConstants.SELECT_ID) String selectID,
        @QueryParam(STTemplateConstants.ACTIVATE_POSTE_FILTER) boolean activatePosteFilter,
        @QueryParam("selectedNode") String selectedNode,
        @QueryParam(STTemplateConstants.IS_MULTI) boolean isMulti,
        @QueryParam(STTemplateConstants.DTO_ATTRIBUTE) String dtoAttribute,
        @QueryParam(STTemplateConstants.FILTER_CE) Boolean filterCE
    ) {
        ThTemplate template = new AjaxLayoutThTemplate("fragments/components/organigrammeSelectArbre", context);

        context.putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, activatePosteFilter);

        // ID sous lequel les noeuds ouverts de l'organigramme concerné
        // sont stockés dans la UserSession
        context.putInContextData(
            OrganigrammeTreeUIServiceImpl.OPEN_NODES_ID_KEY,
            OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_OPEN_NODES_KEY
        );
        if (StringUtils.isNotBlank(selectedNode)) {
            // Si un noeud de l'organigramme est appuyé
            @SuppressWarnings("unchecked")
            Set<String> openNodes = UserSessionHelper.getUserSessionParameter(
                context,
                OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_OPEN_NODES_KEY,
                Set.class
            );
            if (openNodes != null) {
                // On l'ajoute ou l'enlève de la liste des noeuds actifs
                if (openNodes.contains(selectedNode)) {
                    openNodes.remove(selectedNode);
                } else {
                    openNodes.add(selectedNode);
                }
                // On sauvegarde la nouvelle liste de noeuds ouverts dans la
                // UserSession
                UserSessionHelper.putUserSessionParameter(
                    context,
                    OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_OPEN_NODES_KEY,
                    openNodes
                );
            }
        }
        template.setContext(context);

        Map<String, Object> map = new HashMap<>();
        List<OrganigrammeElementDTO> dtos = loadOrganigrammWithAction(context);

        map.put(STTemplateConstants.TREE_LIST, dtos);
        map.put(STTemplateConstants.LEVEL, 1);
        map.put(STTemplateConstants.CURRENT_ID, selectID);
        map.put(STTemplateConstants.IS_OPEN, true);
        map.put(STTemplateConstants.SELECT_ID, selectID);
        map.put(STTemplateConstants.TYPE_SELECTION, typeSelection != null ? String.join(",", typeSelection) : null); // pour la sélection multi-types, on remet dans le template un string pour qu'il soit récupéré correctement par le fragment organigrammeSelectArbre
        map.put(STTemplateConstants.ACTIVATE_POSTE_FILTER, activatePosteFilter);
        map.put(STTemplateConstants.IS_MULTI, isMulti);
        map.put(STTemplateConstants.DTO_ATTRIBUTE, dtoAttribute); // pour le multi, on utilise le
        map.put(STTemplateConstants.FILTER_CE, filterCE);
        // paramètre "dtoAttribute" pour
        // représenter le nom de
        // l'attribut
        template.setData(map);
        return template;
    }

    protected List<OrganigrammeElementDTO> loadOrganigrammWithAction(SpecificContext context) {
        return STUIServiceLocator.getOrganigrammeTreeService().getOrganigramme(context);
    }

    @GET
    public ThTemplate getArbre(
        @QueryParam("showDeactivated") Boolean showDeactivated,
        @QueryParam("selectedNode") String selectedNode
    ) {
        if (showDeactivated == null) {
            showDeactivated =
                (Boolean) context
                    .getWebcontext()
                    .getUserSession()
                    .getOrDefault(OrganigrammeTreeUIServiceImpl.SHOW_DEACTIVATED_KEY, Boolean.FALSE);
        }
        context.putInContextData(STContextDataKey.SHOW_DEACTIVATED, showDeactivated);

        String openNodesId = OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_OPEN_NODES_KEY;
        context.putInContextData(OrganigrammeTreeUIServiceImpl.OPEN_NODES_ID_KEY, openNodesId);
        if (StringUtils.isNotBlank(selectedNode)) {
            // Si un noeud de l'organigramme est appuyé
            @SuppressWarnings("unchecked")
            Set<String> openNodes = UserSessionHelper.getUserSessionParameter(
                context,
                OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_OPEN_NODES_KEY,
                Set.class
            );
            if (openNodes != null) {
                // On l'ajoute ou l'enlève de la liste des noeuds actifs
                if (openNodes.contains(selectedNode)) {
                    openNodes.remove(selectedNode);
                } else {
                    openNodes.add(selectedNode);
                }
                // On sauvegarde la nouvelle liste de noeuds ouverts dans la
                // UserSession
                UserSessionHelper.putUserSessionParameter(
                    context,
                    OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_OPEN_NODES_KEY,
                    openNodes
                );
            }
        }

        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();

            template.setData(map);
        }
        List<OrganigrammeElementDTO> dtos = loadOrganigrammWithAction(context);

        template.getData().put(OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_KEY, dtos);

        template.getData().put(STTemplateConstants.LEVEL, 1);
        template.getData().put(STTemplateConstants.CURRENT_ID, "organigramme");
        template.getData().put(STTemplateConstants.IS_OPEN, true);

        return template;
    }

    @POST
    @Path("/deleteNode")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNode(
        @FormParam("nodeId") String id,
        @FormParam("nodeType") String type,
        @FormParam("curMin") String ministereID
    )
        throws JSONException {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();

        OrganigrammeType orgType = OrganigrammeType.getEnum(type);
        OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(id, orgType);

        List<OrganigrammeNode> lstParent = STServiceLocator.getOrganigrammeService().getParentList(node);

        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(
                context.getSession(),
                node,
                ministereID,
                new OrganigrammeElementDTO(context.getSession(), lstParent.get(0))
            )
        );

        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        OrganigrammeType enumType = OrganigrammeType.getEnum(type);
        if (
            OrganigrammeType.MINISTERE == enumType &&
            context.getAction(STActionEnum.REMOVE_MINISTERE) == null ||
            OrganigrammeType.MINISTERE != enumType &&
            context.getAction(STActionEnum.REMOVE_NODE) == null
        ) {
            throw new STAuthorizationException("/admin/organigramme/deleteNode");
        }

        OrganigrammeTreeUIService service = STUIServiceLocator.getOrganigrammeTreeService();
        String result = service.deleteNode(enumType, id, context);

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }
        if (result != null) {
            return new JsonResponse(SolonStatus.FUNCTIONAL_ERROR, context.getMessageQueue(), result).build();
        } else {
            return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
        }
    }

    @POST
    @Path("copy")
    public ThTemplate copyNode(
        @FormParam("nodeId") String id,
        @FormParam("nodeType") String type,
        @FormParam("curMin") String ministereID
    ) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        PosteNode poste = organigrammeService.getOrganigrammeNodeById(id, OrganigrammeType.POSTE);
        OrganigrammeNode parent = poste.getFirstUSParent();
        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(
                context.getSession(),
                poste,
                ministereID,
                new OrganigrammeElementDTO(context.getSession(), parent)
            )
        );
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        verifyAction(STActionEnum.COPY_NODE, "/admin/organigramme/copy");

        SpecificContext context = getMyContext();
        OrganigrammeTreeUIService service = STUIServiceLocator.getOrganigrammeTreeService();
        service.copyNode(context, id, type);
        return refreshFullArbre(context);
    }

    @POST
    @Path("paste")
    public ThTemplate pasteNode(
        @FormParam("nodeId") String id,
        @FormParam("nodeType") String type,
        @FormParam("curMin") String ministereID,
        @FormParam("withUsers") boolean withUsers
    ) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(id, OrganigrammeType.getEnum(type));
        OrganigrammeNode parent = organigrammeService.getParentList(node).stream().findFirst().orElse(null);

        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(
                context.getSession(),
                node,
                ministereID,
                new OrganigrammeElementDTO(context.getSession(), parent)
            )
        );

        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);
        SpecificContext context = getMyContext();
        OrganigrammeTreeUIService service = STUIServiceLocator.getOrganigrammeTreeService();
        if (withUsers) {
            verifyAction(STActionEnum.PASTE_NODE_WITH_USERS, "/admin/organigramme/paste");
            service.pasteNodeWithUsers(context, id, type);
        } else {
            verifyAction(STActionEnum.PASTE_NODE_WITHOUT_USER, "/admin/organigramme/paste");
            service.pasteNodeWithoutUser(context, id, type);
        }
        return refreshFullArbre(context);
    }

    @GET
    @Path("/activation")
    public ThTemplate manageActivation(
        @QueryParam(STTemplateConstants.TYPE_SELECTION) String typeSelection,
        @QueryParam(STTemplateConstants.SELECT_ID) String selectID,
        @QueryParam("curMin") String ministereId,
        @QueryParam("activate") boolean toActivate
    ) {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeType type = OrganigrammeType.getEnum(typeSelection);
        OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(selectID, type);
        OrganigrammeNode parent = organigrammeService.getParentList(node).stream().findFirst().orElse(null);

        context.putInContextData(
            STContextDataKey.ORGANIGRAMME_NODE,
            new OrganigrammeElementDTO(
                context.getSession(),
                node,
                ministereId,
                new OrganigrammeElementDTO(context.getSession(), parent)
            )
        );
        context.putInContextData(OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_NODE_ID_KEY, selectID);
        context.putInContextData(OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TYPE_KEY, type);
        STUIServiceLocator.getSTOrganigrammeManagerService().computeOrganigrammeActions(context);

        OrganigrammeTreeUIService service = STUIServiceLocator.getOrganigrammeTreeService();

        if (toActivate) {
            if (
                OrganigrammeType.MINISTERE == type &&
                context.getAction(STActionEnum.ENABLE_MINISTERE) == null ||
                OrganigrammeType.MINISTERE != type &&
                context.getAction(STActionEnum.ENABLE_NODE) == null
            ) {
                throw new STAuthorizationException("/admin/organigramme/activation");
            }
            service.setActiveState(context);
        } else {
            if (
                OrganigrammeType.MINISTERE == type &&
                context.getAction(STActionEnum.DISABLE_MINISTERE) == null ||
                OrganigrammeType.MINISTERE != type &&
                context.getAction(STActionEnum.DISABLE_NODE) == null
            ) {
                throw new STAuthorizationException("/admin/organigramme/activation");
            }
            service.setInactiveState(context);
        }

        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getSuccessQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return refreshFullArbre(context);
    }

    @POST
    @Path("/deverrouiller")
    @Produces(MediaType.APPLICATION_JSON)
    public Object unlockNode(@FormParam("idNode") String idNode, @FormParam("type") String type) throws JSONException {
        OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
        OrganigrammeNode node = organigrammeService.getOrganigrammeNodeById(idNode, OrganigrammeType.getEnum(type));

        boolean result = organigrammeService.unlockOrganigrammeNode(node);

        if (!result) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("error.organigramme.node.unlock"));
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    protected ThTemplate refreshFullArbre(SpecificContext context) {
        boolean showDeactivated = (boolean) context
            .getWebcontext()
            .getUserSession()
            .get(OrganigrammeTreeUIServiceImpl.SHOW_DEACTIVATED_KEY);
        ThTemplate template = getArbre(showDeactivated, null);
        template.setContext(context);
        return template;
    }

    @GET
    @Path("/suggestions")
    public String getSuggestions(
        @QueryParam("typeSelection") List<String> typeSelection,
        @QueryParam("input") String input,
        @QueryParam("activatePosteFilter") boolean activatePosteFilter
    )
        throws JsonProcessingException {
        if (CollectionUtils.isNotEmpty(typeSelection)) {
            context.putInContextData(AbstractOrganigrammeManagerService.TYPE_SELECTION_KEY, typeSelection);
        }
        if (StringUtils.isNotBlank(input)) {
            context.putInContextData(AbstractOrganigrammeManagerService.INPUT_KEY, input);
        }
        context.putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, activatePosteFilter);

        STOrganigrammeManagerService service = STUIServiceLocator.getSTOrganigrammeManagerService();
        List<SuggestionDTO> list = service.getSuggestions(context);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(list);
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/components/organigrammeArbre", getMyContext());
    }
}
