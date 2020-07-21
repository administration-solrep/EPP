package fr.dila.st.api.user;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Objet métier fonction unitaire.
 * 
 * @author jtremeaux
 */
public interface BaseFunction extends Serializable {

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
	 * Retourne la description.
	 * 
	 * @return Description
	 */
	String getDescription();

	/**
	 * Renseigne la description.
	 * 
	 * @param description
	 *            Description
	 */
	void setDescription(String description);
}
