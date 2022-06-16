package fr.dila.ss.core.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.ss.core.dto.supervision.SupervisionUserDTO;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;

@RunWith(MockitoJUnitRunner.class)
public class SupervisionConfigTest {
    @Mock
    private SupervisionUserDTO item;

    @Mock
    private CoreSession session;

    @Test
    public void getDataCells() {
        String utilisateur = "utilisateur";
        when(item.getUtilisateur()).thenReturn(utilisateur);

        String prenom = "prenom";
        when(item.getPrenom()).thenReturn(prenom);

        String nom = "nom";
        when(item.getNom()).thenReturn(nom);

        Calendar dateConnexion = new GregorianCalendar(2021, Calendar.AUGUST, 5);
        when(item.getDateConnexion()).thenReturn(dateConnexion);

        SupervisionConfig supervisionConfig = new SupervisionConfig(ImmutableList.of(item), false);

        assertThat(supervisionConfig.getDataCells(session, item))
            .hasSize(supervisionConfig.getSheetName().getHeadersSize())
            .containsExactly(
                utilisateur,
                prenom,
                nom,
                SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.format(dateConnexion)
            );
    }
}
