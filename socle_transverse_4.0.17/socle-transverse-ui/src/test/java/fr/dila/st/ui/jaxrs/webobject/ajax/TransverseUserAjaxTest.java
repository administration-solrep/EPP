package fr.dila.st.ui.jaxrs.webobject.ajax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.services.STUtilisateursUIService;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.utils.URLUtils;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STUIServiceLocator.class, UserSessionHelper.class, STServiceLocator.class, URLUtils.class })
@PowerMockIgnore("javax.management.*")
public class TransverseUserAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    private static final String URL_PREVIOUS_PAGE = "page";
    private static final String USER_ID = "pmartin";

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private TransverseUserAjax page;

    @Mock
    private WebContext webContext;

    @Mock
    private SpecificContext context;

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

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getUserManager()).thenReturn(userManager);
        when(userManager.getUserModel(anyString())).thenReturn(userDoc);
        when(STServiceLocator.getSTUserService()).thenReturn(stUserService);

        page = new TransverseUserAjax();

        Whitebox.setInternalState(page, "context", context);

        PowerMockito.mockStatic(STUIServiceLocator.class);
        when(STUIServiceLocator.getSTUtilisateursUIService()).thenReturn(service);
        when(STUIServiceLocator.getSTUserManagerUIService()).thenReturn(userManangerActionService);

        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());

        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.getActingUser()).thenReturn(USER_ID);
        when(context.getWebcontext()).thenReturn(webContext);
        when(context.getUrlPreviousPage()).thenReturn(URL_PREVIOUS_PAGE);
    }

    @Test
    public void testDeleteUser() throws URISyntaxException {
        when(userManangerActionService.getAllowDeleteUser(context)).thenReturn(true);
        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());

        PowerMockito.mockStatic(UserSessionHelper.class);
        when(UserSessionHelper.getUserSessionParameter(any(), anyString(), any())).thenReturn(null);

        String redirectUrl = "http://host.fr/users/" + URL_PREVIOUS_PAGE;
        PowerMockito.mockStatic(URLUtils.class);
        when(URLUtils.generateRedirectPath(eq(URL_PREVIOUS_PAGE), any(HttpServletRequest.class)))
            .thenReturn(redirectUrl);

        Response response = page.deleteUser(USER_ID);

        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());

        verify(context).setCurrentDocument(userDoc);
        verify(userManangerActionService).getAllowDeleteUser(context);
        verify(userManangerActionService).deleteUser(context);

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(any(), anyString(), any());
    }

    @Test
    public void testDeleteUserKo() {
        when(userManangerActionService.getAllowDeleteUser(context)).thenReturn(false);

        Throwable throwable = catchThrowable(() -> page.deleteUser(USER_ID));
        assertThat(throwable)
            .isInstanceOf(STAuthorizationException.class)
            .hasMessage("Accès à la ressource non autorisé : action suppression utilisateur %s", USER_ID);

        verify(context).setCurrentDocument(userDoc);
        verify(userManangerActionService).getAllowDeleteUser(context);
        verifyNoMoreInteractions(userManangerActionService);
    }
}
