package fr.dila.st.ui.jaxrs.webobject.pages.user;

import static fr.dila.st.ui.enums.STActionCategory.ADMIN_MENU_USER_EDIT;
import static fr.dila.st.ui.enums.STActionCategory.USER_MENU_USER_EDIT;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_LEVEL;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_URL;
import static fr.dila.st.ui.enums.STContextDataKey.USER_SEARCH_FORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.assertions.ResponseAssertions;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STRechercheUtilisateursUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.services.STUtilisateursUIService;
import fr.dila.st.ui.services.actions.STActionsServiceLocator;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UserSearchForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.constants.STURLConstants;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        STUIServiceLocator.class,
        UserSessionHelper.class,
        STServiceLocator.class,
        STActionsServiceLocator.class,
        URLUtils.class
    }
)
@PowerMockIgnore("javax.management.*")
public class TransverseUserTest {
    private static final int BREADCRUMB_BASE_LEVEL_VALUE = 1;
    private static final String BREADCRUMB_BASE_URL_VALUE = "base_url/user/";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    private static final String URL_PREVIOUS_PAGE = "page";
    private static final String USER_ID = "pmartin";

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private TransverseUser page;

    @Mock
    private WebContext webContext;

    @Mock
    private SpecificContext context;

    @Mock
    private HttpServletRequest request;

    @Mock
    private STUtilisateursUIService service;

    @Mock
    private CoreSession session;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private UserManager userManager;

    @Mock
    private DocumentModel userDoc;

    @Mock
    private STUserManagerUIService userManangerActionService;

    @Mock
    private STUserService stUserService;

    @Mock
    private STRechercheUtilisateursUIService rechercheUtilisateursUIService;

    @Captor
    private ArgumentCaptor<Breadcrumb> breadcrumbCaptor;

