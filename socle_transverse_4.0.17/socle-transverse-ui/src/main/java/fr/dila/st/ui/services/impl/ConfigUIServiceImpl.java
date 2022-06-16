package fr.dila.st.ui.services.impl;

import static fr.dila.st.api.constant.STConfigConstants.PRODUCT_TAG;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_COULEUR;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_COULEURBG;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_LIBELLE;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_NAME;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.ConfigDTO;
import fr.dila.st.ui.services.ConfigUIService;

public class ConfigUIServiceImpl implements ConfigUIService {

    @Override
    public ConfigDTO getConfig() {
        return new ConfigDTO(
            STServiceLocator.getConfigService().getValue(SOLON_IDENTIFICATION_PLATEFORME_NAME, EMPTY),
            STServiceLocator.getConfigService().getValue(SOLON_IDENTIFICATION_PLATEFORME_LIBELLE, EMPTY),
            STServiceLocator.getConfigService().getValue(SOLON_IDENTIFICATION_PLATEFORME_COULEUR, EMPTY),
            STServiceLocator.getConfigService().getValue(SOLON_IDENTIFICATION_PLATEFORME_COULEURBG, EMPTY),
            STServiceLocator.getConfigService().getValue(PRODUCT_TAG)
        );
    }
}
