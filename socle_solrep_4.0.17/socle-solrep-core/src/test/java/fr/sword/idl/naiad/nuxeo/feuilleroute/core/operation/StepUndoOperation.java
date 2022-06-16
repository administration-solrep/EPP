package fr.sword.idl.naiad.nuxeo.feuilleroute.core.operation;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.service.WaitingStepRuntimePersister;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;

/**
 *
 */
@Operation(id = StepUndoOperation.ID, category = FeuilleRouteConstant.OPERATION_CATEGORY_ROUTING_NAME)
public class StepUndoOperation {
    public static final String ID = "Feuille.Route.Test.Step.Undo";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void setStepDone() {
        FeuilleRouteStep step = (FeuilleRouteStep) context.get(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY);
        String stepId = step.getDocument().getId();
        WaitingStepRuntimePersister.removeStepId(stepId);
    }
}
