package fr.dila.st.api.service.organigramme;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.user.STUser;
import java.util.List;

public interface STUsAndDirectionService {
    /**
     * Retourne la liste des utilisateurs dans l'unité structurelle
     *
     * @param uniteStructurelleId
     * @return
     *
     */
    List<STUser> getUserFromUniteStructurelle(String uniteStructurelleId);

    /**
     * update l'unité structurelle
     *
     * @param uniteStructurelle
     *            unité Structurelle
     *
     */
    void updateUniteStructurelle(UniteStructurelleNode uniteStructurelle);

    /**
     * Enregistre l'unité structurelle
     *
     * @param newUniteStructurelle
     *            newUniteStructurelle
     */
    UniteStructurelleNode createUniteStructurelle(UniteStructurelleNode newUniteStructurelle);

    /**
     * retourne un documentModel ayant le schema organigramme-unite-structurelle
     *
     * @return poste
     *
     */
    UniteStructurelleNode getBareUniteStructurelleModel();

    /**
     * Retourne la direction depuis l'id du poste
     *
     * @param posteId
     * @return
     *
     */
    List<OrganigrammeNode> getDirectionFromPoste(String posteId);

    /**
     * Retourne les unités structurelles parentes depuis l'id du poste
     *
     * @param posteId
     * @return
     * @throws ClientException
     */
    List<OrganigrammeNode> getUniteStructurelleFromPoste(String posteId);

    UniteStructurelleNode getUniteStructurelleNode(String usId);

    /**
     * Retourne la liste des id utilisateurs de l'unité structurelle à partir de son identifiant.
     *
     * @param uniteStructurelleId
     * @return
     *
     */
    List<String> findUserFromUniteStructurelle(String uniteStructurelleId);

    boolean isDirectionFromMinistere(UniteStructurelleNode directionNode, EntiteNode ministereNode);

    /**
     * Recherche la liste des directions (unités structuelles de niveau 2) associées à un ministère (unités structuelles
     * de niveau 1).
     *
     * @param ministereNode
     *            Ministère
     * @return Liste des directions
     *
     */
    List<UniteStructurelleNode> getDirectionListFromMinistere(EntiteNode ministereNode);

    List<UniteStructurelleNode> getUniteStructurelleParent(UniteStructurelleNode node);

    List<EntiteNode> getEntiteParent(UniteStructurelleNode node);

    List<UniteStructurelleNode> getDirectionsParentsFromUser(String userId);

    List<String> getDirectionNameParentsFromUser(String userId);

    List<UniteStructurelleNode> getUniteStructurelleEnfant(String elementID, OrganigrammeType type);

    String getLabel(String idUniteStructurelle, String defaultLabel);

    String getLabel(String directionPilote);
}
