package fr.sword.idl.naiad.nuxeo.feuilleroute.api.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import org.nuxeo.ecm.core.api.CoreSession;

public interface StepProcess {
    void run(CoreSession session, FeuilleRouteStep step);

    void undo(CoreSession session, FeuilleRouteStep step);
}
