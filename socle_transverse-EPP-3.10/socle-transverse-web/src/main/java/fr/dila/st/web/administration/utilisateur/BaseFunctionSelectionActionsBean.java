package fr.dila.st.web.administration.utilisateur;

import java.io.Serializable;
import java.util.List;

import org.jboss.annotation.ejb.SerializedConcurrentAccess;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.user.Profile;

/**
 * Bean Seam permettant de sélectionner des fonctions unitaires dans une liste de choix multiple.
 * 
 * @author jtremeaux
 */
@Name("baseFunctionSelectionActions")
@SerializedConcurrentAccess
@Scope(ScopeType.PAGE)
public class BaseFunctionSelectionActionsBean implements Serializable {

	/**
	 * Serial UID.
	 */
	private static final long	serialVersionUID	= 1L;

	/**
	 * Fonction unitaire sélectionnée pour l'ajout à une liste de valeurs.
	 */
	protected String			currentBaseFunction;

	/**
	 * Ajout d'un profil à la liste des profils de l'utilisateur.
	 * 
	 * @param profileDoc
	 *            Document profil
	 */
	public void addBaseFunction(DocumentModel profileDoc) {
		Profile profile = profileDoc.getAdapter(Profile.class);
		List<String> baseFunctionList = profile.getBaseFunctionList();
		if (!baseFunctionList.contains(currentBaseFunction)) {
			baseFunctionList.add(currentBaseFunction);
		}
		profile.setBaseFunctionList(baseFunctionList);
	}

	/**
	 * Retrait d'une fonction unitaire à la liste des fonctions unitaires d'un profil.
	 * 
	 * @param profileDoc
	 *            Document profil
	 * @param baseFunction
	 *            Fonction unitaire à retirer
	 */
	public void removeBaseFunction(DocumentModel profileDoc, String baseFunction) {
		Profile profile = profileDoc.getAdapter(Profile.class);
		List<String> baseFunctionList = profile.getBaseFunctionList();
		baseFunctionList.remove(baseFunction);
		profile.setBaseFunctionList(baseFunctionList);
	}

	/**
	 * Getter de currentBaseFunction.
	 * 
	 * @return currentBaseFunction
	 */
	public String getCurrentBaseFunction() {
		return currentBaseFunction;
	}

	/**
	 * Setter de currentBaseFunction.
	 * 
	 * @param currentBaseFunction
	 *            currentBaseFunction
	 */
	public void setCurrentBaseFunction(String currentBaseFunction) {
		this.currentBaseFunction = currentBaseFunction;
	}
}
