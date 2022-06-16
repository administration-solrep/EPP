package fr.sword.idl.naiad.nuxeo.feuilleroute.api.service;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.FeuilleRouteMdl;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * The FeuilleRouteService allows manipulate {@link FeuilleRoute}.
 *
 *
 */
public interface FeuilleRouteDisplayService {
    /**
     * Computes the list of elements {@link RouteTableElement} for this
     * {@link FeuilleRoute}.
     *
     * @param route {@link FeuilleRoute}.
     * @param session The session used to query the {@link FeuilleRoute}.
     * @return A list of {@link FeuilleRouteElement}
     */
    List<RouteTableElement> getRouteElements(FeuilleRoute route, CoreSession session);

    FeuilleRouteMdl getModel(FeuilleRoute route, CoreSession session);
}
