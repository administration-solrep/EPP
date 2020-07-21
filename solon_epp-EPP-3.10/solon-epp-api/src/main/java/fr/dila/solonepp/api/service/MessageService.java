package fr.dila.solonepp.api.service;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.service.evenement.MajInterneContext;

/**
 * Service permettant de gérer les messages.
 * 
 * @author jtremeaux
 */
public interface MessageService extends Serializable {

    /**
     * Crée un nouveau document message (uniquement en mémoire).
     * 
     * @param session Session
     * @param evenementDoc Document événement
     * @param mailboxDoc Document Mailbox
     * @return Nouveau document message
     * @throws ClientException
     */
    DocumentModel createBareMessage(CoreSession session, DocumentModel evenementDoc, DocumentModel mailboxDoc) throws ClientException;

    /**
     * Supprime tous les messages associés à un événement.
     * 
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @throws ClientException
     */
    void deleteMessageByEvenementId(CoreSession session, final String evenementId) throws ClientException;

    /**
     * Recherche le message de l'utilisateur par l'identifiant de l'événement.
     * 
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @return Document message
     * @throws ClientException
     */
    DocumentModel getMessageByEvenementId(CoreSession session, String evenementId) throws ClientException;
    
    /**
     * Recherche le message de l'utilisateur par l'identifiant de l'événement sans regarder le droit de lecture
     * 
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @return Document message
     * @throws ClientException
     */
    List<DocumentModel> getMessagesByEvenementIdWithoutReadPerm(CoreSession session, String evenementId) throws ClientException;
    
    

    /**
     * Suit la transition d'un message destinataire à l'état "en cours".
     * 
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @throws ClientException 
     */
    void followTransitionEnCours(CoreSession session, String evenementId) throws ClientException;

    /**
     * Suit la transition d'un message destinataire à l'état "traité".
     * 
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @throws ClientException 
     */
    void followTransitionTraite(CoreSession session, String evenementId) throws ClientException;

    /**
     * Mise a jour du Visa Interne
     * @param session
     * @param majVisaInterneContext
     * @throws ClientException 
     */
    void majInterne(CoreSession session, MajInterneContext majVisaInterneContext) throws ClientException;
}
