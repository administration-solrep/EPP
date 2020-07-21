package fr.dila.st.api.service;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.PrefetchInfo;

/**
 * Service qui permet de gérer les corbeilles utilisateur / postes, c'est à dire la recherche de dossiers via leur
 * distribution (DossierLink).
 * 
 * @author jtremeaux
 */
public interface CorbeilleService extends Serializable {

	/**
	 * Recherche tous les DossiersLink visibles par l'utilisateur pour un dossier.
	 * 
	 * @param session
	 *            Session
	 * @param dossierId
	 *            Identifiant technique du document dossier
	 * @return Liste de DossierLink
	 * @throws ClientException
	 */
	List<DocumentModel> findDossierLink(CoreSession session, String dossierId) throws ClientException;

	List<DocumentModel> findDossierLink(CoreSession session, Collection<String> dossierIds, PrefetchInfo prefetch) throws ClientException;
	
	/**
	 * Détermine l'ensemble des ID techniques des mailbox des destinataires d'un CaseLink
	 * 
	 * @param caseLinkDoc
	 *            Document CaseLink
	 * @return Ensemble d'ID techniques de mailbox
	 */
	Set<String> getMailboxIdSet(DocumentModel caseLinkDoc);

	/**
	 * get Dosier link from step Id
	 * 
	 * @param session
	 * @param stepId
	 *            the step Id
	 * @throws ClientException
	 */
	DocumentModel getDossierLink(CoreSession session, String stepId) throws ClientException;

	/**
	 * Recherche les DossiersLink correspondant à la distribution d'un dossier dans une des Mailbox passées en
	 * paramètre.
	 * 
	 * @param session
	 *            Session
	 * @param dossierId
	 *            Identifiant technique du document dossier
	 * @param posteIdList
	 *            Collection d'identifiant de mailbox ("poste-1234")...
	 * @return Liste de DossierLink
	 */
	List<DocumentModel> findDossierLinkInMailbox(CoreSession session, String dossierId, Collection<String> mailboxIdList)
			throws ClientException;

	/**
	 * Recherche les DossiersLink correspondant à la distribution d'un dossier dans une Mailbox quelconque, c'est-à-dire
	 * les distributions d'un dossier dans les mailbox de l'utilisateur.
	 * 
	 * @param session
	 *            Session
	 * @param dossierId
	 *            Identifiant technique du document dossier
	 * @return Liste de DossierLink
	 */
	List<DocumentModel> findDossierLinkUnrestricted(CoreSession session, String dossierId) throws ClientException;

	/**
	 * Retourne la liste des labels des etapes courantes d'un dossier
	 * 
	 * @param session
	 * @param dossierId
	 * @return
	 * @throws ClientException
	 */
	List<String> findCurrentStepsLabel(CoreSession session, String dossierId) throws ClientException;

	/**
	 * Recherche les DossiersLink correspondant à la distribution des dossiers dans une des Mailbox passées en
	 * paramètre.
	 * @param session
	 * @param dossiersDocsIds
	 * @param mailboxIdList
	 * @return
	 * @throws ClientException
	 */
	List<DocumentModel> findDossierLinkInMailbox(CoreSession session, List<String> dossiersDocsIds,
			Collection<String> mailboxIdList) throws ClientException;

	/**
	 * Recherche les DossiersLink correspondant à la distribution des dossiers dans une Mailbox quelconque, c'est-à-dire
	 * les distributions des dossiers dans les mailbox de l'utilisateur.
	 * 
	 * @param session
	 *            Session
	 * @param dossierId
	 *            Identifiant technique du document dossier
	 * @return Liste de DossierLink
	 */
	List<DocumentModel> findDossierLinkUnrestricted(CoreSession session, List<String> dossiersDocsIds)
			throws ClientException;
}
