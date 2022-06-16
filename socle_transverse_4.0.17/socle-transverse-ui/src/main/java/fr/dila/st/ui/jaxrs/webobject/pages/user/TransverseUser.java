package fr.dila.st.ui.jaxrs.webobject.pages.user;

import static fr.dila.st.core.service.STServiceLocator.getSTUserService;
import static fr.dila.st.core.service.STServiceLocator.getUserManager;
import static fr.dila.st.ui.enums.STActionCategory.ADMIN_MENU_USER_EDIT;
import static fr.dila.st.ui.enums.STActionCategory.USER_MENU_USER_EDIT;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_LEVEL;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_URL;
import static fr.dila.st.ui.enums.STContextDataKey.USER_ID;
import static fr.dila.st.ui.enums.STContextDataKey.USER_SEARCH_FORM;
import static fr.dila.st.ui.services.STUIServiceLocator.getSTUserManagerUIService;
import static fr.dila.st.ui.services.STUIServiceLocator.getSTUtilisateursUIService;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.util.PermissionHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.STOptinOptions;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UserSearchForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.constants.STURLConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "TransverseUser")
public class TransverseUser extends AbstractUserObject {
    public static final String FICHE_USER_HTML = "pages/admin/users/user";
    public static final String FORM_USER_HTML = "pages/admin/users/userForm";
    public static final String FORM_PASSWORD_HTML = "pages/admin/users/userPassword";

    private static final String USER_PARAM = "id";
    public static final String COMPTE = "Compte";
    public static final String MODIFIER = "Modifier";
    public static final String NOUVEAU_MDP = "Nouveau mot de passe";
    public static final String NOUVEL_UTILISATEUR = "Ajouter un utilisateur";

    public static final String NAVIGATION_TITLE_MODIFICATION = "Modification utilisateur";

    public static final int USER_SEARCH_ORDER = Breadcrumb.TITLE_ORDER;
    public static final String FICHE_USER_SEARCH_LABEL = "Recherche d'utilisateurs";

    public static final ImmutableMap<String, String> TEMPORAIRE_OPTIONS = ImmutableMap.of(
        "oui",
        "choix.oui",
        "non",
        "choix.non"
    );

    @GET
    public ThTemplate getCompte() {
        int breadcrumbLevel = context.getFromContextData(BREADCRUMB_BASE_LEVEL);
        String breadcrumbUrl = context.getFromContextData(BREADCRUMB_BASE_URL);
        context.setNavigationContextTitle(new Breadcrumb(COMPTE, breadcrumbUrl, breadcrumbLevel));

        init(context.getSession().getPrincipal().getActingUser());

        template.getData().put(STTemplateConstants.EDIT_ACTIONS, context.getActions(USER_MENU_USER_EDIT));

        return userGeneration();
    }

    @GET
    @Path("{id}")
    public ThTemplate getUser(@PathParam(USER_PARAM) String userId) {
        init(userId);

        UserForm dto = STUIServiceLocator.getSTUtilisateursUIService().getUtilisateur(context);

        int breadcrumbLevel = context.getFromContextData(BREADCRUMB_BASE_LEVEL);
        breadcrumbLevel += 2;
        String breadcrumbUrl = context.getFromContextData(BREADCRUMB_BASE_URL);
        breadcrumbUrl += "/" + userId;
        context.setNavigationContextTitle(new Breadcrumb(dto.getFullNameIdentifier(), breadcrumbUrl, breadcrumbLevel));
        template.getData().put(STTemplateConstants.EDIT_ACTIONS, context.getActions(ADMIN_MENU_USER_EDIT));

        return userGeneration();
    }

    @GET
    @Path("{id}/edit")
    public ThTemplate getAdminEditUser(@PathParam(USER_PARAM) String userId) {
        context.setCurrentDocument(getUserManager().getUserModel(userId));
        if (!getSTUserManagerUIService().getAllowEditUser(context)) {
            throw new STAuthorizationException("action édition utilisateur " + userId);
        }

        int breadcrumbLevel = context.getFromContextData(BREADCRUMB_BASE_LEVEL);
        breadcrumbLevel += 3;
        String breadcrumbUrl = context.getFromContextData(BREADCRUMB_BASE_URL);
        breadcrumbUrl += String.format("/%s/edit", userId);
        context.setNavigationContextTitle(new Breadcrumb(MODIFIER, breadcrumbUrl, breadcrumbLevel));

        if (
            !PermissionHelper.isAdminMinisteriel(context.getSession().getPrincipal()) &&
            !PermissionHelper.isAdminFonctionnel(context.getSession().getPrincipal())
        ) {
            template.getData().put("userEdit", true);
        }

        return userForm(userId);
    }

