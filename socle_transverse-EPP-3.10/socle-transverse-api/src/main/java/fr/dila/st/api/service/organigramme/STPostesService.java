package fr.dila.st.api.service.organigramme;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.PosteNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.user.STUser;

public interface STPostesService {

	PosteNode getPoste(String posteId) throws ClientException;

	/**
	 * Retourne le poste du SGG
	 * 
	 * @return node du poste dont le boolean chargeMissionSGG vaut TRUE
	 * @throws ClientException
	 */
	PosteNode getSGGPosteNode() throws ClientException;

	/**
	 * retourne un documentModel ayant le schema organigramme-poste
	 * 
	 * @return poste
	 * @throws ClientException
	 */
	PosteNode getBarePosteModel() throws ClientException;

	/**
	 * update le poste
	 * 
	 * @param poste
	 *            poste
	 * @throws ClientException
	 *             ClientException
	 */
	void updatePoste(CoreSession coreSession, PosteNode poste) throws ClientException;

	/**
	 * Renvoie true si chaque ministère du gouvernement (gouvernementNode) contient un poste bdc
	 * 
	 * @param gouvernementNode
	 * @return
	 * @throws ClientException
	 */
	boolean isPosteBdcInEachEntiteFromGouvernement(OrganigrammeNode gouvernementNode) throws ClientException;

	/**
	 * Retourne la liste des utilisateurs du poste à partir de son identifiant.
	 * 
	 * @param posteId
	 * @return
	 * @throws ClientException
	 */
	List<STUser> getUserFromPoste(String posteId) throws ClientException;

	/**
	 * map les psotes avec une session
	 * 
	 * @param postesId
	 * @return
	 * @throws ClientException
	 */
	List<PosteNode> getPostesNodes(Collection<String> postesId) throws ClientException;

	/**
	 * Met l'attibut posteBdc à false pour tous les postes de la liste
	 * 
	 * @param bdcList
	 * @throws ClientException
	 */
	void deactivateBdcPosteList(List<OrganigrammeNode> bdcList) throws ClientException;

	/**
	 * get la Liste des bdc poste
	 * 
	 * @return
	 * @throws ClientException
	 */
	List<PosteNode> getPosteBdcNodeList() throws ClientException;

	/**
	 * Ajout des postes BDC de l'ancien poste BDC vers le nouveau
	 * 
	 * @param posteBdcToMigrate
	 */
	void addBdcPosteToNewPosteBdc(Map<String, List<OrganigrammeNode>> posteBdcToMigrate) throws ClientException;

	/**
	 * retourne la liste des id des postes d'un noeud
	 * 
	 * @param node
	 * @return
	 * @throws ClientException
	 */
	List<String> getPosteIdInSubNode(OrganigrammeNode node) throws ClientException;

	/**
	 * Renvoie les postes bdc du ministère (entiteId)
	 * 
	 * @param entiteId
	 * @return
	 * @throws ClientException
	 */
	List<OrganigrammeNode> getPosteBdcListInEntite(String entiteId) throws ClientException;

	/**
	 * Renvoie le poste bdc du ministère (entiteId)
	 * 
	 * @param entiteId
	 * @return
	 * @throws ClientException
	 */
	PosteNode getPosteBdcInEntite(String entiteId) throws ClientException;

	/**
	 * Enregistre le poste
	 * 
	 * @param newPoste
	 *            newPoste
	 * @throws ClientException
	 *             ClientException
	 */
	void createPoste(CoreSession coreSession, PosteNode newPoste) throws ClientException;

	/**
	 * Vérifie si le poste en paramètre est l'unique poste d'un utilisateur
	 * 
	 * @param posteNode
	 * @return
	 * @throws ClientException
	 */
	boolean userHasOnePosteOnly(PosteNode posteNode) throws ClientException;

	/**
	 * Retourne la liste des id utilisateurs du poste à partir de son identifiant.
	 * 
	 * @param posteId
	 * @return
	 * @throws ClientException
	 */
	List<String> getUserNamesFromPoste(String posteId) throws ClientException;

	List<EntiteNode> getEntitesParents(PosteNode poste) throws ClientException;

	List<UniteStructurelleNode> getUniteStructurelleParentList(PosteNode poste) throws ClientException;

	List<PosteNode> getAllPostesForUser(String userId) throws ClientException;

	List<String> getAllPosteNameForUser(String userId) throws ClientException;

	List<PosteNode> getPosteNodeEnfant(String nodeId, OrganigrammeType type) throws ClientException;

	void addUserToPostes(List<String> postes, String username) throws ClientException;

	List<String> getAllPosteIdsForUser(String userId) throws ClientException;

	void removeUserFromPoste(String poste, String userName) throws ClientException;

	// récupère tous les postes présents dans la table poste
	List<PosteNode> getAllPostes();

	// Supprime toutes les occurences de l'utilisateur dans la table poste
	String deleteUserFromAllPostes(String userId);

}
