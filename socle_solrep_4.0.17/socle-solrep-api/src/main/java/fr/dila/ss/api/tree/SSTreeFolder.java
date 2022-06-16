package fr.dila.ss.api.tree;

public interface SSTreeFolder extends SSTreeNode {
    /**
     *
     * @return vrai si l'instance représente un folder vide, faux sinon
     */
    boolean isEmpty();

    /**
     * setter de l'attribut d'instance isEmpty
     * @param isEmpty
     */
    void setIsEmpty(boolean isEmpty);
}
