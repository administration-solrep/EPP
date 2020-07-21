package fr.dila.st.api.service;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Service permettant de déléguer ses droits à d'autres utilisateurs.
 * 
 * @author jtremeaux
 */
public interface DelegationService {
	/**
	 * Retourne la racine des délégations de l'utilisateur en cours.
	 * 
	 * @param session
	 *            Session
	 * @return Racine des délégations
	 * @throws ClientException
	 */
	DocumentModel getDelegationRoot(CoreSession session) throws ClientException;

	/**
	 * Retourne la liste des délégations actives dont l'utilisateur est destinataire.
	 * 
	 * @param session
	 *            Session
	 * @param userId
	 *            Utilisateur destinataire
	 * @throws ClientException
	 */
	List<DocumentModel> findActiveDelegationForUser(CoreSession session, String userId) throws ClientException;

	/**
	 * Envoir d'un email à la création / modification d'une délégation.
	 * 
	 * @param session
	 *            Session
	 * @param delegationDoc
	 *            Document délégation
	 * @throws ClientException
	 */
	void sendDelegationEmail(CoreSession session, DocumentModel delegationDoc) throws ClientException;
}
