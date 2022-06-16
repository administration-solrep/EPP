package fr.sword.naiad.nuxeo.ufnxql.core.query;

import org.nuxeo.ecm.core.test.CoreFeature;

import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.RunnerFeature;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

@Features({ TransactionalFeature.class, CoreFeature.class })
@Deploy("fr.sword.naiad.nuxeo.ufnxql.core")
@Deploy({ "fr.sword.naiad.nuxeo.ufnxql.core:OSGI-INF/test-ufnxql-config-1-contrib.xml",
        "fr.sword.naiad.nuxeo.ufnxql.core:OSGI-INF/test-ufnxql-config-2-contrib.xml" })
public class FlexibleQueryMakerFeature implements RunnerFeature {

}
