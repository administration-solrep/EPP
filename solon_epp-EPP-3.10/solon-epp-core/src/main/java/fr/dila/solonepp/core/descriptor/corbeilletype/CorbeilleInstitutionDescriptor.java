package fr.dila.solonepp.core.descriptor.corbeilletype;

import java.util.ArrayList;
import java.util.List;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur d'une collection de corbeille pour une institution.
 * 
 * @author jtremeaux
 */
@XObject("corbeilleInstitution")
public class CorbeilleInstitutionDescriptor {

    /**
     * Identifiant technique de l'institution (ex. "SENAT").
     */
    @XNode("@institution")
    private String institution;

    /**
     * Liste des corbeilles ou sections.
     */
    @XNodeList(value = "corbeilleNode", type = ArrayList.class, componentType = CorbeilleNodeDescriptor.class)
    private List<CorbeilleNodeDescriptor> corbeilleNodeList;

    /**
     * Constructeur par défaut de CorbeilleInstitutionDescriptor.
     */
    public CorbeilleInstitutionDescriptor() {
    }

    /**
     * Getter de institution.
     *
     * @return institution
     */
    public String getInstitution() {
        return institution;
    }

    /**
     * Setter de institution.
     *
     * @param institution institution
     */
    public void setInstitution(String institution) {
        this.institution = institution;
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
}
