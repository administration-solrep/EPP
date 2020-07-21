package fr.dila.st.api.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.security.principal.STPrincipal;
import fr.dila.st.api.user.STUser;

/**
 * Interface du service permettant de gérer les profils.
 * 
 * @author jtremeaux
 */
public interface ProfileService extends Serializable {

	/**
	 * Détermine si un profil est modifiable / supprimable. Le profil "Administrateur Fonctionnel" n'est pas modifiable.
	 * 
	 * @param profileId
	 *            Identifiant technique du profil
	 * @return Profil supprimable
	 */
	boolean isProfileUpdatable(String profileId);

	/**
	 * Retourne la liste de tous les profils.
	 * 
	 * @return Liste des profils utilisateurs
	 * @throws ClientException
	 */
	List<DocumentModel> findAllProfil() throws ClientException;

	/**
	 * Retourne la liste de toutes les fonctions unitaires.
	 * 
	 * @return Liste des fonctions unitaires
	 * @throws ClientException
	 */
	List<DocumentModel> findAllBaseFunction() throws ClientException;

	/**
	 * Retourne la liste des fonctions unitaires associées à un profil.
	 * 
	 * @param profil
	 *            Profils
	 * @return Ensemble de fonctions unitaires
	 * @throws ClientException
	 */
	Set<String> getBaseFunctionFromProfil(String profil) throws ClientException;

	/**
	 * Retourne la liste des fonctions unitaires associées à une collection de profils.
	 * 
	 * @param profilList
	 *            Collection de profils
	 * @return Ensemble de fonctions unitaires
	 * @throws ClientException
	 */
	Set<String> getBaseFunctionFromProfil(Collection<String> profilList) throws ClientException;

	/**
	 * Retourne la liste des utilisateurs possédant une fonction unitaure.
	 * 
	 * @param baseFunctionId
	 *            Identifiant technique de la fonction unitaire
	 * @return Liste des utilisateurs
	 * @throws ClientException
	 */
	List<STUser> getUsersFromBaseFunction(String baseFunctionId) throws ClientException;

	/**
	 * Retourne la liste des profils de l'utilisateurs
	 * 
	 * @param principal
	 * @return document modèle profil
	 * @throws ClientException
	 */
	List<DocumentModel> getProfilListForUserCreate(STPrincipal principal) throws ClientException;

	/**
	 * Retourne la liste des profils associés à la fonction unitaire.
	 * 
	 * @param baseFunction
	 *            nom de la fonction
	 * @return Ensemble des profils
	 * @throws ClientException
	 */
	Set<String> getProfilFromBaseFunction(String baseFunction) throws ClientException;
	
	/**
	 * Renvoie une map reliant les noms de profils au DocumentModel associé.
	 * 
	 * @return
	 * @throws ClientException
	 */
	Map<String, DocumentModel> getProfilMap() throws ClientException;
	
	/**
	 * Reset la map des profils.
	 */
	void resetProfilMap();

}
