package fr.dila.st.ui.jaxrs.webobject.ajax;

import static fr.dila.st.ui.enums.STContextDataKey.USERS_LIST_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.USER_SEARCH_FORM;
import static fr.dila.st.ui.services.STUIServiceLocator.getRechercheUtilisateursUIService;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.bean.UserSearchForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.constants.STURLConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "RechercheUtilisateur")
public class STRechercheUsersAjax extends SolonWebObject {
    private static final String ID_SEARCH_USER_FORM = "searchUserForm";

    public STRechercheUsersAjax() {
        super();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("reinit")
    public Response reinitUserSearch() {
        UserSessionHelper.clearUserSessionParameter(context, STUserSessionKey.SEARCH_FORMS_KEY);
        UserSessionHelper.clearUserSessionParameter(context, STUserSessionKey.USER_LIST_FORM_KEY);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("resultats")
    public ThTemplate getUserSearch(@SwBeanParam UserSearchForm userSearchform, @SwBeanParam UsersListForm formTri) {
        template.setName("fragments/components/result-list-user");
        template.setContext(context);

        if (formIsEmpty(userSearchform)) {
            context
                .getMessageQueue()
                .addErrorToQueue(ResourceHelper.getString("recherche.userSearch.empty.form"), ID_SEARCH_USER_FORM);
            return template;
        }

        userSearchform.setMapMinisteres(fetchMapData(userSearchform.getMinisteres(), OrganigrammeType.MINISTERE));
        userSearchform.setMapPostes(fetchMapData(userSearchform.getPostes(), OrganigrammeType.POSTE));
        userSearchform.setMapUnitesStructurelles(
            fetchMapData(userSearchform.getUnitesStructurelles(), OrganigrammeType.UNITE_STRUCTURELLE)
        );

        context.putInContextData(USER_SEARCH_FORM, userSearchform);
        context.putInContextData(USERS_LIST_FORM, formTri);
        context.putInContextData(STContextDataKey.GET_FULL_USER, false);
        STUsersList dto = getRechercheUtilisateursUIService().searchUsers(context);

        // ajout à la session (utile si on revient sur la page et qu'on avait déjà effectué une recherche)
        UserSessionHelper.putUserSessionParameter(context, STUserSessionKey.SEARCH_FORMS_KEY, userSearchform);
        UserSessionHelper.putUserSessionParameter(context, STUserSessionKey.USER_LIST_FORM_KEY, formTri);

        return buildMapResultUser(dto, formTri);
    }

    protected ThTemplate buildMapResultUser(STUsersList dto, UsersListForm formTri) {
        Map<String, Object> map = template.getData();
        map.put(STTemplateConstants.RESULTAT_LIST, dto.getListe());
        map.put(STTemplateConstants.LST_COLONNES, dto.getListeColonnes(formTri));
        map.put(STTemplateConstants.DATA_URL, STURLConstants.ADMIN_USER_SEARCH);
        map.put(STTemplateConstants.NB_RESULTS, dto.getListe().size());
        map.put(STTemplateConstants.DATA_AJAX_URL, STURLConstants.AJAX_USER_SEARCH_RESULTS);
        map.put(STTemplateConstants.USER_ACTION_LIST_LEFT, context.getActions(STActionCategory.USER_ACTION_LIST_LEFT));
        map.put(
            STTemplateConstants.USER_ACTION_LIST_RIGHT,
            context.getActions(STActionCategory.USER_ACTION_LIST_RIGHT)
        );
        map.put(STTemplateConstants.DISPLAY_TABLE, true);
        template.setData(map);
        return template;
    }

    /**
     * Permet de savoir si le fomulaire de recherche est vide ou non
     *
     * @param userSearchForm: formulaire de recherche
     * @return <code>True</code> si le formulaire de recherche et vide
     */
    private boolean formIsEmpty(UserSearchForm userSearchForm) {
        return (
            StringUtils.isBlank(userSearchForm.getMel()) &&
            StringUtils.isBlank(userSearchForm.getNom()) &&
            StringUtils.isBlank(userSearchForm.getPrenom()) &&
            StringUtils.isBlank(userSearchForm.getTelephone()) &&
            StringUtils.isBlank(userSearchForm.getUtilisateur()) &&
            StringUtils.isBlank(userSearchForm.getDateCreationFin()) &&
            StringUtils.isBlank(userSearchForm.getDateCreationDebut()) &&
            StringUtils.isBlank(userSearchForm.getDateExpirationFin()) &&
            StringUtils.isBlank(userSearchForm.getDateExpirationDebut()) &&
            CollectionUtils.isEmpty(userSearchForm.getMinisteres()) &&
            CollectionUtils.isEmpty(userSearchForm.getPostes()) &&
            CollectionUtils.isEmpty(userSearchForm.getProfils()) &&
            CollectionUtils.isEmpty(userSearchForm.getUnitesStructurelles())
        );
    }

    private HashMap<String, String> fetchMapData(List<String> list, OrganigrammeType type) {
        return list == null
            ? null
            : list
                .stream()
                .map(
                    id -> STServiceLocator.getOrganigrammeService().<OrganigrammeNode>getOrganigrammeNodeById(id, type)
                )
                .collect(HashMap::new, (m, v) -> m.put(v.getId(), v.getLabel()), HashMap::putAll);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("supprimer")
    public Response deleteUsers(@FormParam("userIds[]") List<String> userIds) {
        context.putInContextData(STContextDataKey.IDS, userIds);
        getRechercheUtilisateursUIService().deleteUsers(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate();
    }
}
