package fr.dila.st.api.service;

import fr.dila.st.api.administration.NotificationsSuiviBatchs;
import fr.dila.st.api.user.STUser;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Service de gestion des notifications de suivi des batchs
 *
 * @author JBT
 *
 */
public interface NotificationsSuiviBatchsService {
    /**
     * désactive le service d'envoi de notifications pour le suivi des batchs
     *
     * @param session
     *
     */
    void desactiverNotifications(CoreSession session);

    /**
     * active l'envoi des notifications méls pour le suivi des batchs
     *
     * @param session
     *
     */
    void activerNotifications(CoreSession session);

    /**
     * Retourne le document NotificationsSuiviBatchs
     *
     * @param session
     * @return
     *
     */
    NotificationsSuiviBatchs getNotificationsSuiviBatchsDocument(CoreSession session);

    /**
     * Met à jour la liste des destinataires des notifications.
     *
     * @param users liste des identifiants d'utilisateurs
     * @param session CoreSession courante.
     */
    void updateUserNames(List<String> users, CoreSession session);

    /**
     * Récupération de la liste des destinataires des notifications
     *
     * @param session
     *
     */
    List<String> getAllUserName(CoreSession session);

    /**
     * Récupération de l'état du service de notification
     *
     * @param session
     * @return vrai si les notifications sont activées, faux sinon
     *
     */
    boolean sontActiveesNotifications(CoreSession session);

    /**
     * Retourne la liste des users qui doivent recevoir le mail de notification de suivi des batchs
     *
     * @param session
     * @return
     */
    List<STUser> getAllUsers(CoreSession session);
}
