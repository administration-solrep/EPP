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

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.service.MessageService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

/**
 * Listener exécuté après la suppression d'un événement.
 * 
 * @author jtremeaux
 */
public class AfterEvenementRemovedListener implements PostCommitEventListener {

    private void handleEvent(Event event) throws ClientException {
        
        EventContext ctx = event.getContext();
        
        if (ctx instanceof DocumentEventContext){
            DocumentEventContext context = (DocumentEventContext) ctx;
            
            final DocumentModel evenementDoc = context.getSourceDocument();
            
            if (!SolonEppConstant.EVENEMENT_DOC_TYPE.equals(evenementDoc.getType())) {
                return;
            }
            
            final CoreSession session = context.getCoreSession();
            
            // Supprime les messages liés à l'événement
            final MessageService messageService = SolonEppServiceLocator.getMessageService();
            messageService.deleteMessageByEvenementId(session, evenementDoc.getId());
        }
        
       
     }
    
    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        for (Event event : events) {
            if (event.getName().equals(DocumentEventTypes.DOCUMENT_REMOVED)) {
                // Traite uniquement les évènements de suppression d'événements
                handleEvent(event);
            }
        }
    }
}
