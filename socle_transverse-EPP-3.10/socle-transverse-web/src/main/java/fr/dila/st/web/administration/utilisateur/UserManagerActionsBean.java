package fr.dila.st.web.administration.utilisateur;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.directory.sql.PasswordHelper;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.exceptions.UserAlreadyExistsException;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.constant.STViewConstant;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.service.STUserManager;
import fr.dila.st.api.service.organigramme.STPostesService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.user.STPrincipalImpl;
import fr.dila.st.core.util.PropertyUtil;

/**
 * Web du Bean de gestion des utilisateurs.
 * 
 * @author jtremeaux
 */
@Name("userManagerActions")
@Scope(CONVERSATION)
@Install(precedence = FRAMEWORK + 1)
public class UserManagerActionsBean extends org.nuxeo.ecm.webapp.security.UserManagerActionsBean {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= -4574290063095862883L;

	private static final Log		LOG					= LogFactory.getLog(UserManagerActionsBean.class);
	private static final STLogger	LOGGER				= STLogFactory.getLog(UserManagerActionsBean.class);

	private static final String		MINUSCULE			= "^.*(\\p{javaLowerCase})+.*$";
	private static final String		MAJUSCULE			= "^.*(\\p{javaUpperCase})+.*$";
	private static final String		NUMBER				= "^.*(\\d)+.*$";
	private static final String		SPECIAL_CHAR		= "^.*[(`~!@#$%^&*()_+={}\\[\\]\\\\|:;\"\'<>,.?/-]+.*$";

	protected String				newUserPasswordFirst;

	protected String				newUserPasswordSecond;

	/**
	 * search fields
	 */
	protected String				username;

	protected String				firstName;

	protected String				lastName;

	protected String				ministere;

	protected String				direction;

	protected Date					dateDebut;

	protected boolean				fromRecherche		= false;

	protected List<String>			userPostes;
	
	protected List<DocumentModel> 	userProfils;

	protected boolean				displayCancelButton	= false;
	
	@In(create = true, required = false)
    protected transient ProfileSelectionActionsBean profileSelectionActions;
	
	/**
	 * Default constructor
	 */
	public UserManagerActionsBean() {
		super();
	}

	/**
	 * Détermine si l'utilisateur connecté est permanent.
	 * 
	 * @return Utilisateur permanent
	 * @throws ClientException
	 */
	@Factory(value = "currentUserPermanent", scope = ScopeType.SESSION)
	public boolean isCurrentUserPermanent() throws ClientException {
		DocumentModel userDoc = userManager.getUserModel(currentUser.getName());
		STUser stUser = userDoc.getAdapter(STUser.class);
		return stUser.isPermanent();
	}

