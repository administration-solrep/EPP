package fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter;

import fr.dila.ss.api.constant.SSConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteEvent;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.EventFirer;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 */
public class FeuilleRouteImpl extends FeuilleRouteStepsContainerImpl implements FeuilleRoute {
    private static final long serialVersionUID = 1L;

    public FeuilleRouteImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
    }

    @Override
    public void setDone(CoreSession session) {
        followTransition(ElementLifeCycleTransistion.toDone, session, false);
        EventFirer.fireEvent(session, getDocument(), null, FeuilleRouteEvent.afterRouteFinish.name());
    }

    @Override
    public boolean canUndoStep(CoreSession session) {
        return false;
    }

    @Override
    public boolean isFeuilleRouteInstance() {
        return document.getPathAsString().startsWith(SSConstant.FDR_INSTANCE_FOLDER_PATH);
    }
}
