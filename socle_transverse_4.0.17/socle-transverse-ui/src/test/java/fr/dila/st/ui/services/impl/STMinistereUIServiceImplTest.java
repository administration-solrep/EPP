package fr.dila.st.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.GouvernementNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.api.service.organigramme.STGouvernementService;
import fr.dila.st.api.service.organigramme.STMinisteresService;
import fr.dila.st.core.organigramme.EntiteNodeImpl;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.bean.EntiteForm;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.function.Consumer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class STMinistereUIServiceImplTest {
    STMinistereUIServiceImpl ministereUIService;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    SpecificContext context;

    @Mock
    OrganigrammeService organigrammeService;

    @Mock
    STGouvernementService gouvernementService;

    @Mock
    STMinisteresService ministereService;

    @Mock
    EntiteNode ministere;

    @Mock
    OrganigrammeNode gouvernement;

    @Mock
    GouvernementNode gouvernementNode;

    @Mock
    EntiteForm entiteForm;

    @Mock
    EntiteNode entiteNode;

    @Mock
    private CoreSession session;

    @Mock
    private SolonAlertManager messageQueue;

    @Captor
    private ArgumentCaptor<Consumer<EntiteNode>> updaterCaptor;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STServiceLocator.class);
        context = new SpecificContext();
        context.setSession(session);
        context.setMessageQueue(messageQueue);

        NuxeoPrincipal principal = mock(NuxeoPrincipal.class);
        when(principal.getName()).thenReturn("user");
        when(session.getPrincipal()).thenReturn(principal);

        when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        when(STServiceLocator.getSTGouvernementService()).thenReturn(gouvernementService);
        when(organigrammeService.getOrganigrammeNodeById("idMinistere", OrganigrammeType.MINISTERE))
            .thenReturn(ministere);
        when(ministere.getParentId()).thenReturn("idParent");
        when(organigrammeService.getOrganigrammeNodeById("idParent", OrganigrammeType.GOUVERNEMENT))
            .thenReturn(gouvernement);
        when(gouvernementService.getGouvernement(anyString())).thenReturn(gouvernementNode);
        when(STServiceLocator.getSTMinisteresService()).thenReturn(ministereService);

        ministereUIService = spy(new STMinistereUIServiceImpl());
    }

    @Test
    public void testGetEntiteForm() {
        Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
        Date date = calendar.getTime();
        context.putInContextData(STContextDataKey.ID, "idMinistere");

        when(ministere.getId()).thenReturn("idMinistere");
        when(ministere.getLabel()).thenReturn("Label Ministere");
        when(ministere.getEdition()).thenReturn("Edition Ministere");
        when(ministere.getDateDebut()).thenReturn(date);
        when(ministere.getDateFin()).thenReturn(date);
        when(ministere.getOrdre()).thenReturn(1L);
        when(ministere.getFormule()).thenReturn("Formule");
        when(ministere.getMembreGouvernementCivilite()).thenReturn("Monsieur");
        when(ministere.getMembreGouvernementPrenom()).thenReturn("Prenom");
        when(ministere.getMembreGouvernementNom()).thenReturn("Nom");

        when(gouvernement.getId()).thenReturn("idGouvernement");
        when(gouvernement.getLabel()).thenReturn("LabelGouvernement");
        when(gouvernement.getLabelWithNor(null)).thenReturn("LabelGouvernement");
        when(gouvernement.getType()).thenReturn(OrganigrammeType.GOUVERNEMENT);
        when(gouvernement.isActive()).thenReturn(true);

        when(organigrammeService.lockOrganigrammeNode(Mockito.eq(session), Mockito.any())).thenReturn(true);

        EntiteForm entiteFormRes = ministereUIService.getEntiteForm(context);

        assertNotNull(entiteFormRes);
        assertEquals("idMinistere", entiteFormRes.getIdentifiant());
        assertEquals("Label Ministere", entiteFormRes.getAppellation());
        assertEquals("Edition Ministere", entiteFormRes.getEdition());
        assertEquals("01/01/2000", entiteFormRes.getDateDebut());
        assertEquals("01/01/2000", entiteFormRes.getDateFin());
        assertEquals("1", entiteFormRes.getOrdreProtocolaire());
        assertEquals("Formule", entiteFormRes.getFormulesEntetes());
        assertEquals("Monsieur", entiteFormRes.getCivilite());
        assertEquals("Nom", entiteFormRes.getMembreGouvernementNom());
        assertEquals("Prenom", entiteFormRes.getMembreGouvernementPrenom());
        assertNotNull(entiteFormRes.getGouvernement());
        assertEquals("idGouvernement", entiteFormRes.getGouvernement().getKey());
        assertEquals("LabelGouvernement", entiteFormRes.getGouvernement().getLabel());
        assertEquals(OrganigrammeType.GOUVERNEMENT.getValue(), entiteFormRes.getGouvernement().getType());
        assertTrue(entiteFormRes.getGouvernement().getIsActive());
    }

    @Test
    public void testGetEntiteFormLockedByOtherUser() {
        // Given
        context.putInContextData(STContextDataKey.ID, "idMinistere");

        when(ministere.getLockUserName()).thenReturn("user1");
        when(ministere.getLockDate()).thenReturn(new Date());

        when(gouvernement.getType()).thenReturn(OrganigrammeType.GOUVERNEMENT);

        when(organigrammeService.lockOrganigrammeNode(Mockito.eq(session), Mockito.any())).thenReturn(false);

        // When
        ministereUIService.getEntiteForm(context);

        // Then
        verify(messageQueue).addErrorToQueue(Mockito.contains("user1"));
    }

    @Test
    public void testCreateEntite() {
        context.putInContextData(STContextDataKey.ENTITE_FORM, entiteForm);

        when(entiteForm.getIdentifiant()).thenReturn("idMinistere");
        when(entiteForm.getAppellation()).thenReturn("appelation");
        when(entiteForm.getEdition()).thenReturn("Edition Ministere");
        when(entiteForm.getDateDebut()).thenReturn("01/01/2000");
        when(entiteForm.getDateFin()).thenReturn("01/01/2000");
        when(entiteForm.getOrdreProtocolaire()).thenReturn("1");
        when(entiteForm.getFormulesEntetes()).thenReturn("Formule");
        when(entiteForm.getCivilite()).thenReturn("Monsieur");
        when(entiteForm.getMembreGouvernementPrenom()).thenReturn("Prenom");
        when(entiteForm.getMembreGouvernementNom()).thenReturn("Nom");
        when(entiteForm.getIdGouvernement()).thenReturn("idGouvernement");

        when(organigrammeService.checkEntiteUniqueLabelInParent(any(), any())).thenReturn(true);

        ministereUIService.createEntite(context);

        verify(ministereService, times(1)).createEntite(any(EntiteNode.class));
    }

    @Test
    public void testUpdateEntite() {
        context.putInContextData(STContextDataKey.ENTITE_FORM, entiteForm);
        context.putInContextData(STContextDataKey.ID, "idMinistere");

        when(entiteForm.getIdentifiant()).thenReturn("idMinistere");
        when(entiteForm.getAppellation()).thenReturn("appelation");
        when(entiteForm.getEdition()).thenReturn("Edition Ministere");
        when(entiteForm.getDateDebut()).thenReturn("01/01/2000");
        when(entiteForm.getDateFin()).thenReturn("01/01/2000");
        when(entiteForm.getOrdreProtocolaire()).thenReturn("1");
        when(entiteForm.getFormulesEntetes()).thenReturn("Formule");
        when(entiteForm.getCivilite()).thenReturn("Monsieur");
        when(entiteForm.getMembreGouvernementPrenom()).thenReturn("Prenom");
        when(entiteForm.getMembreGouvernementNom()).thenReturn("Nom");
        when(entiteForm.getIdGouvernement()).thenReturn("idGouvernement");

        when(organigrammeService.checkEntiteUniqueLabelInParent(any(), any())).thenReturn(true);

        ministereUIService.updateEntite(context);

        verify(ministereUIService)
            .performNodeUpdate(
                eq(context),
                eq(ministere),
                updaterCaptor.capture(),
                eq("organigramme.success.update.ministere")
            );

        updaterCaptor.getValue().accept(ministere);
        verify(ministereService).updateEntite(session, ministere);
    }

    @Test
    public void testUpdateEntiteNode() throws ParseException {
        EntiteNodeImpl entiteNode = new EntiteNodeImpl();
        Calendar calendar = new GregorianCalendar(2000, Calendar.JANUARY, 1);
        Date date = calendar.getTime();

        when(entiteForm.getAppellation()).thenReturn("Appelation");
        when(entiteForm.getEdition()).thenReturn("Edition Ministere");
        when(entiteForm.getDateDebut()).thenReturn("01/01/2000");
        when(entiteForm.getDateFin()).thenReturn("01/01/2000");
        when(entiteForm.getOrdreProtocolaire()).thenReturn("1");
        when(entiteForm.getFormulesEntetes()).thenReturn("Formule");
        when(entiteForm.getCivilite()).thenReturn("Monsieur");
        when(entiteForm.getMembreGouvernementPrenom()).thenReturn("Prenom");
        when(entiteForm.getMembreGouvernementNom()).thenReturn("Nom");
        when(entiteForm.getIdGouvernement()).thenReturn("idGouvernement");

        EntiteNode entiteNodeRes = STMinistereUIServiceImpl.updateEntiteNode(entiteNode, entiteForm);

        assertNotNull(entiteNodeRes);
        assertEquals("Appelation", entiteNodeRes.getLabel());
        assertEquals("Edition Ministere", entiteNodeRes.getEdition());
        assertEquals(date, entiteNodeRes.getDateDebut());
        assertEquals(date, entiteNodeRes.getDateFin());
        assertEquals(new Long(1L), entiteNodeRes.getOrdre());
        assertEquals("Formule", entiteNodeRes.getFormule());
        assertEquals("Monsieur", entiteNodeRes.getMembreGouvernementCivilite());
        assertEquals("Nom", entiteNodeRes.getMembreGouvernementNom());
        assertEquals("Prenom", entiteNodeRes.getMembreGouvernementPrenom());
        assertEquals("idGouvernement", entiteNodeRes.getParentId());
    }
}
