package fr.dila.st.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.administration.NotificationsSuiviBatchs;
import fr.dila.st.api.user.STUser;

/**
 * Service de gestion des notifications de suivi des batchs
 * 
 * @author JBT
 * 
 */
public interface NotificationsSuiviBatchsService {

	String	RESTRICTION_ACCESS		= "RESTRICTION_ACCESS";

	String	RESTRICTION_DESCRIPTION	= "RESTRICTION_DESCRIPTION";

	/**
	 * désactive le service d'envoi de notifications pour le suivi des batchs
	 * 
	 * @param session
	 * @param description
	 * @throws ClientException
	 */
	void desactiverNotifications(CoreSession session) throws ClientException;

	/**
	 * active l'envoi des notifications méls pour le suivi des batchs
	 * 
	 * @param session
	 * @throws ClientException
	 */
	void activerNotifications(CoreSession session) throws ClientException;

	/**
	 * Retourne le document NotificationsSuiviBatchs
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	NotificationsSuiviBatchs getNotificationsSuiviBatchsDocument(CoreSession session) throws ClientException;

	/**
	 * Ajoute un identifiant à la liste des destinataires des notifications
	 * 
	 * @param userName
	 * @param session
	 * @throws ClientException
	 */
	void addUserName(CoreSession session, String userName) throws ClientException;

	/**
	 * Retrait d'un identifiant de la liste des destinataires des notifications
	 * 
	 * @param userName
	 * @param session
	 * @throws ClientException
	 */
	void removeUserName(CoreSession session, String userName) throws ClientException;

	/**
	 * Récupération de la liste des destinataires des notifications
	 * 
	 * @param session
	 * @throws ClientException
	 */
	List<String> getAllUserName(CoreSession session) throws ClientException;

	/**
	 * Récupération de l'état du service de notification
	 * 
	 * @param session
	 * @return vrai si les notifications sont activées, faux sinon
	 * @throws ClientException
	 */
	boolean sontActiveesNotifications(CoreSession session) throws ClientException;

	/**
	 * Retourne la liste des users qui doivent recevoir le mail de notification de suivi des batchs
	 * 
	 * @param session
	 * @return List<STUser>
	 */
	List<STUser> getAllUsers(CoreSession session);
}
