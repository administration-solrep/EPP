package fr.sword.idl.naiad.nuxeo.feuilleroute.core.operation;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.service.WaitingStepRuntimePersister;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;

/**
 *
 */
@Operation(
    id = StepRunningOperation.ID,
    category = FeuilleRouteConstant.OPERATION_CATEGORY_ROUTING_NAME,
    label = "Set Step Done",
    description = "Set the step as done."
)
public class StepRunningOperation {
    public static final String ID = "Feuille.Route.Test.Step.Waiting";
    private static final Log LOG = LogFactory.getLog(StepRunningOperation.class);

    @Context
    protected OperationContext context;

    @OperationMethod
    public void setStepDone() {
        FeuilleRouteStep step = (FeuilleRouteStep) context.get(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY);
        String stepId = step.getDocument().getId();
        LOG.debug("in " + ID + " - " + stepId + " - " + step.getDocument().getName());
        WaitingStepRuntimePersister.addStepId(stepId);
    }
}
