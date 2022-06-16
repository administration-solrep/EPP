package fr.dila.solonepp.api.service.corbeilletype;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import java.util.List;

/**
 * Noeud de l'arbre des corbeilles.
 * Le noeud est soit :
 * - Une section : collection de corbeilles ;
 * - Une corbeille.
 *
 * @author jtremeaux
 */
public class CorbeilleNode {
    /**
     * Type de noeud : SECTION ou CORBEILLE.
     */
    private String type;

    /**
     * Identifiant technique du noeud.
     */
    private String name;

    /**
     * Libellé du noeud.
     */
    private String label;

    /**
     * Description du noeud.
     */
    private String description;

    private String count;

    /**
     * Liste des colonnes cachées pour cette corbeille
     */
    private List<String> hiddenColumnList;

    /**
     * noeud ouvert
     */
    private boolean opened;

    /**
     * Liste des corbeilles contenues dans cette section (uniquement pour les noeuds de type SECTION).
     */
    private List<CorbeilleNode> corbeilleNodeList;

    /**
     * Retourne vrai si le noeud de l'organigramme est une section.
     *
     * @return Condition
     */
    public boolean isTypeSection() {
        return SolonEppConstant.CORBEILLE_NODE_TYPE_SECTION.equals(type);
    }

    /**
     * Retourne vrai si le noeud de l'organigramme est une corbeille.
     *
     * @return Condition
     */
    public boolean isTypeCorbeille() {
        return SolonEppConstant.CORBEILLE_NODE_TYPE_CORBEILLE.equals(type);
    }

    /**
     * Getter de type.
     *
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter de type.
     *
     * @param type type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter de name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter de name.
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter de label.
     *
     * @return label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Setter de label.
     *
     * @param label label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter de description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter de description.
     *
     * @param description description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter de corbeilleNodeList.
     *
     * @return corbeilleNodeList
     */
    public List<CorbeilleNode> getCorbeilleNodeList() {
        return corbeilleNodeList;
    }

    /**
     * Setter de corbeilleNodeList.
     *
     * @param corbeilleNodeList corbeilleNodeList
     */
    public void setCorbeilleNodeList(List<CorbeilleNode> corbeilleNodeList) {
        this.corbeilleNodeList = corbeilleNodeList;
    }

    /**
     * @return the opened
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * @param opened the opened to set
     */
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    /**
     * @return the hiddenColumnList
     */
    public List<String> getHiddenColumnList() {
        return hiddenColumnList;
    }

    /**
     * @param hiddenColumnList the hiddenColumnList to set
     */
    public void setHiddenColumnList(List<String> hiddenColumnList) {
        this.hiddenColumnList = hiddenColumnList;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCount() {
        return count;
    }
}
