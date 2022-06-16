package fr.dila.st.ui.jaxrs.webobject.ajax;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.actions.suggestion.nomauteur.NomAuteurSuggestionProviderService;
import fr.dila.st.ui.th.model.SpecificContext;
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
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ UserSessionHelper.class, STUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STRechercheAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private NomAuteurSuggestionProviderService nomAuteurSuggestionProviderService;

    @Mock
    private WebContext webContext;

    @Mock
    private HttpServletRequest httpServletRequest;

    public static final String SEARCH_FORMS_KEY = "searchForms";

    @Before
    public void setUp() {
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(STUIServiceLocator.class);

        when(UserSessionHelper.getUserSessionParameter(any(), any(), any())).thenReturn(new HashMap<String, Object>());

        when(STUIServiceLocator.getNomAuteurSuggestionProviderService()).thenReturn(nomAuteurSuggestionProviderService);
    }

    @Test
    public void testSuggestionsAuteurs() throws JsonProcessingException {
        STRechercheAjax STRechercheAjax = new STRechercheAjax();
        ObjectMapper mapper = new ObjectMapper();
        String input = "Jean";
        String typeSelection = "auteur";
        List<String> result = new ArrayList<>(Arrays.asList("Jean valjean"));

        when(nomAuteurSuggestionProviderService.getSuggestions(Mockito.eq(input), Mockito.any(SpecificContext.class)))
            .thenReturn(result);

        String suggest = STRechercheAjax.getSuggestions(input, typeSelection, false, null);

        assertEquals(mapper.writeValueAsString(result), suggest);
    }

    @Test
    public void testSuggestionsWrongType() throws JsonProcessingException {
        STRechercheAjax STRechercheAjax = new STRechercheAjax();
        ObjectMapper mapper = new ObjectMapper();
        String input = "Test";
        String typeSelection = "inconnu";
        List<String> result = new ArrayList<>();

        String suggest = STRechercheAjax.getSuggestions(input, typeSelection, false, null);

        assertEquals(mapper.writeValueAsString(result), suggest);
    }
}
