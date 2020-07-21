package fr.dila.solonepp.core.descriptor.corbeilletype;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur d'un type de message qui peut être contenu dans une corbeille.
 * 
 * @author jtremeaux
 */
@XObject("messageType")
public class MessageTypeDescriptor {

    /**
     * Identifiant technique du type de message.
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
