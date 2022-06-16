package fr.dila.st.api.organigramme;

import java.util.List;

public interface WithSubPosteNode extends OrganigrammeNode {
    /**
     * Retourne la liste des sous-postes.
     *
     * @return Liste des sous-postes
     */
    List<PosteNode> getSubPostesList();

    /**
     * Renseigne la liste des sous-postes.
     *
     * @param subPostesList
     *            Liste des sous-postes
     */
    void setSubPostesList(List<PosteNode> subPostesList);
}
