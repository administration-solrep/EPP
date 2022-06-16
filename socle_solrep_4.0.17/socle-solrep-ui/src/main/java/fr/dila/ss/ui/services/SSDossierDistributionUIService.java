package fr.dila.ss.ui.services;

import fr.dila.st.ui.th.model.SpecificContext;

public interface SSDossierDistributionUIService {
    /**
     * Change le tag "read" sur le DossierLink chargé.
     */
    void changeReadStateDossierLink(SpecificContext context);
}
