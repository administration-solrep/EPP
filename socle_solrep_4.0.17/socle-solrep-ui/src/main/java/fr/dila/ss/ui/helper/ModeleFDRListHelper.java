package fr.dila.ss.ui.helper;

import fr.dila.ss.ui.bean.fdr.FeuilleRouteDTO;
import fr.dila.ss.ui.bean.fdr.ModeleFDRList;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Helper pour listes de modèles de feuilles de route
 *
 */
public class ModeleFDRListHelper {

    private ModeleFDRListHelper() {
        // Default constructor
    }

    /**
     * Construit un objet DossierList à partir de la liste de DTO docList
     *
     * @param docList
     * @param form
     * @param total
     * @return un objet ModeleFDRList
     */
    public static ModeleFDRList buildModeleFDRList(
        List<? extends Map<String, Serializable>> docList,
        ModeleFDRListForm form,
        int total
    ) {
        ModeleFDRList lstResults = new ModeleFDRList();
        lstResults.setNbTotal(total);
        lstResults.buildColonnes(form);
        lstResults.setHasSelect(true);
        lstResults.setHasPagination(true);

        // On fait le mapping des documents vers notre DTO
        for (Map<String, Serializable> doc : docList) {
            if (doc instanceof FeuilleRouteDTO) {
                lstResults.getListe().add((FeuilleRouteDTO) doc);
            }
        }
        return lstResults;
    }
}
