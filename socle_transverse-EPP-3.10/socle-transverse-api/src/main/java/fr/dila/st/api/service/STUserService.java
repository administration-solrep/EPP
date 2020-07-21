package fr.dila.st.api.service;

import java.io.Serializable;
import java.util.List;

import javax.mail.Address;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.user.STUser;

/**
 * Interface du service sur les utilisateurs du socle transverse.
 * 
 * @author ARN
 */
public interface STUserService extends Serializable {

	/**
	 * format pour afficher la civilite abregée, le nom et le prenom de l'utilisateur.
	 */
	String	CIVILITE_ABREGEE_PRENOM_NOM	= "C p n";

	/**
	 * Récupère la civilite, le nom et le prénom de l'utilisateur à partir du login de l'utilisateur. Si le nom de
	 * l'utilisateur n'existe pas, on renvoie le login
	 * 
	 * @param userLogin
	 *            Identifiant technique de l'utilisateur
	 * @return Nom complet et civilité
	 */
	String getUserFullNameAndCivilite(String userLogin);

	/**
	 * Récupère les informations de l'utilisateur à partir du formatage suivant : Le formattage est de la forme
	 * "c p n t" avec : - c : Civilité - C : Civilité abrégée (M. au lieu de Monsieur et Mme. au lieu de Madame) - p :
	 * Prénom - n : Nom - t : Téléphone - m : Mail
	 * 
	 * @param userLogin
	 *            Identifiant technique de l'utilisateur
	 * @return Nom complet et civilité
	 */
	String getUserInfo(String userLogin, String format);

	/**
	 * Récupère la civilite, le nom et le prénom de l'utilisateur à partir d'un documentModel. Si le nom de
	 * l'utilisateur n'existe pas, on renvoie le login.
	 * 
	 * @param userModel
	 *            Utilisateur
	 * @return Nom complet et civilité
	 * @throws ClientException
	 */
	String getUserFullNameAndCivilite(DocumentModel userModel) throws ClientException;

	/**
	 * Récupère les informations de l'utilisateur à partir du formatage suivant : Le formattage est de la forme
	 * "c p n t" avec : - c : Civilité - C : Civilité abrégée (M. au lieu de Monsieur et Mme. au lieu de Madame) - p :
	 * Prénom - n : Nom - t : Téléphone - m : Mail
	 * 
	 * @param userLogin
	 *            Identifiant technique de l'utilisateur
	 * @return Nom complet
	 * @throws ClientException
	 */
	String getUserInfo(DocumentModel userModel, String format) throws ClientException;

	/**
	 * Récupère le nom et le prénom de l'utilisateur à partir du login de l'utilisateur. Si le nom de l'utilisateur
	 * n'existe pas, on renvoie le login.
	 * 
	 * @param userLogin
	 *            Identifiant technique de l'utilisateur
	 * @return Nom complet
	 * @throws ClientException
	 */
	String getUserFullName(String userLogin);

	/**
	 * Récupère le nom et le prénom de l'utilisateur à partir d'un documentModel. Si le nom de l'utilisateur n'existe
	 * pas, on renvoie le login.
	 * 
	 * @param userModel
	 *            Utilisateur
	 * @return Nom complet
	 * @throws ClientException
	 */
	String getUserFullName(DocumentModel userModel) throws ClientException;

	/**
	 * Récupère la liste des profils de l'utilisateurs sous forme d'une chaine de caractère Si le nom de l'utilisateur
	 * n'existe pas, on renvoie "**poste inconnu**".
	 * 
	 * @param userId
	 *            utilisateur identifiant
	 * @return la liste des profils de l'utilisateurs sous forme d'une chaine de caractèret
	 * @throws ClientException
	 */
	String getUserProfils(String userId) throws ClientException;

	/**
	 * Génere le mot de passe de l'utilisateur et l'enregistre dans le LDAP.
	 * 
	 * @param userId
	 *            id de l'utilisateur
	 * @return le mot de passe en clair pour l'envoie par mail à l'utilisateur
	 * @throws ClientException
	 */
	String generateAndSaveNewUserPassword(String userId) throws ClientException;

	/**
	 * Retourne vrai si l'utilisateur doit changer de mot de passe.
	 * 
	 * @param userLogin
	 *            Identifiant technique de l'utilisateur
	 * @return
	 * @throws ClientException
	 */
	boolean isUserPasswordResetNeeded(String userLogin) throws ClientException;

	/**
	 * Force l'utilisateur à changer de mot de passe lorsque le délai de renouvellement est dépassé.
	 * 
	 * @param userLogin
	 *            Identifiant technique de l'utilisateur
	 * 
	 * @throws ClientException
	 */
	void forceChangeOutdatedPassword(String userLogin) throws ClientException;

	/**
	 * Reset le mot de passe d'un utilisateur et renvoie un mail.
	 * 
	 * @param userLogin
	 *            login
	 * @param email
	 *            email
	 * @return
	 * @throws ClientException
	 */
	void askResetPassword(String userLogin, String email) throws Exception;

	/**
	 * Retourne les addresses mail de la liste des utilisateurs.
	 * 
	 * @param recipients
	 *            La liste des utilisateurs
	 * @return Liste d'adresses email
	 * @throws ClientException
	 */
	List<Address> getAddressFromUserList(List<STUser> recipients) throws ClientException;

	/**
	 * Retourne la liste des adresses email sous forme de texte.
	 * 
	 * @param recipients
	 *            Liste d'utilisateurs
	 * @return Liste des adresses email sous forme de texte
	 * @throws ClientException
	 */
	List<String> getEmailAddressFromUserList(List<STUser> recipients) throws ClientException;

	/**
	 * Retourne la liste concaténée des ministères auxquels est rattaché l'utilisateur
	 * 
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	String getUserMinisteres(String userId) throws ClientException;

	/**
	 * Retourne la liste concaténée des directions auxquelles est rattaché l'utilisateur
	 * 
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	String getAllDirectionsRattachement(String userId) throws ClientException;

	/**
	 * Retourne la liste des id des ministères de l'utilisateur
	 */
	List<String> getAllUserMinisteresId(String userId) throws ClientException;

	/**
	 * Retourne la liste concaténée des postes auxquels est rattaché l'utilisateur
	 * @param userId
	 * @return
	 * @throws ClientException
	 */
	String getUserPostes(String userId) throws ClientException;
}