    @GET
    @Path("edit")
    public ThTemplate getEditUser() {
        NuxeoPrincipal principal = context.getSession().getPrincipal();
        String userId = principal.getActingUser();
        context.setCurrentDocument(getUserManager().getUserModel(userId));
        if (!getSTUserManagerUIService().getAllowEditUser(context)) {
            throw new STAuthorizationException("action édition utilisateur courant");
        }

        int breadcrumbLevel = context.getFromContextData(BREADCRUMB_BASE_LEVEL);
        breadcrumbLevel += 2;
        String breadcrumbUrl = context.getFromContextData(BREADCRUMB_BASE_URL);
        breadcrumbUrl += "/edit";
        context.setNavigationContextTitle(new Breadcrumb(MODIFIER, breadcrumbUrl, breadcrumbLevel));

        if (!PermissionHelper.isAdminMinisteriel(principal) && !PermissionHelper.isAdminFonctionnel(principal)) {
            template.getData().put("userEdit", true);
        }

        return userForm(userId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("modify")
    public Response updateUser(@SwBeanParam UserForm userForm) {
        context.setCurrentDocument(getUserManager().getUserModel(userForm.getUtilisateur()));
        if (!getSTUserManagerUIService().getAllowEditUser(context)) {
            throw new STAuthorizationException("action édition utilisateur " + userForm.getUtilisateur());
        }

        context.putInContextData(STContextDataKey.USER_FORM, userForm);
        getSTUtilisateursUIService().validateUserForm(context);

        if (context.getMessageQueue().getErrorQueue().isEmpty()) {
            STUser user = getSTUserService().getUser(userForm.getUtilisateur());
            context.setCurrentDocument(user.getDocument());

            getSTUserManagerUIService().updateUser(context);
        }

        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@SwBeanParam UserForm userForm) {
        init(context.getSession().getPrincipal().getActingUser());
        if (context.getAction(STActionEnum.ADMIN_USER_NEW_USER) == null) {
            throw new STAuthorizationException("action création d'un nouvel utilisateur");
        }

        context.putInContextData(STContextDataKey.USER_CREATION, true);
        context.putInContextData(STContextDataKey.USER_FORM, userForm);
        getSTUserManagerUIService().createUser(context);

        if (CollectionUtils.isEmpty(context.getMessageQueue().getErrorQueue())) {
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
        }

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), userForm.getUtilisateur()).build();
    }

    @GET
    @Path("{id}/passwordChange")
    public ThTemplate adminPasswordChange(@PathParam(USER_PARAM) String userId) {
        if (StringUtils.isBlank(userId)) {
            throw new STValidationException("form.validation.username.notblank");
        }

        init(userId);

        if (context.getAction(STActionEnum.ADMIN_USER_EDIT_PASSWORD_CHANGE) == null) {
            throw new STAuthorizationException("action changement de mot de passe utilisateur " + userId);
        }

        int breadcrumbLevel = context.getFromContextData(BREADCRUMB_BASE_LEVEL);
        breadcrumbLevel += 3;
        String breadcrumbUrl = context.getFromContextData(BREADCRUMB_BASE_URL);
        breadcrumbUrl += String.format("/%s/passwordChange", userId);
        context.setNavigationContextTitle(new Breadcrumb(NOUVEAU_MDP, breadcrumbUrl, breadcrumbLevel));

        template.getData().put("logoutOnSuccess", false);

        return passwordForm();
    }

    @GET
    @Path("passwordChange")
    public ThTemplate userPasswordChange() {
        init(context.getSession().getPrincipal().getActingUser());

        if (context.getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE) == null) {
            throw new STAuthorizationException("action changement de mot de passe utilisateur courant");
        }

        int breadcrumbLevel = context.getFromContextData(BREADCRUMB_BASE_LEVEL);
        breadcrumbLevel += 2;
        String breadcrumbUrl = context.getFromContextData(BREADCRUMB_BASE_URL);
        breadcrumbUrl += "/passwordChange";
        context.setNavigationContextTitle(new Breadcrumb(NOUVEAU_MDP, breadcrumbUrl, breadcrumbLevel));

        template.getData().put("logoutOnSuccess", true);

