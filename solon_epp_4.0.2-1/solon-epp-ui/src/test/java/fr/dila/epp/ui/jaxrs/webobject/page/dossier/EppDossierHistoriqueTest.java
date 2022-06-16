package fr.dila.epp.ui.jaxrs.webobject.page.dossier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import fr.dila.epp.ui.services.HistoriqueDossierUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.st.ui.bean.DossierHistoriqueEPP;
import fr.dila.st.ui.bean.MessageVersion;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SolonEppUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class EppDossierHistoriqueTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private HistoriqueDossierUIService historiqueDossierUIService;

    private EppDossierHistorique controlleur;

    @Before
    public void before() {
        PowerMockito.mockStatic(SolonEppUIServiceLocator.class);
        when(SolonEppUIServiceLocator.getHistoriqueDossierUIService()).thenReturn(historiqueDossierUIService);
        controlleur = new EppDossierHistorique();
    }

    @Test
    public void testGetHistorique() {
        DossierHistoriqueEPP histo = new DossierHistoriqueEPP();
        List<MessageVersion> liste = new ArrayList<>();
        liste.add(new MessageVersion());
        histo.setLstVersions(liste);
        when(historiqueDossierUIService.getHistoriqueDossier(any(SpecificContext.class))).thenReturn(histo);

        ThTemplate template = controlleur.getHistorique("1", "historique");

        assertTrue(template instanceof AjaxLayoutThTemplate);
        assertEquals("fragments/dossier/onglets/historique", template.getName());

        // Vérification de MAJ de la MAP du template
        assertEquals(1, template.getData().size());
        assertNotNull(template.getData().get("lstVersions"));
    }
}
