package fr.dila.st.api.organigramme;

import java.util.List;

public interface WithSubEntitiesNode extends OrganigrammeNode {
    /**
     * Retourne la liste des sous-entities.
     *
     * @return Liste des sous-postes
     */
    List<EntiteNode> getSubEntitesList();

    /**
     * Renseigne la liste des sous-entities.
     *
     * @param subEntiteList
     *            Liste des sous-postes
     */
    void setSubEntitesList(List<EntiteNode> subEntiteList);
}
