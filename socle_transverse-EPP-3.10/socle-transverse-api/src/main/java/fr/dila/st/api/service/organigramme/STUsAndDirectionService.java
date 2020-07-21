package fr.dila.st.api.service.organigramme;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.user.STUser;

public interface STUsAndDirectionService {

	/**
	 * Retourne la liste des utilisateurs dans l'unité structurelle
	 * 
	 * @param uniteStructurelleId
	 * @return
	 * @throws ClientException
	 */
	List<STUser> getUserFromUniteStructurelle(String uniteStructurelleId) throws ClientException;

	/**
	 * update l'unité structurelle
	 * 
	 * @param poste
	 *            poste
	 * @throws ClientException
	 *             ClientException
	 */
	void updateUniteStructurelle(UniteStructurelleNode uniteStructurelle) throws ClientException;

	/**
	 * Enregistre l'unité structurelle
	 * 
	 * @param newUniteStructurelle
	 *            newUniteStructurelle
	 * @throws ClientException
	 *             ClientException
	 */
	UniteStructurelleNode createUniteStructurelle(UniteStructurelleNode newUniteStructurelle) throws ClientException;

	/**
	 * retourne un documentModel ayant le schema organigramme-unite-structurelle
	 * 
	 * @return poste
	 * @throws ClientException
	 */
	UniteStructurelleNode getBareUniteStructurelleModel() throws ClientException;

	/**
	 * Retourne la direction depuis l'id du poste
	 * 
	 * @param posteId
	 * @return
	 * @throws ClientException
	 */
	List<OrganigrammeNode> getDirectionFromPoste(String posteId) throws ClientException;

	UniteStructurelleNode getUniteStructurelleNode(String usId) throws ClientException;

	/**
	 * Retourne la liste des id utilisateurs de l'unité structurelle à partir de son identifiant.
	 * 
	 * @param uniteStructurelleId
	 * @return
	 * @throws ClientException
	 */
	List<String> findUserFromUniteStructurelle(String uniteStructurelleId) throws ClientException;

	/**
	 * Recherche la liste des directions (unités structuelles de niveau 2) associées à un ministère (unités structuelles
	 * de niveau 1).
	 * 
	 * @param ministereNode
	 *            Ministère
	 * @return Liste des directions
	 * @throws ClientException
	 */
	List<UniteStructurelleNode> getDirectionListFromMinistere(EntiteNode ministereNode) throws ClientException;

	List<UniteStructurelleNode> getUniteStructurelleParent(UniteStructurelleNode node) throws ClientException;

	List<EntiteNode> getEntiteParent(UniteStructurelleNode node) throws ClientException;

	List<UniteStructurelleNode> getDirectionsParentsFromUser(String userId) throws ClientException;

	List<String> getDirectionNameParentsFromUser(String userId) throws ClientException;

	List<UniteStructurelleNode> getUniteStructurelleEnfant(String elementID, OrganigrammeType type)
			throws ClientException;

}
