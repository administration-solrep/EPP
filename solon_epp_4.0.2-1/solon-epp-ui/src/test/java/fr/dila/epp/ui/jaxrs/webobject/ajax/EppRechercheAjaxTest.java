package fr.dila.epp.ui.jaxrs.webobject.ajax;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fr.dila.epp.ui.bean.EppMessageDTO;
import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.services.RechercheUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.services.actions.MetadonneesActionService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.epp.ui.th.bean.MessageListForm;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    { WebEngine.class, SolonEppUIServiceLocator.class, SolonEppActionsServiceLocator.class, STUIServiceLocator.class }
)
@PowerMockIgnore("javax.management.*")
public class EppRechercheAjaxTest {
    EppRechercheAjax controlleur;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    SpecificContext context;

    @Mock
    private WebContext webcontext;

    @Mock
    private UserSession userSession;

    @Mock
    private HttpServletRequest request;

    @Mock
    ThTemplate template;

    @Mock
    RechercheUIService rechercheUIService;

    @Mock
    MetadonneesActionService suggestTableRef;

    @Before
    public void before() throws Exception {
        template = mock(ThTemplate.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(SolonEppUIServiceLocator.class);
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(SolonEppActionsServiceLocator.class);
        PowerMockito.whenNew(SpecificContext.class).withAnyArguments().thenReturn(context);

        when(context.getWebcontext()).thenReturn(webcontext);
        when(webcontext.getRequest()).thenReturn(request);
        when(webcontext.getUserSession()).thenReturn(userSession);
        when(SolonEppUIServiceLocator.getRechercheUIService()).thenReturn(rechercheUIService);
        when(SolonEppActionsServiceLocator.getMetadonneesActionService()).thenReturn(suggestTableRef);
        when(suggestTableRef.getSuggestions(any()))
            .thenReturn(
                Lists.newArrayList(
                    new SuggestionDTO("Auteur1", "Auteur Dupont"),
                    new SuggestionDTO("Auteur2", "Auteur Dupond")
                )
            );

        controlleur = new EppRechercheAjax();
    }

    @Test
    public void testDoRecherche() throws Exception {
        MessageListForm msgListForm = new MessageListForm(null);
        MessageList msgList = new MessageList();
        List<EppMessageDTO> liste = Lists.newArrayList(
            new EppMessageDTO("1", "objet", "", "emetteur", "destinataire", "", "communication", "1.0", "25/07/2020")
        );
        msgList.setListe(liste);
        when(rechercheUIService.getResultatsRecherche(any())).thenReturn(msgList);
        PowerMockito.whenNew(MessageListForm.class).withAnyArguments().thenReturn(msgListForm);

        ThTemplate template = controlleur.doRecherche(null);

        assertNotNull(template);
        assertNotNull(template.getContext());

        assertEquals(10, msgList.getListeColones().size());
        assertEquals("corbeille.communication.table.header.iddossier", msgList.getListeColones().get(1).getLabel());
        assertEquals("/recherche", template.getData().get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/recherche/resultats", template.getData().get(STTemplateConstants.DATA_AJAX_URL));
    }

    @Test
    public void testGetSuggestions() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        PowerMockito.whenNew(ObjectMapper.class).withAnyArguments().thenReturn(mapper);

        String sugges = controlleur.getSuggestions("aut", "auteur", "true", "");

        assertEquals(
            "[{\"key\":\"Auteur1\",\"label\":\"Auteur Dupont\"},{\"key\":\"Auteur2\",\"label\":\"Auteur Dupond\"}]",
            sugges
        );
    }
}
