package fr.dila.ss.ui.jaxrs.webobject.page.admin.actualite;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.ss.api.service.ActualiteService;
import fr.dila.ss.core.test.ActualiteFeature;
import fr.dila.ss.core.test.SolrepFeature;
import fr.dila.ss.ui.bean.actualites.ActualiteConsultationDTO;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.time.LocalDate;
import java.util.Collections;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.powermock.reflect.Whitebox;

@RunWith(FeaturesRunner.class)
@Features({ SolrepFeature.class, ActualiteFeature.class })
@Deploy("fr.dila.ss.ui")
public class SSActualiteConsultationIT {
    @Inject
    private ActualiteService actualiteService;

    @Inject
    private ActualiteUIService actualiteUIService;

    @Inject
    private SSActualitesConsultation controller;

    @Inject
    private CoreSession session;

    private SpecificContext context = new SpecificContext();

    @Before
    public void setUp() {
        context.setSession(session);

        Whitebox.setInternalState(controller, "context", context);
    }

    private ActualiteConsultationDTO newActualiteForm() {
        ActualiteConsultationDTO form = new ActualiteConsultationDTO();
        form.setContenu("contenu");
        form.setObjet("objet");
        form.setDateEmission(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        form.setDateValidite(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        form.setIsInHistorique(true);
        form.setPiecesJointes(Collections.emptyList());

        return form;
    }

    @Test
    public void testViewActualite() {
        // Given
        ActualiteConsultationDTO form = newActualiteForm();
        DocumentModel persistedActualite = actualiteService.createActualite(
            context.getSession(),
            actualiteUIService.toDocumentModel(context, form)
        );

        // When
        ThTemplate template = (ThTemplate) controller.viewActualite(persistedActualite.getId());

        // Then
        assertThat(template).isNotNull();
        ActualiteConsultationDTO actualiteConsulte = (ActualiteConsultationDTO) template.getData().get("actualiteDto");
        assertThat(actualiteConsulte).isNotNull();
        assertThat(actualiteConsulte.getObjet()).isEqualTo("objet");
    }
}
