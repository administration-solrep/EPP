package fr.dila.epp.ui.jaxrs.webobject.page.dossier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import fr.dila.epp.ui.enumeration.EppActionCategory;
import fr.dila.epp.ui.services.MetadonneesUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.services.VersionUIService;
import fr.dila.epp.ui.services.actions.CorbeilleActionService;
import fr.dila.epp.ui.services.actions.EvenementTypeActionService;
import fr.dila.epp.ui.services.actions.MetadonneesActionService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.bean.WidgetDTO;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
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
        SolonEppActionsServiceLocator.class,
        SolonEppUIServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class EppDossierDetailCommunicationTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    MetadonneesUIService metadonneesUIService;

    @Mock
    EvenementTypeActionService evenementTypeActionService;

    @Mock
    MetadonneesActionService metadonneesActionService;

    @Mock
    CorbeilleActionService corbeilleService;

    @Mock
    VersionUIService versionUIService;

    @Mock
    SpecificContext specificContext;

    @Mock
    ThTemplate template;

    @Before
    public void before() throws Exception {
        template = mock(ThTemplate.class);
        specificContext = mock(SpecificContext.class);
        PowerMockito.mockStatic(SolonEppUIServiceLocator.class);
        PowerMockito.mockStatic(SolonEppActionsServiceLocator.class);
        when(SolonEppUIServiceLocator.getMetadonneesUIService()).thenReturn(metadonneesUIService);
        when(SolonEppUIServiceLocator.getVersionUIService()).thenReturn(versionUIService);
        when(SolonEppActionsServiceLocator.getEvenementTypeActionService()).thenReturn(evenementTypeActionService);
        when(SolonEppActionsServiceLocator.getMetadonneesActionService()).thenReturn(metadonneesActionService);
        when(SolonEppActionsServiceLocator.getCorbeilleActionService()).thenReturn(corbeilleService);
        when(corbeilleService.getallVersions(specificContext)).thenReturn(Lists.newArrayList());
        when(metadonneesActionService.getSelectedVersion(specificContext)).thenReturn("1.0");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetDetail() {
        EppDossierDetailCommunication controlleur = mock(EppDossierDetailCommunication.class);

        Whitebox.setInternalState(controlleur, "template", template);
        Whitebox.setInternalState(controlleur, "context", specificContext);

        when(evenementTypeActionService.getEvenementSuccessifList(specificContext))
            .thenReturn(Lists.newArrayList(new SelectValueDTO(), new SelectValueDTO()));
        when(metadonneesUIService.getWidgetListForCommunication(specificContext))
            .thenReturn(Lists.newArrayList(new WidgetDTO(), new WidgetDTO()));
        when(metadonneesActionService.getLabelNatureVersion(any(SpecificContext.class))).thenReturn("Version courante");
        when(versionUIService.isActionPossible(specificContext)).thenReturn(true);
        when(versionUIService.getActionList(specificContext)).thenReturn(Arrays.asList("CREER_ALERTE", "PUBLIER"));

        when(specificContext.getActions(EppActionCategory.BASE_COMMUNICATION_DISPLAY))
            .thenReturn(
                Arrays.asList(
                    new Action("CREER_ALERTE", new String[] { EppActionCategory.BASE_COMMUNICATION_DISPLAY.getName() }),
                    new Action(
                        "TRANSMETTRE_PAR_MEL",
                        new String[] { EppActionCategory.BASE_COMMUNICATION_DISPLAY.getName() }
                    )
                )
            );
        when(specificContext.getActions(EppActionCategory.MAIN_COMMUNICATION_DISPLAY))
            .thenReturn(
                Arrays.asList(
                    new Action("MODIFIER", new String[] { EppActionCategory.MAIN_COMMUNICATION_DISPLAY.getName() }),
                    new Action("PUBLIER", new String[] { EppActionCategory.MAIN_COMMUNICATION_DISPLAY.getName() })
                )
            );

        when(controlleur.getDetail(anyString(), anyString(), anyString())).thenCallRealMethod();
        doCallRealMethod().when(template).setData(any());
        doCallRealMethod().when(template).getData();

        controlleur.getDetail("1", "detailCommunication", "");

        // Vérification de MAJ de la MAP du template
        assertEquals(10, template.getData().size());

        assertEquals("1", template.getData().get(STTemplateConstants.ID));
        assertNotNull(template.getData().get(STTemplateConstants.LST_WIDGETS));
        assertEquals(true, template.getData().get("displayComSuccessive"));

        assertEquals("Version courante", template.getData().get("natureVersion"));
        List<WidgetDTO> widgets = (List<WidgetDTO>) template.getData().get(STTemplateConstants.LST_WIDGETS);
        assertNotNull(widgets);
        assertEquals(2, widgets.size());
        List<SelectValueDTO> select = (List<SelectValueDTO>) template.getData().get("comSuccessiveSelect");
        assertNotNull(select);
        assertEquals(2, select.size());

        List<Action> baseActions = (List<Action>) template.getData().get(STTemplateConstants.BASE_ACTIONS);
        assertNotNull(baseActions);
        assertEquals(1, baseActions.size());
        assertEquals("CREER_ALERTE", baseActions.get(0).getId());
        List<Action> mainActions = (List<Action>) template.getData().get(STTemplateConstants.MAIN_ACTIONS);
        assertNotNull(mainActions);
        assertEquals(1, mainActions.size());
        assertEquals("PUBLIER", mainActions.get(0).getId());
        assertNotNull(template.getData().get("lstVersions"));
        assertEquals("1.0", template.getData().get("curVersion"));
        assertNotNull(template.getData().get("curDescription"));
    }
}
