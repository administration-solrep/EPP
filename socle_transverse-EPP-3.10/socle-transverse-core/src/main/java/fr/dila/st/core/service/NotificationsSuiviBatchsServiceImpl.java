package fr.dila.st.core.service;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;

import fr.dila.st.api.administration.NotificationsSuiviBatchs;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.NotificationsSuiviBatchsService;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;

/**
 * Service de gestion des notifications de suivi des batchs
 * 
 * @author JBT
 * 
 */
public class NotificationsSuiviBatchsServiceImpl implements NotificationsSuiviBatchsService {

	private static final STLogger	LOGGER						= STLogFactory
																		.getLog(NotificationsSuiviBatchsServiceImpl.class);
	private static final String		NOTIFS_SUIVI_BATCHS_QUERY	= "select * from NotificationsSuiviBatchs where "
																		+ STSchemaConstant.ECM_ISPROXY_XPATH + " = 0";

	private String					idNotificationsSuiviBatchs	= null;

	/**
	 * Default constructor
	 */
	public NotificationsSuiviBatchsServiceImpl() {
		// do nothing
	}

	@Override
	public void desactiverNotifications(CoreSession session) throws ClientException {
		NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);

		notificationsSuiviBatchs.setEtatNotification(false);

		session.saveDocument(notificationsSuiviBatchs.getDocument());
	}

	@Override
	public void activerNotifications(CoreSession session) throws ClientException {
		NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);

		notificationsSuiviBatchs.setEtatNotification(true);

		session.saveDocument(notificationsSuiviBatchs.getDocument());
	}

	@Override
	public NotificationsSuiviBatchs getNotificationsSuiviBatchsDocument(CoreSession session) throws ClientException {
		DocumentModel notificationsSuiviBatchs;

		if (idNotificationsSuiviBatchs == null) {
			DocumentModelList notificationsSuiviBatchsList = session.query(NOTIFS_SUIVI_BATCHS_QUERY);
			if (notificationsSuiviBatchsList == null || notificationsSuiviBatchsList.size() != 1) {
				throw new ClientException(
						"Le document de notifications de suivi des batchs n'existe pas ou est présent en plus d'un exemplaire");
			}
			idNotificationsSuiviBatchs = notificationsSuiviBatchsList.get(0).getId();
			notificationsSuiviBatchs = notificationsSuiviBatchsList.get(0);
		} else {
			notificationsSuiviBatchs = session.getDocument(new IdRef(idNotificationsSuiviBatchs));
		}
		return notificationsSuiviBatchs.getAdapter(NotificationsSuiviBatchs.class);
	}

	@Override
	public List<String> getAllUserName(CoreSession session) throws ClientException {
		NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
		return notificationsSuiviBatchs.getReceiverMailList();
	}

	@Override
	public void addUserName(CoreSession session, String userName) throws ClientException {
		if (userName != null) {
			NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
			final List<STUser> adminUsers = STServiceLocator.getProfileService().getUsersFromBaseFunction(
					STBaseFunctionConstant.BATCH_READER);
			List<String> usernameList = notificationsSuiviBatchs.getReceiverMailList();
			for (STUser user : adminUsers) {
				// Vérification de l'appartenance aux batchs readers
				if (userName.equals(user.getUsername()) && !usernameList.contains(userName)) {
					// Vérification de la non appartenance à la liste actuelle et ajout au besoin
					usernameList.add(userName);
					notificationsSuiviBatchs.setReceiverMailList(usernameList);
					session.saveDocument(notificationsSuiviBatchs.getDocument());
				}
			}
		}
	}

	@Override
	public void removeUserName(CoreSession session, String userName) throws ClientException {
		NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
		List<String> usernameList = notificationsSuiviBatchs.getReceiverMailList();
		if (userName != null && usernameList != null && usernameList.contains(userName)) {
			usernameList.remove(userName);
			notificationsSuiviBatchs.setReceiverMailList(usernameList);
			session.saveDocument(notificationsSuiviBatchs.getDocument());
		}
	}

	@Override
	public boolean sontActiveesNotifications(CoreSession session) throws ClientException {
		NotificationsSuiviBatchs notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
		return notificationsSuiviBatchs.getEtatNotification();
	}

	@Override
	public List<STUser> getAllUsers(CoreSession session) {
		NotificationsSuiviBatchs notificationsSuiviBatchs;
		final STUserManager userManager = (STUserManager) STServiceLocator.getUserManager();
		try {
			notificationsSuiviBatchs = getNotificationsSuiviBatchsDocument(session);
			List<STUser> listUser = new ArrayList<STUser>();
			for (String username : notificationsSuiviBatchs.getReceiverMailList()) {
				DocumentModel userModel = userManager.getUserModel(username);
				STUser user = userModel.getAdapter(STUser.class);
				if (user != null) {
					listUser.add(user);
				}
			}
			return listUser;
		} catch (ClientException exc) {
			LOGGER.warn(session, STLogEnumImpl.FAIL_GET_USER_TEC, exc);
			return null;
		}
	}

}
