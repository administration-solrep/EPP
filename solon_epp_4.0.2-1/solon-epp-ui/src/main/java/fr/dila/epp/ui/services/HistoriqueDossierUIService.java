package fr.dila.epp.ui.services;

import fr.dila.st.ui.bean.DossierHistoriqueEPP;
import fr.dila.st.ui.th.model.SpecificContext;

public interface HistoriqueDossierUIService {
    /**
     * Retourne l'historique du dossier dont fait partie l'événement courant
     *
     * @param context
     * @return
     */
    DossierHistoriqueEPP getHistoriqueDossier(SpecificContext context);
}
