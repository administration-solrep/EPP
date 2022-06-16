package fr.dila.epp.ui.helper;

import fr.dila.epp.ui.bean.EppMessageDTO;
import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.th.bean.MessageListForm;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Helper pour les listes de messages
 *
 * @author fskaff
 *
 */
public class MessageListHelper {

    private MessageListHelper() {
        // Default constructor
    }

    /**
     * Construit un objet MessageList à partir de la liste de DTO docList
     *
     * @param docList
     * @param form
     * @param total
     * @return
     */
    public static MessageList buildMessageList(
        List<Map<String, Serializable>> docList,
        MessageListForm form,
        int total
    ) {
        MessageList lstResults = new MessageList();
        lstResults.setNbTotal(total);
        lstResults.getListeColones(form);

        // On fait le mapping des documents vers notre DTO
        for (Map<String, Serializable> doc : docList) {
            if (doc instanceof EppMessageDTO) {
                lstResults.getListe().add((EppMessageDTO) doc);
            }
        }
        return lstResults;
    }
}
