package fr.dila.solonepp.core.descriptor.corbeilletype;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur d'un type d'événement qui peut être contenu dans une corbeille.
 *
 * @author jtremeaux
 */
@XObject("evenementType")
public class EvenementTypeDescriptor {
    /**
     * Identifiant technique du type d'événement.
     */
    @XNode(value = "name")
    private String name;

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
}
