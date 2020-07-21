package fr.dila.solonepp.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.PostCommitEventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.solonepp.api.service.JetonService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.constant.STConstant;

/**
 * Listener qui permet d'envoyer un mail après la création d'une notification.
 * 
 * @author jtremeaux
 */
public class AfterNotificationCreatedEmailListener implements PostCommitEventListener {

    public void handleEvent(Event event) throws ClientException {
        // Traite uniquement les évènements de création / modification de documents
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }

        if (!event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)) {
            return;
        }

        // Traite uniquement les documents de type jeton
        DocumentEventContext context = (DocumentEventContext) ctx;
        DocumentModel jetonDoc = context.getSourceDocument();
        if (!STConstant.JETON_DOC_TYPE.equals(jetonDoc.getType())) {
            return;
        }
        CoreSession session = context.getCoreSession();

        // Envoie un email de notification
        final JetonService jetonService = SolonEppServiceLocator.getJetonService();
        jetonService.sendNotificationEmail(session, jetonDoc);
    }

    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        for (Event event : events) {
            if (event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED)) {
                handleEvent(event);
            }
        }
    }
}
