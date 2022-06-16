package fr.dila.st.ui.services;

import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

public interface OrganigrammeTreeUIService {
    /**
     * Méthode qui renvoie l'organigramme complet
     *
     * @param context
     * @return
     */
    List<OrganigrammeElementDTO> getOrganigramme(SpecificContext context);

    /**
     * Passe l'état à actif
     *
     * @param context
     */
    void setActiveState(SpecificContext context);

    /**
     * Passe l'état à inactif
     *
     * @param context
     */
    void setInactiveState(SpecificContext context);

    /**
     * Vérifie la possibilité de supprimer un noeud et le supprime si cela est
     * possible.
     *
     * @param type    type de noeud
     * @param id      id du noeud
     * @param context le contexte
     * @return OK si la suppression est bien effectuée, le path complet du fichier
     *         généré si la suppression est impossible.
     */
    String deleteNode(OrganigrammeType type, String id, SpecificContext context);

    /**
     * Copie un noeud de l'organigramme
     *
     * @param context
     * @param itemId
     * @param type
     */
    void copyNode(SpecificContext context, String itemId, String type);

    /**
     * Colle un noeud de l'organigramme sans mettre les utilisateurs dans les
     * postes
     *
     * @param context
     * @param itemId
     * @param type
     */
    void pasteNodeWithoutUser(SpecificContext context, String itemId, String type);

    /**
     * Colle un noeud de l'organigramme avec les utilisateurs dans les postes
     *
     * @param context
     * @param itemId
     * @param type
     */
    void pasteNodeWithUsers(SpecificContext context, String itemId, String type);

    OrganigrammeNode findNodeHavingIdAndChildType(String id, OrganigrammeType curType);
}
