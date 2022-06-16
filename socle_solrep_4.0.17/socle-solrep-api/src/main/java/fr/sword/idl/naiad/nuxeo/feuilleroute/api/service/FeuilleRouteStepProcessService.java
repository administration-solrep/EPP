package fr.sword.idl.naiad.nuxeo.feuilleroute.api.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStep;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * The FeuilleRouteService allows manipulate {@link FeuilleRoute}.
 *
 *
 */
public interface FeuilleRouteStepProcessService {
    StepProcess getStepProcess(CoreSession session, FeuilleRouteStep step);
}
