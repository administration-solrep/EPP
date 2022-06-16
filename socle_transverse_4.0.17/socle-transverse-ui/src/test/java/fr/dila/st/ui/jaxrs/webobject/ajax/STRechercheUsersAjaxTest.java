package fr.dila.st.ui.jaxrs.webobject.ajax;

import static fr.dila.st.ui.enums.STContextDataKey.GET_FULL_USER;
import static fr.dila.st.ui.enums.STContextDataKey.USERS_LIST_FORM;
import static fr.dila.st.ui.enums.STContextDataKey.USER_SEARCH_FORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.st.ui.assertions.ResponseAssertions;
import fr.dila.st.ui.bean.STUsersList;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STRechercheUtilisateursUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.UserForm;
import fr.dila.st.ui.th.bean.UserSearchForm;
import fr.dila.st.ui.th.bean.UsersListForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.constants.STURLConstants;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STUIServiceLocator.class, UserSessionHelper.class })
public class STRechercheUsersAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private STRechercheUsersAjax page;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private STRechercheUtilisateursUIService rechercheUtilisateursUIService;

    @Before
    public void before() {
        PowerMockito.mockStatic(STUIServiceLocator.class);
        when(STUIServiceLocator.getRechercheUtilisateursUIService()).thenReturn(rechercheUtilisateursUIService);

        PowerMockito.mockStatic(UserSessionHelper.class);

        page = new STRechercheUsersAjax();

        Whitebox.setInternalState(page, "context", context);

        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getRequest()).thenReturn(request);
    }

    @Test
    public void testReinitUserSearch() {
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());

        Response response = page.reinitUserSearch();

        ResponseAssertions.assertResponseWithoutMessages(response);

        PowerMockito.verifyStatic();
        UserSessionHelper.clearUserSessionParameter(context, STUserSessionKey.SEARCH_FORMS_KEY);
        UserSessionHelper.clearUserSessionParameter(context, STUserSessionKey.USER_LIST_FORM_KEY);
    }

    @Test
    public void testGetUserSearch() {
        List<UserForm> userForms = ImmutableList.of(
            new UserForm("Martin", "Patrick", "pmartin", "pmartin@email.com", "28/04/2021"),
            new UserForm("Martin", "Julie", "jmartin", "jmartin@email.com", "28/04/2021"),
            new UserForm("Martinet", "Francois", "fmartinet", "fmartinet@email.com", "28/04/2021")
        );

        STUsersList dto = new STUsersList();
        dto.setListe(userForms);

        when(rechercheUtilisateursUIService.searchUsers(context)).thenReturn(dto);

        Action deleteUserAction = new Action();
        Action addFavoriAction = new Action();
        List<Action> actionsLeft = new ArrayList<>();
        actionsLeft.add(deleteUserAction);
        actionsLeft.add(addFavoriAction);
        when(context.getActions(STActionCategory.USER_ACTION_LIST_LEFT)).thenReturn(actionsLeft);

        Action sendMailAction = new Action();
        List<Action> actionsRight = new ArrayList<>();
        actionsRight.add(sendMailAction);
        when(context.getActions(STActionCategory.USER_ACTION_LIST_RIGHT)).thenReturn(actionsRight);

        UserSearchForm form = new UserSearchForm();
        UsersListForm formTri = new UsersListForm();
        formTri.setUtilisateur(SortOrder.DESC);
        form.setNom("martin");

        ThTemplate template = page.getUserSearch(form, formTri);

        assertThat(template).isNotNull();
        assertThat(template.getName()).isEqualTo("fragments/components/result-list-user");
        assertThat(template.getContext()).isEqualTo(context);

        assertThat(template.getData())
            .isNotNull()
            .containsOnly(
                entry(STTemplateConstants.RESULTAT_LIST, dto.getListe()),
                entry(STTemplateConstants.LST_COLONNES, dto.getListeColonnes(formTri)),
                entry(STTemplateConstants.DATA_URL, STURLConstants.ADMIN_USER_SEARCH),
                entry(STTemplateConstants.NB_RESULTS, dto.getListe().size()),
                entry(STTemplateConstants.DATA_AJAX_URL, STURLConstants.AJAX_USER_SEARCH_RESULTS),
                entry(STTemplateConstants.USER_ACTION_LIST_LEFT, actionsLeft),
                entry(STTemplateConstants.USER_ACTION_LIST_RIGHT, actionsRight),
                entry(STTemplateConstants.DISPLAY_TABLE, true)
            );

        verify(context).putInContextData(USER_SEARCH_FORM, form);
        verify(context).putInContextData(USERS_LIST_FORM, formTri);
        verify(context).putInContextData(GET_FULL_USER, false);

        verify(rechercheUtilisateursUIService).searchUsers(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(context, STUserSessionKey.SEARCH_FORMS_KEY, form);
        UserSessionHelper.putUserSessionParameter(context, STUserSessionKey.USER_LIST_FORM_KEY, formTri);
    }
}
