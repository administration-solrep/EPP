package fr.dila.st.core.test;

import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.BlacklistComponent;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ PlatformFeature.class, AutomationFeature.class })
@Deploy("org.nuxeo.ecm.core.persistence")
@Deploy("fr.sword.naiad.nuxeo.ufnxql.core")
@Deploy("fr.dila.st.api")
@Deploy("fr.dila.st.core")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-datasource.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/service/test-organigramme.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-default-user-directory.xml")
@Deploy("fr.dila.st.core.test:OSGI-INF/service/test-config-contrib.xml")
@BlacklistComponent({ "fr.dila.st.core.datasources.contrib" })
public class STCommonFeature implements RunnerFeature {

    public STCommonFeature() {
        // do nothing
    }
}
