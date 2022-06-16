package fr.dila.st.core.proxy;

import fr.dila.st.core.logger.ChronoLog;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ChronoLog.class })
@PowerMockIgnore("javax.management.*")
public class ChronoLogServiceProxyTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private ChronoLog mockChronoLog;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(ChronoLog.class);
        mockChronoLog = Mockito.mock(ChronoLog.class);
    }

    @Test
    public void testInvoke() throws Throwable {
        Mockito.when(ChronoLog.getInstance()).thenReturn(mockChronoLog);

        ChronoLogServiceProxy<?> proxy = new ChronoLogServiceProxy<>(new TestChronoLog());
        Method method = TestChronoLog.class.getMethod("method", null);
        proxy.invoke(null, method, new Object[] {});

        Mockito.verify(mockChronoLog, Mockito.times(1)).enter(method);
        Mockito.verify(mockChronoLog, Mockito.times(1)).exit(Mockito.eq(method), Mockito.anyLong(), Mockito.eq(null));
    }

    @Test(expected = NuxeoException.class)
    public void testInvokeWithException() throws Throwable {
        Mockito.when(ChronoLog.getInstance()).thenReturn(mockChronoLog);

        ChronoLogServiceProxy<?> proxy = new ChronoLogServiceProxy<>(new TestChronoLog());
        Method method = TestChronoLog.class.getMethod("methodException", null);
        try {
            proxy.invoke(null, method, new Object[] {});
        } catch (Exception ex) {
            throw ex;
        } finally {
            Mockito.verify(mockChronoLog, Mockito.times(1)).enter(method);
            Mockito
                .verify(mockChronoLog, Mockito.times(1))
                .exit(Mockito.eq(method), Mockito.anyLong(), Mockito.any(NuxeoException.class));
        }
    }

    private class TestChronoLog {

        public void method() {}

        public void methodException() {
            throw new NuxeoException();
        }
    }
}
