package fr.dila.st.core.user;

import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.platform.usermanager.NuxeoPrincipalImpl;

import fr.dila.st.api.security.principal.STPrincipal;

/**
 * Implémentation du principal du socle transverse.
 * 
 * @author jtremeaux
 */
public class STPrincipalImpl extends NuxeoPrincipalImpl implements STPrincipal {
	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Ensemble des fonctions unitaires de l'utilisateur.
	 */
	private Set<String>			baseFunctionSet;

	/**
	 * Ensemble des identifiants techniques des postes de l'utilisateur.
	 */
	private Set<String>			posteIdSet;

	/**
	 * Constructeur de STPrincipalImpl.
	 * 
	 * @param name
	 *            name
	 * @param isAnonymous
	 *            isAnonymous
	 * @param isAdministrator
	 *            isAdministrator
	 * @param updateAllGroups
	 *            updateAllGroups
	 * @throws ClientException
	 */
	public STPrincipalImpl(String name, boolean isAnonymous, boolean isAdministrator, boolean updateAllGroups)
			throws ClientException {
		super(name, isAnonymous, isAdministrator, updateAllGroups);
	}

	@Override
	public Set<String> getBaseFunctionSet() {
		return baseFunctionSet;
	}

	@Override
	public void setBaseFunctionSet(Set<String> baseFunctionSet) {
		this.baseFunctionSet = baseFunctionSet;
	}

	@Override
	public Set<String> getPosteIdSet() {
		return posteIdSet;
	}

	@Override
	public void setPosteIdSet(Set<String> posteIdSet) {
		this.posteIdSet = posteIdSet;
	}
}
