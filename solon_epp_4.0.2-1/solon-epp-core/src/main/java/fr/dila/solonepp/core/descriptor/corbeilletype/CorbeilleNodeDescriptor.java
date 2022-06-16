package fr.dila.solonepp.core.descriptor.corbeilletype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur d'une type de section ou de corbeille d'une institution.
 *
 * @author jtremeaux
 */
@XObject("corbeilleInstitution")
public class CorbeilleNodeDescriptor {
    /**
     * Type de noeud : SECTION ou CORBEILLE.
     */
    @XNode("@type")
    private String type;

    /**
     * Identifiant technique du noeud.
     */
    @XNode("@name")
    private String name;

    /**
     * Libellé du noeud.
     */
    @XNode("@label")
    private String label;

    /**
     * Description du noeud.
     */
    @XNode("@description")
    private String description;

    private List<String> hiddenColumnList;

    /**
     * Liste des corbeilles contenues dans cette section (uniquement pour les noeuds de type SECTION).
     */
    @XNodeList(value = "corbeilleNode", type = ArrayList.class, componentType = CorbeilleNodeDescriptor.class)
    private List<CorbeilleNodeDescriptor> corbeilleNodeList;

    /**
     * Liste des corbeilles contenues dans cette section (uniquement pour les noeuds de type SECTION).
     */
    @XNodeList(value = "messageGroup", type = ArrayList.class, componentType = MessageGroupDescriptor.class)
    private List<MessageGroupDescriptor> messageGroupList;

    /**
     * Constructeur par défaut de CorbeilleNodeDescriptor.
     */
    public CorbeilleNodeDescriptor() {}

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
    public List<CorbeilleNodeDescriptor> getCorbeilleNodeList() {
        return corbeilleNodeList;
    }

    /**
     * Setter de corbeilleNodeList.
     *
     * @param corbeilleNodeList corbeilleNodeList
     */
    public void setCorbeilleNodeList(List<CorbeilleNodeDescriptor> corbeilleNodeList) {
        this.corbeilleNodeList = corbeilleNodeList;
    }

    /**
     * Getter de messageGroupList.
     *
     * @return messageGroupList
     */
    public List<MessageGroupDescriptor> getMessageGroupList() {
        return messageGroupList;
    }

    /**
     * Setter de messageGroupList.
     *
     * @param messageGroupList messageGroupList
     */
    public void setMessageGroupList(List<MessageGroupDescriptor> messageGroupList) {
        this.messageGroupList = messageGroupList;
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

    @XNodeList(value = "hiddenColumns/hiddenColumn", componentType = String.class, type = String[].class)
    public void buildEvenementSuccessifList(String[] hiddenColumns) {
        hiddenColumnList = new ArrayList<String>();
        hiddenColumnList.addAll(Arrays.asList(hiddenColumns));
    }
}
