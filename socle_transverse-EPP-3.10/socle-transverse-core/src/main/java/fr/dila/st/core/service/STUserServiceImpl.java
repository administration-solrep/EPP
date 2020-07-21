package fr.dila.st.core.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.security.auth.login.LoginContext;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.sql.PasswordHelper;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.api.login.LoginComponent;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.service.STMailService;
import fr.dila.st.api.service.STUserService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.SessionUtil;

/**
 * @author ARN
 */
public class STUserServiceImpl implements STUserService {

	private static final long		serialVersionUID				= 7475407168269459198L;

	private static final STLogger	LOGGER							= STLogFactory.getLog(STUserServiceImpl.class);

	private static final String		ERROR_GET_INFO_USER				= "erreur lors de la récupération d'information sur l'utilisateur ";

	private static final String		ALPHA_NUM_LIST					= "abcdefghijklmonpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static final String		ALPHA_MIN_LIST					= "abcdefghijklmonpqrstuvwxyz";
	private static final String		ALPHA_MAJ_LIST					= "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String		NUM_LIST						= "0123456789";
	private static final String		CARAC_SPE_LIST					= "$?!:;()[]{}+-=_\\/|*,.";

	private static final String		POSTE_INCONNU					= "**poste inconnu**";
	private static final String		ERROR_MAIL_OR_LOGIN_INVALIDE	= "Identifiant ou mél. invalide";
	private static final String		MINSTERE_INCONNU				= "**ministere inconnu**";

	/**
	 * Default constructor
	 */
	public STUserServiceImpl() {
		// do nothing
	}

	@Override
	public String getUserFullNameAndCivilite(String userLogin) {
		if (isSystem(userLogin)) {
			return userLogin;
		}
		String fullNameAndCivilite = userLogin;
		final UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel userModel;
		try {
			userModel = userManager.getUserModel(fullNameAndCivilite);
			fullNameAndCivilite = getUserFullNameAndCivilite(userModel);
		} catch (Exception exc) {
			LOGGER.warn(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, ERROR_GET_INFO_USER + userLogin);
			LOGGER.debug(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc);
			return userLogin;
		}
		return fullNameAndCivilite;
	}

	@Override
	public String getUserFullNameAndCivilite(DocumentModel userModel) throws ClientException {
		// récupération des inforamtions sur l'utilisateur
		STUser user = userModel.getAdapter(STUser.class);
		String civilite = user.getTitle();
		String firstName = user.getFirstName();
		String lastName = user.getLastName();
		if (lastName == null || lastName.isEmpty()) {
			return userModel.getId();
		}
		StringBuilder fullName = new StringBuilder();
		// ajout de la civilite
		if (civilite != null && !civilite.isEmpty()) {
			fullName.append(civilite);
		}
		// ajout du nom
		fullName.append(" ").append(lastName);
		// ajout du prenom
		if (firstName != null && !firstName.isEmpty()) {
			fullName.append(" ").append(firstName);
		}
		return fullName.toString();
	}

	@Override
	public String getUserFullName(String userLogin) {
		if (StringUtils.isBlank(userLogin)) {
			return null;
		}
		if (isSystem(userLogin)) {
			return userLogin;
		}
		String fullName = null;
		final UserManager userManager = STServiceLocator.getUserManager();
		try {
			DocumentModel userModel = userManager.getUserModel(userLogin);
			if (userModel == null) {
				return userLogin;
			} else {
				fullName = getUserFullName(userModel);
			}
		} catch (Exception exc) {
			LOGGER.warn(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, ERROR_GET_INFO_USER + userLogin);
			LOGGER.debug(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc);
			return userLogin;
		}
		return fullName;
	}

	@Override
	public String getUserFullName(DocumentModel userModel) throws ClientException {
		// récupération des inforamtions sur l'utilisateur
		STUser user = userModel.getAdapter(STUser.class);
		String firstName = user.getFirstName();
		String lastName = user.getLastName();
		if (lastName == null || lastName.isEmpty()) {
			return userModel.getId();
		}

		// ajout du prenom
		StringBuilder fullName = new StringBuilder();
		if (firstName != null && !firstName.isEmpty()) {
			fullName.append(firstName);
		}

		// ajout du nom
		fullName.append(" ").append(lastName);
		return fullName.toString();
	}

