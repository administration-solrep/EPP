package fr.dila.solonepp.web.utilisateur;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringEscapeUtils;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.component.list.UIEditableList;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.platform.usermanager.UserManager;

import fr.dila.solonepp.api.administration.ProfilUtilisateur;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.ProfilUtilisateurService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.api.user.STUser;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

/**
 * Bean Seam de gestion des utilisateurs.
 * 
 * @author bgamard
 */
@Name("userManagerActions")
@Scope(CONVERSATION)
@Install(precedence = FRAMEWORK + 2)
public class UserManagerActionsBean extends fr.dila.st.web.administration.utilisateur.UserManagerActionsBean {

	/**
	 * Logger surcouche socle de log4j
	 */
	private static final STLogger	LOGGER				= STLogFactory.getLog(UserManagerActionsBean.class);

	@In(create = true, required = true)
	protected transient CoreSession	documentManager;

	private static final long		serialVersionUID	= 1L;

	protected boolean				passwordValid		= false;

	protected String				userPassword;

	protected String				errorName;
	
	/**
     * Id of the editable list component where selection ids are put.
     * <p>
     * Component must be an instance of {@link UIEditableList}
     */
    @RequestParameter
    protected String suggestionSelectionListId;
    
    protected String selectedValue;

	@Override
	protected boolean getCanEditUsers(boolean allowCurrentUser) throws ClientException {
		EppPrincipal currentPrincipal = (EppPrincipal) currentUser;
		EppPrincipal userPrincipal = (EppPrincipal) userManager.getPrincipal(selectedUser.getTitle());

		if (currentPrincipal.getInstitutionId() == null || userPrincipal.getInstitutionId() == null) {
			return false;
		}

		// Dans le cas de EPP, on ne peut éditer un utilisateur que s'il appartient à la même institution.
		return super.getCanEditUsers(allowCurrentUser)
				&& currentPrincipal.getInstitutionId().equals(userPrincipal.getInstitutionId());
	}

	protected boolean getCanDeleteUsers(boolean allowCurrentUser) throws ClientException {
		EppPrincipal currentPrincipal = (EppPrincipal) currentUser;
		EppPrincipal userPrincipal = (EppPrincipal) userManager.getPrincipal(selectedUser.getTitle());

		if (currentPrincipal.getInstitutionId() == null || userPrincipal.getInstitutionId() == null) {
			return false;
		}

		// Dans le cas de EPP, on ne peut supprimer un utilisateur que s'il appartient à la même institution.
		return super.getCanDeleteUsers(allowCurrentUser)
				&& currentPrincipal.getInstitutionId().equals(userPrincipal.getInstitutionId());
	}

