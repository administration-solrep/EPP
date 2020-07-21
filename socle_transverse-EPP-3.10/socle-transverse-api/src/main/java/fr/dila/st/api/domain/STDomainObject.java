package fr.dila.st.api.domain;

import java.io.Serializable;
import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Interface de base des objets métiers du socle transverse qui encapsulent un DocumentModel.
 * 
 * @author antoine Rolin
 * @author jtremeaux
 */
public interface STDomainObject extends Serializable {

	/**
	 * Récupération du documentModel.
	 * 
	 * @return DocumentModel
	 */
	DocumentModel getDocument();

	/**
	 * Sauvegarde le document et sauvegarde la session.
	 * 
	 * @param session
	 *            CoreSession
	 * @return DocumentModel
	 */
	DocumentModel save(CoreSession session) throws ClientException;

	String getId();

	// *************************************************************
	// Propriétés du schéma Dublin Core.
	// *************************************************************
	/**
	 * Retourne le titre de l'événement.
	 * 
	 * @return Titre de l'événement
	 */
	String getTitle();

	/**
	 * Renseigne le titre de l'événement.
	 * 
	 * @param title
	 *            Titre de l'événement
	 */
	void setTitle(String title);

	/**
	 * Retourne le créateur du document
	 * 
	 * @return
	 */
	String getCreator();

	/**
	 * Renseigne le nom du créateur du document
	 * 
	 * @param creator
	 */
	void setCreator(String creator);

	/**
	 * Retourne le nom du dernier contributeur du document
	 * 
	 * @return
	 */
	String getLastContributor();

	/**
	 * Renseigne le nom du dernier contributeur du document
	 * 
	 * @param creator
	 */
	void setLastContributor(String lastContributor);

	/**
	 * Retourne la date de dernière modification
	 * 
	 * @return
	 */
	Calendar getModifiedDate();

	/**
	 * Renseigne la date de dernière modification
	 * 
	 * @param dateModified
	 */
	void setModifiedDate(Calendar modifiedDate);

	/**
	 * Indique si le currentLifeCycleState = 'deleted'
	 * 
	 * @return
	 */
	boolean isDeleted() throws ClientException;
}
