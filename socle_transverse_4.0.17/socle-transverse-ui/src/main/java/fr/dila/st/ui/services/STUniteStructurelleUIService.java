package fr.dila.st.ui.services;

import fr.dila.st.ui.th.bean.UniteStructurelleForm;
import fr.dila.st.ui.th.model.SpecificContext;

public interface STUniteStructurelleUIService {
    /**
     * Retourne l'Unite Structurelle Form depuis son l'identifiant
     *
     * @param SpecificContext context
     */
    UniteStructurelleForm getUniteStructurelleForm(SpecificContext context);

    /**
     * Enregistre l'unité structurelle
     *
     * @param SpecificContext context
     */
    void createUniteStructurelle(SpecificContext context);

    /**
     * update l'unité structurelle
     *
     * @param SpecificContext context
     */
    void updateUniteStructurelle(SpecificContext context);
}
