package fr.dila.st.core.test;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ STCommonFeature.class })
@Deploy("fr.dila.st.core.test:OSGI-INF/test-core-type-contrib.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-default-user-directory.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/service/test-config-contrib.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/cmf/cm-document-type-contrib.xml")
public class STFeature implements RunnerFeature {

    public STFeature() {
        // do nothing
    }
}
