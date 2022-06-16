package fr.sword.idl.naiad.nuxeo.feuilleroute.core.operation;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;

/**
 *
 *
 */
@Operation(
    id = StepBackToReadyOperation.ID,
    category = FeuilleRouteConstant.OPERATION_CATEGORY_ROUTING_NAME,
    label = "Set Step back to a ready state",
    description = "Set the step back to a ready state."
)
public class StepBackToReadyOperation {
    public static final String ID = "Feuille.Route.BackToReady";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void setStepDone() {
        FeuilleRouteStep step = (FeuilleRouteStep) context.get(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY);
        step.backToReady(context.getCoreSession());
    }
}
