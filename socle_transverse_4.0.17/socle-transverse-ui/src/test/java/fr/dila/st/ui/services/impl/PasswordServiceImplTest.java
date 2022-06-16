package fr.dila.st.ui.services.impl;

import static java.lang.Boolean.FALSE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import fr.dila.st.api.service.STProfilUtilisateurService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.test.STCommonFeature;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.services.PasswordService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ STCommonFeature.class, SolonMockitoFeature.class })
public class PasswordServiceImplTest {
    private static final String CURRENT_USERNAME = "username";
    private static final String PASSWORD_RESET_KEY = "isPasswordReset";

    private PasswordService service;
    private UserSession userSession;

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession httpSession;

    @Mock
    private CoreSession coreSession;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    @RuntimeService
    private STProfilUtilisateurService profilUtilisateurService;

    @Mock
    @RuntimeService
    private STUserService userService;

    @Before
    public void setUp() {
        service = new PasswordServiceImpl();
        userSession = UserSession.getCurrentSession(request);

        when(context.getWebcontext()).thenReturn(webContext);
        when(request.getSession(false)).thenReturn(httpSession);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getSession()).thenReturn(coreSession);
        when(coreSession.getPrincipal()).thenReturn(principal);
        when(coreSession.getPrincipal().getName()).thenReturn(CURRENT_USERNAME);
    }

    @Test
    public void getData() {
        when(profilUtilisateurService.isUserPasswordOutdated(coreSession, CURRENT_USERNAME)).thenReturn(false);
        when(userService.isUserPasswordResetNeeded(CURRENT_USERNAME)).thenReturn(false);

        Map<String, Object> result = service.getData(context);

        assertThat((Boolean) result.get(PASSWORD_RESET_KEY)).isFalse();
        verify(userService, never()).forceChangeOutdatedPassword(CURRENT_USERNAME);
    }

    @Test
    public void getDataWithPasswordOutdated() {
        when(profilUtilisateurService.isUserPasswordOutdated(coreSession, CURRENT_USERNAME)).thenReturn(true);
        when(userService.isUserPasswordResetNeeded(CURRENT_USERNAME)).thenReturn(true);

        Map<String, Object> result = service.getData(context);

        assertThat((Boolean) result.get(PASSWORD_RESET_KEY)).isTrue();
        verify(userService).forceChangeOutdatedPassword(CURRENT_USERNAME);
    }

    @Test
    public void getDataWithParameterInSession() {
        userSession.put(PASSWORD_RESET_KEY, FALSE);

        Map<String, Object> result = service.getData(context);

        assertThat((Boolean) result.get(PASSWORD_RESET_KEY)).isFalse();
        verifyZeroInteractions(profilUtilisateurService, userService);
    }

    @Test
    public void updatePasswordIsOk() {
        String newPassword = "newPassword";
        when(context.getFromContextData(STContextDataKey.USER_ID)).thenReturn(CURRENT_USERNAME);
        when(userService.saveNewUserPassword(CURRENT_USERNAME, newPassword)).thenReturn(newPassword);

        boolean result = service.updatePassword(context, newPassword);

        assertThat(result).isTrue();
        assertThat((Boolean) userSession.get(PASSWORD_RESET_KEY)).isFalse();

        verifyZeroInteractions(profilUtilisateurService);
    }

    @Test
    public void updatePasswordIsKo() {
        String newPassword = "newPassword";
        when(context.getFromContextData(STContextDataKey.USER_ID)).thenReturn(CURRENT_USERNAME);
        when(userService.saveNewUserPassword(CURRENT_USERNAME, newPassword)).thenReturn(null);

        boolean result = service.updatePassword(context, newPassword);

        assertThat(result).isFalse();
        assertThat((Boolean) userSession.get(PASSWORD_RESET_KEY)).isNull();

        verifyZeroInteractions(profilUtilisateurService);
    }
}
