package fr.dila.ss.ui.services.actions;

import fr.dila.ss.ui.bean.AlerteForm;
import fr.dila.st.ui.services.actions.STAlertActionService;
import fr.dila.st.ui.th.model.SpecificContext;

public interface SSAlertActionService extends STAlertActionService {
    /**
     * Créé une nouvelle alerte non persistée à partir d'une requete.
     *
     * @param
     * @return la nouvelle alerte
     *
     */
    AlerteForm getNewAlertFromRequeteExperte(SpecificContext context);

    /**
     * Sauvegarde l'alerte
     *
     * @return
     */
    String saveAlert(SpecificContext context);

    void delete(SpecificContext context);

    AlerteForm getCurrentAlerteForm(SpecificContext context);
}
