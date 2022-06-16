package fr.dila.st.api.service;

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
     *
     */
    void addAceToAcpInFirstPosition(ACP acp, String aclName, String group, String privilege, boolean grant);

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
     *
     */
    void addAceToAcp(ACP acp, String aclName, String group, String privilege, boolean grant);

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
     * @param grant
     *
     */
    void removeAceToAcp(ACP acp, String aclName, String group, String privilege, boolean grant);

    /**
     * Enleve une ACE à une ACL. Si l'ACL n'existe pas, on le signale dans les logs.
     *
     * @param doc
     * @param aclName
     * @param group
     * @param privilege
     *
     */
    void removeAceToAcp(DocumentModel doc, String aclName, String group, String privilege);

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
     *
     */
    void addAceToAcp(ACP acp, String aclName, String group, String privilege);

    /**
     * Ajoute une ACE à une ACL. L'ACL est créée à la volée si c'est nécessaire.
     *
     * @param doc
     *            Document
     * @param aclName
     *            ID de l'ACL
     * @param group
     *            Fonction unitaire
     * @param privilege
     *            Privilège
     *
     *             ClientException
     */
    void addAceToAcl(DocumentModel doc, String aclName, String group, String privilege);

    /**
     * Ajoute une ACE à l'ACL security.
     *
     * @param doc
     *            Document
     * @param baseFunction
     *            Fonction unitaire
     * @param privilege
     *            Privilège
     *
     *             ClientException
     */
    void addAceToSecurityAcl(DocumentModel doc, String baseFunction, String privilege);

    /**
     * Ajoute une ACE à l'ACL security (sauvegarde l'ACL via la session passée en paramètre).
     *
     * @param session
     *            Session
     * @param doc
     *            Document
     * @param baseFunction
     *            Fonction unitaire
     * @param privilege
     *            Privilège name
     *
     */
    void addAceToSecurityAcl(CoreSession session, DocumentModel doc, String baseFunction, String privilege);
}
