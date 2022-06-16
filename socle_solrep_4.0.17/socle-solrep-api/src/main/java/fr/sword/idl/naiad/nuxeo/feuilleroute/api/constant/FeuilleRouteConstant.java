package fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant;

public final class FeuilleRouteConstant {
    // TYPE DE DOCUMENTS

    public static final String TYPE_FEUILLE_ROUTE = "FeuilleRoute";
    public static final String TYPE_FEUILLE_ROUTE_STEP_FOLDER = "StepFolder";
    public static final String TYPE_FEUILLE_ROUTE_STEP = "RouteStep";
    public static final String TYPE_FEUILLE_ROUTE_INSTANCES_ROOT = "DocumentRouteInstancesRoot";

    // FACET
    public static final String FACET_FEUILLE_ROUTE = "NaiadFeuilleRoute";
    public static final String FACET_FEUILLE_ROUTE_STEP = "NaiadFeuilleRouteStep";
    // META
    public static final String SCHEMA_FEUILLE_ROUTE_STEP_FOLDER = "feuille_route_step_folder";
    public static final String PREFIX_FEUILLE_ROUTE_STEP_FOLDER = "froutstepf";
    public static final String FROUT_STEPFOLDER_EXEC_PROP = "execution";

    public static final String SCHEMA_FEUILLE_ROUTE_INSTANCE = "feuille_route_instance";
    public static final String PREFIX_FEUILLE_ROUTE_INSTANCE = "froutinst";
    public static final String PROP_FROUT_INSTANCE_ATTACHDOCIDS = "attachDocumentIds";
    public static final String XPATH_FROUT_INSTANCE_ATTACHDOCIDS =
        PREFIX_FEUILLE_ROUTE_INSTANCE + ":" + PROP_FROUT_INSTANCE_ATTACHDOCIDS;
    // APPLICATION
    public static final String FEUILLE_ROUTE_INSTANCES_ROOT_ID = "feuille-route-instances";
    public static final String ROUTING_ACL = "routing";

    public static final String OPERATION_STEP_DOCUMENT_KEY = "feuille.route.step";
    public static final String OPERATION_CATEGORY_ROUTING_NAME = "FeuilleRoute";

    public static final String INITIATOR_EVENT_CONTEXT_KEY = "initiator";

    public static final String ROUTE_MANAGERS_GROUP_NAME = "FeuilleRouteManagers";

    private FeuilleRouteConstant() {
        // do nothing
    }
}
