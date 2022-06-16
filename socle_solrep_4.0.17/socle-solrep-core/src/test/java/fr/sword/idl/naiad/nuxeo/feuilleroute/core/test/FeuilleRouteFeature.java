package fr.sword.idl.naiad.nuxeo.feuilleroute.core.test;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ PlatformFeature.class, AutomationFeature.class })
@Deploy("fr.dila.ss.core")
public class FeuilleRouteFeature implements RunnerFeature {

    public FeuilleRouteFeature() {
        // do nothing
    }
}
