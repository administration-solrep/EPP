package fr.dila.st.api.service;

import java.util.Calendar;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

public interface STExportService {

	/**
	 * indique si l'utilisateur a déjà une demande d'export en cours
	 * 
	 * @param session
	 * @param exportDocumentDoc
	 * @return
	 * @throws ClientException
	 */
	boolean isCurrentlyExporting(CoreSession session, DocumentModel exportDocumentDoc) throws ClientException;

	/**
	 * Modifie le document d'export pour indiquer qu'une demande d'export est en cours
	 * 
	 * @param session
	 * @param exportDocumentDoc
	 * @param folderExportName
	 * @param folderExportType
	 * @param exportDocumentType
	 * @param exportDocumentName
	 * @param query
	 * @return
	 * @throws ClientException
	 */
	boolean flagInitExport(CoreSession session, DocumentModel exportDocumentDoc) throws ClientException;

	/**
	 * Modifie le document d'export pour indiquer que la demande d'export est terminée
	 * 
	 * @param session
	 * @param exportDocumentDoc
	 *            le parent contenant les documents d'export
	 * @throws ClientException
	 */
	void flagEndExport(CoreSession session, DocumentModel exportDocumentDoc) throws ClientException;

	/**
	 * récupère la date de la dernière demande d'export au format dd/MM/yyyy hh:mm
	 * 
	 * @param session
	 * @param exportDocumentDoc
	 * @return
	 * @throws ClientException
	 */
	String getExportHorodatageRequest(CoreSession session, DocumentModel exportDocumentDoc) throws ClientException;

	/**
	 * Récupère les documentModel des documents d'export en fonction du parent demandé
	 * 
	 * @param session
	 * @param parentRef
	 * @return
	 * @throws ClientException
	 */
	DocumentModelList getExportDocumentDocs(CoreSession session, DocumentRef parentRef) throws ClientException;

	/**
	 * Retourne le chemin du répertoire racine pour le stockage des documents export
	 * 
	 * @param session
	 * @param parentPath
	 *            le chemin du parent qui contient le répertoire racine des documents d'export
	 * @param folderExportName
	 *            nom du répertoire
	 * @param folderExportType
	 *            type de répertoire
	 * @return
	 * @throws ClientException
	 */
	String getOrCreateExportDocumentRootPath(CoreSession session, String parentPath, String folderExportName,
			String folderExportType) throws ClientException;

	/**
	 * Retourne le répertoire racine pour le stockage des documents export
	 * 
	 * @param session
	 * @param parentPath
	 *            le chemin du parent qui contient le répertoire racine des documents d'export
	 * @param folderExportName
	 *            nom du répertoire
	 * @param folderExportType
	 *            type de répertoire
	 * @return
	 * @throws ClientException
	 */
	DocumentModel getOrCreateExportDocumentRootDoc(CoreSession session, String parentPath, String folderExportName,
			String folderExportType) throws ClientException;

	/**
	 * Supprime les documents d'export dont la dateRequest < dateLimit
	 * 
	 * @param session
	 * @param dateLimit
	 * @param typeExport
	 * @return nombre de documents supprimés
	 * @throws ClientException
	 */
	int removeOldExport(CoreSession session, Calendar dateLimit, String typeExport) throws ClientException;
}
