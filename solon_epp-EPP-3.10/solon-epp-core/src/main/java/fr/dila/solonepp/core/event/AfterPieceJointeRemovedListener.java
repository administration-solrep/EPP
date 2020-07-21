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
import fr.dila.solonepp.api.domain.evenement.PieceJointe;
import fr.dila.solonepp.api.service.PieceJointeFichierService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;

/**
 * Listener exécuté après la suppression d'une pièce jointe.
 * 
 * @author jtremeaux
 */
public class AfterPieceJointeRemovedListener implements PostCommitEventListener {

    private void handleEvent(Event event) throws ClientException {
        
        EventContext ctx = event.getContext();
        
        if (ctx instanceof DocumentEventContext){
            DocumentEventContext context = (DocumentEventContext) ctx;
            
            if (!event.getName().equals(DocumentEventTypes.ABOUT_TO_REMOVE)) {
                return;
            }
            
            final DocumentModel pieceJointeDoc = context.getSourceDocument();
            if (!SolonEppConstant.PIECE_JOINTE_DOC_TYPE.equals(pieceJointeDoc.getType())) {
                return;
            }
            
            final CoreSession session = context.getCoreSession();
            
            // Vérifie si des autres pièces jointes utilisent ce fichier
            final PieceJointeFichierService pieceJointeFichierService = SolonEppServiceLocator.getPieceJointeFichierService();
            PieceJointe pieceJointe = pieceJointeDoc.getAdapter(PieceJointe.class);
            if (pieceJointe.getPieceJointeFichierList() != null) {
                for (String pieceJointeFichierId : pieceJointe.getPieceJointeFichierList()) {
                    pieceJointeFichierService.removePieceJointeFichier(session, pieceJointeDoc, pieceJointeFichierId);
                }
            }
        }
     }
    
    @Override
    public void handleEvent(EventBundle events) throws ClientException {
        for (Event event : events) {
            if (event.getName().equals(DocumentEventTypes.ABOUT_TO_REMOVE)) {
                handleEvent(event);
            }
        }
    }
}
