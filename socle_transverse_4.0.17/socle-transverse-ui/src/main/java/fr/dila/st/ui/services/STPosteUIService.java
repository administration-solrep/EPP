package fr.dila.st.ui.services;

import fr.dila.st.ui.th.bean.PosteForm;
import fr.dila.st.ui.th.bean.PosteWsForm;
import fr.dila.st.ui.th.model.SpecificContext;

public interface STPosteUIService {
    /**
     * Retourne le posteForm depuis son identifiant
     *
     * @param String identifiant
     */
    PosteForm getPosteForm(SpecificContext context);

    /**
     * Enregistre le poste
     *
     * @param PosteForm
     */
    void createPoste(SpecificContext context);

    /**
     * update le poste
     *
     * @param PosteForm
     */
    void updatePoste(SpecificContext context);

    PosteWsForm getPosteWsForm(SpecificContext context);

    void updatePosteWs(SpecificContext context);

    void createPosteWs(SpecificContext context);

    boolean checkUniqueLabelPoste(SpecificContext context);

    boolean checkUniqueLabelPosteWs(SpecificContext context);
}
