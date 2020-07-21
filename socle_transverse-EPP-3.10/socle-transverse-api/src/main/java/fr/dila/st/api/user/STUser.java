package fr.dila.st.api.user;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Objet métier utilisateur.
 * 
 * @author fesposito
 */
public interface STUser extends Serializable {

	DocumentModel getDocument();

	void setDocument(DocumentModel document);

	/**
	 * @return the username
	 */
	String getUsername();

	/**
	 * @param username
	 *            the username to set
	 */
	void setUsername(String username);

	/**
	 * @return the password
	 */
	String getPassword();

	/**
	 * @param password
	 *            the password to set
	 */
	void setPassword(String password);

	/**
	 * @return the firstName
	 */
	String getFirstName();

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	void setFirstName(String firstName);

	/**
	 * @return the lastName
	 */
	String getLastName();

	/**
	 * @param lastName
	 *            the lastName to set
	 */
	void setLastName(String lastName);

	/**
	 * @return the title
	 */
	String getTitle();

	/**
	 * @param title
	 *            the title to set
	 */
	void setTitle(String title);

	/**
	 * @return the employee Type
	 */
	String getEmployeeType();

	/**
	 * @param employeeType
	 *            the employeeType to set
	 */
	void setEmployeeType(String employeeType);

	/**
	 * @return the postalAddress
	 */
	String getPostalAddress();

	/**
	 * @param postalAddress
	 *            the postalAddress to set
	 */
	void setPostalAddress(String postalAddress);

	/**
	 * @return the postalCode
	 */
	String getPostalCode();

	/**
	 * @param postalCode
	 *            the postalCode to set
	 */
	void setPostalCode(String postalCode);

	/**
	 * @return the locality
	 */
	String getLocality();

	/**
	 * @param locality
	 *            the locality to set
	 */
	void setLocality(String locality);

	/**
	 * @return the telephoneNumber
	 */
	String getTelephoneNumber();

	/**
	 * @param telephoneNumber
	 *            the telephoneNumber to set
	 */
	void setTelephoneNumber(String telephoneNumber);

	/**
	 * @return the email
	 */
	String getEmail();

	/**
	 * @param email
	 *            the email to set
	 */
	void setEmail(String email);

	/**
	 * @return the dateDebut
	 */
	Calendar getDateDebut();

	/**
	 * @param dateDebut
	 *            the dateDebut to set
	 */
	void setDateDebut(Calendar dateDebut);

	/**
	 * @return the dateFin
	 */
	Calendar getDateFin();

	/**
	 * @param dateFin
	 *            the dateFin to set
	 */
	void setDateFin(Calendar dateFin);

	/**
	 * Ce champ est utilisé uniquement dans les statistiques pour transférer la valeur
	 * du profil utilisateur.
	 * 
	 * @deprecated removed from EPG-3.5
	 */
	@Deprecated
	Calendar getDateLastConnection();


	/**
	 * Ce champ est utilisé uniquement dans les statistiques pour transférer la valeur
	 * du profil utilisateur.
	 * 
	 * @deprecated removed from EPG-3.5
	 */
	@Deprecated
	void setDateLastConnection(Calendar dateLastConnection);

	/**
	 * @return the temporary
	 */
	boolean isTemporary();

	/**
	 * @param temporary
	 *            the temporary to set
	 */
	void setTemporary(boolean temporary);

	/**
	 * @return the occasional
	 */
	boolean isOccasional();

	/**
	 * @param occasional
	 *            the occasional to set
	 */
	void setOccasional(boolean occasional);

	/**
	 * @return the groups
	 */
	List<String> getGroups();

	/**
	 * @param groups
	 *            the groups to set
	 */
	void setGroups(List<String> groups);

	/**
	 * @return the postes
	 */
	List<String> getPostes();

	/**
	 * @param postes
	 *            the postes to set
	 */
	void setPostes(List<String> postes);

	/**
	 * @return the pwdReset
	 */
	boolean isPwdReset();

	/**
	 * @param pwdReset
	 *            the pwdReset to set
	 */
	void setPwdReset(boolean pwdReset);

	/**
	 * Détermine si l'utilisateur est permanent.
	 * 
	 * @return Utilisateur permanent
	 */
	boolean isPermanent();

	/**
	 * L'utilisateur est-il à supprimer ?
	 * 
	 * @return
	 */
	boolean isDeleted();

	/**
	 * Set l'état supprimé ou non d'un utilisateur
	 * 
	 * @param deleted
	 */
	void setDeleted(boolean deleted);

	/**
	 * True si date de fin > à la date du jour et si deleted = false
	 * 
	 * @return
	 */
	boolean isActive();

}
