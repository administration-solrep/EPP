package fr.dila.st.api.organigramme;

import java.util.List;

public interface WithSubUSNode extends OrganigrammeNode {
    /**
     * Retourne la liste des sous-unités structurelles.
     *
     * @return Liste des sous-unités structurelles
     */
    List<UniteStructurelleNode> getSubUnitesStructurellesList();

    /**
     * Renseigne la liste des sous-unités structurelles.
     *
     * @param subUnitesStructurellesList
     *            Liste des sous-unités structurelles
     */
    void setSubUnitesStructurellesList(List<UniteStructurelleNode> subUnitesStructurellesList);
}
