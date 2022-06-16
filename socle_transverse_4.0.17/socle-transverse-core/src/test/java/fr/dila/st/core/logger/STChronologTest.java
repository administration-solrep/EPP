package fr.dila.st.core.logger;

import static org.junit.Assert.assertEquals;

import fr.dila.st.core.feature.SolonMockitoFeature;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.ThreadContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ SolonMockitoFeature.class })
public class STChronologTest {
    @Mock
    private Log log;

    @Test
    public void testEnter() {
        ChronoLog cr = ChronoLog.getInstance();
        cr.enter("Test");

        assertEquals("IN", ThreadContext.get("log_sens"));
        assertEquals("OK", ThreadContext.get("log_status"));
        assertEquals("", ThreadContext.get("log_temps_exec"));
        assertEquals("", ThreadContext.get("log_comp"));
    }

    @Test
    public void testExit() {
        long time = TimeUnit.MILLISECONDS.toNanos(1);
        ChronoLog cr = ChronoLog.getInstance();
        cr.exit("Test", time, true, "Test");

        assertEquals("OUT", ThreadContext.get("log_sens"));
        assertEquals("OK", ThreadContext.get("log_status"));
        assertEquals(String.valueOf(time), ThreadContext.get("log_temps_exec"));
        assertEquals("Test", ThreadContext.get("log_comp"));
    }
}
