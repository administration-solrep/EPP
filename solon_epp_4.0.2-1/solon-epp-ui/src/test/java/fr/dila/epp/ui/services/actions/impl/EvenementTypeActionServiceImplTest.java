package fr.dila.epp.ui.services.actions.impl;

import static fr.dila.solonepp.api.institution.InstitutionsEnum.ASSEMBLEE_NATIONALE;
import static fr.dila.solonepp.api.institution.InstitutionsEnum.GOUVERNEMENT;
import static fr.dila.solonepp.api.institution.InstitutionsEnum.SENAT;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.dila.epp.ui.services.actions.EvenementTypeActionService;
import fr.dila.solonepp.api.descriptor.evenementtype.DistributionDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.DistributionElementDescriptor;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.bean.SelectValueGroupDTO;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SolonEppServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class EvenementTypeActionServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private SpecificContext context;

    @Mock
    private CoreSession session;

    @Mock
    private EppPrincipal eppPrincipal;

    @Mock
    private EvenementTypeService evenementTypeService;

    private EvenementTypeActionService service;

    @Before
    public void setUp() {
        service = new EvenementTypeActionServiceImpl();

        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(eppPrincipal);
        when(eppPrincipal.getInstitutionId()).thenReturn(ASSEMBLEE_NATIONALE.name());
        PowerMockito.mockStatic(SolonEppServiceLocator.class);
        when(SolonEppServiceLocator.getEvenementTypeService()).thenReturn(evenementTypeService);
    }

    @Test
    public void testGetEvenementTypeList() {
        List<EvenementTypeDescriptor> eventTypeList = buildEvenementTypeDescriptorList();
        when(evenementTypeService.findEvenementType()).thenReturn(eventTypeList);
        List<SelectValueDTO> values = service.getEvenementTypeList();
        assertThat(values)
            .extracting(SelectValueDTO::getId)
            .containsExactly("EVT02", "EVT49", "EVT51", "JSS02", "SD01", "GENERIQUE01");
    }

    @Test
    public void testGetEvenementTypeListFromCategory() {
        List<EvenementTypeDescriptor> eventTypeList = buildEvenementTypeDescriptorList();
        when(evenementTypeService.findEvenementType())
            .thenReturn(Arrays.asList(buildEvenementTypeDescriptor("EVT01", ASSEMBLEE_NATIONALE)));
        when(evenementTypeService.findEvenementByCategory(anyString())).thenReturn(null);
        when(evenementTypeService.findEvenementByCategory("category")).thenReturn(eventTypeList);

        // No category specified
        List<SelectValueDTO> values = service.getEvenementTypeListFromCategory(context);
        assertThat(values).extracting(SelectValueDTO::getId).containsExactly("EVT01");

        // Category defined in context data
        when(context.getFromContextData(ID)).thenReturn("category");
        values = service.getEvenementTypeListFromCategory(context);
        assertThat(values)
            .extracting(SelectValueDTO::getId)
            .containsExactly("GENERIQUE01", "JSS02", "EVT02", "EVT51", "EVT49", "SD01");

        // Unknown category in context data
        when(context.getFromContextData(ID)).thenReturn("unknown");
        Throwable throwable = catchThrowable(() -> service.getEvenementTypeListFromCategory(context));
        assertThat(throwable).isInstanceOf(NuxeoException.class).hasMessage("Procédure non trouvée: unknown");
    }

    @Test
    public void testGetEvenementCreateurList() {
        List<EvenementTypeDescriptor> eventTypeList = buildEvenementTypeDescriptorList();
        when(evenementTypeService.findEvenementTypeCreateur()).thenReturn(eventTypeList);
        List<SelectValueDTO> values = service.getEvenementCreateurList(context);
        assertThat(values).extracting(SelectValueDTO::getId).containsExactly("EVT51", "JSS02", "SD01", "GENERIQUE01");
    }

    @Test
    public void testGetEvenementSuccessifList() {
        SolonAlertManager messageQueue = new SolonAlertManager();
        DocumentModel evenementDoc = mock(DocumentModel.class);
        Evenement evenement = mock(Evenement.class);
        EvenementTypeDescriptor commonEvtType = buildEvenementTypeDescriptor("EVT14", ASSEMBLEE_NATIONALE);
        List<EvenementTypeDescriptor> evtTypeList = buildEvenementTypeDescriptorList();
        evtTypeList.add(commonEvtType);
        when(context.getMessageQueue()).thenReturn(messageQueue);
        when(evenementTypeService.findEvenementTypeSuccessifWithSameProcedure("EVT01")).thenReturn(evtTypeList);
        when(evenementTypeService.findEvenementTypeSuccessifWithSameProcedure("EVT02")).thenThrow(new NuxeoException());
        when(evenementTypeService.findEvenementTypeSuccessif(context.getSession(), "EVT01"))
            .thenReturn(Arrays.asList(commonEvtType));

        // No current document in context
        List<SelectValueDTO> values = service.getEvenementSuccessifList(context);
        assertThat(values).isEmpty();
        assertEquals(
            "create.evenement.type.evenement.vide",
            messageQueue.getWarnQueue().get(0).getAlertMessage().get(0)
        );

        // Evenement as current document in context
        when(context.getCurrentDocument()).thenReturn(evenementDoc);
        when(evenementDoc.getAdapter(Evenement.class)).thenReturn(evenement);
        when(evenement.getTypeEvenement()).thenReturn("EVT01");
        values = service.getEvenementSuccessifList(context);
        assertThat(values)
            .hasOnlyElementsOfType(SelectValueGroupDTO.class)
            .extracting(SelectValueDTO::getLabel)
            .containsExactly("Communications conseillées", "Autres communications");
        List<SelectValueDTO> valuesConseillees = ((SelectValueGroupDTO) values.get(0)).getSelectValues();
        List<SelectValueDTO> valuesAutres = ((SelectValueGroupDTO) values.get(1)).getSelectValues();
        assertThat(valuesConseillees).extracting(SelectValueDTO::getId).containsExactly("EVT14");
        assertThat(valuesAutres)
            .extracting(SelectValueDTO::getId)
            .containsExactly("EVT51", "GENERIQUE01", "JSS02", "SD01");

        // EvenementTypeService throws NuxeoException
        when(evenement.getTypeEvenement()).thenReturn("EVT02");
        values = service.getEvenementSuccessifList(context);
        assertThat(values).isEmpty();
        assertEquals(
            "create.evenement.evenement.successif.recupertation.error",
            messageQueue.getWarnQueue().get(0).getAlertMessage().get(1)
        );
    }

    private List<EvenementTypeDescriptor> buildEvenementTypeDescriptorList() {
        List<EvenementTypeDescriptor> eventTypeList = new ArrayList<>();
        eventTypeList.add(buildEvenementTypeDescriptor("GENERIQUE01", ASSEMBLEE_NATIONALE));
        eventTypeList.add(buildEvenementTypeDescriptor("JSS02", ASSEMBLEE_NATIONALE));
        eventTypeList.add(buildEvenementTypeDescriptor("EVT02", GOUVERNEMENT));
        eventTypeList.add(buildEvenementTypeDescriptor("EVT51", ASSEMBLEE_NATIONALE));
        eventTypeList.add(buildEvenementTypeDescriptor("EVT49", SENAT));
        eventTypeList.add(buildEvenementTypeDescriptor("SD01", ASSEMBLEE_NATIONALE));
        return eventTypeList;
    }

    private EvenementTypeDescriptor buildEvenementTypeDescriptor(String name, InstitutionsEnum institution) {
        EvenementTypeDescriptor descriptor = new EvenementTypeDescriptor();
        descriptor.setName(name);
        descriptor.setLabel("label");
        descriptor.setDistribution(new DistributionDescriptor());
        descriptor.getDistribution().setEmetteur(new DistributionElementDescriptor());
        descriptor.getDistribution().getEmetteur().setInstitution(Collections.singletonMap(institution.name(), ""));
        return descriptor;
    }
}