        return passwordForm();
    }

    @GET
    @Path("newUser")
    public ThTemplate newUserForm() {
        init(context.getSession().getPrincipal().getActingUser());
        if (context.getAction(STActionEnum.ADMIN_USER_NEW_USER) == null) {
            throw new STAuthorizationException("action création d'un nouvel utilisateur");
        }

        int breadcrumbLevel = context.getFromContextData(BREADCRUMB_BASE_LEVEL);
        breadcrumbLevel++;
        String breadcrumbUrl = context.getFromContextData(BREADCRUMB_BASE_URL);
        breadcrumbUrl += "/newUser";
        context.setNavigationContextTitle(new Breadcrumb(NOUVEL_UTILISATEUR, breadcrumbUrl, breadcrumbLevel));

        template.getData().put("userCreation", true);

        return userForm(null);
    }

    private ThTemplate userForm(String userId) {
        context.putInContextData(USER_ID, userId);
        UserForm dto = STUIServiceLocator.getSTUtilisateursUIService().getUtilisateur(context);

        if (StringUtils.isEmpty(dto.getCivilite())) {
            dto.setCivilite("Madame");
        }

        if (StringUtils.isEmpty(dto.getTemporaire())) {
            dto.setTemporaire("non");
        }

        template.getData().put("userForm", dto);
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        template.getData().put("civiliteOptions", STOptinOptions.CIVILITE_OPTIONS);
        template.getData().put("temporaireOptions", TEMPORAIRE_OPTIONS);
        template.setContext(context);
        template.setName(FORM_USER_HTML);

        return template;
    }

    private ThTemplate passwordForm() {
        UserForm dto = STUIServiceLocator.getSTUtilisateursUIService().getUtilisateur(context);

        template.getData().put("userForm", dto);
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        template.setName(FORM_PASSWORD_HTML);
        template.setContext(context);

        return template;
    }

    private ThTemplate userGeneration() {
        UserForm dto = STUIServiceLocator.getSTUtilisateursUIService().getUtilisateur(context);

        template.setContext(context);
        template.setName(FICHE_USER_HTML);

        template.getData().put("ficheUser", dto);
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        return template;
    }

    @GET
    @Path("rechercher")
    public ThTemplate getRechercheUtilisateurs() {
        if (context.getAction(STActionEnum.ADMIN_USER_RECHERCHE) == null) {
            throw new STAuthorizationException(STURLConstants.ADMIN_USER_SEARCH);
        }

        return generateRechercheUtilisateursTemplate(context, template, STURLConstants.ADMIN_USER_SEARCH);
    }

    public ThTemplate generateRechercheUtilisateursTemplate(SpecificContext context, ThTemplate template, String url) {
        context.setNavigationContextTitle(
            new Breadcrumb(FICHE_USER_SEARCH_LABEL, url, USER_SEARCH_ORDER, context.getWebcontext().getRequest())
        );
        template.setContext(context);
        template.setName("/pages/admin/user/searchUsers");

        UserSearchForm userSearchForm = UserSessionHelper.getUserSessionParameter(
            context,
            STUserSessionKey.SEARCH_FORMS_KEY
        );

        context.putInContextData(USER_SEARCH_FORM, userSearchForm);
        STUsersList dto;
        UsersListForm userListForm = null;

        if (userSearchForm == null) {
            dto = new STUsersList(false);
            template.getData().put(STTemplateConstants.DISPLAY_TABLE, false);
            userSearchForm = new UserSearchForm();
        } else {
            context.putInContextData(STContextDataKey.GET_FULL_USER, true);
            userListForm = UserSessionHelper.getUserSessionParameter(context, STUserSessionKey.USER_LIST_FORM_KEY);
            context.putInContextData(STContextDataKey.USERS_LIST_FORM, userListForm);
            dto = STUIServiceLocator.getRechercheUtilisateursUIService().searchUsers(context);
            template.getData().put(STTemplateConstants.DISPLAY_TABLE, true);
        }

        return buildMapResultUser(dto, userSearchForm, userListForm);
    }

    protected ThTemplate buildMapResultUser(
        STUsersList dto,
        UserSearchForm userSearchForm,
        UsersListForm userListForm
    ) {
        template.getData().put(STTemplateConstants.SEARCH_USER_FORM, userSearchForm);
        template.getData().put(STTemplateConstants.RESULTAT_LIST, dto.getListe());
        template.getData().put(STTemplateConstants.LST_COLONNES, dto.getListeColonnes(userListForm));
        template.getData().put(STTemplateConstants.DATA_URL, STURLConstants.ADMIN_USER_SEARCH);
        template.getData().put(STTemplateConstants.NB_RESULTS, dto.getListe().size());
        template.getData().put(STTemplateConstants.DATA_AJAX_URL, STURLConstants.AJAX_USER_SEARCH_RESULTS);
        template
            .getData()
            .put(STTemplateConstants.USER_ACTION_LIST_LEFT, context.getActions(STActionCategory.USER_ACTION_LIST_LEFT));
        template
            .getData()
            .put(
                STTemplateConstants.USER_ACTION_LIST_RIGHT,
                context.getActions(STActionCategory.USER_ACTION_LIST_RIGHT)
            );
        template
            .getData()
            .put(
                STTemplateConstants.ADD_FAVORI_RECHERCHE_ACTION,
                context.getAction(STActionEnum.ADD_USER_FAVORI_RECHERCHE)
            );
        context.putInContextData(STTemplateConstants.RESULT_FORM, new UsersListForm());
        template.setContext(context);

        return template;
    }
}
