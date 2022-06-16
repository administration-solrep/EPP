package fr.dila.solonepp.core;

import fr.dila.st.core.test.STCommonFeature;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.BlacklistComponent;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ PlatformFeature.class, AutomationFeature.class, STCommonFeature.class })
@Deploy("fr.dila.solonepp.core")
@BlacklistComponent({ "fr.dila.st.core.datasources.contrib", "fr.dila.st.core.test.datasourceContrib" })
public class SolonEppFeature implements RunnerFeature {}
