package fr.dila.st.api.service;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Service de persistence des données. Utilise provider & entitymanager. Notamment utilisé pour gestion des passwords
 * 
 */
public interface STPersistanceService extends Serializable {

	/**
	 * Sauvegarde le mot de passe en cours de l'utilisateur et l'ajoute à l'historique. Remplace le mot de passe le plus
	 * ancien de l'historique si la taille max de l'historique a été atteinte
	 * 
	 * @param currentPassword
	 * @param currentUser
	 * @throws ClientException
	 */
	void saveCurrentPassword(final String currentPassword, final String currentUser) throws ClientException;

	/**
	 * Recherche si le mot de passe existe dans l'historique
	 * 
	 * @param currentPassword
	 * @param currentUser
	 * @return vrai si le mot de passe existe, faux sinon
	 * @throws ClientException
	 */
	public boolean checkPasswordHistory(final String currentPassword, final String currentUser) throws ClientException;

}
