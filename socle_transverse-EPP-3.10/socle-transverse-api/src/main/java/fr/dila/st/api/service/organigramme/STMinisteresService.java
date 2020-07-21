package fr.dila.st.api.service.organigramme;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.user.STUser;

public interface STMinisteresService {

	/**
	 * Retourne la liste des ministères auquel est associé le poste.
	 * 
	 * @param posteId
	 *            Identifiant technique du poste
	 * @return Liste de ministères
	 * @throws ClientException
	 */
	List<EntiteNode> getMinistereParentFromPoste(String posteId) throws ClientException;

	/**
	 * Retourne la liste des ministères auquel sont associés les postes.
	 * 
	 * @param posteIds
	 *            liste des identifiants technique du poste
	 * @return Liste de ministères
	 * @throws ClientException
	 */
	List<EntiteNode> getMinistereParentFromPostes(Set<String> posteIds) throws ClientException;

	/**
	 * Retourne la liste des ministères auquel est associé l'unité structurelle.
	 * 
	 * @param ustId
	 *            Identifiant technique de l'unite structurelle
	 * @return Liste de ministères
	 * @throws ClientException
	 */
	List<EntiteNode> getMinistereParentFromUniteStructurelle(String ustId) throws ClientException;

	/**
	 * Retourne les ministères actifs du gouvernement courant
	 * 
	 * @return
	 * @throws ClientException
	 */
	List<EntiteNode> getCurrentMinisteres() throws ClientException;

	/**
	 * Retourne tous les ministères indépendamment de leur gouvernement
	 * 
	 * @return Liste de ministères
	 * @throws ClientException
	 */
	List<EntiteNode> getAllMinisteres() throws ClientException;

	/**
	 * Retourne les ministères du gouvernement courant
	 * 
	 * @param active
	 *            Ministères actif ou non
	 * @return Liste de ministères
	 * @throws ClientException
	 */
	List<EntiteNode> getMinisteres(boolean active) throws ClientException;

	/**
	 * Retourne la liste des utilisateurs dans le ministère
	 * 
	 * @param ministereId
	 * @return
	 * @throws ClientException
	 */
	List<STUser> getUserFromMinistere(String ministereId) throws ClientException;

	/**
	 * Récupère la liste des ministères parents du noeuds
	 * 
	 * @param node
	 *            Noeud de l'organigramme (poste ou unité structurelle)
	 * @param resultList
	 *            Liste des ministères (construite par effet de bord)
	 * @throws ClientException
	 */
	List<EntiteNode> getMinisteresParents(OrganigrammeNode node) throws ClientException;

	/**
	 * Migration d'une entité vers une autre sans copier le poste BDC
	 * 
	 * @param currentMin
	 * @param newMin
	 * @throws ClientException
	 */
	void migrateEntiteToNewGouvernement(String currentMin, String newMin) throws ClientException;

	/**
	 * Migre les ministères dont le timbre n'a pas changé vers le nouveau gouvernement
	 * 
	 * @param entiteId
	 * @param newGouvernementId
	 * @throws ClientException
	 */
	void migrateUnchangedEntiteToNewGouvernement(String entiteId, String newGouvernementId) throws ClientException;

	/**
	 * remap des id sur des {@link EntiteNode}
	 * 
	 * @param entiteIds
	 * @return
	 * @throws ClientException
	 */
	List<EntiteNode> getEntiteNodes(Collection<String> entiteIds) throws ClientException;

	/**
	 * Retourne un noeud entité par son identifiant technique.
	 * 
	 * @param entiteId
	 *            Identifiant technique de l'entité
	 * @return Noeud de l'entité
	 * @throws ClientException
	 */
	EntiteNode getEntiteNode(String entiteId) throws ClientException;

	/**
	 * Enregistre l'entité
	 * 
	 * @param newEntite
	 *            entité
	 * @return
	 * @throws ClientException
	 *             ClientException
	 */
	EntiteNode createEntite(EntiteNode newEntite) throws ClientException;

	/**
	 * update l'entité
	 * 
	 * @param entite
	 *            entite
	 * @throws ClientException
	 *             ClientException
	 */
	void updateEntite(EntiteNode entite) throws ClientException;

	/**
	 * Remonte recursivement l'organigramme (poste / unités structurelles) jusqu'à un noeud de type ministère.
	 * 
	 * @param node
	 *            Noeud de l'organigramme (poste ou unité structurelle)
	 * @param resultList
	 *            Liste des ministères (construite par effet de bord)
	 * @throws ClientException
	 */
	void getMinistereParent(OrganigrammeNode node, List<EntiteNode> resultList) throws ClientException;

	/**
	 * retourne un documentModel ayant le schema organigramme-entite
	 * 
	 * @return entite
	 * @throws ClientException
	 */
	EntiteNode getBareEntiteModel() throws ClientException;

	/**
	 * Retourne la liste des id utilisateurs du ministere à partir de son identifiant.
	 * 
	 * @param ministereId
	 * @return
	 * @throws ClientException
	 */
	public List<String> findUserFromMinistere(String ministereId) throws ClientException;

	List<EntiteNode> getEntiteNodeEnfant(String nodeID) throws ClientException;

}
