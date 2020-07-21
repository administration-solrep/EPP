package fr.dila.st.api.domain.user;

import java.util.Calendar;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface de l'objet métier délégation.
 * 
 * @author jtremeaux
 */
public interface Delegation {

	/**
	 * Retourne le document.
	 * 
	 * @return Document
	 */
	DocumentModel getDocument();

	/**
	 * Renseigne le document.
	 * 
	 * @param document
	 *            Document
	 */
	void setDocument(DocumentModel document);

	/**
	 * Retourne la date de début de la délégation.
	 * 
	 * @return Date de début de la délégation
	 */
	Calendar getDateDebut();

	/**
	 * Renseigne la date de début de la délégation.
	 * 
	 * @param dateDebut
	 *            Date de début de la délégation
	 */
	void setDateDebut(Calendar dateDebut);

	/**
	 * Retourne la date de fin de la délégation.
	 * 
	 * @return Date de fin de la délégation
	 */
	Calendar getDateFin();

	/**
	 * Renseigne la date de fin de la délégation.
	 * 
	 * @param dateFin
	 *            Date de fin de la délégation
	 */
	void setDateFin(Calendar dateFin);

	/**
	 * Retourne l'identifiant technique de l'utilisateur qui délègue ses droits.
	 * 
	 * @return Identifiant technique de l'utilisateur
	 */
	String getSourceId();

	/**
	 * Renseigne l'identifiant technique de l'utilisateur qui délègue ses droits.
	 * 
	 * @param sourceId
	 *            Identifiant technique de l'utilisateur
	 */
	void setSourceId(String sourceId);

	/**
	 * Retourne l'identifiant technique de l'utilisateur qui reçoit les droits.
	 * 
	 * @return Identifiant technique de l'utilisateur
	 */
	String getDestinataireId();

	/**
	 * Renseigne l'identifiant technique de l'utilisateur qui reçoit les droits.
	 * 
	 * @param destinataireId
	 *            Identifiant technique de l'utilisateur
	 */
	void setDestinataireId(String destinataireId);

	/**
	 * Retourne la liste des identifiants techniques des profils.
	 * 
	 * @return Liste des identifiants techniques des profils
	 */
	List<String> getProfilListId();

	/**
	 * Renseigne la liste des identifiants techniques des profils.
	 * 
	 * @param profilListId
	 *            Liste des identifiants techniques des profils
	 */
	void setProfilListId(List<String> profilListId);
}
