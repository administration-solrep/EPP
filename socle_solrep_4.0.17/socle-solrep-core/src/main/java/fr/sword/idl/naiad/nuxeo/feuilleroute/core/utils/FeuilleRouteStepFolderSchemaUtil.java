package fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteExecutionType;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import org.nuxeo.ecm.core.api.DocumentModel;

public final class FeuilleRouteStepFolderSchemaUtil {

    private FeuilleRouteStepFolderSchemaUtil() {
        // do nothing
    }

    public static void setExecution(DocumentModel doc, String value) {
        PropertyUtil.setProperty(
            doc,
            FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_STEP_FOLDER,
            FeuilleRouteConstant.FROUT_STEPFOLDER_EXEC_PROP,
            value
        );
    }

    public static String getExecution(DocumentModel doc) {
        return PropertyUtil.getStringProperty(
            doc,
            FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_STEP_FOLDER,
            FeuilleRouteConstant.FROUT_STEPFOLDER_EXEC_PROP
        );
    }

    public static void setExecution(DocumentModel doc, FeuilleRouteExecutionType value) {
        setExecution(doc, value.name());
    }

    public static FeuilleRouteExecutionType getExecutionType(final DocumentModel doc) {
        return FeuilleRouteExecutionType.valueOf(getExecution(doc));
    }
}
