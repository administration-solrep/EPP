package fr.dila.st.core.service;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelComparator;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.directory.DirectoryException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.platform.computedgroups.UserManagerWithComputedGroups;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.api.service.JournalService;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.util.SessionUtil;

/**
 * Surcharge du gestionnaire d'utilisateurs de Nuxeo. Ce gestionnaire d'utilisateur permet d'instancier les objets
 * Principal spécifiques à notre application.
 * 
 * @author jtremeaux
 */
public class STUserManagerImpl extends UserManagerWithComputedGroups implements STUserManager {

	/**
	 * Serial UID
	 */
	private static final long	serialVersionUID	= -4946382095391343101L;

	private static final Log	LOG					= LogFactory.getLog(STUserManagerImpl.class);

	/**
	 * Default constructor
	 */
	public STUserManagerImpl() {
		super();
	}

	@Override
	public boolean checkUsernamePassword(String username, String password) throws ClientException {
		if (!super.checkUsernamePassword(username, password)) {
			return false;
		}

		try {
			final DocumentModel userdoc = getUserModel(username);
			final STUser user = userdoc.getAdapter(STUser.class);
			final Calendar cal = user.getDateFin();

			return cal == null || !cal.before(Calendar.getInstance());
		} catch (ClientException e) {
			return false;
		}
	}

	@Override
	public boolean isTechnicalUser(String userId) {
		// L'utilisateur qui exécute les batchs est un utilisateur technique
		final ConfigService configService = STServiceLocator.getConfigService();
		String batchUser = configService.getValue(STConfigConstants.NUXEO_BATCH_USER);
		if (batchUser == null || batchUser.equals(userId)) {
			return true;
		}
		return false;
	}

	/**
	 * Surcharge de la méthode nuxeo afin de notifier la création de l'utilisateur dans les logs techniques.
	 * 
	 * @param userModel
	 * @return
	 * @throws ClientException
	 */
	@Override
	public DocumentModel createUser(final DocumentModel userModel) throws ClientException {
		// Chargement des services
		final STMailService stMailService = STServiceLocator.getSTMailService();
		final STUserService stUserService = STServiceLocator.getSTUserService();
		final JournalService journalService = STServiceLocator.getJournalService();
		Session userDir = null;
		CoreSession session = null;

		try {
			userDir = dirService.open(userDirectoryName);
			String userId = getUserId(userModel);

			// check the user does not exist
			if (userDir.hasEntry(userId)) {
				throw new UserAlreadyExistsException();
			}

			String schema = dirService.getDirectorySchema(userDirectoryName);
			String clearUsername = (String) userModel.getProperty(schema, userDir.getIdField());
			String clearPassword = (String) userModel.getProperty(schema, userDir.getPasswordField());

			DocumentModel createdUserModel = userDir.createEntry(userModel);
			userDir.commit();

			syncDigestAuthPassword(clearUsername, clearPassword);

			notifyUserChanged(userId);
			notify(userId, USERCREATED_EVENT_ID);
			session = SessionUtil.getCoreSession();
			try {
				String password = stUserService.generateAndSaveNewUserPassword(userId);
				stMailService.sendMailUserPasswordCreation(session, userId, password);
			} catch (Exception e) {
				LOG.error("Error lors de l'envoi du mèl de mot de passe pour l'utilisateur : " + userId, e);
				createdUserModel.putContextData(STConstant.MAIL_SEND_ERROR, true);
			}

			// Event pour le journal
			final String comment = "Création d'un utilisateur [" + userId + "]";
			journalService.journaliserActionAdministration(session, USERCREATED_EVENT_ID, comment);

			return createdUserModel;

		} finally {
			if (userDir != null) {
				userDir.close();
			}
			SessionUtil.close(session);
		}
	}

	@Override
	public void updateUserPostes(DocumentModel userModel, List<String> newPostesList) {
		STUser user = userModel.getAdapter(STUser.class);
		user.setPostes(newPostesList);
		
		// Dans le cas ou la postalAddress d'un utilisateur est enlevee, il faut placer un espace pour eviter une
		// erreur LDAP (Mantis 156074)
		try {
			final STUser oldVersionUser = this.getUserModel(userModel.getId()).getAdapter(STUser.class); // user en base
			
			final String sourcePostalAddress = oldVersionUser.getPostalAddress();
			final String newPostalAddress = user.getPostalAddress();

			if (sourcePostalAddress != null && !sourcePostalAddress.isEmpty()
					&& (newPostalAddress == null || newPostalAddress.isEmpty())) {
				user.setPostalAddress(" ");
			}
			
		} catch (ClientException e) {
			final String userName = user.getUsername();
			LOG.error("Erreur lors de la récupération de l'id en base de l'utilisateur " + userName + 
					"alors que sa postalAddress est vidée", e);
		} 
	}

