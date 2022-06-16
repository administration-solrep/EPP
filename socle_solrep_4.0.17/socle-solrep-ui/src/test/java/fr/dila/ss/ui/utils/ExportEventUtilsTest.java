package fr.dila.ss.ui.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.api.constant.STRechercheExportEventConstants;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(STServiceLocator.class)
public class ExportEventUtilsTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private EventProducer eventProducer;

    @Mock
    private SpecificContext context;

    @Mock
    private CoreSession session;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private SolonAlertManager alertManager;

    @Captor
    private ArgumentCaptor<Event> eventCaptor;

    @Before
    public void setUp() throws Exception {
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(context.getMessageQueue()).thenReturn(alertManager);
    }

    @Test
    public void testFireExportEventWithEmail() {
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STServiceLocator.getEventProducer()).thenReturn(eventProducer);

        when(principal.getEmail()).thenReturn("test@coexia.eu");

        ExportEventUtils.fireExportEvent(context, STRechercheExportEventConstants.EVENT_NAME, ImmutableMap.of());

        verify(eventProducer).fireEvent(eventCaptor.capture());

        Event event = eventCaptor.getValue();
        assertThat(event.getName()).isEqualTo(STRechercheExportEventConstants.EVENT_NAME);

        verify(alertManager)
            .addToastSuccess(
                "La demande d'export a été prise en compte. L'export vous sera transmis par courrier électronique dès qu'il sera disponible."
            );
    }

    @Test
    public void testFireExportEventWithoutEmail() {
        when(principal.getEmail()).thenReturn(" ");

        ExportEventUtils.fireExportEvent(context, STRechercheExportEventConstants.EVENT_NAME, ImmutableMap.of());

        verify(eventProducer, never()).fireEvent(any(Event.class));

        verify(alertManager)
            .addErrorToQueue("Aucun courriel n'est défini dans votre profil pour envoyer le mail d'export.");
    }
}
