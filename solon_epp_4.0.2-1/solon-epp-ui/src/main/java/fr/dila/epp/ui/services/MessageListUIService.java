package fr.dila.epp.ui.services;

import fr.dila.epp.ui.bean.MessageList;
import fr.dila.st.ui.th.model.SpecificContext;

public interface MessageListUIService {
    /**
     * Retourne la liste des messages de la corbeille
     *
     * @param MESSAGE_LIST_FORM
     * @return
     */
    MessageList getMessageListForCorbeille(SpecificContext context);
}
