package fr.dila.st.core.test;

import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ CoreFeature.class })
@Deploy("org.nuxeo.runtime.datasource")
@Deploy("org.nuxeo.runtime.metrics")
@Deploy("org.nuxeo.ecm.core.persistence")
@Deploy("org.nuxeo.ecm.platform.audit")
@Deploy("fr.dila.st.core.test:OSGI-INF/test-datasource.xml")
public class STAuditFeature implements RunnerFeature {

    public STAuditFeature() {
        // do nothing
    }
}
