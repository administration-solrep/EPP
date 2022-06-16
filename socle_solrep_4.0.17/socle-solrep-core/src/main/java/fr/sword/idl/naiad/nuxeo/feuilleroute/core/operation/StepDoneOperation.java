package fr.sword.idl.naiad.nuxeo.feuilleroute.core.operation;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;

/**
 * An operation that set the step to the done state.
 *
 *
 */
@Operation(
    id = StepDoneOperation.ID,
    category = FeuilleRouteConstant.OPERATION_CATEGORY_ROUTING_NAME,
    label = "Set Step Done",
    description = "Set the step as done."
)
public class StepDoneOperation {
    public static final String ID = "Feuille.Route.Step.Done";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void setStepDone() {
        FeuilleRouteStep step = (FeuilleRouteStep) context.get(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY);
        step.setDone(context.getCoreSession());
    }
}
