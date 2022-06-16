package fr.dila.ss.core.service;

import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME;
import static fr.dila.st.api.constant.STBaseFunctionConstant.ADMIN_MINISTERIEL_GROUP_NAME;
import static fr.dila.st.api.constant.STBaseFunctionConstant.SUPERVISEUR_SGG_GROUP_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.ss.api.service.DocumentRoutingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;

@RunWith(MockitoJUnitRunner.class)
public class DocumentRoutingServiceImplTest {
    private DocumentRoutingService service;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private SSRouteStep stepDoc;

    @Before
    public void setUp() {
        service =
            new DocumentRoutingServiceImpl() {

                @Override
                public void validateMoveRouteStepBefore(DocumentModel routeStepDoc) {}
            };
    }

    @Test
    public void testAnyoneCanDoActionIfStepNotObligatoire() {
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();

        when(principal.isAdministrator()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();

        when(principal.isMemberOf(ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();

        when(principal.isMemberOf(SUPERVISEUR_SGG_GROUP_NAME)).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();

        when(principal.isMemberOf(ADMIN_MINISTERIEL_GROUP_NAME)).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();
    }

    @Test
    public void testAdministratorCanDoActionIfStepObligatoire() {
        when(principal.isAdministrator()).thenReturn(true);

        when(stepDoc.isObligatoireSGG()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();

        when(stepDoc.isObligatoireSGG()).thenReturn(false);
        when(stepDoc.isObligatoireMinistere()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();
    }

    @Test
    public void testAdminFonctionnelCanDoActionIfStepObligatoire() {
        when(principal.isMemberOf(ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(true);

        when(stepDoc.isObligatoireSGG()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();

        when(stepDoc.isObligatoireSGG()).thenReturn(false);
        when(stepDoc.isObligatoireMinistere()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();
    }

    @Test
    public void testSuperviseurSGGCanDoActionIfStepObligatoire() {
        when(principal.isMemberOf(SUPERVISEUR_SGG_GROUP_NAME)).thenReturn(true);

        when(stepDoc.isObligatoireSGG()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();

        when(stepDoc.isObligatoireSGG()).thenReturn(false);
        when(stepDoc.isObligatoireMinistere()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();
    }

    @Test
    public void testAdminMinCanDoActionIfStepObligatoire() {
        when(principal.isMemberOf(ADMIN_MINISTERIEL_GROUP_NAME)).thenReturn(true);

        when(stepDoc.isObligatoireSGG()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isFalse();

        when(stepDoc.isObligatoireSGG()).thenReturn(false);
        when(stepDoc.isObligatoireMinistere()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isTrue();
    }

    @Test
    public void testNonAdminMinCannotDoActionIfStepObligatoire() {
        when(stepDoc.isObligatoireSGG()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isFalse();

        when(stepDoc.isObligatoireSGG()).thenReturn(false);
        when(stepDoc.isObligatoireMinistere()).thenReturn(true);
        assertThat(service.canDoActionAccordingToStepObligatoireProperty(principal, stepDoc)).isFalse();
    }
}
