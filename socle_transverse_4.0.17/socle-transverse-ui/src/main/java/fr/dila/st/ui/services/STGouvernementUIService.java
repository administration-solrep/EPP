package fr.dila.st.ui.services;

import fr.dila.st.ui.th.bean.GouvernementForm;
import fr.dila.st.ui.th.model.SpecificContext;

public interface STGouvernementUIService {
    /**
     * Retourne le GouvernementForm depuis son identifiant
     *
     * @param String identifiant
     */
    GouvernementForm getGouvernementForm(SpecificContext context);

    /**
     * Enregistre le Gouvernement
     *
     * @param GouvernementForm
     */
    void createGouvernement(SpecificContext context);

    /**
     * update le Gouvernement
     *
     * @param GouvernementForm
     */
    void updateGouvernement(SpecificContext context);
}
