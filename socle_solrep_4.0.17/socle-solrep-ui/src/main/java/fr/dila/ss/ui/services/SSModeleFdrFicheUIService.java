package fr.dila.ss.ui.services;

import fr.dila.ss.ui.th.bean.ModeleFdrForm;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;

public interface SSModeleFdrFicheUIService {
    void updateModele(SpecificContext context, ModeleFdrForm modeleForm);

    /*
     * Récupérer la liste des types étapes pour la recherche
     * Doit être Overried par chaque appli !!
     */
    List<SelectValueDTO> getTypeEtapeRecherche();

    /*
     * Récupérer la liste des types étapes
     * Doit être Overried par chaque appli !!
     */
    Map<String, String> getTypeEtape();
}