	public boolean isFromCurrentUserMinistere(STUser user) throws ClientException {
		DocumentModel currentUserModel = refreshUser(currentUser.getName());
		STUser currentUser = currentUserModel.getAdapter(STUser.class);
		List<String> posteListCurrentUser = currentUser.getPostes();
		List<String> posteListUser = user.getPostes();


		if (posteListCurrentUser != null && !posteListCurrentUser.isEmpty() &&
				posteListUser != null && !posteListUser.isEmpty()) {

			for (String posteCurrentUser : posteListCurrentUser) {
				List<EntiteNode> entiteNodeListCurrentUser = STServiceLocator.getSTMinisteresService().getMinistereParentFromPoste(posteCurrentUser);
				for (EntiteNode entiteNodeCurrentUser : entiteNodeListCurrentUser){
					for (String posteUser : posteListUser) {
						List<EntiteNode> entiteNodeListUser = STServiceLocator.getSTMinisteresService().getMinistereParentFromPoste(posteUser);
						for(EntiteNode entiteNodeUser : entiteNodeListUser) {
							if(entiteNodeCurrentUser.getLabel().equals(entiteNodeUser.getLabel())) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	protected boolean getCanEditUsers(boolean allowCurrentUser) throws ClientException {
		if (userManager.areUsersReadOnly()) {
			return false;
		}
		if (currentUser instanceof STPrincipalImpl) {
			STPrincipal pal = (STPrincipal) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_UPDATER)) {
				return true;
			}
			if (allowCurrentUser && selectedUser != null) {
				if (pal.getName().equals(selectedUser.getId())) {
					return true;
				}
			}
		}
		return false;
	}

	protected boolean getCanCreateUsers(boolean allowCurrentUser) throws ClientException {
		if (userManager.areUsersReadOnly()) {
			return false;
		}
		if (currentUser instanceof NuxeoPrincipal) {
			NuxeoPrincipal pal = (NuxeoPrincipal) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_CREATOR)) {
				return true;
			}
		}
		return false;
	}

	protected boolean getCanDeleteUsers(boolean allowCurrentUser) throws ClientException {
		if (userManager.areUsersReadOnly()) {
			return false;
		}
		if (currentUser instanceof STPrincipalImpl) {
			STPrincipal pal = (STPrincipal) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_DELETER)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean getAllowEditUser() throws ClientException {
		return getCanEditUsers(true) && (selectedUser != null && !BaseSession.isReadOnlyEntry(selectedUser));
	}

	public boolean getAllowEditUserTechnicalData() throws ClientException {
		return getCanEditUsers(false) && !BaseSession.isReadOnlyEntry(selectedUser);
	}

	@Override
	public boolean getAllowCreateUser() throws ClientException {
		return getCanCreateUsers(false);
	}

	/**
	 * Retourne vrai si l'utilisateur est UtilisateurUpdater
	 * 
	 * @return
	 * @throws ClientException
	 */
	public boolean isUserUpdater() throws ClientException {
		if (currentUser instanceof STPrincipalImpl) {
			STPrincipalImpl pal = (STPrincipalImpl) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.UTILISATEUR_UPDATER)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retourne la liste des profils associés à un utilisateur. La liste des profils correspond à celle stockée en base,
	 * c'est à dire qu'elle ne comprend pas les délégations.
	 * 
	 * @param userId
	 *            Identifiant technique de l'utilisateur
	 * @return Liste des profils
	 * @throws ClientException
	 */
	public List<String> getProfilList(String userId) throws ClientException {
		final UserManager userManager = STServiceLocator.getUserManager();
		DocumentModel userDoc = userManager.getUserModel(userId);
		STUser user = userDoc.getAdapter(STUser.class);
		return user.getGroups();
	}

	@Override
	public boolean getAllowDeleteUser() throws ClientException {
		return getCanDeleteUsers(false) && !BaseSession.isReadOnlyEntry(selectedUser);
	}

	public String viewMonCompte() throws ClientException {
		return viewMonCompte(selectedUser, true);
	}

	public String viewMonCompte(String userName) throws ClientException {
		return viewMonCompte(userManager.getUserModel(userName), false);
	}

	protected String viewMonCompte(DocumentModel user, boolean refresh) throws ClientException {
		if (user != null) {
			selectedUser = user;
			if (refresh) {
				selectedUser = refreshUser(user.getId());
			}
			if (selectedUser != null) {
				return "view_mon_compte";
			}
		}
		return null;
	}

	public String editMonCompte() throws ClientException {
		selectedUser = refreshUser(selectedUser.getId());
		return "edit_mon_compte";
	}

	public String resetCurrentUserPassword(String userName) throws ClientException {
		selectedUser = refreshUser(userName);
		return "reset_current_user_password";
	}

	@Override
	public void validateUserName(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof String) || !StringUtils.containsOnly((String) value, VALID_CHARS)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"label.userManager.wrong.username"), null);
			// also add global message
			context.addMessage(null, message);
			throw new ValidatorException(message);
		}
		if (((String) value).length() < 8) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"label.userManager.wrongLength.username"), null);
			// also add global message
			context.addMessage(null, message);
			throw new ValidatorException(message);
		}
	}

	/**
	 * Surcharge getNewUser d'utilisateur afin de renseigner la date de début par défaut
	 */
	@Override
	public DocumentModel getNewUser() throws ClientException {
		if (newUser == null) {
			newUser = userManager.getBareUserModel();
		}
		STUser newSTUser = newUser.getAdapter(STUser.class);
		// M156903 - getNewUser() is called multiple times by creation form
		// as create_user.xhtml page does not cache method result
		// (<nxl:layout name="user" mode="create" value="#{userManagerActions.newUser}" />)
		// protect already set value
		if (newSTUser.getDateDebut() == null) {
			newSTUser.setDateDebut(Calendar.getInstance());
		}
		userPostes = new ArrayList<String>();
		return newUser;
	}

	@Override
	public String changePassword() throws ClientException {
		STUser user = selectedUser.getAdapter(STUser.class);
		user.setPwdReset(false);
		if (!PasswordHelper.isHashed(user.getPassword())) {
			String password = user.getPassword();
			user.setPassword(PasswordHelper.hashPassword(password, PasswordHelper.SSHA));
			STServiceLocator.getSTPersistanceService().saveCurrentPassword(
					PasswordHelper.hashPassword(password, PasswordHelper.SSHA), user.getUsername());
			CoreSession session = super.navigationContext.getOrCreateDocumentManager();
			final String lUsername = user.getUsername();
			new UnrestrictedSessionRunner(session) {
				@Override
				public void run() throws ClientException {
					final DocumentModel docModel = STServiceLocator.getUserWorkspaceService()
							.getCurrentUserPersonalWorkspace(lUsername, session.getRootDocument());
					final PathRef profilUserRef = new PathRef(docModel.getPathAsString());
					DocumentModel profilUtilisateurDocument = session.getDocument(profilUserRef);
					// Initialisation de la date de changement de mot de passe
					if (profilUtilisateurDocument != null) {
						PropertyUtil.setProperty(profilUtilisateurDocument, "profil_utilisateur",
								"pru:dernierChangementMotDePasse", Calendar.getInstance());
						session.saveDocument(profilUtilisateurDocument);
						session.save();
					}
				}
			}.runUnrestricted();
		}
		return super.changePassword();
	}

	public String forcedChangePassword() throws ClientException {
		STUser user = selectedUser.getAdapter(STUser.class);

		if (!newUserPasswordFirst.equals(newUserPasswordSecond)) {
			facesMessages.add(StatusMessage.Severity.WARN,
					resourcesAccessor.getMessages().get("warn.user.password.notEqual"));
			return null;
		}

		if (!PasswordHelper.isHashed(newUserPasswordFirst)) {
			user.setPassword(PasswordHelper.hashPassword(newUserPasswordFirst, PasswordHelper.SSHA));
			STServiceLocator.getSTPersistanceService().saveCurrentPassword(
					PasswordHelper.hashPassword(newUserPasswordFirst, PasswordHelper.SSHA), user.getUsername());
		}

		user.setPwdReset(false);
		
		loadUserProfils();
		
		return super.changePassword();
	}

	/**
	 * Validation de la date de fin (obligatoire si l'utilisateur est temporaire) TODO : Ne fonctionne pas pour le
	 * moment avec le widget date de NUXEO
	 * 
	 * @param facesContext
	 * @param uIComponent
	 * @param object
	 * @throws ValidatorException
	 */
	public void validateDateFin(FacesContext facesContext, UIComponent component, Object object)
			throws ValidatorException {
		Date inDateFin = (Date) object;

		// TODO Récuperer l'id autrement
		String temporaryId = "nxw_temporary_select";// (String) attributes.get("temporaryId");
		HtmlSelectOneRadio temporaryComp = (HtmlSelectOneRadio) component.findComponent(temporaryId);
		Object temporaryValue = temporaryComp.getLocalValue();

		if (temporaryValue != null && "TRUE".equals(temporaryValue) && inDateFin == null) {
			FacesMessage message = new FacesMessage();
			message.setSummary("Un utilisateur temporaire doit avoir une date de fin.");
			throw new ValidatorException(message);
		}
		Date today = DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
		if (inDateFin != null && inDateFin.compareTo(today) < 0) {
			FacesMessage message = new FacesMessage();
			message.setSummary("La date de fin doit être au moins égale à la date du jour");
			throw new ValidatorException(message);
		}
	}

	@Override
	public void validatePassword(FacesContext context, UIComponent component, Object value) {
		String username = (selectedUser == null ? currentUser.getName() : selectedUser.getId());
		Map<String, Object> attributes = component.getAttributes();
		String firstPasswordInputId = (String) attributes.get("firstPasswordInputId");
		String secondPasswordInputId = (String) attributes.get("secondPasswordInputId");
		if (firstPasswordInputId == null || secondPasswordInputId == null) {
			LOG.error("Cannot validate passwords: input id(s) not found");
			return;
		}

		UIInput firstPasswordComp = (UIInput) component.findComponent(firstPasswordInputId);
		UIInput secondPasswordComp = (UIInput) component.findComponent(secondPasswordInputId);
		if (firstPasswordComp == null || secondPasswordComp == null) {
			LOG.error("Cannot validate passwords: input(s) not found");
			return;
		}

		Object firstPassword = firstPasswordComp.getLocalValue();
		Object secondPassword = secondPasswordComp.getLocalValue();

		if (firstPassword == null || secondPassword == null) {
			LOG.error("Cannot validate passwords: value(s) not found");
			return;
		}

		if (!firstPassword.equals(secondPassword)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"label.userManager.password.not.identical"), null);
			throw new ValidatorException(message);
		}

		// Préparation du message d'erreur global
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
				"label.userManager.password.not.valid"), null);
		try {
			if (!userManager.validatePassword((String) firstPassword)) {
				throw new ValidatorException(message);
			}
		} catch (ClientException e) {
			throw new ValidatorException(message);
		}
		try {
			if (userManager.checkUsernamePassword(username, ((String) firstPassword))) {
				throw new ValidatorException(message);
			}
		} catch (ClientException e) {
			LOG.warn("Cannot validate that the password has changed : impossible to check old password");
		}

		try {
			if (STServiceLocator.getSTPersistanceService().checkPasswordHistory((String) firstPassword, username)) {
				throw new ValidatorException(message);
			}
		} catch (ClientException e) {
			throw new ValidatorException(message);
		}

		// Vérification de l'absence de similarité avec l'identifiant
		if (username != null && ((String) firstPassword).indexOf(username) != -1) {
			throw new ValidatorException(message);
		}

		// Vérification du contenu du mot de passe avec respect des patterns
		if (!((String) firstPassword).matches(MINUSCULE) || !((String) firstPassword).matches(MAJUSCULE)
				|| !((String) firstPassword).matches(NUMBER) || !((String) firstPassword).matches(SPECIAL_CHAR)) {
			throw new ValidatorException(message);
		}
	}

	/**
	 * @return the newUserPasswordFirst
	 */
	public String getNewUserPasswordFirst() {
		return newUserPasswordFirst;
	}

	/**
	 * @param newUserPasswordFirst
	 *            the newUserPasswordFirst to set
	 */
	public void setNewUserPasswordFirst(String newUserPasswordFirst) {
		this.newUserPasswordFirst = newUserPasswordFirst;
	}

	/**
	 * @return the newUserPasswordSecond
	 */
	public String getNewUserPasswordSecond() {
		return newUserPasswordSecond;
	}

	/**
	 * @param newUserPasswordSecond
	 *            the newUserPasswordSecond to set
	 */
	public void setNewUserPasswordSecond(String newUserPasswordSecond) {
		this.newUserPasswordSecond = newUserPasswordSecond;
	}

	/**
	 * @return the fromRecherche
	 */
	public boolean isFromRecherche() {
		return fromRecherche;
	}

	/**
	 * @param fromRecherche
	 *            the fromRecherche to set
	 */
	public void setFromRecherche(boolean fromRecherche) {
		this.fromRecherche = fromRecherche;
	}

	/**
	 * @return the displayCancelButton
	 */
	public boolean isDisplayCancelButton() {
		return displayCancelButton;
	}

	/**
	 * @param displayCancelButton
	 *            the displayCancelButton to set
	 */
	public void isDisplayCancelButton(boolean displayCancelButton) {
		this.displayCancelButton = displayCancelButton;
	}

	@Override
	public String viewUser() throws ClientException {
		fromRecherche = false;
		return super.viewUser();
	}

	public String viewUserFromRecherche() throws ClientException {
		fromRecherche = true;
		return super.viewUser();
	}

	/**
	 * Lance la recherche d'utilisateur
	 * 
	 * @return null
	 * @throws ClientException
	 */
	public String searchUser() throws ClientException {

		Map<String, Serializable> filter = new HashMap<String, Serializable>();

		boolean fieldSearch = false;
		boolean minSearch = false;
		boolean dirSearch = false;

		if (!StringUtils.isEmpty(username)) {
			filter.put("username", username);
			fieldSearch = true;
		}
		if (!StringUtils.isEmpty(firstName)) {
			filter.put("firstName", firstName);
			fieldSearch = true;
		}
		if (!StringUtils.isEmpty(lastName)) {
			filter.put("lastName", lastName);
			fieldSearch = true;
		}
		if (dateDebut != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd000000");
			filter.put("dateDebut", sdf.format(dateDebut) + "Z");
			fieldSearch = true;
		}

		DocumentModelList userMinistere = new DocumentModelListImpl();

		if (!StringUtils.isEmpty(ministere)) {
			List<STUser> stUserList = STServiceLocator.getSTMinisteresService().getUserFromMinistere(ministere);

			for (STUser user : stUserList) {
				userMinistere.add(user.getDocument());
			}
			minSearch = true;
		}

		DocumentModelList userDirection = new DocumentModelListImpl();

		if (!StringUtils.isEmpty(direction)) {
			List<STUser> stUserList = STServiceLocator.getSTUsAndDirectionService().getUserFromUniteStructurelle(
					direction);

			for (STUser user : stUserList) {
				userDirection.add(user.getDocument());
			}
			dirSearch = true;
		}

		users = new DocumentModelListImpl();
		if (fieldSearch) {
			Set<String> fulltext = new HashSet<String>();
			fulltext.addAll(filter.keySet());
			if (fulltext.contains("dateDebut")) {
				fulltext.remove("dateDebut");
			}
			DocumentModelList usersFieldSearch = STServiceLocator.getUserManager().searchUsers(filter, fulltext);

			if (!minSearch && !dirSearch) {
				users = usersFieldSearch;
			} else {
				for (DocumentModel user : usersFieldSearch) {

					if (minSearch && !dirSearch) {
						if (userMinistere.contains(user)) {
							users.add(user);
						}
					} else if (!minSearch && dirSearch) {
						if (userDirection.contains(user)) {
							users.add(user);
						}
					} else if (minSearch && dirSearch) {
						if (userMinistere.contains(user) && userDirection.contains(user)) {
							users.add(user);
						}
					}
				}
			}
		} else {

			if (minSearch && !dirSearch) {
				users = userMinistere;
			} else if (!minSearch && dirSearch) {
				users = userDirection;
			} else if (minSearch && dirSearch) {
				for (DocumentModel user : userDirection) {
					if (userMinistere.contains(user)) {
						users.add(user);
					}
				}
			}
		}

		return null;

	}

	public String resetSearch() {
		selectedLetter = null;
		selectedUser = null;
		username = null;
		firstName = null;
		lastName = null;
		ministere = null;
		direction = null;
		dateDebut = null;
		users = new DocumentModelListImpl();
		return null;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the ministere
	 */
	public String getMinistere() {
		return ministere;
	}

	/**
	 * @param ministere
	 *            the ministere to set
	 */
	public void setMinistere(String ministere) {
		this.ministere = ministere;
	}

	/**
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * @return the dateDebut
	 */
	public Date getDateDebut() {
		return dateDebut;
	}

	/**
	 * @param dateDebut
	 *            the dateDebut to set
	 */
	public void setDateDebut(Date dateDebut) {
		this.dateDebut = dateDebut;
	}

	public String resetCreation() throws ClientException {
		newUser = null;
		return viewUsers();
	}

	public void validateMail(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof String)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"label.mail.empty.email"), null);
			throw new ValidatorException(message);
		}

		// On peut mettre plusieurs mails dans le même champ, séparé par des espaces, des "," ou des ";"
		String emailsStr = ((String) value).trim();
		String[] emails = emailsStr.split(",|;| ");
		for (String email : emails) {
			if (!StringUtils.isBlank(email)
					&& !email.trim().matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9._-]{2,}\\.[a-zA-Z]{2,4}$")) {
				FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
						"label.mail.wrong.email"), null);
				throw new ValidatorException(message);
			}
		}
	}

	/**
	 * Reset du user avant création d'un nouveau
	 * 
	 * @return
	 * @throws ClientException
	 */
	public String newCreation() throws ClientException {
		newUser = null;
		selectedUser = null;
		userPostes = new ArrayList<String>();
		userProfils = new ArrayList<DocumentModel>();
		return STViewConstant.CREATE_USER_VIEW;
	}

	@Override
	public String createUser() throws ClientException, UserAlreadyExistsException {
		try {
			boolean profilsOk = addProfilesToUser(newUser);
			if(!profilsOk) {
				facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("error.userManager.emptyProfils"));
				return null;
			}
			selectedUser = userManager.createUser(newUser);
			
			((STUserManager) userManager).updateUserPostes(selectedUser, userPostes);
			newUser = null;
			userPostes = new ArrayList<String>();
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("info.userManager.userCreated"));
			resetUsers();

			if (selectedUser.getContextData(STConstant.MAIL_SEND_ERROR) != null
					&& (Boolean) selectedUser.getContextData(STConstant.MAIL_SEND_ERROR)) {
				selectedUser.putContextData(STConstant.MAIL_SEND_ERROR, null);
				facesMessages.add(StatusMessage.Severity.ERROR, "Erreur lors de l'envoi du mél de mot de passe");
			}

			return viewUser();
		} catch (UserAlreadyExistsException e) {
			facesMessages.add(StatusMessage.Severity.ERROR,
					resourcesAccessor.getMessages().get("error.userManager.userAlreadyExists"));
			return null;
		} catch (ClientException e) {
			LOGGER.error(STLogEnumImpl.CREATE_USER_TEC, e);
			facesMessages.add(StatusMessage.Severity.ERROR, e.getMessage());
			return null;
		}
	}

	public String viewUsersFromLetter() throws ClientException {
		searchString = null;
		searchUserModel = null;
		return viewUsers();
	}

	/**
	 * @param selectedUserId
	 *            the selectedUserId to set
	 * @throws ClientException
	 */
	public void setSelectedUserId(String selectedUserId) throws ClientException {
		loadUserForPopup(selectedUserId);
	}

	public void loadUserForPopup(String userId) throws ClientException {
		selectedUser = refreshUser(userId);
	}

	/**
	 * Retourne le ministère du premier poste de l'utilisateur
	 * 
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	public String getMinistereRattachement(String userId) throws ClientException {

		DocumentModel userModel = refreshUser(userId);
		STUser user = userModel.getAdapter(STUser.class);
		List<String> posteList = user.getPostes();

		if (posteList != null && !posteList.isEmpty()) {
			String posteId = posteList.get(0);
			List<EntiteNode> entiteNodeList = STServiceLocator.getSTMinisteresService().getMinistereParentFromPoste(
					posteId);
			if (entiteNodeList != null && !entiteNodeList.isEmpty()) {
				EntiteNode entiteNode = entiteNodeList.get(0);
				return entiteNode.getLabel();
			}
		}
		return null;
	}

	/**
	 * Retourne les ministères de l'utilisateur
	 * 
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	public List<String> getAllMinisteresRattachement(String userId) throws ClientException {

		List<PosteNode> posteList = STServiceLocator.getSTPostesService().getAllPostesForUser(userId);
		List<String> ministeres = new ArrayList<String>();

		if (posteList != null && !posteList.isEmpty()) {
			for (PosteNode poste : posteList) {
				List<EntiteNode> entiteNodeList = STServiceLocator.getSTMinisteresService().getMinisteresParents(poste);
				if (entiteNodeList != null && !entiteNodeList.isEmpty()) {
					EntiteNode entiteNode = entiteNodeList.get(0);
					ministeres.add(entiteNode.getLabel());
				}
			}
		}
		return ministeres;
	}

	/**
	 * Retourne les postes de l'utisateur
	 * 
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	public List<String> getAllPostesRattachement(String userId) throws ClientException {
		List<String> postesList = new ArrayList<String>();
		DocumentModel userModel = refreshUser(userId);
		STUser user = userModel.getAdapter(STUser.class);
		STPostesService posteService = STServiceLocator.getSTPostesService();
		for (String posteId : user.getPostes()) {
			postesList.add(posteService.getPoste(posteId).getLabel());
		}
		return postesList;
	}

	/**
	 * Retourne la direction du premier poste de l'utilisateur
	 * 
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	public String getDirectionRattachement(String userId) throws ClientException {
		DocumentModel userModel = refreshUser(userId);
		STUser user = userModel.getAdapter(STUser.class);
		List<String> posteList = user.getPostes();

		if (posteList != null && !posteList.isEmpty()) {
			String posteId = posteList.get(0);
			List<OrganigrammeNode> dirNodeList = STServiceLocator.getSTUsAndDirectionService().getDirectionFromPoste(
					posteId);
			if (dirNodeList != null && !dirNodeList.isEmpty()) {
				OrganigrammeNode dirNode = dirNodeList.get(0);
				return dirNode.getLabel();
			}
		}
		return null;
	}

	/**
	 * Retourne les directions de l'utilisateur
	 * 
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	public List<String> getAllDirectionsRattachement(String userId) throws ClientException {
		DocumentModel userModel = refreshUser(userId);
		STUser user = userModel.getAdapter(STUser.class);
		List<String> posteList = user.getPostes();
		List<String> directions = new ArrayList<String>();

		if (posteList != null && !posteList.isEmpty()) {
			for (String posteId : posteList) {
				List<OrganigrammeNode> dirNodeList = STServiceLocator.getSTUsAndDirectionService()
						.getDirectionFromPoste(posteId);
				if (dirNodeList != null && !dirNodeList.isEmpty()) {
					OrganigrammeNode dirNode = dirNodeList.get(0);
					directions.add(dirNode.getLabel());
				}
			}
		}
		return directions;
	}

	@Override
	protected void updateUserCatalog() throws ClientException {
		// on ne veut pas afficher les utilisateurs supprimés
		Map<String, Serializable> filter = new HashMap<String, Serializable>();
		filter.put("deleted", "FALSE");
		DocumentModelList allUsers = userManager.searchUsers(filter, null);
		userCatalog = new HashMap<String, DocumentModelList>();
		String userSortField = userManager.getUserSortField();
		for (DocumentModel user : allUsers) {

			String displayName = null;
			if (userSortField != null) {
				org.nuxeo.ecm.core.api.DataModel dm = user.getDataModels().values().iterator().next();
				displayName = (String) dm.getData(userSortField);
			}
			if (StringUtils.isEmpty(displayName)) {
				displayName = user.getId();
			}
			if (displayName != null) {
				String firstLetter = displayName.substring(0, 1).toUpperCase();

				DocumentModelList list = userCatalog.get(firstLetter);
				if (list == null) {
					list = new DocumentModelListImpl();
					userCatalog.put(firstLetter, list);
				}
				list.add(user);
			}
		}
	}

	/**
	 * Controle l'accès à la vue correspondante
	 * 
	 */
	public boolean isAccessAuthorized() {
		if (currentUser instanceof STPrincipalImpl) {
			STPrincipal ssPrincipal = (STPrincipal) currentUser;
			return (ssPrincipal.isAdministrator() || ssPrincipal
					.isMemberOf(STBaseFunctionConstant.ADMINISTRATION_UTILISATEUR_READER));
		}
		return false;
	}

	/**
	 * Surchargé afin de renseigner le realSelectedUser
	 */
	@Override
	protected String viewUser(DocumentModel user, boolean refresh) throws ClientException {
		if (user != null) {
			selectedUser = user;
			if (refresh) {
				selectedUser = refreshUser(user.getId());
			}
			if (selectedUser != null) {
				return "view_user";
			}
		}
		return null;

	}

	/**
	 * @param userPostes
	 *            the userPostes to set
	 */
	public void setUserPostes(List<String> userPostes) {
		this.userPostes = userPostes;
	}

	/**
	 * @return the userPostes
	 */
	public List<String> getUserPostes() {
		if (userPostes == null) {
			userPostes = new ArrayList<String>();
		}
		try {
			if (selectedUser != null) {
				userPostes = STServiceLocator.getSTPostesService().getAllPosteIdsForUser(selectedUser.getId());
			}
		} catch (ClientException ce) {
			LOGGER.error(null, STLogEnumImpl.FAIL_GET_POSTE_TEC, ce);
		}
		return userPostes;
	}
	
	/**
	 * @return the userProfils
	 * @throws ClientException 
	 */
	public List<DocumentModel> getUserProfils() throws ClientException {
		if (userProfils == null) {
			loadUserProfils();
		}
		return userProfils;
	}
	
	protected void loadUserProfils() throws ClientException {
		ProfileService profilService = STServiceLocator.getProfileService();
		Map<String, DocumentModel> profilMap = profilService.getProfilMap();

		if (selectedUser != null) {
			userProfils = new ArrayList<DocumentModel>();
			for (String profilName : getProfilList(selectedUser.getId())) {
				userProfils.add(profilMap.get(profilName.trim()));
			}
		}
	}

	/**
	 * Mise à jour des profils depuis la variable userProfils dans le
	 * documentModel avant de le sauvegarder. On vérifie ensuite que la liste
	 * des profils est non vide.
	 * 
	 * @return true si la liste des profils est non vide, false sinon.
	 */
	protected boolean addProfilesToUser(DocumentModel userModel) {
		// Mise à jour des profils de l'utilisateur
		if(userProfils.isEmpty()) {
			return false;
		}
		STUser user = userModel.getAdapter(STUser.class);
		List<String> profilIdList = new ArrayList<String>();
		for (DocumentModel profileModel : userProfils) {
			profilIdList.add(profileModel.getId());
		}
		user.setGroups(profilIdList);
		
		return true;
	}

	@Override
	public String updateUser() throws ClientException {
		try {
			boolean profilsOk = addProfilesToUser(selectedUser);
			if(!profilsOk) {
				facesMessages.add(StatusMessage.Severity.ERROR, resourcesAccessor.getMessages().get("error.userManager.emptyProfils"));
				return null;
			}
			
			((STUserManager) userManager).updateUser(selectedUser, userPostes);
			// refresh users and groups list
			resetUsers();
			return viewUser(selectedUser.getId());
		} catch (Exception t) {
			throw ClientException.wrap(t);
		}
	}
	
	public void removeProfile(DocumentModel profil) {
		userProfils.remove(profil);
	}
	  
	public void addProfile() throws ClientException {
		String profileId = profileSelectionActions.getCurrentProfile();
		
		ProfileService profilService = STServiceLocator.getProfileService();
		Map<String, DocumentModel> profilMap = profilService.getProfilMap();
		
		DocumentModel profilToAdd = profilMap.get(profileId);
		
		if(!userProfils.contains(profilToAdd) ) {
			userProfils.add(profilToAdd);
		}
	}
	
	@Override
	protected DocumentModel refreshUser(String userName) throws ClientException {
		DocumentModel user = super.refreshUser(userName);
		loadUserProfils();
		return user;
	}

}
