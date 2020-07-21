package fr.dila.st.web.administration.utilisateur;

import java.io.Serializable;
import java.util.List;

import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.user.STUser;

/**
 * Bean Seam permettant de sélectionner des profils dans une liste de choix multiple.
 * 
 * @author jtremeaux
 */
@Name("profileSelectionActions")
@SerializedConcurrentAccess
@Scope(ScopeType.PAGE)
public class ProfileSelectionActionsBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Profil sélectionné pour l'ajout d'un profil à une liste de valeurs.
	 */
	protected String			currentProfile;

	/**
	 * Default constructor
	 */
	public ProfileSelectionActionsBean() {
		// do nothing
	}

	/**
	 * Ajout d'un profil à la liste des profils de l'utilisateur.
	 * 
	 * @param userDoc
	 *            Document utilisateur
	 */
	public void addProfile(DocumentModel userDoc) {
		STUser user = userDoc.getAdapter(STUser.class);
		List<String> profilIdList = user.getGroups();
		if (!profilIdList.contains(currentProfile)) {
			profilIdList.add(currentProfile);
		}
		user.setGroups(profilIdList);
	}

	/**
	 * Retrait d'un profil à la liste des profils de l'utilisateur.
	 * 
	 * @param userDoc
	 *            Document utilisateur
	 * @param profile
	 *            Profil à retirer
	 */
	public void removeProfile(DocumentModel userDoc, String profile) {
		STUser user = userDoc.getAdapter(STUser.class);
		List<String> profilIdList = user.getGroups();
		profilIdList.remove(profile);
		user.setGroups(profilIdList);
	}

	/**
	 * Getter de currentProfile.
	 * 
	 * @return currentProfile
	 */
	public String getCurrentProfile() {
		return currentProfile;
	}

	/**
	 * Setter de currentProfile.
	 * 
	 * @param currentProfile
	 *            currentProfile
	 */
	public void setCurrentProfile(String currentProfile) {
		this.currentProfile = currentProfile;
	}
}
