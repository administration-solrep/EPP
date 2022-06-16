package fr.dila.ss.core.listener;

import static fr.dila.ss.core.listener.SSSessionListener.UNAUTH_USER;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.logger.STLoggerImpl;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.web.common.session.NuxeoHttpSessionMonitor;
import org.nuxeo.ecm.platform.web.common.session.SessionInfo;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ NuxeoHttpSessionMonitor.class, STLogFactory.class })
@PowerMockIgnore("javax.management.*")
public class SSSessionListenerTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSSessionListener listener;

    @Mock
    private NuxeoHttpSessionMonitor mockSessionMonitor;

    @Mock
    private HttpSessionEvent mockSessionEvent;

    @Mock
    private HttpSession mockSession;

    @Spy
    private STLogger logger = new STLoggerImpl(SSSessionListener.class);

    @Before
    public void before() {
        listener = new SSSessionListener();

        initMocks(this);

        when(mockSessionEvent.getSession()).thenReturn(mockSession);

        mockStatic(NuxeoHttpSessionMonitor.class);
        when(NuxeoHttpSessionMonitor.instance()).thenReturn(mockSessionMonitor);

        mockStatic(STLogFactory.class);
        when(STLogFactory.getLog(eq(SSSessionListener.class))).thenReturn(logger);
    }

    @Test
    public void testSessionDestroyedUnAuthenticated() {
        SessionInfo si = new SessionInfo("1");
        si.setLoginName(UNAUTH_USER);
        when(mockSessionMonitor.associatedUser(eq(mockSession), eq(UNAUTH_USER))).thenReturn(si);
        listener.sessionDestroyed(mockSessionEvent);
        verifyZeroInteractions(logger);
    }
    // On ne peut pas tester les autres cas dans un TU car on a beosin du runtime => IT

}
