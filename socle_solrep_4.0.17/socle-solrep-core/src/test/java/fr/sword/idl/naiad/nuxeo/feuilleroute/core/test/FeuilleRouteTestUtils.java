package fr.sword.idl.naiad.nuxeo.feuilleroute.core.test;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.FeuilleRouteStepFolderSchemaUtil;
import fr.sword.naiad.nuxeo.commons.core.schema.DublincorePropertyUtil;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 *
 */
public final class FeuilleRouteTestUtils {

    private FeuilleRouteTestUtils() {
        // do nothing
    }

    public static DocumentModel createDocumentRouteModel(CoreSession session, String name, String path) {
        DocumentModel route = createDocumentModel(session, name, FeuilleRouteConstant.TYPE_FEUILLE_ROUTE, path);
        createDocumentModel(session, "step1", FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP, route.getPathAsString());
        createDocumentModel(session, "step2", FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP, route.getPathAsString());
        DocumentModel parallelFolder1 = createDocumentModel(
            session,
            "parallel1",
            FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP_FOLDER,
            route.getPathAsString()
        );
        FeuilleRouteStepFolderSchemaUtil.setExecution(parallelFolder1, FeuilleRouteExecutionType.parallel.name());
        session.saveDocument(parallelFolder1);
        createDocumentModel(
            session,
            "step31",
            FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP,
            parallelFolder1.getPathAsString()
        );
        createDocumentModel(
            session,
            "step32",
            FeuilleRouteConstant.TYPE_FEUILLE_ROUTE_STEP,
            parallelFolder1.getPathAsString()
        );
        session.save();
        return route;
    }

    public static DocumentModel createDocumentModel(CoreSession session, String name, String type, String path) {
        DocumentModel route1 = session.createDocumentModel(path, name, type);
        DublincorePropertyUtil.setTitle(route1, name);
        return session.createDocument(route1);
    }

    public static FeuilleRoute createDocumentRoute(CoreSession session, String name) {
        DocumentModel model = createDocumentRouteModel(session, name, FeuilleRouteTestConstants.WORKSPACES_PATH);
        return model.getAdapter(FeuilleRoute.class);
    }

    public static DocumentModel createTestDocument(String name, CoreSession session) {
        return createDocumentModel(session, name, "Note", FeuilleRouteTestConstants.WORKSPACES_PATH);
    }
}
