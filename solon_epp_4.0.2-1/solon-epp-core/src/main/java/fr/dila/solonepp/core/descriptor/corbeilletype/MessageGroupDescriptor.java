package fr.dila.solonepp.core.descriptor.corbeilletype;

import java.util.HashMap;
import java.util.Map;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur d'un groupe de message qui peut être contenu dans une corbeille.
 *
 * @author jtremeaux
 */
@XObject("messageGroup")
public class MessageGroupDescriptor {
    /**
     * Table des types de message que peut contenir la corbeille.
     */
    @XNodeMap(
        value = "messageTypes/messageType",
        key = "@name",
        type = HashMap.class,
        componentType = MessageTypeDescriptor.class
    )
    private Map<String, MessageTypeDescriptor> messageTypeMap;

    /**
     * Table des types d'événement que peut contenir la corbeille.
     */
    @XNodeMap(
        value = "evenementTypes/evenementType",
        key = "@name",
        type = HashMap.class,
        componentType = EvenementTypeDescriptor.class
    )
    private Map<String, EvenementTypeDescriptor> evenementTypeMap;

    /**
     * Constructeur par défaut de MessageGroupDescriptor.
     */
    public MessageGroupDescriptor() {}

    /**
     * Getter de messageTypeMap.
     *
     * @return messageTypeMap
     */
    public Map<String, MessageTypeDescriptor> getMessageTypeMap() {
        return messageTypeMap;
    }

    /**
     * Setter de messageTypeMap.
     *
     * @param messageTypeMap messageTypeMap
     */
    public void setMessageTypeMap(Map<String, MessageTypeDescriptor> messageTypeMap) {
        this.messageTypeMap = messageTypeMap;
    }

    /**
     * Getter de evenementTypeMap.
     *
     * @return evenementTypeMap
     */
    public Map<String, EvenementTypeDescriptor> getEvenementTypeMap() {
        return evenementTypeMap;
    }

    /**
     * Setter de evenementTypeMap.
     *
     * @param evenementTypeMap evenementTypeMap
     */
    public void setEvenementTypeMap(Map<String, EvenementTypeDescriptor> evenementTypeMap) {
        this.evenementTypeMap = evenementTypeMap;
    }
}
