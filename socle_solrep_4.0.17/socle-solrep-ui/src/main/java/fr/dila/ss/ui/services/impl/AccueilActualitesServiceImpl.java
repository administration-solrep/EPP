package fr.dila.ss.ui.services.impl;

import static fr.dila.ss.ui.services.SSUIServiceLocator.getActualiteUIService;

import fr.dila.ss.ui.bean.actualites.ActualiteConsultationDTO;
import fr.dila.ss.ui.services.AccueilActualitesService;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

public class AccueilActualitesServiceImpl implements AccueilActualitesService {
    private static final String ACTUALITES_NON_LUES_VIDE_KEY = "actualitesNonLuesVide";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Boolean isPasswordReset = context.getFromContextData(STContextDataKey.IS_PASSWORD_RESET);
        Map<String, Object> map = new HashMap<>();

        if (
            BooleanUtils.isFalse(isPasswordReset) &&
            BooleanUtils.isNotTrue(
                UserSessionHelper.getUserSessionParameter(context, ACTUALITES_NON_LUES_VIDE_KEY, Boolean.class)
            )
        ) {
            List<ActualiteConsultationDTO> actualites = getActualiteUIService().fetchUserActualitesNonLues(context);
            UserSessionHelper.putUserSessionParameter(
                context,
                ACTUALITES_NON_LUES_VIDE_KEY,
                CollectionUtils.isEmpty(actualites)
            );

            map.put(SSTemplateConstants.DISPLAY_ACTUALITES_LIST, actualites);
        }

        return map;
    }
}