    private UserForm expectedDto;
    private List<Action> actions = new ArrayList<>();
    private Action action = new Action();

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getUserManager()).thenReturn(userManager);
        when(userManager.getUserModel(anyString())).thenReturn(userDoc);
        when(STServiceLocator.getSTUserService()).thenReturn(stUserService);

        page = new TransverseUser();

        Whitebox.setInternalState(page, "context", context);

        expectedDto = new UserForm("Martin", "Patrick", USER_ID, "", "");

        PowerMockito.mockStatic(STUIServiceLocator.class);
        when(STUIServiceLocator.getSTUtilisateursUIService()).thenReturn(service);
        when(service.getUtilisateur(any())).thenReturn(expectedDto);
        when(STUIServiceLocator.getRechercheUtilisateursUIService()).thenReturn(rechercheUtilisateursUIService);

        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());

        when(STUIServiceLocator.getSTUserManagerUIService()).thenReturn(userManangerActionService);

        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getActingUser()).thenReturn(USER_ID);
        when(context.getWebcontext()).thenReturn(webContext);
        when(context.getUrlPreviousPage()).thenReturn(URL_PREVIOUS_PAGE);
        when(context.getActions(any(STActionCategory.class))).thenReturn(actions);

        when(webContext.getRequest()).thenReturn(request);
        when(request.getQueryString()).thenReturn("");

        when(context.getFromContextData(BREADCRUMB_BASE_LEVEL)).thenReturn(BREADCRUMB_BASE_LEVEL_VALUE);
        when(context.getFromContextData(BREADCRUMB_BASE_URL)).thenReturn(BREADCRUMB_BASE_URL_VALUE);
    }

    @Test
    public void testGetCompte() {
        ThTemplate template = page.getCompte();

        assertUserGeneration(template);

        verify(context)
            .setNavigationContextTitle(
                new Breadcrumb(TransverseUser.COMPTE, BREADCRUMB_BASE_URL_VALUE, BREADCRUMB_BASE_LEVEL_VALUE)
            );
        verify(context).getActions(USER_MENU_USER_EDIT);
    }

    @Test
    public void testGetUser() {
        ThTemplate template = page.getUser(USER_ID);

        assertUserGeneration(template);

        verify(context)
            .setNavigationContextTitle(
                new Breadcrumb(expectedDto.getFullNameIdentifier(), BREADCRUMB_BASE_URL_VALUE + '/' + USER_ID, 3)
            );
        verify(context).getActions(ADMIN_MENU_USER_EDIT);
    }

    @Test
    public void testGetAdminEditUser() {
        when(userManangerActionService.getAllowEditUser(context)).thenReturn(true);

        ThTemplate template = page.getAdminEditUser(USER_ID);

        assertUserForm(template);

        verify(context)
            .setNavigationContextTitle(
                new Breadcrumb(TransverseUser.MODIFIER, BREADCRUMB_BASE_URL_VALUE + '/' + USER_ID + "/edit", 4)
            );
    }

    @Test
    public void testGetAdminEditUserKo() {
        when(userManangerActionService.getAllowEditUser(context)).thenReturn(false);

        Throwable throwable = catchThrowable(() -> page.getAdminEditUser(USER_ID));
        assertThat(throwable)
            .isInstanceOf(STAuthorizationException.class)
            .hasMessage("Accès à la ressource non autorisé : action édition utilisateur %s", USER_ID);

        verify(context).setCurrentDocument(userDoc);
        verifyNoMoreInteractions(context, service);
    }

    @Test
    public void testGetEditUser() {
        when(userManangerActionService.getAllowEditUser(context)).thenReturn(true);

        ThTemplate template = page.getEditUser();

        assertUserForm(template);

        verify(context)
            .setNavigationContextTitle(new Breadcrumb(TransverseUser.MODIFIER, BREADCRUMB_BASE_URL_VALUE + "/edit", 3));
        assertThat((boolean) template.getData().get("userEdit")).isTrue();
    }

    @Test
    public void testGetEditUserKo() {
        when(userManangerActionService.getAllowEditUser(context)).thenReturn(false);

        Throwable throwable = catchThrowable(() -> page.getEditUser());
        assertThat(throwable)
            .isInstanceOf(STAuthorizationException.class)
            .hasMessage("Accès à la ressource non autorisé : action édition utilisateur courant");

        verify(context).setCurrentDocument(userDoc);
        verify(context).getSession();
        verifyNoMoreInteractions(context, service);
    }

    @Test
    public void testUpdateUser() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(userManangerActionService.getAllowEditUser(context)).thenReturn(true);
        expectedDto.setUtilisateur("user");
        when(stUserService.getUser("user")).thenReturn(mock(STUser.class));

        PowerMockito.mockStatic(UserSessionHelper.class);
        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(null);

        Response response = page.updateUser(expectedDto);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(context).setCurrentDocument(userDoc);
        verify(context).putInContextData(STContextDataKey.USER_FORM, expectedDto);
        verify(userManangerActionService).getAllowEditUser(context);
        verify(userManangerActionService).updateUser(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(any(), anyString(), any());
    }

    @Test
    public void testUpdateUserKo() {
        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addErrorToQueue("error");
        when(context.getMessageQueue()).thenReturn(alertManager);
        when(userManangerActionService.getAllowEditUser(context)).thenReturn(true);

        PowerMockito.mockStatic(UserSessionHelper.class);
        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(null);

        Response response = page.updateUser(expectedDto);

        ResponseAssertions.assertResponseWithDangerMessages(response, Lists.newArrayList("error"));

        verify(context).setCurrentDocument(userDoc);
        verify(context).putInContextData(STContextDataKey.USER_FORM, expectedDto);
        verify(userManangerActionService).getAllowEditUser(context);
        verifyNoMoreInteractions(userManangerActionService);

        PowerMockito.verifyStatic(times(0));
        UserSessionHelper.putUserSessionParameter(any(), anyString(), any());
    }

    @Test
    public void testCreateUser() {
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getActingUser()).thenReturn(USER_ID);
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());
        when(context.getAction(STActionEnum.ADMIN_USER_NEW_USER)).thenReturn(new Action());
        expectedDto.setUtilisateur("user");

        PowerMockito.mockStatic(UserSessionHelper.class);
        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(null);

        Response response = page.createUser(expectedDto);

        ResponseAssertions.assertResponseWithoutMessages(response);

        verify(context).putInContextData(STContextDataKey.USER_CREATION, true);
        verify(context).putInContextData(STContextDataKey.USER_FORM, expectedDto);
        verify(userManangerActionService).initUserContext(context);
        verify(userManangerActionService).createUser(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(any(), anyString(), any());
    }

    @Test
    public void testCreateUserKo() {
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getActingUser()).thenReturn(USER_ID);
        SolonAlertManager alertManager = new SolonAlertManager();
        alertManager.addErrorToQueue("error");
        when(context.getMessageQueue()).thenReturn(alertManager);
        when(context.getAction(STActionEnum.ADMIN_USER_NEW_USER)).thenReturn(new Action());
        expectedDto.setUtilisateur("user");

        PowerMockito.mockStatic(UserSessionHelper.class);
        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(null);

        Response response = page.createUser(expectedDto);

        ResponseAssertions.assertResponseWithDangerMessages(response, Lists.newArrayList("error"));

        verify(context).putInContextData(STContextDataKey.USER_CREATION, true);
        verify(context).putInContextData(STContextDataKey.USER_FORM, expectedDto);
        verify(userManangerActionService).initUserContext(context);
        verify(userManangerActionService).createUser(context);

        PowerMockito.verifyStatic(times(0));
        UserSessionHelper.putUserSessionParameter(any(), anyString(), any());
    }

    @Test
    public void testCreateUserInvalidRights() {
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getActingUser()).thenReturn(USER_ID);
        when(context.getAction(STActionEnum.ADMIN_USER_NEW_USER)).thenReturn(null);
        expectedDto.setUtilisateur("user");

        Throwable throwable = catchThrowable(() -> page.createUser(expectedDto));
        assertThat(throwable)
            .isInstanceOf(STAuthorizationException.class)
            .hasMessage("Accès à la ressource non autorisé : action création d'un nouvel utilisateur");

        verify(context).getSession();
        verify(context).getAction(STActionEnum.ADMIN_USER_NEW_USER);
        verify(context).putInContextData(STContextDataKey.USER_ID, USER_ID);
        verify(userManangerActionService).initUserContext(context);
        verifyNoMoreInteractions(context, userManangerActionService);
    }

    @Test
    public void testAdminPasswordChange() {
        when(context.getFromContextData(BREADCRUMB_BASE_LEVEL)).thenReturn(1);
        when(context.getFromContextData(BREADCRUMB_BASE_URL)).thenReturn("");
        when(context.getAction(STActionEnum.ADMIN_USER_EDIT_PASSWORD_CHANGE)).thenReturn(action);

        ThTemplate template = page.adminPasswordChange(USER_ID);

        assertPasswordForm(template, false);

        verify(context).setNavigationContextTitle(breadcrumbCaptor.capture());
        assertEquals(TransverseUser.NOUVEAU_MDP, breadcrumbCaptor.getValue().getKey());
        assertEquals("/pmartin/passwordChange", breadcrumbCaptor.getValue().getUrl());
        assertEquals(new Integer(4), breadcrumbCaptor.getValue().getOrder());
        verify(context).getAction(STActionEnum.ADMIN_USER_EDIT_PASSWORD_CHANGE);
    }

    @Test
    public void testAdminPasswordChangeKo() {
        when(context.getAction(STActionEnum.ADMIN_USER_EDIT_PASSWORD_CHANGE)).thenReturn(null);

        Throwable throwable = catchThrowable(() -> page.adminPasswordChange(USER_ID));
        assertThat(throwable)
            .isInstanceOf(STAuthorizationException.class)
            .hasMessage(
                "Accès à la ressource non autorisé : action changement de mot de passe utilisateur %s",
                USER_ID
            );

        verify(userManangerActionService).initUserContext(context);
        verifyNoMoreInteractions(userManangerActionService);
    }

    @Test
    public void testUserPasswordChange() {
        when(context.getFromContextData(BREADCRUMB_BASE_LEVEL)).thenReturn(1);
        when(context.getFromContextData(BREADCRUMB_BASE_URL)).thenReturn("");
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(context.getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE)).thenReturn(action);
        when(principal.getActingUser()).thenReturn(USER_ID);

        ThTemplate template = page.userPasswordChange();

        assertPasswordForm(template, true);

        verify(context).setNavigationContextTitle(breadcrumbCaptor.capture());
        assertEquals(TransverseUser.NOUVEAU_MDP, breadcrumbCaptor.getValue().getKey());
        assertEquals("/passwordChange", breadcrumbCaptor.getValue().getUrl());
        assertEquals(new Integer(3), breadcrumbCaptor.getValue().getOrder());

        verify(context).getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE);
    }

    @Test
    public void testUserPasswordChangeKo() {
        when(context.getAction(STActionEnum.USER_EDIT_PASSWORD_CHANGE)).thenReturn(null);
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getActingUser()).thenReturn(USER_ID);

        Throwable throwable = catchThrowable(() -> page.userPasswordChange());
        assertThat(throwable)
            .isInstanceOf(STAuthorizationException.class)
            .hasMessage("Accès à la ressource non autorisé : action changement de mot de passe utilisateur courant");

        verify(userManangerActionService).initUserContext(context);
        verifyNoMoreInteractions(userManangerActionService);
    }

    @Test
    public void testGetRechercheUtilisateur() {
        List<UserForm> userForms = ImmutableList.of(
            new UserForm("Martin", "Patrick", "pmartin", "pmartin@email.com", "28/04/2021"),
            new UserForm("Martin", "Julie", "jmartin", "jmartin@email.com", "28/04/2021"),
            new UserForm("Martinet", "Francois", "fmartinet", "fmartinet@email.com", "28/04/2021")
        );
        STUsersList dto = new STUsersList();
        dto.setListe(userForms);

        when(rechercheUtilisateursUIService.searchUsers(context)).thenReturn(dto);

        UserSearchForm userSearchForm = new UserSearchForm();
        UsersListForm userListForm = new UsersListForm();
        userListForm.setUtilisateur(SortOrder.DESC);

        PowerMockito.mockStatic(UserSessionHelper.class);
        when(UserSessionHelper.getUserSessionParameter(eq(context), eq(STUserSessionKey.SEARCH_FORMS_KEY)))
            .thenReturn(userSearchForm);
        when(UserSessionHelper.getUserSessionParameter(eq(context), eq(STUserSessionKey.USER_LIST_FORM_KEY)))
            .thenReturn(userListForm);

        when(context.getAction(STActionEnum.ADMIN_USER_RECHERCHE)).thenReturn(new Action());

        Action deleteUserAction = new Action();
        Action addFavoriAction = new Action();
        Action addFavoriRechercheAction = new Action();
        List<Action> actionsLeft = new ArrayList<>();
        actionsLeft.add(deleteUserAction);
        actionsLeft.add(addFavoriAction);
        when(context.getActions(STActionCategory.USER_ACTION_LIST_LEFT)).thenReturn(actionsLeft);

        Action sendMailAction = new Action();
        List<Action> actionsRight = new ArrayList<>();
        actionsRight.add(sendMailAction);
        when(context.getActions(STActionCategory.USER_ACTION_LIST_RIGHT)).thenReturn(actionsRight);

        when(context.getAction(STActionEnum.ADD_USER_FAVORI_RECHERCHE)).thenReturn(addFavoriRechercheAction);

        ThTemplate template = page.getRechercheUtilisateurs();

        assertTemplate(template, "/pages/admin/user/searchUsers");

        assertThat(template.getData())
            .isNotNull()
            .containsOnly(
                entry(STTemplateConstants.SEARCH_USER_FORM, userSearchForm),
                entry(STTemplateConstants.RESULTAT_LIST, dto.getListe()),
                entry(STTemplateConstants.LST_COLONNES, dto.getListeColonnes(userListForm)),
                entry(STTemplateConstants.DATA_URL, STURLConstants.ADMIN_USER_SEARCH),
                entry(STTemplateConstants.NB_RESULTS, dto.getListe().size()),
                entry(STTemplateConstants.DATA_AJAX_URL, STURLConstants.AJAX_USER_SEARCH_RESULTS),
                entry(STTemplateConstants.USER_ACTION_LIST_LEFT, actionsLeft),
                entry(STTemplateConstants.USER_ACTION_LIST_RIGHT, actionsRight),
                entry(STTemplateConstants.DISPLAY_TABLE, true),
                entry(STTemplateConstants.ADD_FAVORI_RECHERCHE_ACTION, addFavoriRechercheAction)
            );

        verify(context)
            .setNavigationContextTitle(
                new Breadcrumb(
                    TransverseUser.FICHE_USER_SEARCH_LABEL,
                    STURLConstants.ADMIN_USER_SEARCH,
                    TransverseUser.USER_SEARCH_ORDER,
                    request
                )
            );
        verify(context).putInContextData(USER_SEARCH_FORM, userSearchForm);
        verify(context).putInContextData(STContextDataKey.GET_FULL_USER, true);
        verify(rechercheUtilisateursUIService).searchUsers(context);
    }

    private void assertUserGeneration(ThTemplate template) {
        assertTemplateWithData(template, TransverseUser.FICHE_USER_HTML, "ficheUser");

        assertThat(template.getData()).containsEntry(STTemplateConstants.EDIT_ACTIONS, actions);

        verify(userManangerActionService).initUserContext(context);
        verify(context).putInContextData(STContextDataKey.USER_ID, USER_ID);
    }

    private void assertUserForm(ThTemplate template) {
        assertTemplateWithData(template, TransverseUser.FORM_USER_HTML, "userForm");

        verify(userManangerActionService).getAllowEditUser(context);
        verify(context).setCurrentDocument(userDoc);
        verify(context).putInContextData(STContextDataKey.USER_ID, USER_ID);
    }

    private void assertPasswordForm(ThTemplate template, boolean expectedLogoutOnSuccess) {
        assertTemplateWithData(template, TransverseUser.FORM_PASSWORD_HTML, "userForm");

        assertThat(template.getData()).containsEntry("logoutOnSuccess", expectedLogoutOnSuccess);

        verify(context).putInContextData(STContextDataKey.USER_ID, USER_ID);
    }

    private void assertTemplateWithData(ThTemplate template, String expectedName, String formKey) {
        assertTemplate(template, expectedName);

        assertThat(template.getData()).containsEntry(formKey, expectedDto);
        assertThat(template.getData()).containsEntry(STTemplateConstants.URL_PREVIOUS_PAGE, URL_PREVIOUS_PAGE);
    }

    private void assertTemplate(ThTemplate template, String expectedName) {
        assertThat(template).isNotNull();
        assertThat(template.getName()).isEqualTo(expectedName);

        assertThat(template.getContext()).isEqualTo(context);

        assertThat(template.getData()).isNotNull();
    }
}
