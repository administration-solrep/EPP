package fr.dila.st.web.document;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import fr.dila.st.api.domain.user.Delegation;
import fr.dila.st.api.feuilleroute.STRouteStep;
import fr.dila.st.api.user.BaseFunction;
import fr.dila.st.api.user.Profile;
import fr.dila.st.api.user.STUser;

/**
 * Ce WebBean permet d'injecter les classes des objets métiers pour les rendre disponible dans le contexte Seam.
 * 
 * @author jtremeaux
 */
@Name("documentModelActions")
@Scope(ScopeType.APPLICATION)
@Install(precedence = FRAMEWORK)
public class DocumentModelActionsBean implements Serializable {
	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Retourne l'interface de l'objet métier Profile.
	 * 
	 * @return Interface de l'objet métier
	 */
	@Factory(value = "Profile", scope = ScopeType.APPLICATION)
	public Class<Profile> getProfile() {
		return Profile.class;
	}

	/**
	 * Retourne l'interface de l'objet métier BaseFunction.
	 * 
	 * @return Interface de l'objet métier
	 */
	@Factory(value = "BaseFunction", scope = ScopeType.APPLICATION)
	public Class<BaseFunction> getBaseFunction() {
		return BaseFunction.class;
	}

	/**
	 * Retourne l'interface de l'objet métier STRouteStep.
	 * 
	 * @return Interface de l'objet métier
	 */
	@Factory(value = "STRouteStep", scope = ScopeType.APPLICATION)
	public Class<STRouteStep> getSTRrouteStep() {
		return STRouteStep.class;
	}

	/**
	 * Retourne l'interface de l'objet métier Delegation.
	 * 
	 * @return Interface de l'objet métier
	 */
	@Factory(value = "Delegation", scope = ScopeType.APPLICATION)
	public Class<Delegation> getDelegation() {
		return Delegation.class;
	}
	
	@Factory(value = "STUser", scope = ScopeType.APPLICATION)
	public Class<STUser> getSTUser() {
		return STUser.class;
	}
}