	@Override
	public void updateUser(DocumentModel userModel, List<String> newPostesList) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();

		updateUserPostes(userModel, newPostesList);
		updateUserData(userModel);
		String userId = getUserId(userModel);
		// Event pour le journal
		CoreSession session = SessionUtil.getCoreSession();
		try {
			String comment = "Modification d'un utilisateur [" + userId + "]";
			journalService.journaliserActionAdministration(session, USERMODIFIED_EVENT_ID, comment);
		} finally {
			if (session != null) {
				CoreInstance.getInstance().close(session);
			}
		}

	}

	@Override
	public void deleteUser(DocumentModel userModel) throws ClientException {
		final JournalService journalService = STServiceLocator.getJournalService();
		Session userDir = null;
		String userId = getUserId(userModel);
		CoreSession session = null;

		try {
			userDir = dirService.open(userDirectoryName);
			STUser user = userModel.getAdapter(STUser.class);
			user.setDeleted(true);
			userDir.updateEntry(user.getDocument());
			userDir.commit();
			notifyUserChanged(userId);
			notify(userId, USERDELETED_EVENT_ID);

			// Event pour le journal
			session = SessionUtil.getCoreSession();
			String comment = "Suppression d'un utilisateur [" + userId + "]";
			journalService.journaliserActionAdministration(session, USERDELETED_EVENT_ID, comment);
		} finally {
			if (userDir != null) {
				userDir.close();
			}
			if (session != null) {
				CoreInstance.getInstance().close(session);
			}
		}
	}

	@Override
	public void deleteUser(String userId) throws ClientException {
		Session userDir = null;
		try {
			userDir = dirService.open(userDirectoryName);
			if (!userDir.hasEntry(userId)) {
				throw new DirectoryException("User does not exist: " + userId);
			}
			DocumentModel user = userDir.getEntry(userId);
			deleteUser(user);

		} finally {
			if (userDir != null) {
				userDir.close();
			}
		}
	}

	@Override
	public void physicalDeleteUser(DocumentModel userModel) throws ClientException {
		String userId = getUserId(userModel);
		physicalDeleteUser(userId);
	}

	@Override
	public void physicalDeleteUser(String userId) throws ClientException {
		Session userDir = null;
		try {
			userDir = dirService.open(userDirectoryName);
			if (!userDir.hasEntry(userId)) {
				throw new DirectoryException("User does not exist: " + userId);
			}
			userDir.deleteEntry(userId);
			userDir.commit();
			notifyUserChanged(userId);
			notify(userId, USERDELETED_EVENT_ID);

		} finally {
			if (userDir != null) {
				userDir.close();
			}
		}
	}

	@Override
	public DocumentModelList searchUsers(String pattern) throws ClientException {
		DocumentModelList entries = new DocumentModelListImpl();
		if (pattern == null || pattern.length() == 0) {
			entries = searchUsers(Collections.<String, Serializable> emptyMap(), null);
		} else {
			Map<String, DocumentModel> uniqueEntries = new HashMap<String, DocumentModel>();

			for (Entry<String, MatchType> fieldEntry : userSearchFields.entrySet()) {
				Map<String, Serializable> filter = new HashMap<String, Serializable>();
				filter.put(fieldEntry.getKey(), pattern);
				Set<String> fulltext = new HashSet<String>(filter.keySet());
				filter.put("deleted", "FALSE");
				DocumentModelList fetchedEntries;
				if (fieldEntry.getValue() == MatchType.SUBSTRING) {
					fetchedEntries = searchUsers(filter, fulltext, null);
				} else {
					fetchedEntries = searchUsers(filter, null, null);
				}
				for (DocumentModel entry : fetchedEntries) {
					uniqueEntries.put(entry.getId(), entry);
				}
			}
			LOG.debug(String.format("found %d unique entries", uniqueEntries.size()));
			entries.addAll(uniqueEntries.values());
		}
		// sort
		Collections.sort(entries, new DocumentModelComparator(userSchemaName, getUserSortMap()));

		return entries;
	}

	@Override
	public void updateUserData(DocumentModel userModel) throws ClientException {
		Session userDir = null;
		String userId = getUserId(userModel);
		try {
			userDir = dirService.open(userDirectoryName);

			if (!userDir.hasEntry(userId)) {
				throw new DirectoryException("user does not exist: " + userId);
			}

			String schema = dirService.getDirectorySchema(userDirectoryName);
			String clearUsername = (String) userModel.getProperty(schema, userDir.getIdField());
			String clearPassword = (String) userModel.getProperty(schema, userDir.getPasswordField());

			userDir.updateEntry(userModel);
			userDir.commit();

			syncDigestAuthPassword(clearUsername, clearPassword);

			notifyUserChanged(userId);
			notify(userId, USERMODIFIED_EVENT_ID);

		} finally {
			if (userDir != null) {
				userDir.close();
			}
		}
	}
}
