package fr.dila.st.api.domain;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;

public interface ExportDocument extends STDomainObject {

	/**
	 * 
	 * @return le propriétaire de l'export
	 */
	String getOwner();

	/**
	 * Renseigne le propriétaire de l'export
	 * 
	 * @param owner
	 * @throws ClientException
	 */
	void setOwner(String owner) throws ClientException;

	/**
	 * 
	 * @return la date de demande d'export
	 */
	Calendar getDateRequest();

	/**
	 * Renseigne la date de demande d'export
	 * 
	 * @param date
	 * @throws ClientException
	 */
	void setDateRequest(Calendar date) throws ClientException;

	/**
	 * 
	 * @return vrai si le life cycle state du document = exporting
	 * @throws ClientException
	 */
	boolean isExporting() throws ClientException;

	/**
	 * modifie le life cycle state du document - vrai = exporting / faux = done
	 * 
	 * @param refreshing
	 * @throws ClientException
	 */
	void setExporting(boolean exporting) throws ClientException;

	/**
	 * Renseigne le contenu du document
	 * 
	 * @param content
	 * @throws ClientException
	 */
	void setFileContent(Blob content) throws ClientException;

	/**
	 * Récupère le contenu du document
	 * 
	 * @return
	 */
	Blob getFileContent();
}
