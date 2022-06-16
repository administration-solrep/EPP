package fr.dila.st.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.AlertContainer;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.bean.GouvernementForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Date;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STGouvernementUIServiceImplTest {
    private static final String ID = "id";
    private static final String LABEL = "label";
    private static final String DATE_DEBUT = "01/01/2000";
    private static final String DATE_FIN = "01/01/2020";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private STGouvernementUIServiceImpl service;

    @Mock
    private SpecificContext context;

    @Mock
    private OrganigrammeService organigrammeService;

    @Mock
    private STGouvernementService gouvernementService;

    @Mock
    private GouvernementNode gouvernementNode;

    @Captor
    private ArgumentCaptor<GouvernementNode> gouvernementNodeCaptor;

    @Mock
    private GouvernementForm gouvernementForm;

    @Before
    public void before() throws Exception {
        service = new STGouvernementUIServiceImpl();

        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        when(STServiceLocator.getSTGouvernementService()).thenReturn(gouvernementService);

        SolonAlertManager alertManager = new SolonAlertManager();
        when(context.getMessageQueue()).thenReturn(alertManager);

        when(gouvernementForm.getId()).thenReturn(ID);
    }

    @Test
    public void testGetGouvernementForm() {
        Date date1 = SolonDateConverter.DATE_SLASH.parseToDateOrNull(DATE_DEBUT);
        Date date2 = SolonDateConverter.DATE_SLASH.parseToDateOrNull(DATE_FIN);

        when(context.getFromContextData(STContextDataKey.ID)).thenReturn(ID);
        when(organigrammeService.getOrganigrammeNodeById(ID, OrganigrammeType.GOUVERNEMENT))
            .thenReturn(gouvernementNode);
        when(gouvernementNode.getId()).thenReturn(ID);
        when(gouvernementNode.getLabel()).thenReturn(LABEL);
        when(gouvernementNode.getDateDebut()).thenReturn(date1);
        when(gouvernementNode.getDateFin()).thenReturn(date2);

        GouvernementForm gvtForm = service.getGouvernementForm(context);

        assertNotNull(gvtForm);
        assertEquals(ID, gvtForm.getId());
        assertEquals(LABEL, gvtForm.getAppellation());
        assertEquals(DATE_DEBUT, gvtForm.getDateDebut());
        assertEquals(DATE_FIN, gvtForm.getDateFin());
    }

    @Test
    public void testCreateGouvernement() {
        Date dateDebut = SolonDateConverter.DATE_SLASH.parseToDateOrNull(DATE_DEBUT);
        Date dateFin = SolonDateConverter.DATE_SLASH.parseToDateOrNull(DATE_FIN);

        when(context.getFromContextData(STContextDataKey.GVT_FORM)).thenReturn(gouvernementForm);
        when(gouvernementForm.getAppellation()).thenReturn(LABEL);
        when(gouvernementForm.getDateDebut()).thenReturn(DATE_DEBUT);
        when(gouvernementForm.getDateFin()).thenReturn(DATE_FIN);

        service.createGouvernement(context);

        verify(gouvernementService).createGouvernement(gouvernementNodeCaptor.capture());
        GouvernementNode gouvernementNode = gouvernementNodeCaptor.getValue();
        assertEquals(LABEL, gouvernementNode.getLabel());
        assertEquals(dateDebut, gouvernementNode.getDateDebut());
        assertEquals(dateFin, gouvernementNode.getDateFin());
    }

    @Test
    public void testUpdateGouvernement() {
        when(context.getFromContextData(STContextDataKey.GVT_FORM)).thenReturn(gouvernementForm);
        when(organigrammeService.getOrganigrammeNodeById(ID, OrganigrammeType.GOUVERNEMENT))
            .thenReturn(gouvernementNode);
        when(gouvernementForm.getAppellation()).thenReturn(LABEL);
        when(gouvernementForm.getDateDebut()).thenReturn("01/01/2000");
        when(gouvernementForm.getDateFin()).thenReturn("01/01/2000");

        service.updateGouvernement(context);

        verify(gouvernementService).updateGouvernement(any(GouvernementNode.class));
        assertNotNull(context.getMessageQueue().getSuccessQueue());
        assertEquals(1, context.getMessageQueue().getSuccessQueue().size());
        AlertContainer successQueue = context.getMessageQueue().getSuccessQueue().get(0);
        assertEquals("Gouvernement modifié", successQueue.getAlertMessage().get(0));
    }
}