	@Override
	public String generateAndSaveNewUserPassword(String userId) throws ClientException {

		UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel doc;
		doc = userManager.getUserModel(userId);

		STUser user = doc.getAdapter(STUser.class);

		if (user.getPassword() != null) {
			STServiceLocator.getSTPersistanceService().saveCurrentPassword(user.getPassword(), user.getUsername());
		}

		String password = getRandomSecurePassword();
		String hashedPassword = PasswordHelper.hashPassword(password, PasswordHelper.SSHA);

		user.setPassword(hashedPassword);
		user.setPwdReset(true);

		userManager.updateUser(user.getDocument());

		return password;
	}

	protected static String getRandomPassword(int length) {
		StringBuilder randomPwd = new StringBuilder();

		Random rand = new Random();
		for (int i = 0; i < length; i++) {
			randomPwd.append(ALPHA_NUM_LIST.charAt(rand.nextInt(ALPHA_NUM_LIST.length())));
		}
		return randomPwd.toString();
	}

	protected static String getRandomSecurePassword() {
		StringBuilder randomPwd = new StringBuilder();
		Random rand = new Random();
		for (int i = 0; i < 2; i++) {
			randomPwd.append(ALPHA_MIN_LIST.charAt(rand.nextInt(ALPHA_MIN_LIST.length())));
		}
		for (int i = 0; i < 2; i++) {
			randomPwd.append(ALPHA_MAJ_LIST.charAt(rand.nextInt(ALPHA_MAJ_LIST.length())));
		}
		for (int i = 0; i < 2; i++) {
			randomPwd.append(CARAC_SPE_LIST.charAt(rand.nextInt(CARAC_SPE_LIST.length())));
		}
		for (int i = 0; i < 2; i++) {
			randomPwd.append(NUM_LIST.charAt(rand.nextInt(NUM_LIST.length())));
		}
		return randomPwd.toString();
	}

	@Override
	public boolean isUserPasswordResetNeeded(String userLogin) throws ClientException {
		boolean rstPassword = false;
		final UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel userModel;
		try {
			userModel = userManager.getUserModel(userLogin);
			STUser user = userModel.getAdapter(STUser.class);
			rstPassword = user.isPwdReset();
		} catch (ClientException exc) {
			throw new ClientException(ERROR_GET_INFO_USER, exc);
		}
		return rstPassword;
	}

	@Override
	public void forceChangeOutdatedPassword(String userLogin) throws ClientException {
		final UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel userModel;
		try {
			userModel = userManager.getUserModel(userLogin);
			STUser user = userModel.getAdapter(STUser.class);
			// Force la nécessité d'un changement de mot de passe
			user.setPwdReset(true);
			userManager.updateUser(user.getDocument());
		} catch (ClientException exc) {
			throw new ClientException(ERROR_GET_INFO_USER, exc);
		}
	}

	@Override
	public void askResetPassword(String userLogin, String email) throws Exception {

		UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel doc;
		doc = userManager.getUserModel(userLogin);

		if (doc == null) {
			throw new ClientException(ERROR_MAIL_OR_LOGIN_INVALIDE);
		}

		STUser user = doc.getAdapter(STUser.class);

		if (email != null && email.equalsIgnoreCase(user.getEmail())) {
			STMailService stMailService = STServiceLocator.getSTMailService();

			LoginContext loginContext = null;
			CoreSession coreSession = null;
			try {
				loginContext = Framework.login();
				coreSession = SessionUtil.getCoreSession();
				String password = generateAndSaveNewUserPassword(userLogin);
				stMailService.sendMailResetPassword(coreSession, userLogin, password);
			} catch (Exception exc) {
				throw new Exception("Erreur lors de la génération du mot de passe", exc);
			} finally {
				SessionUtil.close(coreSession);
				// logout
				if (loginContext != null) {
					loginContext.logout();
				}
			}
		} else {
			throw new Exception(ERROR_MAIL_OR_LOGIN_INVALIDE);
		}
	}

