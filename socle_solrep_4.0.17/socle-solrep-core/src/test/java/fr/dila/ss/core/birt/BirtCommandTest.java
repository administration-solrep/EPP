package fr.dila.ss.core.birt;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RuntimeFeature;

@RunWith(FeaturesRunner.class)
@Features(RuntimeFeature.class)
@Deploy("fr.dila.ss.core")
@Deploy("org.nuxeo.ecm.platform.commandline.executor")
public class BirtCommandTest {

    @Test
    public void testBirtCommandAvailability() {
        CommandLineExecutorService cles = Framework.getService(CommandLineExecutorService.class);

        assertTrue(cles.getAvailableCommands().contains(BirtAppCommand.BIRTAPP_COMMAND));
    }
}