	/**
	 * Change le mot de passe utilisateur par "newUserPasswordFirst" sans renvoyer l'utilisateur à la page de login.
	 * 
	 * @return String
	 * @throws ClientException
	 */
	public String forcedChangePasswordWithoutLogout() throws ClientException {
		if (passwordValid) {
			// on sélectionne l'utilisateur courant
			this.selectedUser = refreshUser(currentUser.getName());
			STUser user = selectedUser.getAdapter(STUser.class);
			if (user != null) {
				try {
					final ProfilUtilisateurService profilUtilisateurService = SolonEppServiceLocator
							.getProfilUtilisateurService();
					ProfilUtilisateur profilUtilisateur = null;
					DocumentModel profilDoc = profilUtilisateurService.getOrCreateUserProfilFromId(documentManager,
							user.getUsername());
					if (profilDoc != null) {
						profilUtilisateur = profilDoc.getAdapter(ProfilUtilisateur.class);
					}
					if (profilUtilisateur != null) {
						// Mise à jour de la date
						profilUtilisateur.setDernierChangementMotDePasse(Calendar.getInstance());
						documentManager.saveDocument(profilUtilisateur.getDocument());
						documentManager.save();
					}
				} catch (Exception e) {
					LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_PROFIL_UTILISATEUR_TEC);
				}
			}
			super.forcedChangePassword();
			clearPasswordFields();
		}
		return null;
	}

	@Override
	public String changePassword() throws ClientException {
		// Ajout de la date de changement du mot de passe
		STUser user = selectedUser.getAdapter(STUser.class);
		if (user != null) {
			try {
				SolonEppServiceLocator.getProfilUtilisateurService().changeDatePassword(documentManager,
						user.getUsername());
			} catch (Exception e) {
				LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_PROFIL_UTILISATEUR_TEC);
			}
		}
		return super.changePassword();
	}

	@Override
	public String forcedChangePassword() throws ClientException {
		// Ajout de la date de changement du mot de passe
		STUser user = selectedUser.getAdapter(STUser.class);
		if (user != null) {
			try {
				final ProfilUtilisateurService profilUtilisateurService = SolonEppServiceLocator
						.getProfilUtilisateurService();
				ProfilUtilisateur profilUtilisateur = null;
				DocumentModel profilDoc = profilUtilisateurService.getOrCreateUserProfilFromId(documentManager,
						user.getUsername());
				if (profilDoc != null) {
					profilUtilisateur = profilDoc.getAdapter(ProfilUtilisateur.class);
				}
				if (profilUtilisateur != null) {
					// Mise à jour de la date
					profilUtilisateur.setDernierChangementMotDePasse(Calendar.getInstance());
					documentManager.saveDocument(profilUtilisateur.getDocument());
					documentManager.save();
				}
			} catch (Exception e) {
				LOGGER.error(documentManager, STLogEnumImpl.FAIL_GET_PROFIL_UTILISATEUR_TEC);
			}
		}
		return super.forcedChangePassword();
	};

	/**
	 * Reset des mots de passes saisis par l'utilisateur
	 */
	public void clearPasswordFields() {
		this.userPassword = null;
		this.newUserPasswordFirst = null;
		this.newUserPasswordSecond = null;
		this.selectedUser = null;
	}

	/**
	 * Validation des mots de passes saisis par l'utilisateur.
	 * 
	 * @param context
	 * @param component
	 * @param value
	 */
	public void validatePasswordFull(FacesContext context, UIComponent component, Object value) {
		// validation de l'ancien mot de passe
		passwordValid = false;
		setErrorName("");
		Map<String, Object> attributes = component.getAttributes();
		String oldPasswordInputId = (String) attributes.get("oldPasswordInputId");
		if (oldPasswordInputId == null) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_VALIDATE_PASSWORDS_TEC, " Input id(s) not found");

			return;
		}

		UIInput oldPasswordComp = (UIInput) component.findComponent(oldPasswordInputId);
		if (oldPasswordComp == null) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_VALIDATE_PASSWORDS_TEC, " Input(s) not found");

			return;
		}

		Object oldPassword = oldPasswordComp.getLocalValue();

		if (oldPassword == null) {
			LOGGER.error(documentManager, STLogEnumImpl.FAIL_VALIDATE_PASSWORDS_TEC, " Value(s) not found");
			return;
		}

		// vérification du mot de passe
		UserManager userManager = STServiceLocator.getUserManager();
		if (currentUser != null && currentUser.getName() != null
				&& !userManager.authenticate(currentUser.getName(), (String) oldPassword)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"label.userManager.password.false"), null);
			setErrorName(message.getSummary());
			throw new ValidatorException(message);
		}

		// validation du nouveau mot de passe
		try {
			super.validatePassword(context, component, value);
		} catch (ValidatorException e) {
			setErrorName(e.getFacesMessage().getSummary());
			throw new ValidatorException(e.getFacesMessage());
		}

		// si aucune erreur de validation n'est détecté le mot de passe est considéré comme valide
		passwordValid = true;
	}

	// Méthodes liées au changement de mot de passe
	/**
	 * Récupère le mot de passe saisi par l'utilisateur.
	 * 
	 * @return userPassword
	 */
	public String getUserPassword() {
		return userPassword;
	}

	/**
	 * Définit le mot de passe saisi par l'utilisateur.
	 * 
	 * @param userPassword
	 */
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public boolean getPasswordValid() {
		return passwordValid;
	}

	public void setPasswordValid(boolean passwordValid) {
		this.passwordValid = passwordValid;
	}

	public String getErrorName() {
		return errorName;
	}

	public void setErrorName(String errorName) {
		// escape for javascript return
		this.errorName = StringEscapeUtils.escapeJavaScript(errorName);
	}
	
	/**
	 * Adds selection from selector as a list element
	 * <p>
	 * Must pass request parameter "suggestionSelectionListId" holding the
	 * binding to model. Selection will be retrieved using the
	 * {@link #getSelectedValue()} method.
	 * @throws ClientException 
	 */
	public void addBoundSelectionToList(ActionEvent event) throws ClientException {
		UIComponent component = event.getComponent();
		if (component == null) {
			return;
		}

		ProfileService profilService = STServiceLocator.getProfileService();
		Map<String, DocumentModel> profilMap = profilService.getProfilMap();

		DocumentModel profilToAdd = profilMap.get(getSelectedValue());

		if (!userProfils.contains(profilToAdd)) {
			userProfils.add(profilToAdd);
		}
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

	@Override
	public String resetCreation() throws ClientException {
		userProfils = new ArrayList<DocumentModel>();
		return super.resetCreation();
	}
}
