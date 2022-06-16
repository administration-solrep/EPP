package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteStepProcessService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.StepProcess;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;

public class FeuilleRouteStepProcessServiceImpl implements FeuilleRouteStepProcessService {
    private final Map<String, StepProcess> stepProcesses = new HashMap<>();

    public FeuilleRouteStepProcessServiceImpl() {
        // do nothing
    }

    @Override
    public StepProcess getStepProcess(CoreSession session, FeuilleRouteStep step) {
        String type = step.getDocument().getType();
        StepProcess process = stepProcesses.get(type);
        if (process != null) {
            return process;
        }

        throw new NuxeoException(
            String.format("No process found for %s - %s", step.getDocument().getType(), step.getDocument().getId())
        );
    }

    public void register(String docType, StepProcess process) {
        stepProcesses.put(docType, process);
    }
}
