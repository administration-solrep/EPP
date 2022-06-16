package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper.ActionableValidator;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;

/**
 * A Test Helper class that simulate persistence of Step information. This persistence is transient to the JVM.
 *
 *
 */
public class WaitingStepRuntimePersister {
    private static final List<String> RUNNING_STEPS = new ArrayList<String>();

    private static final List<String> DONE_STEPS = new ArrayList<String>();
    private static final Log LOG = LogFactory.getLog(WaitingStepRuntimePersister.class);

    private WaitingStepRuntimePersister() {
        // do nothing
    }

    public static void addStepId(String id) {
        if (RUNNING_STEPS.contains(id)) {
            throw new RuntimeException("Asking twice to wait on the same step.");
        }
        LOG.debug("ADD RUNNING : " + id);
        RUNNING_STEPS.add(id);
    }

    public static List<String> getRunningStepIds() {
        return RUNNING_STEPS;
    }

    public static FeuilleRouteStep getStep(String id, CoreSession session) {
        return session.getDocument(new IdRef(id)).getAdapter(FeuilleRouteStep.class);
    }

    public static List<String> getDoneStepIds() {
        return DONE_STEPS;
    }

    public static void resumeStep(final String id, CoreSession session) {
        if (!RUNNING_STEPS.contains(id)) {
            throw new RuntimeException("Asking to resume a non peristed step.");
        }
        ActionableValidator validator = new ActionableValidator(new SimpleActionableObject(id), session);
        validator.validate();
        LOG.debug("RESUME RUNNING : " + id + " - " + session.getDocument(new IdRef(id)).getName());
        RUNNING_STEPS.remove(id);
        DONE_STEPS.add(id);
    }

    /**
     * @param stepId
     */
    public static void removeStepId(String stepId) {
        LOG.debug("REMOVE RUNNING : " + stepId);
        RUNNING_STEPS.remove(stepId);
        DONE_STEPS.remove(stepId);
    }

    public static void resetAll() {
        RUNNING_STEPS.clear();
        DONE_STEPS.clear();
    }
}
