package fr.dila.ss.ui.services.actions.impl;

import static org.junit.Assert.assertEquals;

import fr.dila.st.api.caselink.STDossierLink;
import fr.dila.st.ui.th.model.SpecificContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SpecificContext.class, WebContext.class, UserSession.class })
public class SSCorbeilleActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    SSCorbeilleActionServiceImpl service;

    @Mock
    SpecificContext context;

    @Mock
    STDossierLink dossierLink;

    @Before
    public void before() {
        context = Mockito.mock(SpecificContext.class);
        dossierLink = Mockito.mock(STDossierLink.class);
        service = Mockito.mock(SSCorbeilleActionServiceImpl.class);
    }

    @Test
    public void testIsDossierLoadedInCorbeille() {
        Mockito.when(service.isDossierLoadedInCorbeille(context)).thenCallRealMethod();

        Mockito.when(service.getCurrentDossierLink(Mockito.any())).thenReturn(null);
        assertEquals(false, service.isDossierLoadedInCorbeille(context));

        Mockito.when(service.getCurrentDossierLink(Mockito.any())).thenReturn(dossierLink);
        assertEquals(true, service.isDossierLoadedInCorbeille(context));
    }
}
