package fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Run all its children one after the other and is donen when the last children
 * is done.
 *
 */
public class SerialRunner extends AbstractElementRunner {
    private static final Log LOG = LogFactory.getLog(SerialRunner.class);

    public SerialRunner() {
        super();
    }

    @Override
    protected void runOnChild(CoreSession session, FeuilleRouteElement element, List<FeuilleRouteElement> children) {
        if (!element.isRunning()) {
            element.setRunning(session);
        }
        if (children.isEmpty()) {
            element.setDone(session);
            return;
        }
        // run all the child unless there is a wait state
        for (FeuilleRouteElement child : children) {
            LOG.trace(
                "SerialRunner " +
                element.getDocument().getId() +
                " - " +
                child.getDocument().getId() +
                " - " +
                child.getName()
            );
            if (!child.isDone()) {
                child.run(session);
                if (!child.isDone()) {
                    return;
                }
            }
        }
        // all child ran, we're done
        element.setDone(session);
    }
}
