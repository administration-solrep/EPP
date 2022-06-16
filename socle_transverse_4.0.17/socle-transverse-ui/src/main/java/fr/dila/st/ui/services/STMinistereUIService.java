package fr.dila.st.ui.services;

import fr.dila.st.ui.th.bean.EntiteForm;
import fr.dila.st.ui.th.model.SpecificContext;

public interface STMinistereUIService {
    /**
     * Retourne le ministereForm depuis son identifiant
     *
     * @param String identifiant
     */
    EntiteForm getEntiteForm(SpecificContext context);

    /**
     * Enregistre le ministere
     *
     * @param EntiteForm
     */
    void createEntite(SpecificContext context);

    /**
     * update le ministere
     *
     * @param EntiteForm
     */
    void updateEntite(SpecificContext context);
}
