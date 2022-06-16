package fr.dila.st.ui.core.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.service.STServiceLocator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ STServiceLocator.class })
public class SolonWebengineFormAuthTest {
    private static final String USERNAME = "myUser";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SolonWebengineFormAuth auth = new SolonWebengineFormAuth();

    @Mock
    private HttpServletRequest mockedRequest;

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private HttpServletResponse mockedResponse;

    @Mock
    private STUserService mockUserService;

    @Captor
    private ArgumentCaptor<String> stringCaptor;

    @Before
    public void before() {
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getSTUserService()).thenReturn(mockUserService);
        when(mockUserService.isMigratedUser(USERNAME)).thenReturn(true);
    }

    @Test
    public void testInit() {
        assertEquals("/login", auth.getLoginPage());

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("LoginPageKey", "paramlogin");
        auth.initPlugin(parameters);
        assertEquals("paramlogin", auth.getLoginPage());
    }

    @Test
    public void testGetLoginPath() {
        assertEquals("home", auth.getLoginPathInfo(mockedRequest));
    }

    @Test
    public void testHandleLoginPrompt() throws Exception {
        Vector<String> elts = new Vector<String>();

        when(mockedRequest.getParameterNames()).thenReturn(elts.elements());
        String baseUrl = "http://reponses/";
        assertTrue(auth.handleLoginPrompt(mockedRequest, mockedResponse, baseUrl));
        String location = "http://reponses/site/app-ui/login#main_content";
        verify(mockedResponse).sendRedirect(location);
    }

    /**
     * Un utilisateur migré doit être redirigé vers la page de demande de nouveau mot de passe.
     */
    @Test
    public void testOnError_migratedUser() throws IOException {
        when(mockedRequest.getParameter("username")).thenReturn(USERNAME);

        auth.onError(mockedRequest, mockedResponse);

        verify(mockedResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(mockedResponse).sendRedirect(stringCaptor.capture());
        String redirectUrl = stringCaptor.getValue();
        assertThat(redirectUrl).matches("^reinitMdp\\?username=" + USERNAME + "&r=.+$");
    }
}
