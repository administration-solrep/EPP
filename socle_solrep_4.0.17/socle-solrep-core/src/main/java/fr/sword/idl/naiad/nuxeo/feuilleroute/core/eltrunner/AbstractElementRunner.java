package fr.sword.idl.naiad.nuxeo.feuilleroute.core.eltrunner;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.eltrunner.ElementRunner;
import fr.sword.naiad.nuxeo.commons.core.constant.CommonSchemaConstant;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 *
 */
public abstract class AbstractElementRunner implements ElementRunner {
    private static final String QUERY_CHILD_FMT =
        "SELECT * FROM Document WHERE " +
        CommonSchemaConstant.XPATH_ECM_LIFECYCLE +
        " != 'deleted' AND " +
        CommonSchemaConstant.XPATH_ECM_PARENTID +
        " = '%s' ORDER BY ecm:pos";

    protected AbstractElementRunner() {
        // do nothing
    }

    protected List<FeuilleRouteElement> getChildrenElement(CoreSession session, FeuilleRouteElement element) {
        final String query = String.format(QUERY_CHILD_FMT, element.getDocument().getId());
        DocumentModelList children = session.query(query);
        List<FeuilleRouteElement> elements = new ArrayList<>();
        for (DocumentModel model : children) {
            elements.add(model.getAdapter(FeuilleRouteElement.class));
        }
        return elements;
    }

    @Override
    public final void run(CoreSession session, FeuilleRouteElement element) {
        List<FeuilleRouteElement> children = getChildrenElement(session, element);
        runOnChild(session, element, children);
    }

    @Override
    public void undo(CoreSession session, FeuilleRouteElement element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(CoreSession session, FeuilleRouteElement element) {
        List<FeuilleRouteElement> children = getChildrenElement(session, element);
        for (FeuilleRouteElement child : children) {
            child.cancel(session);
        }
        element.setCanceled(session);
    }

    protected abstract void runOnChild(
        CoreSession session,
        FeuilleRouteElement element,
        List<FeuilleRouteElement> children
    );
}
