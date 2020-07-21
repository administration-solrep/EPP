package fr.dila.solonepp.core.event;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.dila.solonepp.api.domain.message.Message;

public class AfterMessageModifiedListener implements EventListener {

	public AfterMessageModifiedListener(){
		// do nothing
	}
	
    public void handleEvent(Event event) throws ClientException {

        if (!(event.getContext() instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext ctx = (DocumentEventContext) event.getContext();
        CoreSession session = ctx.getCoreSession();

        // Traite uniquement les modifications ou la creation de document
        if (!((event.getName().equals(DocumentEventTypes.DOCUMENT_UPDATED))
                || (event.getName().equals(DocumentEventTypes.DOCUMENT_CREATED))
                || (event.getName().equals(LifeCycleConstants.TRANSITION_EVENT)))) {
            return;
        }
        
        // Traite uniquement les modifications de document ayant pour type Message
        DocumentModel model = ctx.getSourceDocument();
        String docType = model.getType();
        if (!SolonEppConstant.MESSAGE_DOC_TYPE.equals(docType)) {
            return;
        }

        // détermination de l'état dénormalisé
        Message message = model.getAdapter(Message.class);
        String etatMessage = null;
        // Renseigne l'état du message
        if (message.isTypeEmetteur()) {
            if (message.isEtatNonTraite()) {
                etatMessage = SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_COURS_REDACTION;
            } else if (message.isEtatTraite()) {
                if (!message.isArNecessaire()) {
                    etatMessage = SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EMIS;
                } else {
                    if (message.getArNonDonneCount() > 0) {
                        etatMessage = SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_ATTENTE_AR;
                    } else {
                        etatMessage = SolonEppLifecycleConstant.MESSAGE_ETAT_WS_AR_RECU;
                    }
                }
            } else {
                throw new ClientException("Etat du message inconnu: " + model.getCurrentLifeCycleState());
            }
        } else if (message.isTypeDestinataire() || message.isTypeCopie()) {
            if (message.isEtatNonTraite()) {
                etatMessage = SolonEppLifecycleConstant.MESSAGE_ETAT_WS_NON_TRAITE;
            } else if (message.isEtatEnCours()) {
                etatMessage = SolonEppLifecycleConstant.MESSAGE_ETAT_WS_EN_COURS_TRAITEMENT;
            } else if (message.isEtatTraite()) {
                etatMessage = SolonEppLifecycleConstant.MESSAGE_ETAT_WS_TRAITE;
            } else {
                throw new ClientException("Etat du message inconnu: " + model.getCurrentLifeCycleState());
            }
        } else {
            throw new ClientException("Type du message inconnu: " + message.getMessageType());
        }
        
        // on set l'état s'il est différent
        if (etatMessage != null && !etatMessage.equals(message.getEtatMessage())) {
            message.setEtatMessage(etatMessage);
            session.saveDocument(model);
        }
    }
}
