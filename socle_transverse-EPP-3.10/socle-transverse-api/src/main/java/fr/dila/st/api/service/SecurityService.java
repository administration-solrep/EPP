package fr.dila.st.api.service;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.security.ACP;

/**
 * Service de sécurité du socle transverse.
 * 
 * @author jtremeaux
 */
public interface SecurityService {

	/**
	 * Ajoute une nouvelle ACE d'une nouvelle ACL à une ACP en première position. note : à utiliser pour les ACE de type
	 * Deny que l'on doit vérifier en premier.
	 * 
	 * @param acp
	 *            ACP
	 * @param aclName
	 *            Nom de l'ACL (créée à la volée si nécessaire)
	 * @param group
	 *            Nom du groupe
	 * @param privilege
	 *            Nom du privilège
	 * @param grant
	 *            Accès / déni d'accès
	 * @throws ClientException
	 */
	void addAceToAcpInFirstPosition(ACP acp, String aclName, String group, String privilege, boolean grant)
			throws ClientException;

	/**
	 * Ajoute une ACE à une ACP.
	 * 
	 * @param acp
	 *            ACP
	 * @param aclName
	 *            Nom de l'ACL (créée à la volée si nécessaire)
	 * @param group
	 *            Nom du groupe
	 * @param privilege
	 *            Nom du privilège
	 * @param grant
	 *            Accès / déni d'accès
	 * @throws ClientException
	 */
	void addAceToAcp(ACP acp, String aclName, String group, String privilege, boolean grant) throws ClientException;

	/**
	 * Enleve une ACE à une ACP.
	 * 
	 * @param acp
	 *            ACP
	 * @param aclName
	 *            Nom de l'ACL (créée à la volée si nécessaire)
	 * @param group
	 *            Nom du groupe
	 * @param privilege
	 *            Nom du privilège
	 * @throws ClientException
	 */
	void removeAceToAcp(ACP acp, String aclName, String group, String privilege, boolean grant) throws ClientException;

	/**
	 * Enleve une ACE à une ACL. Si l'ACL n'existe pas, on le signale dans les logs.
	 * 
	 * @param doc
	 * @param aclName
	 * @param group
	 * @param privilege
	 * @throws ClientException
	 */
	void removeAceToAcp(DocumentModel doc, String aclName, String group, String privilege) throws ClientException;

	/**
	 * Ajoute une ACE grant à une ACP.
	 * 
	 * @param acp
	 *            ACP
	 * @param aclName
	 *            Nom de l'ACL (créée à la volée si nécessaire)
	 * @param group
	 *            Nom du groupe
	 * @param privilege
	 *            Nom du privilège
	 * @throws ClientException
	 */
	void addAceToAcp(ACP acp, String aclName, String group, String privilege) throws ClientException;

	/**
	 * Ajoute une ACE à une ACL. L'ACL est créée à la volée si c'est nécessaire.
	 * 
	 * @param doc
	 *            Document
	 * @param aclName
	 *            ID de l'ACL
	 * @param baseFunction
	 *            Fonction unitaire
	 * @param privilege
	 *            Privilège
	 * @throws ClientException
	 *             ClientException
	 */
	void addAceToAcl(DocumentModel doc, String aclName, String group, String privilege) throws ClientException;

	/**
	 * Ajoute une ACE à l'ACL security.
	 * 
	 * @param doc
	 *            Document
	 * @param baseFunction
	 *            Fonction unitaire
	 * @param privilege
	 *            Privilège
	 * @throws ClientException
	 *             ClientException
	 */
	void addAceToSecurityAcl(DocumentModel doc, String baseFunction, String privilege) throws ClientException;

	/**
	 * Ajoute une ACE à l'ACL security (sauvegarde l'ACL via la session passée en paramètre).
	 * 
	 * @param session
	 *            Session
	 * @param doc
	 *            Document
	 * @param baseFunction
	 *            Fonction unitaire
	 * @param string
	 *            Privilège name
	 * @throws ClientException
	 *             ClientException
	 */
	void addAceToSecurityAcl(CoreSession session, DocumentModel doc, String baseFunction, String privilege)
			throws ClientException;
}
