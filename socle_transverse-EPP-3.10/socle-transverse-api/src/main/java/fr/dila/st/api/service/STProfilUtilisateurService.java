package fr.dila.st.api.service;

import java.io.Serializable;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.user.STProfilUtilisateur;
import fr.dila.st.api.user.STUser;

/**
 * Interface du service ProfilUtilisateur
 * 
 * @author user
 * 
 */
public interface STProfilUtilisateurService extends Serializable {

	/**
	 * Récupère le documentModel du profil utilisateur
	 * 
	 * @param session
	 * @param username
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getProfilUtilisateurDoc(CoreSession session, String username) throws ClientException;

	/**
	 * retourne le profil de l'utilisateur
	 * 
	 * @param username
	 * @return
	 * @throws ClientException
	 */
	STProfilUtilisateur getProfilUtilisateur(CoreSession session, String username) throws ClientException;

	/**
	 * retourne le profil de l'utilisateur courant
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	STProfilUtilisateur getProfilUtilisateurForCurrentUser(CoreSession session) throws ClientException;

	/**
	 * récupère ou initialise un profilUtilisateur s'il n'existe pas
	 * 
	 * @param session
	 * @param userId
	 * @return le documentModel du profilUtilisateur
	 * @throws ClientException
	 */
	DocumentModel getOrCreateUserProfilFromId(CoreSession session, String userId) throws ClientException;

	/**
	 * Récupère le profil utilisateur de l'utilisateur courant. Le créé s'il n'existe pas
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getOrCreateCurrentUserProfil(CoreSession session) throws ClientException;

	/**
	 * Calcul le nombre de jour avant qu'un mot de passe n'expire
	 * 
	 * @param session
	 * @param user
	 * @return
	 * @throws ClientException
	 */
	int getNumberDayBeforeOutdatedPassword(CoreSession session, STUser user) throws ClientException;

	/**
	 * Détermine si le mot de passe utilisateur est expiré
	 * 
	 * @param session
	 * @param username
	 * @return
	 * @throws ClientException
	 */
	boolean isUserPasswordOutdated(CoreSession session, String username) throws ClientException;

	/**
	 * Met à jour la date de dernière modification du mot de passe
	 * 
	 * @param session
	 * @param username
	 * @throws ClientException
	 */
	void changeDatePassword(CoreSession session, String username) throws ClientException;

	/**
	 * Récupère la liste des utilisateurs pour lesquels le mot de passe va expirer prochainement
	 * 
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	Set<STUser> getToRemindChangePasswordUserList(CoreSession session) throws ClientException;

	/**
	 * Indique si l'utilisateur est supprimé
	 * 
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	boolean isUserDeleted(String userId) throws ClientException;

}
