package fr.dila.st.api.service.organigramme;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.api.user.STUser;

/**
 * Interface du service de gestion de l'organigramme.
 * 
 * @author FEO
 */
public interface OrganigrammeService extends Serializable {

	/**
	 * Supprime un noeud de l'organigramme Suppression logique (deleted = TRUE) Si pas de FDR active dans les postes
	 * 
	 * @param coreSession
	 *            core session
	 * @param dn
	 *            Identifiant technique du poste
	 * @throws ClientException
	 */
	void deleteFromDn(OrganigrammeNode node, boolean notifyUser) throws ClientException;

	/**
	 * desactive un node avec vérification des postes actifs
	 * 
	 * @param coreSession
	 * @param selectedNode
	 * @throws ClientException
	 */
	void disableNodeFromDn(String selectedNode, OrganigrammeType type) throws ClientException;

	/**
	 * Active un node
	 * 
	 * @param coreSession
	 * @param selectedNode
	 * @throws ClientException
	 */
	void enableNodeFromDn(String selectedNode, OrganigrammeType type) throws ClientException;

	/**
	 * desactive un node sans vérification des postes actifs
	 * 
	 * @param coreSession
	 * @param selectedNode
	 * @throws ClientException
	 */
	void disableNodeFromDnNoChildrenCheck(String selectedNode, OrganigrammeType type) throws ClientException;

	/**
	 * Retourne le mail de l'utilisateur depuis l'uid
	 * 
	 * @param uid
	 * @return
	 * @throws ClientException
	 */
	String getMailFromUid(String uid) throws ClientException;

	/**
	 * Renvoie le user
	 * 
	 * @param userId
	 *            id du user
	 * @return {@link OrganigrammeNode}
	 * @throws ClientException
	 */
	public UserNode getUserNode(String userId) throws ClientException;

	/**
	 * Lock l'élément d'organigramme
	 * 
	 * @param session
	 *            core session
	 * @param node
	 *            élément à verrouiller
	 * @return true si le verrou est posé
	 * @throws ClientException
	 */
	boolean lockOrganigrammeNode(CoreSession session, OrganigrammeNode node) throws ClientException;

	/**
	 * Déverrouile l'élément d'organigramme
	 * 
	 * @param session
	 * @param node
	 * @return
	 * @throws ClientException
	 */
	boolean unlockOrganigrammeNode(OrganigrammeNode node) throws ClientException;

	/**
	 * Renvoie false si le nom de l'élément existe déjà sous le même parent
	 * 
	 * @param node
	 *            le noeud dont on teste le label
	 * @return
	 * @throws ClientException
	 */
	boolean checkUniqueLabelInParent(CoreSession session, OrganigrammeNode adapter) throws ClientException;

	/**
	 * Renvoie false si le nom de l'élément existe déjà override de la methode checkUniqueLabel qui ne vérifie le nom
	 * unique que si les 2 éléments ont un ministère parent en commun
	 * 
	 * @param node
	 * @return
	 * @throws ClientException
	 */
	boolean checkUniqueLabel(OrganigrammeNode node) throws ClientException;

	/**
	 * Copie le noeud et ses enfants dans le parent. Sans copier les utilisateurs.
	 * 
	 * @param coreSession
	 * @param nodeToCopy
	 * @param parentNode
	 * @throws ClientException
	 */
	void copyNodeWithoutUser(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode)
			throws ClientException;

	/**
	 * Copie le noeud et ses enfants dans le parent copie aussi les utilisateurs.
	 * 
	 * @param coreSession
	 * @param nodeToCopy
	 * @param parentNode
	 * @throws ClientException
	 */
	void copyNodeWithUsers(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode)
			throws ClientException;

	/**
	 * Retourne la liste des éléments verrouillés
	 * 
	 * @return List<OrganigrammeNode> Liste des élements verrouillés
	 * @throws ClientException
	 */
	List<OrganigrammeNode> getLockedNodes() throws ClientException;

