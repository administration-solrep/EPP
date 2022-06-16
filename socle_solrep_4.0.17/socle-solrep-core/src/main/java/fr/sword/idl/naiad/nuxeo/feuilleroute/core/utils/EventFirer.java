package fr.sword.idl.naiad.nuxeo.feuilleroute.core.utils;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public final class EventFirer {
    public static final String FEUILLE_ROUTE_CATEGORY = "FeuilleRoute";

    private EventFirer() {
        // do nothing
    }

    public static void fireEvent(
        CoreSession coreSession,
        DocumentModel elementDoc,
        Map<String, Serializable> eventProperties,
        String eventName
    ) {
        if (eventProperties == null) {
            eventProperties = new HashMap<>();
        }
        eventProperties.put(DocumentEventContext.CATEGORY_PROPERTY_KEY, FEUILLE_ROUTE_CATEGORY);
        DocumentEventContext envContext = new DocumentEventContext(coreSession, coreSession.getPrincipal(), elementDoc);
        envContext.setProperties(eventProperties);
        ServiceUtil.getRequiredService(EventProducer.class).fireEvent(envContext.newEvent(eventName));
    }
}
