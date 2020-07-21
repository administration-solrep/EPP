package fr.dila.solonepp.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.management.api.AdministrativeStatusManager;

import fr.dila.st.core.util.SessionUtil;

/**
 * Listener qui effectue des actions après le démarrage de l'application Nuxeo.
 * 
 * @author jtremeaux
 */
public class AfterApplicationStartedListener implements EventListener {
    @Override
    public void handleEvent(Event event) throws ClientException {
        String eventId = event.getName();
        String serviceId = (String) event.getContext().getProperty(AdministrativeStatusManager.ADMINISTRATIVE_EVENT_SERVICE);

        if (serviceId.equals(AdministrativeStatusManager.GLOBAL_INSTANCE_AVAILABILITY)) {
            if (eventId.equals(AdministrativeStatusManager.ACTIVATED_EVENT)) {
                CoreSession session = null;
                try {
                    session = SessionUtil.getCoreSession();
                    
                    // Crée automatiquement les Mailbox institution
//                    createInstitutionMailbox(session);
                } finally {
                    if (session != null) {
                        CoreInstance.getInstance().close(session);
                    }
                }
            }
        }
    }
}
