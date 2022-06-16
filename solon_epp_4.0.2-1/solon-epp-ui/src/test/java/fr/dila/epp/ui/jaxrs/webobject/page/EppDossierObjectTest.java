package fr.dila.epp.ui.jaxrs.webobject.page;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.epp.ui.services.EvenementUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.th.constants.EppTemplateConstants;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.ui.bean.OngletConteneur;
import fr.dila.st.ui.enums.ActionCategory;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        SpecificContext.class,
        SolonWebObject.class,
        WebEngine.class,
        SolonEppServiceLocator.class,
        SolonEppUIServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class EppDossierObjectTest {
    private static final String ID_MESSAGE = "idMessage";

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
    private CoreSession session;

    @Mock
    private HttpServletRequest request;

    @Mock
    ThTemplate template;

    @Mock
    EvenementTypeService evenementTypeService;

    @Mock
    EvenementUIService evenementUIService;

    @Before
    public void before() throws Exception {
        context = mock(SpecificContext.class);
        template = mock(ThTemplate.class);
        webcontext = mock(WebContext.class);
        PowerMockito.mockStatic(WebEngine.class);
        when(webcontext.getRequest()).thenReturn(request);
        PowerMockito.mockStatic(SolonEppServiceLocator.class);
        when(SolonEppServiceLocator.getEvenementTypeService()).thenReturn(evenementTypeService);
        PowerMockito.mockStatic(SolonEppUIServiceLocator.class);
        when(SolonEppUIServiceLocator.getEvenementUIService()).thenReturn(evenementUIService);
    }

    @Test
    public void testGetDossier() throws Exception {
        EppDossierObject controlleur = mock(EppDossierObject.class);

        Whitebox.setInternalState(controlleur, "template", template);
        Whitebox.setInternalState(controlleur, "context", context);

        List<Action> actions = new ArrayList<>();
        actions.add(new Action("detailCommunication", new String[] { STActionCategory.VIEW_ACTION_LIST.getName() }));
        when(context.getActions(any(ActionCategory.class))).thenReturn(actions);
        when(context.getWebcontext()).thenReturn(webcontext);
        when(context.getSession()).thenReturn(session);

        DocumentModel evenementDoc = mock(DocumentModel.class);
        Evenement evenement = mock(Evenement.class);
        EvenementTypeDescriptor descriptor = new EvenementTypeDescriptor();
        descriptor.setLabel("LEX-02 : PPL - Information du Gouvernement du dépôt");
        when(context.getCurrentDocument()).thenReturn(evenementDoc);
        when(evenementDoc.getAdapter(Evenement.class)).thenReturn(evenement);
        when(evenement.getTypeEvenement()).thenReturn("EVT02");
        when(evenementTypeService.getEvenementType("EVT02")).thenReturn(descriptor);

        OngletConteneur onglet = new OngletConteneur();
        when(controlleur.actionsToTabs(eq(actions), any(String.class))).thenReturn(onglet);

        when(controlleur.newObject("EppDossierDetailCommunication", context, template)).thenReturn(null);
        when(controlleur.getDossier(anyString(), anyString(), anyString())).thenCallRealMethod();

        doCallRealMethod().when(template).setData(any());
        doCallRealMethod().when(template).getData();

        controlleur.getDossier(ID_MESSAGE, "detailCommunication", "");

        //Vérification de MAJ de la MAP du template
        verify(template, times(1)).setData(any());
        assertEquals(7, template.getData().size());

        assertEquals(ID_MESSAGE, template.getData().get(EppTemplateConstants.DOSSIER_ID));
        assertEquals(
            "Communication : LEX-02 : PPL - Information du Gouvernement du dépôt",
            template.getData().get(STTemplateConstants.TITRE)
        );
        assertEquals(onglet, template.getData().get("myTabs"));

        //Vérification du context
        verify(template, times(1)).setContext(context);

        //Vérification appel controlleur enfant
        verify(controlleur, times(1)).newObject("EppDossierDetailCommunication", context, template);
    }
}
