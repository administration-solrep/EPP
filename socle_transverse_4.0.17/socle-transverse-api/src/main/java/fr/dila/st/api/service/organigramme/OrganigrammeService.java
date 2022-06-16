package fr.dila.st.api.service.organigramme;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UserNode;
import fr.dila.st.api.user.STUser;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Interface du service de gestion de l'organigramme.
 *
 * @author FEO
 */
public interface OrganigrammeService extends Serializable {
    /**
     * Supprime un noeud de l'organigramme Suppression logique (deleted = TRUE) Si
     * pas de FDR active dans les postes
     *
     * @param node
     * @param notifyUser
     *
     * @return null si pas d'erreur, un rapport en cas d'impossibilité
     */
    Object deleteFromDn(CoreSession session, OrganigrammeNode node, boolean notifyUser);

    /**
     * desactive un node avec vérification des postes actifs
     *
     * @param selectedNode
     * @param type
     *
     */
    void disableNodeFromDn(CoreSession session, String selectedNode, OrganigrammeType type);

    /**
     * Active un node
     *
     * @param selectedNode
     * @param type
     *
     */
    void enableNodeFromDn(CoreSession session, String selectedNode, OrganigrammeType type);

    /**
     * desactive un node sans vérification des postes actifs
     *
     * @param selectedNode
     * @param type
     *
     */
    void disableNodeFromDnNoChildrenCheck(String selectedNode, OrganigrammeType type);

    /**
     * Retourne le mail de l'utilisateur depuis l'uid
     */
    String getMailFromUsername(String username);

    /**
     * Renvoie le user
     *
     * @param userId
     *            id du user
     * @return {@link OrganigrammeNode}
     *
     */
    public UserNode getUserNode(String userId);

    /**
     * Lock l'élément d'organigramme
     *
     * @param session
     *            core session
     * @param node
     *            élément à verrouiller
     * @return true si le verrou est posé
     *
     */
    boolean lockOrganigrammeNode(CoreSession session, OrganigrammeNode node);

    /**
     * Déverrouile l'élément d'organigramme
     *
     * @param node
     * @return
     *
     */
    boolean unlockOrganigrammeNode(OrganigrammeNode node);

    /**
     * Renvoie false si le nom de l'élément existe déjà sous le même parent
     *
     * @param session
     * @param adapter
     *            le noeud dont on teste le label
     * @return
     *
     */
    boolean checkEntiteUniqueLabelInParent(CoreSession session, EntiteNode adapter);

    /**
     * Renvoie false si le nom de l'élément existe déjà override de la methode checkUniqueLabel qui ne vérifie le nom
     * unique que si les 2 éléments ont un ministère parent en commun
     *
     * @param node
     * @return
     *
     */
    boolean checkUniqueLabel(OrganigrammeNode node);

    /**
     * Copie le noeud et ses enfants dans le parent. Sans copier les utilisateurs.
     *
     * @param coreSession
     * @param nodeToCopy
     * @param parentNode
     *
     */
    void copyNodeWithoutUser(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode);

    /**
     * Copie le noeud et ses enfants dans le parent copie aussi les utilisateurs.
     *
     * @param coreSession
     * @param nodeToCopy
     * @param parentNode
     *
     */
    void copyNodeWithUsers(CoreSession coreSession, OrganigrammeNode nodeToCopy, OrganigrammeNode parentNode);

    /**
     * Retourne la liste des éléments verrouillés
     *
     * @return Liste des élements verrouillés
     *
     */
    List<OrganigrammeNode> getLockedNodes();

    /**
     * retourne la liste des enfants d'un {@link OrganigrammeNode}
     *
     * @param coreSession
     * @param node
     * @param showDeactivedNode
     *
     * @return
     *
     */
    List<OrganigrammeNode> getChildrenList(CoreSession coreSession, OrganigrammeNode node, Boolean showDeactivedNode);

    /**
     * retourne la liste des parents d'un {@link OrganigrammeNode}
     *
     * @param node
     * @return
     *
     */
    List<OrganigrammeNode> getParentList(OrganigrammeNode node);

    boolean checkNodeContainsChild(OrganigrammeNode node, OrganigrammeNode childNode);

    /**
     * Retourne le premier enfant d'un element dans l'organigramme
     *
     * @param parent
     * @param session
     * @param showDeactivedNode
     * @return
     */
    OrganigrammeNode getFirstChild(OrganigrammeNode parent, CoreSession session, Boolean showDeactivedNode);

    /**
     * Liste des noeuds à la racine
     *
     * @return Liste des noeuds à la racine
     *
     */
    List<? extends OrganigrammeNode> getRootNodes();

    /**
     * Récupère un OrganigrammeNode d'id nodeId dans une session ldap
     *
     * @param nodeId
     * @param type
     * @return
     *
     */
    <T extends OrganigrammeNode> T getOrganigrammeNodeById(String nodeId, OrganigrammeType type);

    List<OrganigrammeNode> getOrganigrammeNodesById(Map<String, OrganigrammeType> elems);

    OrganigrammeNode getOrganigrammeNodeById(String nodeId);

    /**
     * Créé un noeud dans l'organigramme à partir d'un OrganigrammeNode
     *
     * @param node
     *
     */
    OrganigrammeNode createNode(OrganigrammeNode node);

    /**
     * Mise a jour d'un node
     *
     * @param node
     *            element à mettre à jour
     * @param notifyJournal
     *            permet d'envoyer un event pour le mettre dans le journal d'administration
     *
     */
    void updateNode(OrganigrammeNode node, Boolean notifyJournal);

    /**
     * @return true si l'utilisateur spécifié est dans un sous noeud du noeud spécifié.
     */
    boolean isUserInSubNode(OrganigrammeNode nodeParent, String username);

    /**
     * Recherche tous les utilisateurs appartenant au noeud spécifié (descend récursivement dans les sous-noeuds de
     * l'arbre).
     *
     * @param nodeParent
     *            Noeud à recherche
     * @return Liste d'utilisateurs
     *
     */
    List<STUser> getUsersInSubNode(OrganigrammeNode nodeParent);

    void updateNodes(List<? extends OrganigrammeNode> listNode, Boolean notifyJournal);

    /**
     * Recherche tous les utilisateurs appartenant au noeud spécifié (descend récursivement dans les sous-noeuds de
     * l'arbre).
     *
     * @param nodeParent
     *            Noeud à recherche
     * @return Liste d'utilisateurs
     *
     */
    List<String> findUserInSubNode(OrganigrammeNode nodeParent);

    List<OrganigrammeNode> getOrganigrameLikeLabels(String label, List<OrganigrammeType> type);

    /**
     * Retourne la liste de tous les postes d'un noeud de l'organigramme et de ses sous-noeuds
     */
    List<PosteNode> getAllSubPostes(OrganigrammeNode minNode);

    <T> List<T> query(String query, Map<String, Object> params);

    List<String> getAllUserInSubNode(Set<String> ministereId);
}
