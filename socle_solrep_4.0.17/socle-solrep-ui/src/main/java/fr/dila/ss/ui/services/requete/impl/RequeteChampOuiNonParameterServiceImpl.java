package fr.dila.ss.ui.services.requete.impl;

import com.google.common.collect.ImmutableMap;
import fr.dila.ss.api.constant.SSRechercheChampConstants;
import fr.dila.st.core.requete.recherchechamp.ChampParameter;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class RequeteChampOuiNonParameterServiceImpl implements ChampParameter {

    @Override
    public Map<String, Serializable> getAdditionalParameters() {
        return ImmutableMap.of(
            SSRechercheChampConstants.OPTIONS_PARAMETER_NAME,
            new ArrayList<>(
                Arrays.asList(
                    new SelectValueDTO("1", ResourceHelper.getString("suivi.requete.oui.label")),
                    new SelectValueDTO("0", ResourceHelper.getString("suivi.requete.non.label"))
                )
            )
        );
    }
}
