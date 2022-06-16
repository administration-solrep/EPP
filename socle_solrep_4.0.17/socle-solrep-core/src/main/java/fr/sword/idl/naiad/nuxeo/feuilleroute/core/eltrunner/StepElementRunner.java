package fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteEvent;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteStepProcessService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.StepProcess;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.EventFirer;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Run the operation chain for this step.
 *
 */
public class StepElementRunner implements ElementRunner {

    public StepElementRunner() {
        // do nothing
    }

    @Override
    public void run(CoreSession session, FeuilleRouteElement element) {
        if (element.isRunning() || element.isDeleted()) {
            return;
        } else {
            element.setRunning(session);
        }
        if (!(element instanceof FeuilleRouteStep)) {
            throw new NuxeoException("Method run should be overriden in parent class.");
        }
        EventFirer.fireEvent(session, element.getDocument(), null, FeuilleRouteEvent.beforeStepRunning.name());

        if (!element.isDone()) {
            EventFirer.fireEvent(session, element.getDocument(), null, FeuilleRouteEvent.stepWaiting.name());
        }

        StepProcess stepProcess = getStepProcessService().getStepProcess(session, (FeuilleRouteStep) element);
        stepProcess.run(session, (FeuilleRouteStep) element);
    }

    private FeuilleRouteStepProcessService getStepProcessService() {
        return ServiceUtil.getRequiredService(FeuilleRouteStepProcessService.class);
    }

    @Override
    public void undo(CoreSession session, FeuilleRouteElement element) {
        EventFirer.fireEvent(session, element.getDocument(), null, FeuilleRouteEvent.beforeUndoingStep.name());

        StepProcess stepProcess = getStepProcessService().getStepProcess(session, (FeuilleRouteStep) element);
        stepProcess.undo(session, (FeuilleRouteStep) element);

        EventFirer.fireEvent(session, element.getDocument(), null, FeuilleRouteEvent.afterUndoingStep.name());
    }

    @Override
    public void cancel(CoreSession session, FeuilleRouteElement element) {
        if (element.isReady() || element.isDone()) {
            element.setCanceled(session);
        } else if (element.isRunning()) {
            try {
                undo(session, element);
            } finally {
                element.setCanceled(session);
            }
        } else {
            throw new NuxeoException("Not allowed to cancel an element neither in ready, done or running state.");
        }
    }
}
