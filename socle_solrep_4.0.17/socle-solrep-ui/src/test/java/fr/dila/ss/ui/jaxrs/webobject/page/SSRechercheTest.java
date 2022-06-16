package fr.dila.ss.ui.jaxrs.webobject.page;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.servlet.http.HttpServletRequest;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ UserSessionHelper.class })
@PowerMockIgnore("javax.management.*")
public class SSRechercheTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private SpecificContext specificContext;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private HttpServletRequest request;

    private SSRecherche controlleur;

    @Captor
    private ArgumentCaptor<Breadcrumb> bc;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(UserSessionHelper.class);

        controlleur = new SSRecherche();
        Whitebox.setInternalState(controlleur, "context", specificContext);

        when(specificContext.getWebcontext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);

        when(webContext.getRequest()).thenReturn(request);
        when(request.getQueryString()).thenReturn("");
    }

    @Test
    public void testBuildTemplateRapideSearchWithNor() {
        String nor = "nor-test";
        when(UserSessionHelper.getUserSessionParameter(specificContext, SSUserSessionKey.NOR)).thenReturn(nor);

        ThTemplate template = controlleur.buildTemplateRapideSearch(nor);

        assertNotNull(template);
        assertEquals("pages/results", template.getName());

        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(specificContext, SSUserSessionKey.NOR, nor);

        Breadcrumb breadcrumb = new Breadcrumb(
            String.format("Recherche rapide : %s", nor),
            SSRecherche.DATA_URL + nor,
            Breadcrumb.TITLE_ORDER,
            specificContext.getWebcontext().getRequest()
        );
        verify(specificContext).setNavigationContextTitle(breadcrumb);
    }

    @Test
    public void testBuildTemplateRapideSearchWithoutNor() {
        String nor = "nor-test";
        when(UserSessionHelper.getUserSessionParameter(specificContext, SSUserSessionKey.NOR)).thenReturn(nor);

        ThTemplate template = controlleur.buildTemplateRapideSearch(null);

        assertNotNull(template);
        assertEquals("pages/results", template.getName());

        PowerMockito.verifyStatic(never());
        UserSessionHelper.putUserSessionParameter(specificContext, SSUserSessionKey.NOR, nor);

        Breadcrumb breadcrumb = new Breadcrumb(
            String.format("Recherche rapide : %s", nor),
            SSRecherche.DATA_URL + nor,
            Breadcrumb.TITLE_ORDER,
            specificContext.getWebcontext().getRequest()
        );
        verify(specificContext).setNavigationContextTitle(bc.capture());
        Assertions.assertThat(breadcrumb).isEqualTo(bc.getValue());
    }
}
