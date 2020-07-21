package fr.dila.st.api.user;

import java.io.Serializable;
import java.util.List;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Objet métier profil.
 * 
 * @author jtremeaux
 */
public interface Profile extends Serializable {

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
	 * Retourne la liste des fonctions unitaires.
	 * 
	 * @return Liste des fonctions unitaires
	 */
	List<String> getBaseFunctionList();

	/**
	 * Renseigne la liste des fonctions unitaires.
	 * 
	 * @param baseFunctionList
	 *            Liste des fonctions unitaires
	 */
	void setBaseFunctionList(List<String> baseFunctionList);
}
