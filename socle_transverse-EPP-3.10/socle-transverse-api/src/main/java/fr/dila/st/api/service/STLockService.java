package fr.dila.st.api.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.webapp.edit.lock.LockActions;

/**
 * Interface du service de verrous du socle transverse.
 * 
 * @author jtremeaux
 */
public interface STLockService extends Serializable {
	String	LOCKER		= "document.locker";

	String	LOCK_TIME	= "document.lock.time";

	// TODO fonctionnalité faisant référence au Web à déplacer plutot dans le LockActionsBean
	boolean unlockDoc(DocumentModel document, CoreSession documentManager, LockActions lockActions)
			throws ClientException;

	/**
	 * Verrouille un document.
	 * 
	 * @param session
	 *            Session
	 * @param document
	 *            Document à verrouiller
	 * @return Succès
	 * @throws ClientException
	 */
	boolean lockDoc(CoreSession session, DocumentModel document) throws ClientException;

	/**
	 * Déverrouille un document, même si l'utilisateur ne possède pas les droits nécessaires.
	 * 
	 * @param session
	 *            Session
	 * @param document
	 *            Document à déverrouiller
	 * @return Succès
	 * @throws ClientException
	 */
	boolean unlockDocUnrestricted(CoreSession session, DocumentModel document) throws ClientException;

	/**
	 * Déverrouille un document.
	 * 
	 * @param session
	 *            Session
	 * @param document
	 *            Document à déverrouiller
	 * @return Succès
	 * @throws ClientException
	 */
	boolean unlockDoc(CoreSession session, DocumentModel document) throws ClientException;

	/**
	 * Retourne les données de verrou du document.
	 * 
	 * @param session
	 *            Session
	 * @param document
	 *            Document
	 * @return Données de verrou du document
	 * @throws ClientException
	 */
	Map<String, String> getLockDetails(CoreSession session, DocumentModel document) throws ClientException;

	/**
	 * Retourne vrai si le verrou est modifiable par l'utilisateur.
	 * 
	 * @param session
	 *            Session
	 * @param document
	 *            Document
	 * @param user
	 *            Utilisateur
	 * @return Vrai si le verrou est modifible par l'utilisateur
	 * @throws ClientException
	 */
	boolean isLockActionnableByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user)
			throws ClientException;

	/**
	 * Retourne vrai si le document est verrouillé par l'utilisateur.
	 * 
	 * @param session
	 *            Session
	 * @param document
	 *            Document
	 * @param user
	 *            Utilisateur
	 * @return Vrai si le verrou est modifible par l'utilisateur
	 * @throws ClientException
	 */
	boolean isLockByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user) throws ClientException;

	/**
	 * Retourne vrai si le document est verrouillé par l'utilisateur courrant.
	 * 
	 * @param session
	 *            Session
	 * @param document
	 *            Document
	 * @return Vrai si le verrou est modifible par l'utilisateur
	 * @throws ClientException
	 */
	boolean isLockByCurrentUser(CoreSession session, DocumentModel document) throws ClientException;

	// TODO fonctionnalité faisant référence au Web à déplacer plutot dans le LockActionsBean
	boolean isLockActionnableByUser(CoreSession session, DocumentModel document, NuxeoPrincipal user,
			LockActions lockActions) throws ClientException;

	/**
	 * Cherche dans la base pour les ids des documents donnés en parametre ceux qui sont verrouillé
	 * 
	 * @param session
	 * @param docIds
	 * @return ensemble d'id verrouillé
	 * @throws ClientException
	 */
	Set<String> extractLocked(CoreSession session, Collection<String> docIds) throws ClientException;

	/**
	 * Cherche dans la base pour les ids des documents donnés en parametre ceux qui sont verrouillé et le login de
	 * l'utilisateur ayant locké le document.
	 * 
	 * @param session
	 * @param docIds
	 * @return ensemble d'id verrouillé
	 * @throws ClientException
	 */
	Map<String, String> extractLockedInfo(CoreSession session, Collection<String> docIds) throws ClientException;

	/**
	 * unlock tous les documents lockés par l'utilisateur
	 * 
	 * @param session
	 * @param userId
	 * @throws ClientException
	 */
	void unlockAllDocumentLockedByUser(CoreSession session, String userId) throws ClientException;
}
