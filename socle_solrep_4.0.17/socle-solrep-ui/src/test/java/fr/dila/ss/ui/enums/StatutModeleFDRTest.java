package fr.dila.ss.ui.enums;

import static fr.dila.ss.core.enumeration.StatutModeleFDR.BROUILLON;
import static fr.dila.ss.core.enumeration.StatutModeleFDR.VALIDATION_DEMANDEE;
import static fr.dila.ss.core.enumeration.StatutModeleFDR.VALIDE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.ss.api.criteria.FeuilleRouteCriteria;
import fr.dila.ss.api.feuilleroute.SSFeuilleRoute;
import fr.dila.ss.core.enumeration.StatutModeleFDR;
import fr.sword.idl.naiad.nuxeo.feuilleroute.api.adapter.FeuilleRouteElement.ElementLifeCycleState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.DocumentModel;

@RunWith(MockitoJUnitRunner.class)
public class StatutModeleFDRTest {
    private static final String INVALID_VALUE = "EN_COURS";

    @Mock
    private DocumentModel doc;

    @Mock
    private SSFeuilleRoute fdr;

    @Test
    public void getStatutFromDocShouldReturnValidee() {
        when(doc.getAdapter(SSFeuilleRoute.class)).thenReturn(fdr);
        when(fdr.isValidated()).thenReturn(true);
        when(fdr.isDemandeValidation()).thenReturn(false);
        when(fdr.isDraft()).thenReturn(false);

        assertThat(StatutModeleFDR.getStatutFromDoc(doc)).isEqualTo(VALIDE.name());
    }

    @Test
    public void getStatutFromDocShouldReturnValidationDemandee() {
        when(doc.getAdapter(SSFeuilleRoute.class)).thenReturn(fdr);
        when(fdr.isValidated()).thenReturn(false);
        when(fdr.isDemandeValidation()).thenReturn(true);
        when(fdr.isDraft()).thenReturn(false);

        assertThat(StatutModeleFDR.getStatutFromDoc(doc)).isEqualTo(VALIDATION_DEMANDEE.name());
    }

    @Test
    public void getStatutFromDocShouldReturnBrouillon() {
        when(doc.getAdapter(SSFeuilleRoute.class)).thenReturn(fdr);
        when(fdr.isValidated()).thenReturn(false);
        when(fdr.isDemandeValidation()).thenReturn(false);
        when(fdr.isDraft()).thenReturn(true);

        assertThat(StatutModeleFDR.getStatutFromDoc(doc)).isEqualTo(BROUILLON.name());
    }

    @Test
    public void fromValue() {
        StatutModeleFDR statut = StatutModeleFDR.fromValue("VALIDATION_DEMANDEE");

        assertThat(statut).isEqualTo(VALIDATION_DEMANDEE);
    }

    @Test
    public void fromValueWithInvalidValue() {
        StatutModeleFDR statut = StatutModeleFDR.fromValue(INVALID_VALUE);

        assertThat(statut).isNull();
    }

    @Test
    public void updateFeuilleRouteCriteriaFromStatutValidee() {
        FeuilleRouteCriteria criteria = new FeuilleRouteCriteria();
        VALIDE.updateFeuilleRouteCriteria(criteria);
        assertThat(criteria.getCurrentLifeCycleState()).isEqualTo(ElementLifeCycleState.validated.name());
        assertThat(criteria.isDemandeValidation()).isNull();
    }

    @Test
    public void updateFeuilleRouteCriteriaFromStatutValidationDemandee() {
        FeuilleRouteCriteria criteria = new FeuilleRouteCriteria();
        VALIDATION_DEMANDEE.updateFeuilleRouteCriteria(criteria);
        assertThat(criteria.getCurrentLifeCycleState()).isNull();
        assertThat(criteria.isDemandeValidation()).isTrue();
    }

    @Test
    public void updateFeuilleRouteCriteriaFromStatutBrouillon() {
        FeuilleRouteCriteria criteria = new FeuilleRouteCriteria();
        BROUILLON.updateFeuilleRouteCriteria(criteria);
        assertThat(criteria.getCurrentLifeCycleState()).isEqualTo(ElementLifeCycleState.draft.name());
        assertThat(criteria.isDemandeValidation()).isFalse();
    }
}
