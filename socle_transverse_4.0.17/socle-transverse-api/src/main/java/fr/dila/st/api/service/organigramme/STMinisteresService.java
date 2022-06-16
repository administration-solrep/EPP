package fr.dila.st.api.service.organigramme;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.user.STUser;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.nuxeo.ecm.core.api.CoreSession;

public interface STMinisteresService {
    /**
     * Retourne la liste des ministères auquel est associé le poste.
     *
     * @param posteId
     *            Identifiant technique du poste
     * @return Liste de ministères
     *
     */
    List<EntiteNode> getMinistereParentFromPoste(String posteId);

    /**
     * Retourne la liste des ministères auquel sont associés les postes.
     *
     * @param posteIds
     *            liste des identifiants technique du poste
     * @return Liste de ministères
     *
     */
    List<EntiteNode> getMinistereParentFromPostes(Set<String> posteIds);

    /**
     * Retourne la liste des ministères auquel est associé l'unité structurelle.
     *
     * @param ustId
     *            Identifiant technique de l'unite structurelle
     * @return Liste de ministères
     *
     */
    List<EntiteNode> getMinistereParentFromUniteStructurelle(String ustId);

    /**
     * Retourne les ministères actifs du gouvernement courant
     *
     * @return
     *
     */
    List<EntiteNode> getCurrentMinisteres();

    /**
     * Retourne tous les ministères indépendamment de leur gouvernement
     *
     * @return Liste de ministères
     *
     */
    List<EntiteNode> getAllMinisteres();

    /**
     * Retourne les ministères du gouvernement courant
     *
     * @param active
     *            Ministères actif ou non
     * @return Liste de ministères
     *
     */
    List<EntiteNode> getMinisteres(boolean active);

    boolean isUserFromMinistere(String ministereId, String username);

    /**
     * Retourne la liste des utilisateurs dans le ministère
     *
     * @param ministereId
     * @return
     *
     */
    List<STUser> getUserFromMinistere(String ministereId);

    List<STUser> getAdminsMinisterielsFromMinistere(String ministereId);

    /**
     * Récupère la liste des ministères parents du noeuds
     *
     * @param node
     *            Noeud de l'organigramme (poste ou unité structurelle)
     *
     */
    List<EntiteNode> getMinisteresParents(OrganigrammeNode node);

    /**
     * Migration d'une entité vers une autre sans copier le poste BDC
     *
     * @param currentMin
     * @param newMin
     *
     */
    void migrateEntiteToNewGouvernement(String currentMin, String newMin);

    /**
     * Migre les ministères dont le timbre n'a pas changé vers le nouveau gouvernement
     *
     * @param entiteId
     * @param newGouvernementId
     *
     */
    void migrateUnchangedEntiteToNewGouvernement(String entiteId, String newGouvernementId);

    /**
     * remap des id sur des {@link EntiteNode}
     *
     * @param entiteIds
     * @return
     *
     */
    List<EntiteNode> getEntiteNodes(Collection<String> entiteIds);

    /**
     * Retourne un noeud entité par son identifiant technique.
     *
     * @param entiteId
     *            Identifiant technique de l'entité
     * @return Noeud de l'entité
     *
     */
    EntiteNode getEntiteNode(String entiteId);

    /**
     * Enregistre l'entité
     *
     * @param newEntite
     *            entité
     * @return
     *
     *             ClientException
     */
    EntiteNode createEntite(EntiteNode newEntite);

    /**
     * update l'entité
     *
     * @param entite
     *            entite
     *
     */
    void updateEntite(CoreSession session, EntiteNode entite);

    /**
     * Remonte recursivement l'organigramme (poste / unités structurelles) jusqu'à un noeud de type ministère.
     *
     * @param node
     *            Noeud de l'organigramme (poste ou unité structurelle)
     * @param resultList
     *            Liste des ministères (construite par effet de bord)
     *
     */
    void getMinistereParent(OrganigrammeNode node, List<EntiteNode> resultList);

    /**
     * Retourne un noeud entité
     *
     * @return entite
     *
     */
    EntiteNode getBareEntiteModel();

    /**
     * Retourne la liste des id utilisateurs du ministere à partir de son identifiant.
     *
     * @param ministereId
     * @return
     *
     */
    public List<String> findUserFromMinistere(String ministereId);

    List<EntiteNode> getEntiteNodeEnfant(String nodeID);
}
