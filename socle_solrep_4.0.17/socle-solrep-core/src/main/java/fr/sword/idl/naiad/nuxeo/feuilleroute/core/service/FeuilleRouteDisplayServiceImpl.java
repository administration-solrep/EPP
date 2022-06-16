package fr.sword.idl.naiad.nuxeo.feuilleroute.core.service;

import fr.dila.st.core.query.QueryHelper;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.Elem;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.FeuilleRouteMdl;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteFolderElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTable;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.RouteTableElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.Step;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.model.StepGroup;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRouteDisplayService;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import java.util.ArrayList;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/**
 *
 *
 */
public class FeuilleRouteDisplayServiceImpl implements FeuilleRouteDisplayService {

    public FeuilleRouteDisplayServiceImpl() {
        // do nothing
    }

    @Override
    public FeuilleRouteMdl getModel(FeuilleRoute route, CoreSession session) {
        FeuilleRouteMdl mdl = new FeuilleRouteMdl();
        fill(route.getDocument(), mdl);

        List<DocumentModel> children = session.getChildren(route.getDocument().getRef());
        for (DocumentModel child : children) {
            mdl.getChildren().add(convertToElem(session, child));
        }

        return mdl;
    }

    private Elem convertToElem(CoreSession session, DocumentModel doc) {
        if (doc.hasSchema(FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_STEP_FOLDER)) {
            StepGroup elt = new StepGroup();
            fill(doc, elt);
            List<DocumentModel> children = session.getChildren(doc.getRef());
            for (DocumentModel child : children) {
                elt.getChildren().add(convertToElem(session, child));
            }
            return elt;
        } else {
            Step elt = new Step();
            fill(doc, elt);
            return elt;
        }
    }

    private void fill(DocumentModel doc, Elem elt) {
        elt.setDocId(doc.getId());
        elt.setDocType(doc.getType());
        elt.setTitle(DublincorePropertyUtil.getTitle(doc));
        if (doc.hasSchema(FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_STEP_FOLDER) && elt instanceof StepGroup) {
            StepGroup grp = (StepGroup) elt;
            grp.setType(FeuilleRouteStepFolderSchemaUtil.getExecution(doc));
        }
    }

    @Override
    public List<RouteTableElement> getRouteElements(FeuilleRoute route, CoreSession session) {
        RouteTable table = new RouteTable();
        List<RouteTableElement> elements = new ArrayList<>();
        processElementsInFolder(route.getDocument(), elements, table, session, 0, null);
        int maxDepth = 0;
        for (RouteTableElement element : elements) {
            int d = element.getDepth();
            maxDepth = d > maxDepth ? d : maxDepth;
        }
        table.setMaxDepth(maxDepth);
        for (RouteTableElement element : elements) {
            element.computeFirstChildList();
        }
        return elements;
    }

    private void processElementsInFolder(
        DocumentModel doc,
        List<RouteTableElement> elements,
        RouteTable table,
        CoreSession session,
        int depth,
        RouteFolderElement folder
    ) {
        DocumentModelList children = session.getChildren(doc.getRef());
        boolean first = true;
        for (DocumentModel child : children) {
            if (child.isFolder() && !QueryHelper.isFolderEmpty(session, child.getId())) {
                RouteFolderElement thisFolder = new RouteFolderElement(
                    child.getAdapter(FeuilleRouteElement.class),
                    table,
                    first,
                    folder,
                    depth
                );
                processElementsInFolder(child, elements, table, session, depth + 1, thisFolder);
            } else {
                if (folder != null) {
                    folder.increaseTotalChildCount();
                } else {
                    table.increaseTotalChildCount();
                }
                elements.add(
                    new RouteTableElement(child.getAdapter(FeuilleRouteElement.class), table, depth, folder, first)
                );
            }
            first = false;
        }
    }
}
