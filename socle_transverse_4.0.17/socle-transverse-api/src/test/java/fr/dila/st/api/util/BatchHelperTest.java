package fr.dila.st.api.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import fr.dila.st.api.service.EtatApplicationService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.runtime.api.Framework;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ Framework.class })
@PowerMockIgnore("javax.management.*")
public class BatchHelperTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    EtatApplicationService etatApplicationService;

    @Before
    public void setUp() {
        initMocks(this);

        mockStatic(Framework.class);
        when(Framework.getService(Mockito.eq(EtatApplicationService.class))).thenReturn(etatApplicationService);
    }

    @Test
    public void testCanBatchBeLaunchedTrue() {
        //On met l'application en mode non restrictif techniquement
        when(etatApplicationService.isApplicationTechnicallyRestricted()).thenReturn(false);

        //Les batch doivent pouvoir s'exécuter
        assertTrue(BatchHelper.canBatchBeLaunched());

        //On met l'application en mode restrictif fonctionnelle
        when(etatApplicationService.isApplicationRestricted(Mockito.any())).thenReturn(true);

        //Les batch doivent toujours pouvoir s'exécuter
        assertTrue(BatchHelper.canBatchBeLaunched());
    }

    @Test
    public void testCanBatchBeLaunchedFalse() {
        //On met l'application en mode restrictif techniquement
        when(etatApplicationService.isApplicationTechnicallyRestricted()).thenReturn(true);

        //Les batch ne doivent pas pouvoir s'exécuter
        assertFalse(BatchHelper.canBatchBeLaunched());

        //On met l'application en mode non restrictif fonctionnelle
        when(etatApplicationService.isApplicationRestricted(Mockito.any())).thenReturn(false);

        //Les batch ne doivent toujours pas pouvoir s'exécuter
        assertFalse(BatchHelper.canBatchBeLaunched());
    }
}
