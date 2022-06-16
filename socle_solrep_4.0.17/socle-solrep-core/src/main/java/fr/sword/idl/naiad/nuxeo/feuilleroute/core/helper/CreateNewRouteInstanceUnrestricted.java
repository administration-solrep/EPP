package fr.sword.idl.naiad.nuxeo.feuilleroute.core.helper;

import static fr.dila.ss.api.constant.SSFeuilleRouteConstant.FEUILLE_ROUTE_SCHEMA;
import static fr.dila.st.api.constant.STSchemaConstant.COMMON_SCHEMA;
import static fr.dila.st.api.constant.STSchemaConstant.DUBLINCORE_SCHEMA;
import static fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_INSTANCE;
import static fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant.SCHEMA_FEUILLE_ROUTE_STEP_FOLDER;

import fr.dila.st.core.util.UnrestrictedGetDocumentRunner;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRoute;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteConstant;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.constant.FeuilleRouteEvent;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.service.FeuilleRoutePersister;
import fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils.EventFirer;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.runtime.api.Framework;

/**
 *
 */
public class CreateNewRouteInstanceUnrestricted extends UnrestrictedSessionRunner {
    private static final Log LOG = LogFactory.getLog(CreateNewRouteInstanceUnrestricted.class);

    private DocumentModel instance;

    private final DocumentRef modelRef;

    private final List<String> docIds;

    private final boolean startInstance;

    private final String initiator;

    private final FeuilleRoutePersister persister;

    /**
     * @param session
     * @param model   model of route. This document is not manipulated (ie: it is ok to use a document model)
     * @param docIds
     */
    public CreateNewRouteInstanceUnrestricted(
        CoreSession session,
        FeuilleRoute model,
        List<String> docIds,
        boolean startInstance,
        FeuilleRoutePersister persister
    ) {
        super(session);
        this.modelRef = model.getDocument().getRef();
        this.docIds = docIds;
        this.startInstance = startInstance;
        this.initiator = session.getPrincipal().getName();
        this.persister = persister;
    }

    @Override
    public void run() {
        DocumentModel modelDoc = session.getDocument(modelRef);
        instance = persister.createDocumentRouteInstanceFromDocumentRouteModel(modelDoc, session);
        FeuilleRoute routeInstance = instance.getAdapter(FeuilleRoute.class);
        routeInstance.setAttachedDocuments(docIds);
        Map<String, Serializable> props = new HashMap<>();
        props.put(FeuilleRouteConstant.INITIATOR_EVENT_CONTEXT_KEY, initiator);
        fireEvent(session, routeInstance, props, FeuilleRouteEvent.beforeRouteReady.name());
        routeInstance.setReady(session);
        fireEvent(session, routeInstance, props, FeuilleRouteEvent.afterRouteReady.name());
        routeInstance.save(session);
        session.save();
        if (Framework.isTestModeSet()) {
            Framework.getService(EventService.class).waitForAsyncCompletion();
        }
        if (startInstance) {
            fireEvent(session, routeInstance, null, FeuilleRouteEvent.beforeRouteStart.name());
            routeInstance.run(session);
        }
        UnrestrictedGetDocumentRunner uget = new UnrestrictedGetDocumentRunner(
            session,
            FEUILLE_ROUTE_SCHEMA,
            SCHEMA_FEUILLE_ROUTE_STEP_FOLDER,
            SCHEMA_FEUILLE_ROUTE_INSTANCE,
            COMMON_SCHEMA,
            DUBLINCORE_SCHEMA
        );
        instance = uget.getById(instance.getId());
    }

    public FeuilleRoute getInstance() {
        return instance.getAdapter(FeuilleRoute.class);
    }

    protected void fireEvent(
        CoreSession coreSession,
        FeuilleRouteElement element,
        Map<String, Serializable> eventProperties,
        String eventName
    ) {
        EventFirer.fireEvent(coreSession, element.getDocument(), eventProperties, eventName);
    }

    public static FeuilleRoute create(
        CoreSession session,
        FeuilleRoute model,
        List<String> docIds,
        boolean startInstance,
        FeuilleRoutePersister persister
    ) {
        CreateNewRouteInstanceUnrestricted runner = new CreateNewRouteInstanceUnrestricted(
            session,
            model,
            docIds,
            startInstance,
            persister
        );
        runner.runUnrestricted();
        LOG.debug(runner.getInstance().getDocument().getId());
        return runner.getInstance();
    }
}
