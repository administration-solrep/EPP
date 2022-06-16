package fr.dila.solonepp.core.event;

import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STConstant;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Listener qui permet de notifier les services web des institutions après la création d'une notification.
 *
 * @author jtremeaux
 */
public class AfterNotificationCreatedWsListener implements PostCommitEventListener {

    private void handleEvent(Event event) {
        // Traite uniquement les évènements de création / modification de documents
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        if (!event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)) {
            return;
        }

        // Traite uniquement les documents de type délégation
        DocumentEventContext context = (DocumentEventContext) ctx;
        DocumentModel jetonDoc = context.getSourceDocument();
        if (!STConstant.JETON_DOC_TYPE.equals(jetonDoc.getType())) {
            return;
        }
        CoreSession session = context.getCoreSession();

        // Notifie les Web Services
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.sendNotificationWebService(session, jetonDoc);
    }

    @Override
    public void handleEvent(EventBundle events) {
        for (Event event : events) {
            if (event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)) {
                handleEvent(event);
            }
        }
    }
}
