package fr.dila.ss.ui.services;

import fr.dila.ss.ui.bean.SSConsultDossierDTO;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Map;

public interface SSDossierUIService<T extends SSConsultDossierDTO> {
    T getDossierConsult(SpecificContext context);

    default void loadDossierActions(SpecificContext context, ThTemplate template) {
        Map<String, Object> map = template.getData();

        context.putInContextData(SSContextDataKey.DOSSIER_IS_READ, true);

        map.put(SSTemplateConstants.FDR_ACTIONS, context.getActions(SSActionCategory.DOSSIER_TOPBAR_ACTIONS_FDR));
        map.put(
            STTemplateConstants.GENERALE_ACTIONS,
            context.getActions(SSActionCategory.DOSSIER_TOPBAR_ACTIONS_GENERAL)
        );
        map.put(STTemplateConstants.LOCK_ACTIONS, context.getActions(SSActionCategory.DOSSIER_TOPBAR_ACTIONS_LOCKS));
        map.put(SSTemplateConstants.NOTE_ACTIONS, context.getActions(SSActionCategory.DOSSIER_TOPBAR_ACTIONS_NOTE));

        map.put(SSTemplateConstants.DIVERS_ACTIONS, context.getActions(SSActionCategory.DOSSIER_TOPBAR_ACTIONS_DIVERS));
        map.put(SSTemplateConstants.PRINT_ACTIONS, context.getActions(SSActionCategory.DOSSIER_TOPBAR_ACTIONS_PRINT));
    }
}
