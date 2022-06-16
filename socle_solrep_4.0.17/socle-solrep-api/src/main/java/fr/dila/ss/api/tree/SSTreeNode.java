package fr.dila.ss.api.tree;

import fr.dila.st.api.domain.STDomainObject;

public interface SSTreeNode extends STDomainObject {
    /**
     * Récupère l'id du noeud.
     *
     * @return
     */
    String getId();

    /**
     * Récupère le type du noeud.
     *
     * @return
     */
    String getType();

    /**
     * Renseigne la profondeur du noeud.
     *
     */
    void setDepth(int depth);

    /**
     * Récupère la profondeur du noeud.
     * N'est pas présent dans le document nuxeo, doit être renseigné auparavant
     *
     * @return int
     */
    int getDepth();

    String getName();
}
