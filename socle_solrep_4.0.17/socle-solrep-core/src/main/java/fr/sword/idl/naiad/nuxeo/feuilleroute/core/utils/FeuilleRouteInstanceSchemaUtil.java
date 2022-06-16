package fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils;

import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public final class FeuilleRouteInstanceSchemaUtil {

    private FeuilleRouteInstanceSchemaUtil() {
        // do nothing
    }

    public static List<String> getAttachedDocuments(DocumentModel doc) {
        return PropertyUtil.getStringListProperty(
            doc,
            FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_INSTANCE,
            FeuilleRouteConstant.PROP_FROUT_INSTANCE_ATTACHDOCIDS
        );
    }

    public static void setAttachedDocuments(DocumentModel doc, List<String> value) {
        PropertyUtil.setProperty(
            doc,
            FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_INSTANCE,
            FeuilleRouteConstant.PROP_FROUT_INSTANCE_ATTACHDOCIDS,
            value
        );
    }
}