	@Override
	public String getUserInfo(String userLogin, String format) {
		String fullName = userLogin;
		final UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel userModel;
		try {
			userModel = userManager.getUserModel(fullName);
			fullName = getUserInfo(userModel, format);
		} catch (ClientException exc) {
			LOGGER.warn(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, ERROR_GET_INFO_USER + userLogin);
			LOGGER.debug(null, STLogEnumImpl.FAIL_GET_INFORMATION_TEC, exc);
			return userLogin;
		}
		return fullName;
	}

	@Override
	public String getUserInfo(DocumentModel userModel, String format) throws ClientException {
		String userName = null;

		if (userModel == null) {
			return userName;
		} else {
			if (format == null || format.isEmpty()) {
				userName = "c p n t m";
			} else {
				userName = format;
			}
			STUser user = userModel.getAdapter(STUser.class);
			String title = user.getTitle() == null ? "" : user.getTitle();
			String first = user.getFirstName() == null ? "" : user.getFirstName();
			String last = user.getLastName() == null ? "" : user.getLastName();
			String phone = user.getTelephoneNumber() == null ? "" : user.getTelephoneNumber();
			String mail = user.getEmail() == null ? "" : user.getEmail();

			userName = userName.replace("c", "{c}");
			userName = userName.replace("C", "{C}");
			userName = userName.replace("p", "{p}");
			userName = userName.replace("n", "{n}");
			userName = userName.replace("t", "{t}");
			userName = userName.replace("m", "{m}");

			userName = userName.replace("{c}", title);
			if (userName.contains("{C}")) {
				userName = userName.replace("{C}", title);
				userName = userName.replace("Monsieur", "M.");
				userName = userName.replace("Madame", "Mme.");
			}
			userName = userName.replace("{p}", first);
			userName = userName.replace("{n}", last);
			userName = userName.replace("{t}", phone);
			userName = userName.replace("{m}", mail);

			return userName.trim();
		}
	}

	@Override
	public String getUserProfils(String userId) throws ClientException {
		try {
			final UserManager userManager = STServiceLocator.getUserManager();
			if (userId == null || userId.isEmpty() || userManager == null) {
				return null;
			}
			// récupération de l'utilisateur
			DocumentModel userModel = userManager.getUserModel(userId);
			STUser stUserModel = userModel.getAdapter(STUser.class);
			if (stUserModel == null) {
				return null;
			}
			// récupération des identifiants de postes
			List<String> postesIds = stUserModel.getPostes();
			if (postesIds == null || postesIds.isEmpty()) {
				return "";
			}
			// récupérations des labels des postes
			List<String> listPosteLabels = new ArrayList<String>();

			List<PosteNode> listNode = STServiceLocator.getSTPostesService().getPostesNodes(postesIds);
			for (OrganigrammeNode node : listNode) {
				// récpération du label du poste
				if (node.getLabel() != null) {
					listPosteLabels.add(node.getLabel());
				}
			}
			// affichage de la liste des postes
			if (listPosteLabels.isEmpty()) {
				return "";
			} else {
				return StringUtils.join(listPosteLabels, ", ");
			}
		} catch (ClientException exc) {
			LOGGER.warn(null, STLogEnumImpl.FAIL_GET_POSTE_TEC, userId);
			LOGGER.debug(null, STLogEnumImpl.FAIL_GET_POSTE_TEC, exc);
			return POSTE_INCONNU;
		}
	}

