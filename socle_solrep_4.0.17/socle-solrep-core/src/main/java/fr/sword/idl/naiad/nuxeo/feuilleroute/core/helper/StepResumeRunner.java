package fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Allows to resume a route from the id of a step.
 *
 *
 */
public class StepResumeRunner {
    protected String stepDocId;

    public StepResumeRunner(String stepDocId) {
        this.stepDocId = stepDocId;
    }

    public void resumeStep(CoreSession session) {
        DocumentModel model = session.getDocument(new IdRef(stepDocId));
        FeuilleRouteStep step = model.getAdapter(FeuilleRouteStep.class);
        step.setDone(session);
        final FeuilleRoute route = step.getFeuilleRoute(session);
        new UnrestrictedSessionRunner(session) {

            @Override
            public void run() {
                route.run(session);
            }
        }
        .runUnrestricted();
    }
}
