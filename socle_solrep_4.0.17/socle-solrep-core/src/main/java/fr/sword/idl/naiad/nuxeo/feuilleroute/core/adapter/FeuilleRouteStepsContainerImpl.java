package fr.sword.idl.naiad.nuxeo.feuilleroute.core.adapter;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteStepsContainer;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteEvent;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.EventFirer;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteInstanceSchemaUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 */
public class FeuilleRouteStepsContainerImpl extends FeuilleRouteElementImpl implements FeuilleRouteStepsContainer {
    private static final long serialVersionUID = 1L;

    public FeuilleRouteStepsContainerImpl(DocumentModel doc, ElementRunner runner) {
        super(doc, runner);
    }

    public void setAttachedDocuments(List<String> documentIds) {
        FeuilleRouteInstanceSchemaUtil.setAttachedDocuments(document, documentIds);
    }

    public List<String> getAttachedDocuments() {
        return FeuilleRouteInstanceSchemaUtil.getAttachedDocuments(document);
    }

    @Override
    public void setDone(CoreSession session) {
        followTransition(ElementLifeCycleTransistion.toDone, session, false);
    }

    @Override
    public void validate(CoreSession session) {
        // validate this routeModel
        EventFirer.fireEvent(session, getDocument(), null, FeuilleRouteEvent.beforeRouteValidated.name());
        setValidated(session);
        EventFirer.fireEvent(session, getDocument(), null, FeuilleRouteEvent.afterRouteValidated.name());
        setReadOnly(session);
    }
}