	@Override
	public List<Address> getAddressFromUserList(List<STUser> recipients) throws ClientException {
		List<Address> emailsAdresses = new ArrayList<Address>();
		if (recipients == null || recipients.isEmpty()) {
			return emailsAdresses;
		}

		// récupération des adresses mails
		for (STUser stUser : recipients) {
			if (StringUtils.isNotBlank(stUser.getEmail())) {
				try {
					String name = null;
					if (stUser.getLastName() != null && stUser.getFirstName() != null) {
						name = stUser.getFirstName() + " " + stUser.getLastName();
					} else if (stUser.getLastName() != null) {
						name = stUser.getLastName();
					}
					emailsAdresses.add(new InternetAddress(stUser.getEmail(), name));
				} catch (UnsupportedEncodingException exc) {
					LOGGER.error(null, STLogEnumImpl.FAIL_GET_MAIL_TEC, exc);
				}
			}
		}
		return emailsAdresses;
	}

	/**
	 * Retourne la liste des adresses email sous forme de texte.
	 * 
	 * @param addressList
	 *            Liste d'adresses email
	 * @return Liste des adresses email sous forme de texte
	 */
	protected List<String> fromAddressToString(List<Address> addressList) {
		List<String> recipients = new ArrayList<String>();
		for (Address address : addressList) {
			recipients.add(address.toString());
		}
		return recipients;
	}

	@Override
	public List<String> getEmailAddressFromUserList(List<STUser> recipients) throws ClientException {
		List<Address> addressList = getAddressFromUserList(recipients);
		return fromAddressToString(addressList);
	}

	private boolean isSystem(final String userId) {
		return LoginComponent.SYSTEM_USERNAME.equals(userId);
	}

	@Override
	public String getUserMinisteres(String userId) throws ClientException {

		List<PosteNode> posteList = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
		List<String> ministeres = new ArrayList<String>();
		if (posteList != null && !posteList.isEmpty()) {
			for (PosteNode poste : posteList) {
				List<EntiteNode> entiteNodeList = STServiceLocator.getSTMinisteresService().getMinisteresParents(poste);
				if (entiteNodeList != null && !entiteNodeList.isEmpty()) {
					EntiteNode entiteNode = entiteNodeList.get(0);
					if (!ministeres.contains(entiteNode.getLabel())) {
						ministeres.add(entiteNode.getLabel());
					}
				}
			}
		}

		if (ministeres.isEmpty()) {
			return MINSTERE_INCONNU;
		}
		// On concatène ensuite la liste de ministères
		return StringUtils.join(ministeres, ", ");
	}

	@Override
	public String getAllDirectionsRattachement(String userId) throws ClientException {
		List<PosteNode> posteListNode = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
		List<String> directions = new ArrayList<String>();

		if (posteListNode != null && !posteListNode.isEmpty()) {
			for (PosteNode posteNode : posteListNode) {
				String posteId = posteNode.getId();
				List<OrganigrammeNode> dirNodeList = STServiceLocator.getSTUsAndDirectionService()
						.getDirectionFromPoste(posteId);
				if (dirNodeList != null && !dirNodeList.isEmpty()) {
					OrganigrammeNode dirNode = dirNodeList.get(0);
					if (!directions.contains(dirNode.getLabel())) {
						directions.add(dirNode.getLabel());
					}
				}
			}
		}
		return StringUtils.join(directions, ", ");
	}

	@Override
	public String getUserPostes(String userId) throws ClientException {
		List<PosteNode> posteListNode = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
		Set<String> postes = new HashSet<String>();

		if (posteListNode != null && !posteListNode.isEmpty()) {
			for (PosteNode posteNode : posteListNode) {
				postes.add(posteNode.getLabel());
			}
		}
		return StringUtils.join(postes, ", ");
	}

	@Override
	public List<String> getAllUserMinisteresId(String userId) throws ClientException {
		List<PosteNode> posteList = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
		List<String> ministeres = new ArrayList<String>();
		if (posteList != null && !posteList.isEmpty()) {
			for (PosteNode poste : posteList) {
				List<EntiteNode> entiteNodeList = STServiceLocator.getSTMinisteresService().getMinisteresParents(poste);
				if (entiteNodeList != null && !entiteNodeList.isEmpty()) {
					EntiteNode entiteNode = entiteNodeList.get(0);
					if (!ministeres.contains(entiteNode.getId())) {
						ministeres.add(entiteNode.getId());
					}
				}
			}
		}
		return ministeres;
	}
}
