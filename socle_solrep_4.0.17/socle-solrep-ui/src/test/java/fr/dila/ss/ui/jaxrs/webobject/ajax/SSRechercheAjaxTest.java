package fr.dila.ss.ui.jaxrs.webobject.ajax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dila.ss.ui.enums.SSUserSessionKey;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.actions.suggestion.feuilleroute.FeuilleRouteSuggestionProviderService;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.bean.AbstractSortablePaginationForm;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ UserSessionHelper.class, SSUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SSRechercheAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private FeuilleRouteSuggestionProviderService feuilleRouteSuggestionProviderService;

    private SSRechercheAjax page;

    @Mock
    private SpecificContext specificContext;

    @Mock
    private CoreSession coreSession;

    @Mock
    private WebContext webContext;

    @Mock
    private UserSession userSession;

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        page = new SSRechercheAjax();
        Whitebox.setInternalState(page, "context", specificContext);

        when(specificContext.getWebcontext()).thenReturn(webContext);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(webContext.getRequest()).thenReturn(request);

        when(specificContext.getSession()).thenReturn(coreSession);

        when(SSUIServiceLocator.getFeuilleRouteSuggestionProviderService())
            .thenReturn(feuilleRouteSuggestionProviderService);
    }

    @Test
    public void testSuggestionsFdr() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String input = "Test";
        String typeSelection = "intituleFdr";
        List<String> result = new ArrayList<>(Arrays.asList("Fdr test", "test fdr"));

        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(new HashMap<String, Object>());
        when(feuilleRouteSuggestionProviderService.getSuggestions(input, specificContext)).thenReturn(result);

        String suggest = page.getSuggestions(input, typeSelection, false, null);

        assertEquals(mapper.writeValueAsString(result), suggest);
    }

    @Test
    public void testBuildTemplateRapideSearch() {
        AbstractSortablePaginationForm dossierListForm = mock(AbstractSortablePaginationForm.class);
        String nor = "nor-test";

        when(dossierListForm.getIsTableChangeEvent()).thenReturn(true);
        when(UserSessionHelper.getUserSessionParameter(specificContext, SSUserSessionKey.NOR)).thenReturn(nor);

        ThTemplate template = page.buildTemplateRapideSearch(dossierListForm);

        assertNotNull(template);
        assertEquals("fragments/table/tableDossiers", template.getName());

        Breadcrumb breadcrumb = new Breadcrumb(
            String.format("Consultation du dossier %s", nor),
            "/recherche/rapide",
            Breadcrumb.TITLE_ORDER,
            specificContext.getWebcontext().getRequest()
        );
        verify(specificContext).setNavigationContextTitle(breadcrumb);
        PowerMockito.verifyStatic();
        UserSessionHelper.putUserSessionParameter(
            specificContext,
            SSUserSessionKey.SEARCH_RESULT_FORM,
            dossierListForm
        );
    }
}
