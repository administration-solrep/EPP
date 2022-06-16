package fr.dila.ss.ui.jaxrs.webobject.page.admin.actualite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.ss.ui.bean.actualites.ActualiteConsultationDTO;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.powermock.reflect.Whitebox;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(SSUIServiceLocator.class)
@PowerMockIgnore("javax.management.*")
public class SSActualitesConsultationTest {
    private static final String URL_PREVIOUS = "previous";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSActualitesConsultation page;

    @Mock
    private SpecificContext context;

    @Mock
    private CoreSession session;

    @Mock
    private DocumentModel actualiteDoc;

    @Mock
    private ActualiteUIService actualiteUIService;

    @Before
    public void before() {
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        when(SSUIServiceLocator.getActualiteUIService()).thenReturn(actualiteUIService);

        page = new SSActualitesConsultation();

        Whitebox.setInternalState(page, "context", context);

        when(context.getSession()).thenReturn(session);
    }

    @Test
    public void testViewActualite() {
        String actualiteId = "actu-id";

        when(session.getDocument(new IdRef(actualiteId))).thenReturn(actualiteDoc);

        ActualiteConsultationDTO dto = new ActualiteConsultationDTO();
        dto.setId(actualiteId);
        dto.setObjet("objet");
        when(actualiteUIService.toActualiteForm(actualiteDoc)).thenReturn(dto);

        when(context.getUrlPreviousPage()).thenReturn(URL_PREVIOUS);

        ThTemplate template = (ThTemplate) page.viewActualite(actualiteId);

        assertThat(template).isNotNull();
        assertThat(template.getName()).isEqualTo("pages/admin/actualites/actualiteView");
        assertThat(template.getContext()).isEqualTo(context);

        assertThat(template.getData())
            .isNotNull()
            .containsOnly(
                entry(SSTemplateConstants.ACTUALITE_DTO, dto),
                entry(STTemplateConstants.URL_PREVIOUS_PAGE, URL_PREVIOUS)
            );

        verify(actualiteUIService).toActualiteForm(actualiteDoc);
        verify(context)
            .setNavigationContextTitle(
                new Breadcrumb(
                    dto.getObjet(),
                    "/admin/actualites/consultation/" + dto.getId(),
                    Breadcrumb.TITLE_ORDER + 10
                )
            );
    }
}
