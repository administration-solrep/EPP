package fr.dila.ss.ui.services.organigramme;

import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import java.util.List;
import org.nuxeo.ecm.core.api.CoreSession;

public interface SSOrganigrammeManagerUIService {
    /**
     * Recupere l'id de la mailbox à partir de l'id du poste utilisé dans organigramme_select_node_widget.xhtml
     *
     * @param posteId
     *            id du poste
     * @return id de la mailbox
     */
    String getMailboxIdFromPosteId(String posteId);

    Boolean contains(String selectionType, String type);

    /**
     * Retourne le label d'un node
     *
     * @param selectionType
     * @param id
     * @return
     */
    String getOrganigrammeNodeLabel(String selectionType, String id);

    /**
     * @return the newGouvernement
     */
    OrganigrammeNode getNewGouvernement();

    /**
     * @return the newEntite
     */
    OrganigrammeNode getNewEntite(String gvtNodeId);

    /**
     * @return the newPoste
     */
    OrganigrammeNode getNewPoste(String parentNodeId, String parentNodeType);

    /**
     * @return the newUniteStructurelle
     */
    UniteStructurelleNode getNewUniteStructurelle(String parentNodeId, String parentNodeType);

    boolean checkUniteStructurelleLockedByCurrentUser(SSPrincipal ssPrincipal, OrganigrammeNode nodeToUpdate);

    /**
     * Test si l'utilisateur peut modifier l'organigramme
     *
     * @param ministereId
     * @return
     */
    boolean allowUpdateOrganigramme(SSPrincipal ssPrincipal, String ministereId);

    /**
     * Test si l'utilisateur peut ajouter un poste en fonction de son ministère
     *
     * @param ministereId
     * @return
     */
    boolean allowAddPoste(SSPrincipal ssPrincipal, String ministereId);

    /**
     * Test si l'utilisateur peut ajouter un élément en fonction de son ministère
     *
     * @param ministereId
     * @return
     */
    boolean allowAddNode(SSPrincipal ssPrincipal, String ministereId);

    List<OrganigrammeNode> getCurrentGouvernementEntite(String currentGouvernement, CoreSession session);

    /**
     * Verrouille un élément d'organigramme
     *
     * @param node
     */
    boolean lockOrganigrammeNode(OrganigrammeNode node, CoreSession session);

    /**
     * Verrouille un élément d'organigramme
     *
     * @param node
     */
    boolean unlockOrganigrammeNode(OrganigrammeNode node);

    boolean isCurrentUserUnlocker(SSPrincipal ssPrincipal, String locker);

    /**
     * Retourne la liste des ministères du gouvernement courant
     *
     * @return une liste de noeuds organigramme
     */
    List<EntiteNode> getCurrentMinisteres();

    /**
     * Retourne la liste des ministères du gouvernement courant, trié par ordre protocolaire
     *
     * @return une liste de noeuds organigramme
     */
    List<EntiteNode> getSortedCurrentMinisteres();
}
