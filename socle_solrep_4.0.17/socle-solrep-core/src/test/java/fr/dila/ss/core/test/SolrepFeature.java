package fr.dila.ss.core.test;

import fr.dila.st.core.test.STCommonFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ PlatformFeature.class, STCommonFeature.class })
@Deploy("fr.dila.ss.core")
public class SolrepFeature implements RunnerFeature {

    public SolrepFeature() {
        // Default constructor
    }
}
