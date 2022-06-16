package fr.sword.idl.naiad.nuxeo.feuilleroute.core.operation;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper.StepResumeRunner;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;

/**
 * An Operation that allows to resume a step.
 *
 *
 */
@Operation(
    id = ResumeStepOperation.ID,
    category = FeuilleRouteConstant.OPERATION_CATEGORY_ROUTING_NAME,
    label = "Resume Step",
    description = "Resume a step that were in running step."
)
public class ResumeStepOperation {
    public static final String ID = "Feuille.Route.Resume.Step";

    @Context
    protected OperationContext context;

    @OperationMethod
    public void resume() {
        FeuilleRouteStep step = (FeuilleRouteStep) context.get(FeuilleRouteConstant.OPERATION_STEP_DOCUMENT_KEY);
        StepResumeRunner runner = new StepResumeRunner(step.getDocument().getId());
        runner.resumeStep(context.getCoreSession());
    }
}
