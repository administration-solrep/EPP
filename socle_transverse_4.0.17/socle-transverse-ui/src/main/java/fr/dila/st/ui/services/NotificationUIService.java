package fr.dila.st.ui.services;

import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.bean.NotificationDTO;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Calendar;

public interface NotificationUIService {
    /**
     * Renvoie la date de dernière mise à jour de la corbeille/communication courante.
     */
    default Calendar getLastUpdate(SpecificContext context) {
        return ObjectHelper.requireNonNullElseGet(
            UserSessionHelper.getUserSessionParameter(context, STUserSessionKey.LAST_USER_NOTIFICATION),
            Calendar::getInstance
        );
    }

    /**
     * Retourne les données permettant d'afficher une notification en cas de
     * changement de la corbeille/communication courante.
     */
    NotificationDTO getNotificationDTO(SpecificContext context);

    long getNotificationDelai(SpecificContext context);

    void reloadCacheTdrEppIfNecessary(SpecificContext context);
}
