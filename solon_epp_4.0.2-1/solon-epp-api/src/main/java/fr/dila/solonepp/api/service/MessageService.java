package fr.dila.solonepp.api.service;

import fr.dila.solonepp.api.service.evenement.MajInterneContext;
import java.io.Serializable;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

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
     */
    DocumentModel createBareMessage(CoreSession session, DocumentModel evenementDoc, DocumentModel mailboxDoc);

    /**
     * Supprime tous les messages associés à un événement.
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     */
    void deleteMessageByEvenementId(CoreSession session, final String evenementId);

    /**
     * Recherche le message de l'utilisateur par l'identifiant de l'événement.
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @return Document message
     */
    DocumentModel getMessageByEvenementId(CoreSession session, String evenementId);

    /**
     * Recherche le message de l'utilisateur par l'identifiant de l'événement sans regarder le droit de lecture
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     * @return Document message
     */
    List<DocumentModel> getMessagesByEvenementIdWithoutReadPerm(CoreSession session, String evenementId);

    /**
     * Suit la transition d'un message destinataire à l'état "en cours".
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     */
    void followTransitionEnCours(CoreSession session, String evenementId);

    /**
     * Suit la transition d'un message destinataire à l'état "traité".
     *
     * @param session Session
     * @param evenementId Identifiant technique de l'événement
     */
    void followTransitionTraite(CoreSession session, String evenementId);

    /**
     * Mise a jour du Visa Interne
     * @param session
     * @param majVisaInterneContext
     */
    void majInterne(CoreSession session, MajInterneContext majVisaInterneContext);
}
