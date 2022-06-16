package fr.dila.st.core.util;

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(STServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class STMailHelperTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private ConfigService mockConfigService;

    private final String SOLON_MAIL_PREFIX_FROM = "test-";

    @Before
    public void before() {
        mockStatic(STServiceLocator.class);
        when(STServiceLocator.getConfigService()).thenReturn(mockConfigService);
    }

    /**
     * Situation standard : un email valide est configuré.
     */
    @Test
    public void test_retrieveSenderMailFromConfig_shouldBeOk() throws AddressException {
        String mail = "user@domain.fr";
        when(mockConfigService.getValue(STConfigConstants.SOLON_MAIL_PREFIX_FROM, ""))
            .thenReturn(SOLON_MAIL_PREFIX_FROM);
        when(mockConfigService.getValue(STConfigConstants.MAIL_FROM)).thenReturn(mail);

        InternetAddress address = STMailHelper.retrieveSenderMailFromConfig();
        Assertions.assertThat(address.getAddress()).isEqualTo(SOLON_MAIL_PREFIX_FROM + mail);
    }

    /**
     * Pas de configuration pour le mail -> utilisation de la constante par défaut
     */
    @Test
    public void test_retrieveSenderMailFromConfig_nullConfig() throws AddressException {
        InternetAddress address = STMailHelper.retrieveSenderMailFromConfig();
        Assertions.assertThat(address.getAddress()).isEqualTo(STMailHelper.DEFAULT_MAIL_FROM);
    }

    /**
     * Mail invalide -> l'adresse est nulle
     */
    @Test
    public void test_retrieveSenderMailFromConfig_invalidAddress() throws AddressException {
        String mail = "ceci est une adresse email invalide";
        when(mockConfigService.getValue(STConfigConstants.MAIL_FROM)).thenReturn(mail);

        InternetAddress address = STMailHelper.retrieveSenderMailFromConfig();
        Assertions.assertThat(address).isNull();
    }
}
