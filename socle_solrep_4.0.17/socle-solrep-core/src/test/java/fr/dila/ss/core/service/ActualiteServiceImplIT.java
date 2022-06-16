package fr.dila.ss.core.service;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.ss.api.actualite.Actualite;
import fr.dila.ss.api.actualite.ActualiteRequete;
import fr.dila.ss.api.constant.ActualiteConstant;
import fr.dila.ss.api.service.ActualiteService;
import fr.dila.ss.core.test.ActualiteFeature;
import fr.dila.ss.core.test.SolrepFeature;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features({ SolrepFeature.class, ActualiteFeature.class })
public class ActualiteServiceImplIT {
    @Inject
    private ActualiteService service;

    @Inject
    private CoreSession session;

    private DocumentModel newValidActualite() {
        DocumentModel actualiteDoc = session.createDocumentModel(ActualiteConstant.ACTUALITE_DOCUMENT_TYPE);

        Actualite actualite = actualiteDoc.getAdapter(Actualite.class);
        actualite.setDateEmission(LocalDate.now());
        actualite.setDateValidite(LocalDate.now());
        actualite.setIsInHistorique(false);
        actualite.setObjet("objet");
        actualite.setContenu("objet");

        return actualiteDoc;
    }

    @Test
    public void testCreateActualite() {
        // Given
        DocumentModel actualiteDoc = newValidActualite();

        // When
        DocumentModel persistedActualiteDoc = service.createActualite(session, actualiteDoc);
        session.save();

        // Then
        Assertions.assertThat(persistedActualiteDoc).isNotNull();
    }

    @Test
    public void testFetchActualitesSansFiltres() {
        DocumentModel actualiteDoc = newValidActualite();
        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, docs -> !docs.isEmpty());
    }

    @Test
    public void testFetchActualitesDateEmissionDebutAfter() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDateEmissionDebut(LocalDate.now().plusDays(1));

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, List::isEmpty);
    }

    @Test
    public void testFetchActualitesDateEmissionDebutSameDay() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDateEmissionDebut(LocalDate.now());

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, docs -> !docs.isEmpty());
    }

    @Test
    public void testFetchActualitesDateEmissionFinBefore() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDateEmissionFin(LocalDate.now().minusDays(1));

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, List::isEmpty);
    }

    @Test
    public void testFetchActualitesDateEmissionFinSameDay() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDateEmissionFin(LocalDate.now());

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, docs -> !docs.isEmpty());
    }

    @Test
    public void testFetchActualitesDateValiditeDebutAfter() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDateValiditeDebut(LocalDate.now().plusDays(1));

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, List::isEmpty);
    }

    @Test
    public void testFetchActualitesDateValiditeDebutSameDay() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDateValiditeDebut(LocalDate.now());

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, docs -> !docs.isEmpty());
    }

    @Test
    public void testFetchActualitesDateValiditeFinBefore() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDateValiditeFin(LocalDate.now().minusDays(1));

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, List::isEmpty);
    }

    @Test
    public void testFetchActualitesDateValiditeFinSameDay() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDateValiditeFin(LocalDate.now());

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, docs -> !docs.isEmpty());
    }

    @Test
    public void testFetchActualitesSansPiecesJointes() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setHasPj(false);

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, docs -> !docs.isEmpty());
    }

    @Test
    public void testFetchActualitesAvecPiecesJointes() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setHasPj(true);

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, List::isEmpty);
    }

    @Test
    public void testFetchActualitesDansHistorique() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDansHistorique(true);

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, List::isEmpty);
    }

    @Test
    public void testFetchActualitesPasDansHistorique() {
        DocumentModel actualiteDoc = newValidActualite();

        DocumentModel actualiteRequeteDoc = session.createDocumentModel(
            ActualiteConstant.ACTUALITE_REQUETE_DOCUMENT_TYPE
        );
        ActualiteRequete actualiteRequete = actualiteRequeteDoc.getAdapter(ActualiteRequete.class);
        actualiteRequete.setDansHistorique(false);

        testFetchActualitesPredicate(actualiteDoc, actualiteRequeteDoc, docs -> !docs.isEmpty());
    }

    public void testFetchActualitesPredicate(
        DocumentModel actualiteDoc,
        DocumentModel actualiteRequeteDoc,
        Predicate<List<DocumentModel>> test
    ) {
        // Given
        service.createActualite(session, actualiteDoc);
        session.save();

        // When
        List<DocumentModel> actualitesDocs = service
            .getActualitesPageProvider(session, actualiteRequeteDoc, Collections.emptyList(), 20, 0)
            .getCurrentPage();

        // Then
        assertThat(test.test(actualitesDocs)).isTrue();
    }
}
