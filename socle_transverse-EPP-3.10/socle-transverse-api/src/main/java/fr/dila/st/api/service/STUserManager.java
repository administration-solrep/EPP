package fr.dila.st.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.usermanager.UserManager;

/**
 * Interface du gestionnaire d'utilisateurs du socle transverse.
 * 
 * @author jtremeaux
 */
public interface STUserManager extends UserManager {

	/**
	 * Détermine si l'utilisateur est un utilisateur technique (ne reçoit pas de délégation, ...)
	 * 
	 * @param userId
	 *            Identifiant technique de l'utilisateur
	 * @return Utilisateur technique
	 */
	boolean isTechnicalUser(String userId);

	/**
	 * Suppression physique d'un utilisateur
	 * 
	 * @param userModel
	 * @throws ClientException
	 */
	void physicalDeleteUser(DocumentModel userModel) throws ClientException;

	/**
	 * Suppression physique d'un utilisateur
	 * 
	 * @param userId
	 * @throws ClientException
	 */
	void physicalDeleteUser(String userId) throws ClientException;

	void updateUser(DocumentModel userModel, List<String> newPostesList) throws ClientException;

	void updateUserPostes(DocumentModel userModel, List<String> newPostesList);

	public void updateUserData(DocumentModel userModel) throws ClientException;
}
