package fr.dila.ss.core.test;

import fr.dila.ss.core.operation.distribution.CreateActualiteRootOperation;
import javax.inject.Inject;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RunnerFeature;

@Features({ SolrepFeature.class })
public class ActualiteFeature implements RunnerFeature {
    @Inject
    private CoreFeature coreFeature;

    @Inject
    private AutomationService automationService;

    public ActualiteFeature() {
        // Default constructor
    }

    @Override
    public void beforeSetup(FeaturesRunner runner) throws Exception {
        OperationContext context = new OperationContext(coreFeature.getCoreSession());
        automationService.run(context, CreateActualiteRootOperation.ID);
    }
}
