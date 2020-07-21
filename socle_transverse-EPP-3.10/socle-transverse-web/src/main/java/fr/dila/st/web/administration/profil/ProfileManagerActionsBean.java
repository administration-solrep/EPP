package fr.dila.st.web.administration.profil;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.directory.BaseSession;
import org.nuxeo.ecm.directory.SizeLimitExceededException;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.platform.usermanager.exceptions.GroupAlreadyExistsException;
import org.nuxeo.ecm.webapp.helpers.ResourcesAccessor;

import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.service.ProfileService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.PropertyUtil;
import fr.dila.st.web.action.NavigationWebActionsBean;

/**
 * Bean Seam de gestion des profils utilisateurs.
 * 
 * @author jtremeaux
 */
@Name("profileManagerActions")
@Scope(CONVERSATION)
@Install(precedence = FRAMEWORK)
public class ProfileManagerActionsBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long						serialVersionUID	= 1L;

	/**
	 * Logger.
	 */
	private static final Log						log					= LogFactory
																				.getLog(ProfileManagerActionsBean.class);

	private String									ALL					= "all";

	private String									VALID_CHARS			= "0123456789_-"
																				+ "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	@In(create = true)
	protected transient UserManager					userManager;

	@In(create = true)
	protected Principal								currentUser;

	@In(create = true, required = false)
	protected transient FacesMessages				facesMessages;

	@In(create = true)
	protected transient ResourcesAccessor			resourcesAccessor;

	@In(create = true, required = false)
	protected transient ActionManager				actionManager;

	@In(create = true)
	protected transient NavigationWebActionsBean	navigationWebActions;

	@DataModel(value = "profileList")
	protected DocumentModelList						profiles;

	@DataModelSelection("profileList")
	protected DocumentModel							selectedProfile;

	protected DocumentModel							newProfile;

	protected String								profileListingMode;

	protected Boolean								canEditProfiles;

	protected Boolean								canCreateProfiles;

	protected Boolean								canDeleteProfiles;

	protected String								searchString		= "";

	protected boolean								searchOverflow;

	protected Map<String, DocumentModel>			baseFunctionMap;

	protected String getProfileListingMode() throws ClientException {
		if (profileListingMode == null) {
			profileListingMode = userManager.getGroupListingMode();
		}
		return profileListingMode;
	}

	/**
	 * Retourne la liste de toutes les fonctions unitaires.
	 * 
	 * @return Liste de toutes les fonctions unitaires
	 * @throws ClientException
	 */
	public Collection<DocumentModel> getBaseFunctionList() throws ClientException {
		final ProfileService profileService = STServiceLocator.getProfileService();
		return profileService.findAllBaseFunction();
	}

	/**
	 * Retourne un tableau association <id technique, document fonction unitaire>.
	 * 
	 * @return Tableau associatif
	 * @throws ClientException
	 */
	public Map<String, DocumentModel> getBaseFunctionMap() throws ClientException {
		if (baseFunctionMap == null) {
			final ProfileService profileService = STServiceLocator.getProfileService();
			List<DocumentModel> baseFunctionList = profileService.findAllBaseFunction();
			baseFunctionMap = new HashMap<String, DocumentModel>();
			for (DocumentModel baseFunctionDoc : baseFunctionList) {
				baseFunctionMap.put(baseFunctionDoc.getId(), baseFunctionDoc);
			}
		}
		return baseFunctionMap;
	}

	@Factory(value = "profileList")
	public DocumentModelList getProfiles() throws ClientException {
		if (profiles == null) {
			searchOverflow = false;
			try {
				String profileListingMode = getProfileListingMode();
				if (ALL.equals(profileListingMode) || "*".equals(getTrimmedSearchString())) {
					profiles = userManager.searchGroups(Collections.<String, Serializable> emptyMap(), null);
				} else if (!StringUtils.isEmpty(getTrimmedSearchString())) {
					Map<String, Serializable> filter = new HashMap<String, Serializable>();
					// XXX: search only on id, better conf should be set in user manager interface
					filter.put(userManager.getGroupIdField(), getTrimmedSearchString());
					// parameters must be serializable so copy keySet to HashSet
					profiles = userManager.searchGroups(filter, new HashSet<String>(filter.keySet()));
				}
			} catch (SizeLimitExceededException e) {
				searchOverflow = true;
			}
		}
		if (profiles == null) {
			profiles = new DocumentModelListImpl();
		}
		return profiles;
	}

	public void resetProfiles() {
		profiles = null;
		
		ProfileService profilService = STServiceLocator.getProfileService();
		profilService.resetProfilMap();
	}

	// refresh to get references
	protected DocumentModel refreshProfile(String profileName) throws ClientException {
		return userManager.getGroupModel(profileName);
	}

	protected String viewProfile(DocumentModel profile, boolean refresh) throws ClientException {
		if (profile != null) {
			selectedProfile = profile;
			if (refresh) {
				selectedProfile = refreshProfile(profile.getId());
			}
			if (selectedProfile != null) {
				return "view_profile";
			}
		}
		return null;
	}

	public String viewProfiles() {
		navigationWebActions.setCurrentLeftMenuItemAction(actionManager.getAction("admin_utilisateur_profil"));
		return "view_profiles";
	}

	public String viewProfile() throws ClientException {
		return viewProfile(selectedProfile, true);
	}

	public String viewProfile(String profileName) throws ClientException {
		return viewProfile(userManager.getGroupModel(profileName), false);
	}

	public String editProfile() throws ClientException {
		selectedProfile = refreshProfile(selectedProfile.getId());
		return "edit_profile";
	}

	public boolean isSelectedProfileReadOnly() {
		Serializable virtualFlag = selectedProfile.getContextData().getScopedValue("virtual");
		return virtualFlag != null && virtualFlag.equals(true);
	}

	public DocumentModel getSelectedProfile() {
		return selectedProfile;
	}

	public DocumentModel getNewProfile() throws ClientException {
		if (newProfile == null) {
			newProfile = userManager.getBareGroupModel();
		}
		return newProfile;
	}

	public String deleteProfile() throws ClientException {
		userManager.deleteGroup(selectedProfile);
		// reset users and profiles
		resetProfiles();
		return viewProfiles();
	}

	public String updateProfile() throws ClientException {
		userManager.updateGroup(selectedProfile);
		// reset users and profiles
		resetProfiles();
		return viewProfile(selectedProfile.getId());
	}

	public void validateProfileName(FacesContext context, UIComponent component, Object value) {
		if (!(value instanceof String) || !StringUtils.containsOnly((String) value, VALID_CHARS)) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, ComponentUtils.translate(context,
					"label.profileManager.wrongProfileName"), null);
			((EditableValueHolder) component).setValid(false);
			context.addMessage(component.getClientId(context), message);
			// also add global message
			context.addMessage(null, message);
		}
	}

	public String createProfile() throws ClientException {
		try {
			selectedProfile = userManager.createGroup(newProfile);
			newProfile = null;
			// reset so that profile list is computed again
			resetProfiles();
			facesMessages.add(StatusMessage.Severity.INFO,
					resourcesAccessor.getMessages().get("label.profileManager.profileCreated"));
			return viewProfile(selectedProfile, false);
		} catch (GroupAlreadyExistsException e) {
			String message = resourcesAccessor.getMessages().get("label.profileManager.profileAlreadyExists");
			facesMessages.addToControl("profileName", StatusMessage.Severity.ERROR, message);
			return null;
		}
	}

	/**
	 * Controle l'accès à la vue correspondante
	 * 
	 */
	public boolean isAccessAuthorized() {
		STPrincipal ssPrincipal = (STPrincipal) currentUser;
		return (ssPrincipal.isAdministrator() || ssPrincipal
				.isMemberOf(STBaseFunctionConstant.ADMINISTRATION_PROFIL_READER));
	}

	/**
	 * Retourne vrai si l'utilisateur peut éditer les profils.
	 * 
	 * @return Vrai si l'utilisateur peut éditer les profils
	 * @throws ClientException
	 *             ClientException
	 */
	protected boolean getCanEditProfiles() throws ClientException {
		if (canEditProfiles != null) {
			return canEditProfiles;
		}

		canEditProfiles = false;
		if (!userManager.areGroupsReadOnly() && currentUser instanceof NuxeoPrincipal) {
			NuxeoPrincipal pal = (NuxeoPrincipal) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.PROFIL_UPDATER)) {
				canEditProfiles = true;
			}
		}
		return canEditProfiles;
	}

	/**
	 * Retourne vrai si l'utilisateur peut supprimer des profils.
	 * 
	 * @return Vrai si l'utilisateur peut supprimer des profils
	 * @throws ClientException
	 *             ClientException
	 */
	protected boolean getCanDeleteProfiles() throws ClientException {
		if (canDeleteProfiles != null) {
			return canDeleteProfiles;
		}

		canDeleteProfiles = false;
		if (!userManager.areGroupsReadOnly() && currentUser instanceof NuxeoPrincipal) {
			NuxeoPrincipal pal = (NuxeoPrincipal) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.PROFIL_DELETER)) {
				canDeleteProfiles = true;
			}
		}
		return canDeleteProfiles;
	}

	/**
	 * Retourne vrai si l'utilisateur peut créer des profils.
	 * 
	 * @return Vrai si l'utilisateur peut créer des profils
	 * @throws ClientException
	 *             ClientException
	 */
	public boolean getAllowCreateProfile() throws ClientException {
		if (canCreateProfiles != null) {
			return canCreateProfiles;
		}

		canCreateProfiles = false;
		if (!userManager.areGroupsReadOnly() && currentUser instanceof NuxeoPrincipal) {
			NuxeoPrincipal pal = (NuxeoPrincipal) currentUser;
			if (pal.isAdministrator() || pal.isMemberOf(STBaseFunctionConstant.PROFIL_CREATOR)) {
				canCreateProfiles = true;
			}
		}
		return canCreateProfiles;
	}

	/**
	 * Retourne vrai si l'utilisateur peut supprimer le profil sélectionné.
	 * 
	 * @return Vrai si l'utilisateur peut supprimer le profil sélectionné
	 * @throws ClientException
	 *             ClientException
	 */
	public boolean getAllowDeleteProfile() throws ClientException {
		final ProfileService profileService = STServiceLocator.getProfileService();
		return getCanDeleteProfiles() && !BaseSession.isReadOnlyEntry(selectedProfile)
				&& profileService.isProfileUpdatable(selectedProfile.getId());
	}

	/**
	 * Retourne vrai si l'utilisateur peut éditer le profil sélectionné.
	 * 
	 * @return Vrai si l'utilisateur peut éditer le profil sélectionné
	 * @throws ClientException
	 *             ClientException
	 */
	public boolean getAllowEditProfile() throws ClientException {
		final ProfileService profileService = STServiceLocator.getProfileService();
		return getCanEditProfiles() && !BaseSession.isReadOnlyEntry(selectedProfile)
				&& profileService.isProfileUpdatable(selectedProfile.getId());
	}

	public String getSearchString() {
		return searchString;
	}

	protected String getTrimmedSearchString() {
		if (searchString == null) {
			return null;
		}
		return searchString.trim();
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public String searchProfiles() {
		// reset so that profiles are recomputed
		resetProfiles();
		return viewProfiles();
	}

	public String clearSearch() {
		searchString = null;
		return searchProfiles();
	}

	public boolean isSearchOverflow() {
		return searchOverflow;
	}

	/*
	 * ----- Methods for AJAX calls, do not return anything to avoid redirect -----
	 */

	public void setSelectedProfile(DocumentModel profile) throws ClientException {
		selectedProfile = refreshProfile(profile.getId());
	}

	public void deleteProfileNoRedirect() throws ClientException {
		deleteProfile();
		resetProfiles();
	}

	public void createProfileNoRedirect() throws ClientException {
		createProfile();
		resetProfiles();
	}

	public void updateProfileNoRedirect() throws ClientException {
		updateProfile();
		resetProfiles();
	}

	/**
	 * Tri les fonctions par ordre alphabetique.
	 * 
	 * @param baseFonctionList
	 * @return
	 */
	public List<String> sortBaseFonctions(List<String> baseFonctionList) {

		Collections.sort(baseFonctionList, new Comparator<String>() {
			@Override
			public int compare(String baseFunction1, String baseFunction2) {

				try {
					DocumentModel baseFunctionDoc1 = getBaseFunctionMap().get(baseFunction1);
					DocumentModel baseFunctionDoc2 = getBaseFunctionMap().get(baseFunction2);
					String description1 = "";
					if (baseFunctionDoc1 != null) {
						description1 = PropertyUtil.getStringProperty(baseFunctionDoc1,
								STSchemaConstant.BASE_FUNCTION_SCHEMA,
								STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY);
					}
					String description2 = "";
					if (baseFunctionDoc2 != null) {
						description2 = PropertyUtil.getStringProperty(baseFunctionDoc2,
								STSchemaConstant.BASE_FUNCTION_SCHEMA,
								STSchemaConstant.BASE_FUNCTION_DESCRIPTION_PROPERTY);
					}
					return description1.compareTo(description2);
				} catch (ClientException e) {
					log.error("Erreur lors de la récuperation des fonctions", e);
					return 0;
				}
			}
		});

		return baseFonctionList;
	}

}
