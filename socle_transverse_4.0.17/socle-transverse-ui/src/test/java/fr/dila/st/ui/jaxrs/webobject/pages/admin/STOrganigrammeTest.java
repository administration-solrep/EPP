package fr.dila.st.ui.jaxrs.webobject.pages.admin;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.STUtilisateurTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ WebEngine.class, STUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STOrganigrammeTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Spy
    STOrganigramme controller;

    @Mock
    WebContext webContext;

    @Mock
    NuxeoPrincipal principal;

    @Mock
    CoreSession session;

    @Mock
    UserSession userSession;

    @Spy
    SpecificContext context;

    @Mock
    OrganigrammeTreeUIService organigrammeTreeUIService;

    @Mock
    List<OrganigrammeElementDTO> dtos;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(STUIServiceLocator.class);

        when(STUIServiceLocator.getOrganigrammeTreeService()).thenReturn(organigrammeTreeUIService);

        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(context.getWebcontext()).thenReturn(webContext);
        when(context.getSession()).thenReturn(session);
        when(principal.isMemberOf(anyString())).thenReturn(true);
        doCallRealMethod().when(context).setContextData(any());
        doCallRealMethod().when(context).getContextData();
        doCallRealMethod().when(context).setMessageQueue(any());
        doCallRealMethod().when(context).getMessageQueue();
        doReturn(null).when(controller).newObject(any(), any(), any());
    }

    @Test
    public void testUserOrganigramme() throws Exception {
        when(organigrammeTreeUIService.getOrganigramme(any(SpecificContext.class))).thenReturn(dtos);

        Whitebox.setInternalState(controller, "context", context);
        when(userSession.get(SpecificContext.LAST_TEMPLATE)).thenReturn(STUtilisateurTemplate.class);
        controller.getUserOrganigramme();

        verify(controller).newObject(eq("OrganigrammeAjax"), eq(context), any());
    }

    @Test
    public void getMinistere() {
        controller.getMinistere();
        verify(controller).newObject(eq("OrganigrammeMinistere"), any(), any());
    }

    @Test
    public void getUniteStructurelle() {
        controller.getUniteStructurelle();
        verify(controller).newObject(eq("OrganigrammeUniteStructurelle"), any(), any());
    }

    @Test
    public void getPoste() {
        controller.getPoste();
        verify(controller).newObject(eq("OrganigrammePoste"), any(), any());
    }
}
