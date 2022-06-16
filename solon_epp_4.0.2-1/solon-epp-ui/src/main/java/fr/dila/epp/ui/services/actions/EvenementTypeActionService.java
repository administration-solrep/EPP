package fr.dila.epp.ui.services.actions;

import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;

/**
 * Actions sur les types d'événements.
 *
 * @author fskaff
 *
 */
public interface EvenementTypeActionService {
    /**
     * Retourne la liste de tous les événements.
     *
     * @return Liste de tous les événements
     */
    List<SelectValueDTO> getEvenementTypeList();

    /**
     * Retourne la liste de tous les événements d'une catégorie.
     *
     * @param ID
     *            categorieEvenementId : identifiant technique de la catégorie d'événements
     * @return Liste de tous les événements
     */
    List<SelectValueDTO> getEvenementTypeListFromCategory(SpecificContext context);

    /**
     * Retourne la liste des événements créateurs que l'utilisateur peut créer
     *
     * @param context
     * @return
     */
    List<SelectValueDTO> getEvenementCreateurList(SpecificContext context);

    /**
     * Retourne la liste des événements successifs à l'événement courant
     *
     * @param context
     * @return
     */
    List<SelectValueDTO> getEvenementSuccessifList(SpecificContext context);
}
