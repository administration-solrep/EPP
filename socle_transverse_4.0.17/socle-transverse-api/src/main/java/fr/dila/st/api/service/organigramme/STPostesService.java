package fr.dila.st.api.service.organigramme;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.user.STUser;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.CoreSession;

public interface STPostesService {
    PosteNode getPoste(String posteId);

    String getPosteLabel(String posteId);

    /**
     * Retourne le poste du SGG
     *
     * @return node du poste dont le boolean chargeMissionSGG vaut TRUE
     *
     */
    PosteNode getSGGPosteNode();

    /**
     * retourne un documentModel ayant le schema organigramme-poste
     *
     * @return poste
     *
     */
    PosteNode getBarePosteModel();

    /**
     * update le poste
     *
     * @param poste
     *            poste
     *
     *             ClientException
     */
    void updatePoste(CoreSession coreSession, PosteNode poste);

    /**
     * Renvoie la liste des libellés de ministères ne contenant pas de poste bdc
     *
     * @param gouvernementNode
     * @return
     *
     */
    List<String> getEntiteWithoutBDCInGouvernement(OrganigrammeNode gouvernementNode);

    /**
     * Retourne la liste des utilisateurs du poste à partir de son identifiant.
     *
     * @param posteId
     * @return
     *
     */
    List<STUser> getUserFromPoste(String posteId);

    /**
     * map les psotes avec une session
     *
     * @param postesId
     * @return
     *
     */
    List<PosteNode> getPostesNodes(Collection<String> postesId);

    /**
     * Met l'attibut posteBdc à false pour tous les postes de la liste
     *
     * @param bdcList
     *
     */
    void deactivateBdcPosteList(List<OrganigrammeNode> bdcList);

    /**
     * get la Liste des bdc poste
     *
     * @return
     *
     */
    List<PosteNode> getPosteBdcNodeList();

    /**
     * Ajout des postes BDC de l'ancien poste BDC vers le nouveau
     *
     * @param posteBdcToMigrate
     */
    void addBdcPosteToNewPosteBdc(Map<String, List<OrganigrammeNode>> posteBdcToMigrate);

    /**
     * retourne la liste des id des postes d'un noeud
     *
     * @param node
     * @return
     *
     */
    List<String> getPosteIdInSubNode(OrganigrammeNode node);

    /**
     * Renvoie les postes bdc du ministère (entiteId)
     *
     * @param entiteId
     * @return
     *
     */
    List<OrganigrammeNode> getPosteBdcListInEntite(String entiteId);

    /**
     * Renvoie le poste bdc du ministère (entiteId)
     *
     * @param entiteId
     * @return
     *
     */
    PosteNode getPosteBdcInEntite(String entiteId);

    /**
     * Enregistre le poste
     *
     * @param newPoste
     *            newPoste
     *
     *             ClientException
     */
    void createPoste(CoreSession coreSession, PosteNode newPoste);

    /**
     * Vérifie si le poste en paramètre est l'unique poste d'un utilisateur
     *
     * @param posteNode
     * @return
     *
     */
    boolean userHasOnePosteOnly(PosteNode posteNode);

    /**
     * Renvoie la liste des utilisateurs de ce poste qui ne sont que dans ce poste
     *
     * @param posteNode
     * @return une collection d'objets STUser.
     */
    Collection<STUser> getUsersHavingOnePosteOnly(PosteNode posteNode);

    /**
     * Retourne la liste des id utilisateurs du poste à partir de son identifiant.
     *
     * @param posteId
     * @return
     *
     */
    List<String> getUserNamesFromPoste(String posteId);

    List<EntiteNode> getEntitesParents(PosteNode poste);

    List<UniteStructurelleNode> getUniteStructurelleParentList(PosteNode poste);

    List<PosteNode> getAllPostesForUser(String userId);

    List<String> getAllPosteNameForUser(String userId);

    List<PosteNode> getPosteNodeEnfant(String nodeId, OrganigrammeType type);

    void addUserToPostes(List<String> postes, String username);

    List<String> getAllPosteIdsForUser(String userId);

    void removeUserFromPoste(String poste, String userName);

    // récupère tous les postes présents dans la table poste
    List<PosteNode> getAllPostes();

    // Supprime toutes les occurences de l'utilisateur dans la table poste
    String deleteUserFromAllPostes(String userId);

    /**
     * Renvoie true si les users ont au moins un poste en commun.
     *
     * @param userId1 identifiant du premier user
     * @param userId2 identifiant du deuxième user
     * @return true si l'intersection des postes est non vide.
     */
    boolean haveAnyCommonPoste(String userId1, String userId2);

    /**
     * Ajoute le préfixe poste- à l'id du poste si ce dernier n'est pas présent
     * @param id ID du poste
     * @return ID du poste préfixé
     */
    String prefixPosteId(String id);
}