	/**
	 * retourne la liste des enfants d'un {@link OrganigrammeNode}
	 * 
	 * @param coreSession
	 * @param node
	 * @param showDeactivedNode
	 * 
	 * @return
	 * @throws ClientException
	 */
	List<OrganigrammeNode> getChildrenList(CoreSession coreSession, OrganigrammeNode node, Boolean showDeactivedNode)
			throws ClientException;

	/**
	 * retourne la liste des parents d'un {@link OrganigrammeNode}
	 * 
	 * @param node
	 * @return
	 * @throws ClientException
	 */
	List<OrganigrammeNode> getParentList(OrganigrammeNode node) throws ClientException;

	/**
	 * retourne la liste des parents d'un noeud en fonction de sa classe
	 * 
	 * @param node
	 * @param ldapSessionContainer
	 * @return
	 * @throws ClientException
	 */
	List<OrganigrammeNode> getParentList(OrganigrammeNode node, EntityManager manager) throws ClientException;

	/**
	 * Verifie si un des parents est un enfant de l'unité structurelle
	 * 
	 * @param usNode
	 * @param node
	 * @return
	 * @throws ClientException
	 */
	boolean checkParentListContainsChildren(UniteStructurelleNode usNode, OrganigrammeNode node,
			Boolean showDeactivedNode) throws ClientException;

	/**
	 * Retourne le premier enfant d'un element dans l'organigramme
	 * 
	 * @param parent
	 * @param session
	 * @param showDeactivedNode
	 * @return
	 */
	OrganigrammeNode getFirstChild(OrganigrammeNode parent, CoreSession session, Boolean showDeactivedNode)
			throws ClientException;

	/**
	 * Liste des noeuds à la racine
	 * 
	 * @return Liste des noeuds à la racine
	 * @throws ClientException
	 */
	List<? extends OrganigrammeNode> getRootNodes() throws ClientException;

	/**
	 * Récupère un OrganigrammeNode d'id nodeId dans une session ldap
	 * 
	 * @param nodeId
	 * @param session
	 *            session ldap dans lequel se trouve le node
	 * @return
	 * @throws ClientException
	 */
	OrganigrammeNode getOrganigrammeNodeById(String nodeId, OrganigrammeType type) throws ClientException;

	List<OrganigrammeNode> getOrganigrammeNodesById(Map<String, OrganigrammeType> elems) throws ClientException;

	/**
	 * Créé un noeud dans l'organigramme à partir d'un OrganigrammeNode
	 * 
	 * @param coreSession
	 * @param node
	 * @throws ClientException
	 */
	OrganigrammeNode createNode(OrganigrammeNode node) throws ClientException;

	/**
	 * Mise a jour d'un node
	 * 
	 * @param coreSession
	 * @param node
	 *            element à mettre à jour
	 * @param notifyJournal
	 *            permet d'envoyer un event pour le mettre dans le journal d'administration
	 * @throws ClientException
	 */
	void updateNode(OrganigrammeNode node, Boolean notifyJournal) throws ClientException;

	/**
	 * Recherche tous les utilisateurs appartenant au noeud spécifié (descend récursivement dans les sous-noeuds de
	 * l'arbre).
	 * 
	 * @param nodeParent
	 *            Noeud à recherche
	 * @return Liste d'utilisateurs
	 * @throws ClientException
	 */
	List<STUser> getUsersInSubNode(OrganigrammeNode nodeParent) throws ClientException;

	void updateNodes(List<? extends OrganigrammeNode> listNode, Boolean notifyJournal) throws ClientException;

	/**
	 * Recherche tous les utilisateurs appartenant au noeud spécifié (descend récursivement dans les sous-noeuds de
	 * l'arbre).
	 * 
	 * @param nodeParent
	 *            Noeud à recherche
	 * @return Liste d'utilisateurs
	 * @throws ClientException
	 */
	List<String> findUserInSubNode(OrganigrammeNode nodeParent) throws ClientException;

	List<OrganigrammeNode> getOrganigrameLikeLabel(String label, OrganigrammeType type) throws ClientException;
	
	/**
	 * Retourne la liste de tous les postes d'un noeud de l'organigramme et de ses sous-noeuds
	 */
	public List<PosteNode> getAllSubPostes(OrganigrammeNode minNode) throws ClientException;

}
