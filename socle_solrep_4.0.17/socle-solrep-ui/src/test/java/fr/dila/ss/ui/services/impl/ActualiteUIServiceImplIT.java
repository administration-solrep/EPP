package fr.dila.ss.ui.services.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.inject.Inject;
import fr.dila.ss.api.actualite.Actualite;
import fr.dila.ss.api.service.ActualiteService;
import fr.dila.ss.core.test.ActualiteFeature;
import fr.dila.ss.ui.bean.actualites.ActualiteConsultationDTO;
import fr.dila.ss.ui.bean.actualites.ActualiteCreationDTO;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.st.core.util.DateUtil;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.model.SpecificContext;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CloseableCoreSession;
import org.nuxeo.ecm.core.api.CoreInstance;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(ActualiteFeature.class)
@Deploy("fr.dila.ss.ui")
public class ActualiteUIServiceImplIT {
    @Inject
    private ActualiteService service;

    @Inject
    private ActualiteUIService uiService;

    @Inject
    private CoreSession session;

    @Inject
    private UserManager um;

    @Test
    public void testService() {
        assertThat(uiService).isNotNull();
    }

    @Test
    public void testFecthActualitesNonLues() {
        // Given
        SpecificContext context = new SpecificContext();
        context.setSession(session);

        ActualiteCreationDTO dto = new ActualiteCreationDTO();
        dto.setDateEmission(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        dto.setDateValidite(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(1)));
        dto.setObjet("objet");
        dto.setContenu("contenu");

        DocumentModel actualiteDoc = uiService.toDocumentModel(context, dto);
        service.createActualite(session, actualiteDoc);
        session.save();

        // When
        List<ActualiteConsultationDTO> actualites = uiService.fetchUserActualitesNonLues(context);

        // Then
        assertThat(actualites).isNotEmpty();
    }

    @Test
    public void testFecthActualitesNonLuesDateEmissionFuture() {
        // Given
        SpecificContext context = new SpecificContext();
        context.setSession(session);

        ActualiteCreationDTO dto = new ActualiteCreationDTO();
        dto.setDateEmission(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(1)));
        dto.setDateValidite(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(2)));
        dto.setObjet("objet");
        dto.setContenu("contenu");

        DocumentModel actualiteDoc = uiService.toDocumentModel(context, dto);
        service.createActualite(session, actualiteDoc);
        session.save();

        // When
        List<ActualiteConsultationDTO> actualites = uiService.fetchUserActualitesNonLues(context);

        // Then
        assertThat(actualites).isEmpty();
    }

    @Test
    public void testFecthActualitesNonLuesDateValiditePast() {
        // Given
        SpecificContext context = new SpecificContext();
        context.setSession(session);

        ActualiteCreationDTO dto = new ActualiteCreationDTO();
        dto.setDateEmission(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        dto.setDateValidite(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(1)));
        dto.setObjet("objet");
        dto.setContenu("contenu");

        DocumentModel actualiteDoc = uiService.toDocumentModel(context, dto);
        service.createActualite(session, actualiteDoc);
        session.save();

        Actualite actualite = actualiteDoc.getAdapter(Actualite.class);
        actualite.setDateEmission(LocalDate.now().minusDays(2));
        actualite.setDateValidite(LocalDate.now().minusDays(1));
        session.saveDocument(actualiteDoc);
        session.save();

        // When
        List<ActualiteConsultationDTO> actualites = uiService.fetchUserActualitesNonLues(context);

        // Then
        assertThat(actualites).isEmpty();
    }

    @Test
    public void testFecthActualitesNonLuesActualiteLue() {
        // Given
        SpecificContext context = new SpecificContext();
        context.setSession(session);

        ActualiteCreationDTO dto = new ActualiteCreationDTO();
        dto.setDateEmission(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        dto.setDateValidite(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(1)));
        dto.setObjet("objet");
        dto.setContenu("contenu");

        DocumentModel actualiteDoc = uiService.toDocumentModel(context, dto);
        DocumentModel persistedActualiteDoc = service.createActualite(session, actualiteDoc);
        session.save();

        context.putInContextData(STContextDataKey.ID, persistedActualiteDoc.getId());
        uiService.setActualiteLue(context);
        session.save();

        // When
        List<ActualiteConsultationDTO> actualites = uiService.fetchUserActualitesNonLues(context);

        // Then
        assertThat(actualites).isEmpty();
    }

    @Test
    public void testSetActualiteLueByUser() {
        // Given
        SpecificContext context = new SpecificContext();
        context.setSession(session);

        ActualiteCreationDTO dto = new ActualiteCreationDTO();
        dto.setDateEmission(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        dto.setDateValidite(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(1)));
        dto.setObjet("objet");
        dto.setContenu("contenu");

        DocumentModel actualiteDoc = uiService.toDocumentModel(context, dto);
        DocumentModel persistedActualiteDoc = service.createActualite(session, actualiteDoc);
        session.save();

        try (
            CloseableCoreSession userSession = CoreInstance.openCoreSession(
                session.getRepositoryName(),
                um.getPrincipal("user")
            )
        ) {
            // When
            context.setSession(userSession);

            context.putInContextData(STContextDataKey.ID, persistedActualiteDoc.getId());
            uiService.setActualiteLue(context);

            session.save();
        }

        // Then
        session
            .getDocument(new IdRef(persistedActualiteDoc.getId()))
            .getAdapter(Actualite.class)
            .getLecteurs()
            .contains("user");
    }

    @Test
    public void testSetActualiteLueTwice() {
        // Given
        SpecificContext context = new SpecificContext();
        context.setSession(session);

        ActualiteCreationDTO dto = new ActualiteCreationDTO();
        dto.setDateEmission(DateUtil.localDateToGregorianCalendar(LocalDate.now()));
        dto.setDateValidite(DateUtil.localDateToGregorianCalendar(LocalDate.now().plusDays(1)));
        dto.setObjet("objet");
        dto.setContenu("contenu");

        DocumentModel actualiteDoc = uiService.toDocumentModel(context, dto);
        DocumentModel persistedActualiteDoc = service.createActualite(session, actualiteDoc);
        session.save();

        context.putInContextData(STContextDataKey.ID, persistedActualiteDoc.getId());

        try (
            CloseableCoreSession userSession = CoreInstance.openCoreSession(
                session.getRepositoryName(),
                um.getPrincipal("user")
            )
        ) {
            context.setSession(userSession);
            uiService.setActualiteLue(context);

            // When
            uiService.setActualiteLue(context);
        }

        // Then
        Assertions
            .assertThat(
                session.getDocument(new IdRef(persistedActualiteDoc.getId())).getAdapter(Actualite.class).getLecteurs()
            )
            .containsOnlyOnce("user");
    }
}
