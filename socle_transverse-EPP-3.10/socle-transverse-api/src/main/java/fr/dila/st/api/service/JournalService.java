package fr.dila.st.api.service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.platform.audit.api.LogEntry;

import fr.dila.st.api.feuilleroute.STRouteStep;

/**
 * Service permettant de requêter le journal d'Audit.
 * 
 * @author bby
 */
public interface JournalService {

	/**
	 * Retourne le nombre total d'evenement matchant les critères (Requete pour le journal affiche dans l'espace
	 * d'administration)
	 * 
	 * @param mapFilter
	 *            filtre à appliquer
	 * @param pageNb
	 *            numero de la page
	 * @param pageSize
	 *            nombre de document par page
	 * @return retourne le nombre total d'evenement
	 * @throws ClientException
	 */
	int getEventsCount(List<String> dossierIds, Map<String, Object> mapFilter, int pageNb, int pageSize)
			throws ClientException;

	/**
	 * Supprime les logs anterieur à dateLimit
	 */
	void purger(Date dateLimit) throws ClientException;

	/**
	 * ajouter des logs journal
	 * 
	 * @param entries
	 *            leslogs à ajouter
	 */
	void addLogEntries(List<LogEntry> entries);

	/**
	 * supprimer tous les logs d'un document
	 * 
	 * @param docId
	 *            the document Id
	 * @throws ClientException
	 */
	void purger(final String docId) throws ClientException;

	List<LogEntry> queryDocumentAllLogs(List<String> dossierIds, Map<String, Object> mapFilter, int pageNb,
			int pageSize, List<SortInfo> sortInfos) throws ClientException;

	List<LogEntry> getLogEntries(String dossierId) throws ClientException;

	/**
	 * Journalise une action de catégorie feuille de route sur une étape
	 * 
	 * @param session
	 * @param etape
	 *            STRouteStep : l'étape sur laquelle l'action a été effectuée
	 * @param dossierDoc
	 *            DocumentModel : le dossier sur laquelle l'action a été effectuée
	 * @param eventName
	 *            String : le nom de l'évènement déclenché pour la journalisation
	 * @param comment
	 *            String : commentaire utilisé pour la journalisation
	 * @throws ClientException
	 */
	void journaliserActionEtapeFDR(CoreSession session, STRouteStep etape, DocumentModel dossierDoc, String eventName,
			String comment) throws ClientException;

	/**
	 * Journalise une action de catégorie feuille de route
	 * 
	 * @param session
	 * @param dossierDoc
	 *            DocumentModel : le dossier sur laquelle l'action a été effectuée
	 * @param eventName
	 *            String : le nom de l'évènement déclenché pour la journalisation
	 * @param comment
	 *            String : commentaire utilisé pour la journalisation
	 * @throws ClientException
	 */
	void journaliserActionFDR(CoreSession session, DocumentModel dossierDoc, String eventName, String comment)
			throws ClientException;

	/**
	 * Journalise une action de catégorie bordereau
	 * 
	 * @param session
	 * @param dossierDoc
	 *            DocumentModel : le dossier sur laquelle l'action a été effectuée
	 * @param eventName
	 *            String : le nom de l'évènement déclenché pour la journalisation
	 * @param comment
	 *            String : commentaire utilisé pour la journalisation
	 * @throws ClientException
	 */
	void journaliserActionBordereau(CoreSession session, DocumentModel dossierDoc, String eventName, String comment)
			throws ClientException;

	/**
	 * Journalise une action de catégorie parapheur
	 * 
	 * @param session
	 * @param dossierDoc
	 *            DocumentModel : le dossier sur laquelle l'action a été effectuée
	 * @param eventName
	 *            String : le nom de l'évènement déclenché pour la journalisation
	 * @param comment
	 *            String : commentaire utilisé pour la journalisation
	 * @throws ClientException
	 */
	void journaliserActionParapheur(CoreSession session, DocumentModel dossierDoc, String eventName, String comment)
			throws ClientException;

