package fr.dila.epp.ui.services.impl;

import static fr.dila.epp.ui.enumeration.EppContextDataKey.CURRENT_VERSION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableSet;
import fr.dila.epp.ui.bean.DetailDossier;
import fr.dila.epp.ui.services.MetadonneesUIService;
import fr.dila.epp.ui.services.actions.CorbeilleActionService;
import fr.dila.epp.ui.services.actions.MetadonneesActionService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.domain.dossier.Dossier;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.DossierService;
import fr.dila.solonepp.api.service.MetaDonneesService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import fr.dila.st.core.descriptor.parlement.PropertyDescriptorImpl;
import fr.dila.st.ui.bean.WidgetDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SolonEppServiceLocator.class, SolonEppActionsServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class MetadonneesUIServiceImplTest {
    private static final String URL_DOSSIER_SENAT = "www.senat.fr/ABDC_0000";
    private static final String ID_DOSSIER = "ABCD";
    private static final String ID_EVENEMENT = "ABCD_00000";
    private static final String TYPE_EVENEMENT = "EVT01";

    MetadonneesUIService service;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    DossierService dossierService;

    @Mock
    MetaDonneesService metaDonneesService;

    @Mock
    CorbeilleActionService corbeilleActionService;

    @Mock
    MetadonneesActionService metadonneesActionService;

    @Mock
    CoreSession session;

    @Mock
    EppPrincipal principal;

    @Before
    public void before() {
        service = new MetadonneesUIServiceImpl();
        PowerMockito.mockStatic(SolonEppServiceLocator.class);
        PowerMockito.mockStatic(SolonEppActionsServiceLocator.class);
        when(SolonEppServiceLocator.getDossierService()).thenReturn(dossierService);
        when(SolonEppServiceLocator.getMetaDonneesService()).thenReturn(metaDonneesService);
        when(SolonEppActionsServiceLocator.getCorbeilleActionService()).thenReturn(corbeilleActionService);
        when(SolonEppActionsServiceLocator.getMetadonneesActionService()).thenReturn(metadonneesActionService);

        when(session.getPrincipal()).thenReturn(principal);
        when(principal.isInstitutionAn()).thenReturn(false);
        when(principal.isInstitutionSenat()).thenReturn(true);
    }

    @Test
    public void testGetWidgetListForCommunication() {
        SpecificContext context = mock(SpecificContext.class);
        when(context.getSession()).thenReturn(session);

        DocumentModel evenementDoc = mock(DocumentModel.class);
        Evenement evenement = mock(Evenement.class);
        when(evenementDoc.getAdapter(Evenement.class)).thenReturn(evenement);

        when(context.getCurrentDocument()).thenReturn(evenementDoc);
        when(evenement.getTypeEvenement()).thenReturn(TYPE_EVENEMENT);
        when(evenement.getIdEvenement()).thenReturn(ID_EVENEMENT);

        Version version = mock(Version.class);
        when(context.getFromContextData(CURRENT_VERSION)).thenReturn(version);
        when(version.getUrlDossierSenat()).thenReturn(URL_DOSSIER_SENAT);

        when(metaDonneesService.getMapProperty(TYPE_EVENEMENT)).thenReturn(buildPropertyMap());
        when(metadonneesActionService.isColumnVisible(context)).thenReturn(true);

        List<WidgetDTO> widgets = service.getWidgetListForCommunication(context);

        assertEquals(2, widgets.size());
        WidgetDTO widget1 = widgets.get(0);
        assertEquals("title", widget1.getName());
        assertEquals("label.epp.metadonnee.title", widget1.getLabel());
        assertEquals("text", widget1.getTypeChamp());
        assertEquals(ID_EVENEMENT, widget1.getValueParamByName("valeur"));
        WidgetDTO widget2 = widgets.get(1);
        assertEquals("urlDossierSenat", widget2.getName());
        assertEquals("label.epp.metadonnee.urlDossierSenat", widget2.getLabel());
        assertEquals("url", widget2.getTypeChamp());
        assertEquals(URL_DOSSIER_SENAT, widget2.getValueParamByName("valeur"));
    }

    @Test
    public void testGetDetailDossier() {
        SpecificContext context = new SpecificContext();
        CoreSession session = mock(CoreSession.class);
        context.setSession(session);

        DocumentModel evenementDoc = mock(DocumentModel.class);
        Evenement evenement = mock(Evenement.class);
        when(evenementDoc.getAdapter(Evenement.class)).thenReturn(evenement);
        context.setCurrentDocument(evenementDoc);
        when(evenement.getDossier()).thenReturn(ID_DOSSIER);

        DocumentModel dossierDoc = mock(DocumentModel.class);
        Dossier dossier = mock(Dossier.class);
        when(dossierDoc.getAdapter(Dossier.class)).thenReturn(dossier);
        when(dossier.getTitle()).thenReturn(ID_DOSSIER);
        when(dossier.getUrlDossierSenat()).thenReturn(URL_DOSSIER_SENAT);

        when(dossierService.getDossier(session, ID_DOSSIER)).thenReturn(dossierDoc);
        when(dossierService.getEvenementTypeDossierList(session, dossierDoc)).thenReturn(ImmutableSet.of(ID_EVENEMENT));
        when(metaDonneesService.getMapProperty(ID_EVENEMENT)).thenReturn(buildPropertyMap());

        DetailDossier detailDossier = service.getDetailDossier(context);

        assertNotNull(detailDossier);
        assertEquals(2, detailDossier.getLstWidgets().size());
        WidgetDTO widget1 = detailDossier.getLstWidgets().get(0);
        assertEquals("title", widget1.getName());
        assertEquals("label.epp.metadonnee.title", widget1.getLabel());
        assertEquals("text", widget1.getTypeChamp());
        assertEquals(ID_DOSSIER, widget1.getValueParamByName("valeur"));
        WidgetDTO widget2 = detailDossier.getLstWidgets().get(1);
        assertEquals("urlDossierSenat", widget2.getName());
        assertEquals("label.epp.metadonnee.urlDossierSenat", widget2.getLabel());
        assertEquals("url", widget2.getTypeChamp());
        assertEquals(URL_DOSSIER_SENAT, widget2.getValueParamByName("valeur"));
    }

    private Map<String, PropertyDescriptor> buildPropertyMap() {
        Map<String, PropertyDescriptor> propertyMap = new LinkedHashMap<>();
        propertyMap.put("title", buildPropertyDescriptor("title", "text", true));
        propertyMap.put("niveauLectureNumero", buildPropertyDescriptor("niveauLectureNumero", "int", true));
        propertyMap.put(
            "dateRefusProcedureEngagementAn",
            buildPropertyDescriptor("dateRefusProcedureEngagementAn", "timestamp", false)
        );
        propertyMap.put("urlDossierSenat", buildPropertyDescriptor("urlDossierSenat", "text", true));
        return propertyMap;
    }

    private PropertyDescriptor buildPropertyDescriptor(String name, String type, boolean ficheDossier) {
        PropertyDescriptor property = new PropertyDescriptorImpl();
        property.setName(name);
        property.setType(type);
        property.setFicheDossier(ficheDossier);

        return property;
    }
}
