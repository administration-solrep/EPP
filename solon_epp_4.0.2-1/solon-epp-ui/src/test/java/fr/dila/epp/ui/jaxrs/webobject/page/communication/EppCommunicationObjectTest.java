package fr.dila.epp.ui.jaxrs.webobject.page.communication;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import avro.shaded.com.google.common.collect.Lists;
import fr.dila.epp.ui.enumeration.EppActionCategory;
import fr.dila.epp.ui.services.EvenementUIService;
import fr.dila.epp.ui.services.MetadonneesUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.WidgetDTO;
import fr.dila.st.ui.enums.ActionCategory;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.DtoJsonHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.impl.SolonAlertManager;
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
        DtoJsonHelper.class,
        SolonEppServiceLocator.class,
        SolonEppUIServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class EppCommunicationObjectTest {

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

    @Mock
    MetadonneesUIService metadonneesUIService;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(DtoJsonHelper.class);
        context = mock(SpecificContext.class);
        template = mock(ThTemplate.class);
        webcontext = mock(WebContext.class);
        PowerMockito.mockStatic(WebEngine.class);
        when(webcontext.getRequest()).thenReturn(request);
        PowerMockito.mockStatic(SolonEppServiceLocator.class);
        when(SolonEppServiceLocator.getEvenementTypeService()).thenReturn(evenementTypeService);
        PowerMockito.mockStatic(SolonEppUIServiceLocator.class);
        when(SolonEppUIServiceLocator.getEvenementUIService()).thenReturn(evenementUIService);
        when(SolonEppUIServiceLocator.getMetadonneesUIService()).thenReturn(metadonneesUIService);
    }

    @Test
    public void testModifierCommunication() throws Exception {
        EppCommunicationObject controlleur = mock(EppCommunicationObject.class);

        Whitebox.setInternalState(controlleur, "template", template);
        Whitebox.setInternalState(controlleur, "context", context);

        DocumentModel evenementDoc = mock(DocumentModel.class);
        Evenement evenement = mock(Evenement.class);
        EvenementTypeDescriptor descriptor = new EvenementTypeDescriptor();
        descriptor.setLabel("LEX-02 : PPL - Information du Gouvernement du dépôt");
        when(context.getCurrentDocument()).thenReturn(evenementDoc);
        when(evenementDoc.getAdapter(Evenement.class)).thenReturn(evenement);
        when(evenement.getTypeEvenement()).thenReturn("EVT02");
        when(evenementTypeService.getEvenementType("EVT02")).thenReturn(descriptor);

        when(metadonneesUIService.getWidgetListForCommunication(context))
            .thenReturn(Lists.newArrayList(new WidgetDTO(), new WidgetDTO()));

        List<Action> actions = new ArrayList<>();
        actions.add(
            new Action("detailCommunication", new String[] { EppActionCategory.BASE_COMMUNICATION_EDIT.getName() })
        );
        actions.add(
            new Action("detailCommunication", new String[] { EppActionCategory.MAIN_COMMUNICATION_EDIT.getName() })
        );
        when(context.getActions(any(ActionCategory.class))).thenReturn(actions);
        when(context.getWebcontext()).thenReturn(webcontext);
        when(context.getSession()).thenReturn(session);

        when(controlleur.modifierCommunication(anyString())).thenCallRealMethod();

        doCallRealMethod().when(template).setData(any());
        doCallRealMethod().when(template).getData();

        controlleur.modifierCommunication("1");

        //Vérification de MAJ de la MAP du template
        verify(template, times(1)).setData(any());
        assertEquals(8, template.getData().size());

        //Vérification du context
        verify(template, times(1)).setContext(context);
        verify(template, times(1)).setName(any());
    }

    @Test
    public void testEnregistrerCommunication() throws Exception {
        EppCommunicationObject communication = new EppCommunicationObject();

        when(context.getMessageQueue()).thenReturn(new SolonAlertManager());

        JsonResponse reponse = (JsonResponse) communication.enregistrerCommunication("", "", true, "", "").getEntity();
        assertEquals(SolonStatus.OK, reponse.getStatut());
    }
}
