package fr.dila.solonepp.api.descriptor.evenementtype;

import java.util.HashMap;
import java.util.Map;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur des institutions autorisés en tant qu'émetteur / institution / copie de la distribution.
 *
 * @author jtremeaux
 */
@XObject
public class DistributionElementDescriptor {
    /**
     * Ensemble des institutions autorisées.
     */
    @XNodeMap(value = "institution", key = "@name", type = HashMap.class, componentType = String.class)
    private Map<String, String> institution;

    /**
     * Si vrai, cette institution doit être renseignée.
     */
    @XNode("@obligatoire")
    private boolean obligatoire;

    /**
     * Getter de institution.
     *
     * @return institution
     */
    public Map<String, String> getInstitution() {
        return institution;
    }

    /**
     * Setter de institution.
     *
     * @param institution institution
     */
    public void setInstitution(Map<String, String> institution) {
        this.institution = institution;
    }

    /**
     * Getter de obligatoire.
     *
     * @return obligatoire
     */
    public boolean isObligatoire() {
        return obligatoire;
    }

    /**
     * Setter de obligatoire.
     *
     * @param obligatoire obligatoire
     */
    public void setObligatoire(boolean obligatoire) {
        this.obligatoire = obligatoire;
    }
}
