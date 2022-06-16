package fr.dila.st.ui.helper;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ WebEngine.class, Framework.class })
@PowerMockIgnore("javax.management.*")
public class UserSessionHelperTest {
    private static final String USER_SESSION_PARAMETER_KEY = "TEST_KEY";
    private static final String USER_SESSION_PARAMETER_STRING_VALUE = "test value";
    private static final List<Integer> USER_SESSION_PARAMETER_LIST_VALUE = newArrayList(1, 2, 5, 9);

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private SpecificContext context;

    @Mock
    private WebContext webContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    private UserSession userSession;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(WebEngine.class);

        userSession = UserSession.getCurrentSession(request);

        when(request.getSession(false)).thenReturn(session);

        when(context.getWebcontext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);
    }

    @Test
    public void getUserSessionParameterWithStringParameter() {
        userSession.put(USER_SESSION_PARAMETER_KEY, USER_SESSION_PARAMETER_STRING_VALUE);

        String parameterValue = UserSessionHelper.getUserSessionParameter(
            context,
            USER_SESSION_PARAMETER_KEY,
            String.class
        );
        assertThat(parameterValue).isEqualTo(USER_SESSION_PARAMETER_STRING_VALUE);
    }

    @Test
    public void getUserSessionParameterWithListParameter() {
        userSession.put(USER_SESSION_PARAMETER_KEY, USER_SESSION_PARAMETER_LIST_VALUE);

        List<Integer> parameterValues = UserSessionHelper.getUserSessionParameter(context, USER_SESSION_PARAMETER_KEY);
        assertThat(parameterValues).isEqualTo(USER_SESSION_PARAMETER_LIST_VALUE);
    }

    @Test
    public void getUserSessionParameterWithNullParameter() {
        String parameterValue = UserSessionHelper.getUserSessionParameter(context, null, String.class);

        assertThat(parameterValue).isNull();
    }

    @Test
    public void putUserSessionParameterWithStringParameter() {
        UserSessionHelper.putUserSessionParameter(
            context,
            USER_SESSION_PARAMETER_KEY,
            USER_SESSION_PARAMETER_STRING_VALUE
        );

        assertThat(userSession).containsExactly(entry(USER_SESSION_PARAMETER_KEY, USER_SESSION_PARAMETER_STRING_VALUE));
    }

    @Test
    public void putUserSessionParameterWithListParameter() {
        UserSessionHelper.putUserSessionParameter(
            context,
            USER_SESSION_PARAMETER_KEY,
            USER_SESSION_PARAMETER_LIST_VALUE
        );

        assertThat(userSession).containsExactly(entry(USER_SESSION_PARAMETER_KEY, USER_SESSION_PARAMETER_LIST_VALUE));
    }
}
