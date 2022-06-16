package fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Run all of its children simultaneous and is done when all the children are
 * done.
 *
 */
public class ParallelRunner extends AbstractElementRunner {

    public ParallelRunner() {
        super();
    }

    @Override
    protected void runOnChild(CoreSession session, FeuilleRouteElement element, List<FeuilleRouteElement> children) {
        if (children.isEmpty()) {
            element.setRunning(session);
            element.setDone(session);
            return;
        }
        if (!element.isRunning()) {
            element.setRunning(session);
            boolean someChildrenNotDone = false;
            for (FeuilleRouteElement child : children) {
                child.run(session);
                if (!child.isDone()) {
                    someChildrenNotDone = true;
                }
            }
            if (!someChildrenNotDone) {
                element.setDone(session);
            }
            return;
        } else {
            boolean someChildrenNotDone = false;
            for (FeuilleRouteElement child : children) {
                if (!child.isDone()) {
                    child.run(session);
                    if (!child.isDone()) {
                        someChildrenNotDone = true;
                    }
                }
            }
            if (!someChildrenNotDone) {
                element.setDone(session);
            }
        }
    }
}
