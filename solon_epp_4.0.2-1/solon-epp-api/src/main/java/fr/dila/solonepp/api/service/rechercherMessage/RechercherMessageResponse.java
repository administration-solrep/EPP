package fr.dila.solonepp.api.service.rechercherMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Données de la réponse pour rechercher un message.
 *
 * @author sly
 */
public class RechercherMessageResponse {
    /**
     * Ligne de résultat.
     */
    private final List<RechercherMessageDTO> messageList;

    public RechercherMessageResponse() {
        this.messageList = new ArrayList<RechercherMessageDTO>();
    }

    /**
     * Getter de rechercherMessageElementList.
     *
     * @return rechercherMessageElementList
     */
    public List<RechercherMessageDTO> getMessagetList() {
        return messageList;
    }
}
