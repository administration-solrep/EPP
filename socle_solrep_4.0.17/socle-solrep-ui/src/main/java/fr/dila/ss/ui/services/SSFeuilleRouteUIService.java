package fr.dila.ss.ui.services;

import fr.dila.ss.ui.bean.fdr.EtapeDTO;
import fr.dila.ss.ui.bean.fdr.FdrTableDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Date;
import java.util.List;

public interface SSFeuilleRouteUIService {
    /**
     * Construction du DTO de la feuille de route
     *
     * @param context
     * @return DTO de la feuille de route
     */
    FdrTableDTO getFeuilleRouteDTO(SpecificContext context);

    void initEtapeActionsDTO(SpecificContext context);

    void initStepFolderActionsDTO(SpecificContext context);

    void addEtapes(SpecificContext context);

    EtapeDTO getEtapeDTO(SpecificContext context);

    EtapeDTO saveEtape(SpecificContext context);

    /**
     * Récupère la liste des libellés des étapes à venir sous la forme 'Type étape - Poste'
     *
     * @param context
     * @param id
     * @return
     */
    List<String> getNextStepLabels(SpecificContext context, String id);

    /**
     * Récupère le libellé de l'étape sélectionnée sous la forme Type étape - Poste'
     *
     * @param context
     * @return
     */
    List<String> getCurrentStepLabel(SpecificContext context);

    /**
     * Récupère la date de validation de la dernière étape de la feuille de route
     *
     * @param context
     * @param id
     * @return
     */
    Date getLastStepDate(SpecificContext context, String id);
}