	/**
	 * Journalise une action de catégorie fond de dossier
	 * 
	 * @param session
	 * @param dossierDoc
	 *            DocumentModel : le dossier sur laquelle l'action a été effectuée
	 * @param eventName
	 *            String : le nom de l'évènement déclenché pour la journalisation
	 * @param comment
	 *            String : commentaire utilisé pour la journalisation
	 * @throws ClientException
	 */
	void journaliserActionFDD(CoreSession session, DocumentModel dossierDoc, String eventName, String comment)
			throws ClientException;

	/**
	 * Journalise une action de catégorie administration sur un dossier
	 * 
	 * @param session
	 * @param dossierDoc
	 *            DocumentModel : le dossier sur laquelle l'action a été effectuée
	 * @param eventName
	 *            String : le nom de l'évènement déclenché pour la journalisation
	 * @param comment
	 *            String : commentaire utilisé pour la journalisation
	 * @throws ClientException
	 */
	void journaliserActionAdministration(CoreSession session, DocumentModel dossierDoc, String eventName, String comment)
			throws ClientException;

	/**
	 * Journalise une action de catégorie administration
	 * 
	 * @param session
	 * @param eventName
	 *            String : le nom de l'évènement déclenché pour la journalisation
	 * @param comment
	 *            String : commentaire utilisé pour la journalisation
	 * @throws ClientException
	 */
	void journaliserActionAdministration(CoreSession session, String eventName, String comment) throws ClientException;

	/**
	 * Journalise une action sur un dossier avec un DocumentEventContext
	 * 
	 * @param session
	 * @param dossierDoc
	 *            DocumentModel : le dossier sur laquelle l'action a été effectuée
	 * @param eventName
	 *            String : le nom de l'évènement déclenché pour la journalisation
	 * @param comment
	 *            String : commentaire de la journalisation
	 * @param category
	 *            String : catégorie de journalisation
	 * @throws ClientException
	 */
	void journaliserAction(CoreSession session, DocumentModel dossierDoc, String eventName, String comment,
			String category) throws ClientException;

	/**
	 * Journalise une action sur un dossier avec un DocumentEventContext en utilisant l'utilisateur principal passé en
	 * paramètre
	 * 
	 * @param session
	 * @param dossierDoc
	 * @param eventName
	 * @param comment
	 * @param category
	 * @throws ClientException
	 */
	void journaliserActionForUser(CoreSession session, Principal user, DocumentModel dossierDoc, String eventName,
			String comment, String category) throws ClientException;

	/**
	 * 
	 */
	void journaliserActionAdministration(CoreSession session, String idDossier, String eventName, String comment)
			throws ClientException;
	
	/**
	 * Journalise une action de catégorie administration avec un identifiant dossier et un principal donné
	 * @param session
	 * @param principal
	 * @param idDossier
	 * @param eventName
	 * @param comment
	 * @throws ClientException
	 */
	void journaliserActionAdministration(CoreSession session, Principal principal, String idDossier, String eventName, String comment)
			throws ClientException;
	
	/**
	 * Journalise une action de catégorie administration avec un dossier et un principal donné
	 * @param session
	 * @param principal
	 * @param dossierDoc
	 * @param eventName
	 * @param comment
	 * @throws ClientException
	 */
	void journaliserActionAdministration(CoreSession session, Principal principal, DocumentModel dossierDoc, String eventName,
			String comment) throws ClientException;

	/**
	 * Journalise une action dans le PAN (EPG) avec un EventContext en utilisant l'utilisateur principal passé en
	 * paramètre
	 * 
	 * @param session
	 * @param dossierDoc
	 * @param eventName
	 * @param comment
	 * @param category
	 * @throws ClientException
	 */
	void journaliserActionPAN(CoreSession session, String idDossier, String eventName, String comment, String category)
			throws ClientException;
}
