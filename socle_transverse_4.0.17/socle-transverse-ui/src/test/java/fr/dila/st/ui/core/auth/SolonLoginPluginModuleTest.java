package fr.dila.st.ui.core.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import fr.dila.st.ui.services.EtatApplicationUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.function.Supplier;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.api.login.UserIdentificationInfo;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ Framework.class, ServiceUtil.class, STUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SolonLoginPluginModuleTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    SolonLoginPluginModule module;

    @Mock
    private UserManager userManager;

    @Mock
    private NuxeoPrincipal nuxeoPrincipal;

    @Mock
    private EtatApplicationUIService etatApplicationService;

    @Before
    public void before() {
        PowerMockito.mockStatic(ServiceUtil.class);
        when(ServiceUtil.getRequiredService(UserManager.class)).thenReturn(userManager);
        PowerMockito.mockStatic(STUIServiceLocator.class);
        when(STUIServiceLocator.getEtatApplicationUIService()).thenReturn(etatApplicationService);

        PowerMockito.mockStatic(Framework.class);

        module = new SolonLoginPluginModule();
    }

    @Test
    public void testInitLoginModule() {
        assertTrue(module.initLoginModule());
    }

    @Test
    public void testValidatedUserIdentity() {
        //Test on ne connait pas le user
        when(Framework.doPrivileged(Mockito.any(Supplier.class))).thenReturn(null);
        String returnStr = module.validatedUserIdentity(new UserIdentificationInfo("null", ""));
        assertNull(returnStr);

        //Test on connait le user
        when(Framework.doPrivileged(Mockito.any(Supplier.class))).thenReturn(nuxeoPrincipal);

        //Utilisateur avec bon mdp
        when(userManager.checkUsernamePassword(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        //Test le user est administrator
        when(nuxeoPrincipal.isAdministrator()).thenReturn(true);
        returnStr = module.validatedUserIdentity(new UserIdentificationInfo("Administrator", "Admin"));
        assertNull(returnStr);

        //Test le user n'est pas administrator
        when(nuxeoPrincipal.isAdministrator()).thenReturn(false);
        returnStr = module.validatedUserIdentity(new UserIdentificationInfo("monUser", "user"));
        assertEquals("monUser", returnStr);

        //Utilisateur avec mauvais mdp
        when(userManager.checkUsernamePassword(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        //Test le user n'est pas administrator
        when(nuxeoPrincipal.isAdministrator()).thenReturn(false);
        returnStr = module.validatedUserIdentity(new UserIdentificationInfo("monUser", "user"));
        assertNull(returnStr);
    }
}
