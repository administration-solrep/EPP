package fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Runner responsible to run or undo an element of a route.
 *
 */
public interface ElementRunner {
    /**
     * Run this element. If an exception is thrown while doing, it cancels the
     * route.
     */
    void run(CoreSession session, FeuilleRouteElement element);

    /**
     * Run the undo chain on this element. If this element is not a step, then
     * throw an exception.
     */
    void undo(CoreSession session, FeuilleRouteElement element);

    /**
     * Cancel this element.
     *
     * @see FeuilleRoute#cancel(CoreSession)
     */
    void cancel(CoreSession session, FeuilleRouteElement element);
}
