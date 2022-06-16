package fr.dila.ss.core.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import fr.dila.ss.api.actualite.Actualite;
import fr.dila.ss.api.service.ActualiteService;
import fr.dila.st.api.constant.STBaseFunctionConstant;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.exception.STValidationException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRule;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.api.impl.DocumentModelImpl;

public class ActualiteServiceImpTest {
    @Rule
    public MockitoJUnitRule rule = new MockitoJUnitRule(this);

    private ActualiteService service = new ActualiteServiceImpl();

    @Mock
    private CoreSession session;

    @Mock
    private NuxeoPrincipal principal;

    private Actualite goodActualite() {
        Actualite actualite = mock(Actualite.class);
        when(actualite.getDateEmission()).thenReturn(LocalDate.now());
        when(actualite.getDateValidite()).thenReturn(LocalDate.now());
        when(actualite.getIsInHistorique()).thenReturn(true);
        when(actualite.getObjet()).thenReturn("objet");
        when(actualite.getContenu()).thenReturn("contenu");
        return actualite;
    }

    @Test
    public void testCreateActualiteHasAuthorization() {
        // Given
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.isMemberOf(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(true);

        DocumentModel actualiteDoc = mock(DocumentModelImpl.class);
        Mockito.doCallRealMethod().when(actualiteDoc).getProperty(Mockito.any(), Mockito.any());
        Mockito.doCallRealMethod().when(actualiteDoc).setProperty(Mockito.any(), Mockito.any(), Mockito.any());

        Actualite actualite = goodActualite();
        when(actualiteDoc.getAdapter(Actualite.class)).thenReturn(actualite);

        // When
        Throwable throwable = Assertions.catchThrowable(() -> service.createActualite(session, actualiteDoc));

        // Then
        Assertions.assertThat(throwable).isNull();
    }

    @Test
    public void testCreateActualiteNoAuthorization() {
        // Given
        when(session.getPrincipal()).thenReturn(principal);

        DocumentModel actualiteDoc = mock(DocumentModel.class);

        Actualite actualite = goodActualite();
        when(actualiteDoc.getAdapter(Actualite.class)).thenReturn(actualite);

        // When
        Throwable throwable = Assertions.catchThrowable(() -> service.createActualite(session, actualiteDoc));

        // Then
        Assertions.assertThat(throwable).isInstanceOf(STAuthorizationException.class);
    }

    @Test
    public void testCreateActualiteDateEmissionRequired() {
        testInvalidActualite(act -> when(act.getDateEmission()).thenReturn(null));
    }

    @Test
    public void testCreateActualiteDateValiditeRequired() {
        testInvalidActualite(act -> when(act.getDateValidite()).thenReturn(null));
    }

    @Test
    public void testCreateActualiteDateValiditeAfterDateEmission() {
        testInvalidActualite(
            act -> {
                when(act.getDateValidite()).thenReturn(LocalDate.of(2010, 01, 01));
            }
        );
    }

    @Test
    public void testCreateActualiteObjetRequired() {
        testInvalidActualite(act -> when(act.getObjet()).thenReturn(null));
    }

    @Test
    public void testCreateActualiteContenuRequired() {
        testInvalidActualite(act -> when(act.getContenu()).thenReturn(null));
    }

    @Test
    public void testCreateActualiteFilesEmptyIfNotInHistorique() {
        testInvalidActualite(
            act -> {
                when(act.getIsInHistorique()).thenReturn(false);
                when(act.getPiecesJointes())
                    .thenReturn(Arrays.asList(ImmutableMap.<String, Serializable>of("cle", "file")));
            }
        );
    }

    private void testInvalidActualite(Consumer<Actualite> invalidation) {
        // Given
        when(session.getPrincipal()).thenReturn(principal);
        when(principal.isMemberOf(STBaseFunctionConstant.ADMIN_FONCTIONNEL_GROUP_NAME)).thenReturn(true);

        DocumentModel actualiteDoc = mock(DocumentModel.class);
        Actualite actualite = goodActualite();
        when(actualiteDoc.getAdapter(Actualite.class)).thenReturn(actualite);

        invalidation.accept(actualite);

        // When
        Throwable validation = Assertions.catchThrowable(() -> service.createActualite(session, actualiteDoc));

        // Then
        Assertions.assertThat(validation).isInstanceOf(STValidationException.class);
    }
}
