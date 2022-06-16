package fr.dila.st.ui.services.impl;

import static fr.dila.st.api.constant.STConfigConstants.PRODUCT_TAG;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_COULEUR;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_COULEURBG;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_LIBELLE;
import static fr.dila.st.api.constant.STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_NAME;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.feature.SolonMockitoFeature;
import fr.dila.st.core.test.STCommonFeature;
import fr.dila.st.ui.bean.ConfigDTO;
import fr.dila.st.ui.services.ConfigUIService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ STCommonFeature.class, SolonMockitoFeature.class })
public class ConfigUIServiceImplTest {
    private static final String VERSION =
        "4.0.0-SNAPSHOT 27/05/2020 17:10 version : 99658aa2d5a46d41ca89488f5ec6b6d42d568f1b";

    private ConfigUIService service;

    @Mock
    @RuntimeService
    private ConfigService configService;

    @Before
    public void setUp() {
        service = new ConfigUIServiceImpl();

        when(configService.getValue(PRODUCT_TAG)).thenReturn(VERSION);
    }

    @Test
    public void getConfig() {
        String name = "Réponses";
        when(configService.getValue(SOLON_IDENTIFICATION_PLATEFORME_NAME, EMPTY)).thenReturn(name);

        String environmentName = "Développement SWORD";
        when(configService.getValue(SOLON_IDENTIFICATION_PLATEFORME_LIBELLE, EMPTY)).thenReturn(environmentName);

        String color = "0080ff";
        when(configService.getValue(SOLON_IDENTIFICATION_PLATEFORME_COULEUR, EMPTY)).thenReturn(color);

        String backgroundColor = "B4DCFF";
        when(configService.getValue(SOLON_IDENTIFICATION_PLATEFORME_COULEURBG, EMPTY)).thenReturn(backgroundColor);

        ConfigDTO config = service.getConfig();

        assertThat(config.getName()).isEqualTo(name);
        assertThat(config.getEnvironmentName()).isEqualTo(environmentName);
        assertThat(config.getColor()).isEqualTo(color);
        assertThat(config.getBackgroundColor()).isEqualTo(backgroundColor);
        assertThat(config.getVersion()).isEqualTo(VERSION);
    }

    @Test
    public void getConfigWithEmptyValues() {
        when(configService.getValue(SOLON_IDENTIFICATION_PLATEFORME_NAME, EMPTY)).thenReturn(EMPTY);
        when(configService.getValue(SOLON_IDENTIFICATION_PLATEFORME_LIBELLE, EMPTY)).thenReturn(EMPTY);
        when(configService.getValue(SOLON_IDENTIFICATION_PLATEFORME_COULEUR, EMPTY)).thenReturn(EMPTY);
        when(configService.getValue(SOLON_IDENTIFICATION_PLATEFORME_COULEURBG, EMPTY)).thenReturn(EMPTY);

        ConfigDTO config = service.getConfig();

        assertThat(config.getName()).isEmpty();
        assertThat(config.getEnvironmentName()).isEmpty();
        assertThat(config.getColor()).isEmpty();
        assertThat(config.getBackgroundColor()).isEmpty();
        assertThat(config.getVersion()).isEqualTo(VERSION);
    }
}
