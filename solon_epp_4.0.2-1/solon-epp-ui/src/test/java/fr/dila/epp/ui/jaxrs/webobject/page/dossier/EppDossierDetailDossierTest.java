package fr.dila.epp.ui.jaxrs.webobject.page.dossier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import avro.shaded.com.google.common.collect.Lists;
import fr.dila.epp.ui.bean.DetailDossier;
import fr.dila.epp.ui.services.MetadonneesUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.st.ui.bean.WidgetDTO;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.WebEngine;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SpecificContext.class, SolonWebObject.class, WebEngine.class, SolonEppUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class EppDossierDetailDossierTest {
    @Mock
    SpecificContext specificContext;

    @Mock
    ThTemplate template;

    @Mock
    MetadonneesUIService metadonneesUIService;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Before
    public void before() throws Exception {
        template = mock(ThTemplate.class);
        specificContext = mock(SpecificContext.class);
        PowerMockito.mockStatic(SolonEppUIServiceLocator.class);
        when(SolonEppUIServiceLocator.getMetadonneesUIService()).thenReturn(metadonneesUIService);
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    public void testGetDetail() {
        EppDossierDetailDossier controlleur = mock(EppDossierDetailDossier.class);

        Whitebox.setInternalState(controlleur, "template", template);
        Whitebox.setInternalState(controlleur, "context", specificContext);

        DetailDossier detailDoss = new DetailDossier();
        detailDoss.setLstWidgets(Lists.newArrayList(new WidgetDTO(), new WidgetDTO()));
        when(metadonneesUIService.getDetailDossier(specificContext)).thenReturn(detailDoss);

        when(controlleur.getDetail(anyString(), anyString())).thenCallRealMethod();
        doCallRealMethod().when(template).setData(any());
        doCallRealMethod().when(template).getData();

        controlleur.getDetail("1", "detailDossier");

        // Vérification de MAJ de la MAP du template
        assertEquals(1, template.getData().size());
    }
}
