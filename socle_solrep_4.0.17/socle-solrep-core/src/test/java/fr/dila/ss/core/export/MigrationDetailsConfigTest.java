package fr.dila.ss.core.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import fr.dila.ss.api.migration.MigrationDetailModel;
import fr.dila.st.core.util.SolonDateConverter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;

@RunWith(MockitoJUnitRunner.class)
public class MigrationDetailsConfigTest {
    @Mock
    private MigrationDetailModel item;

    @Mock
    private CoreSession session;

    @Test
    public void getDataCells() {
        Date startDate = new GregorianCalendar(2021, Calendar.AUGUST, 1).getTime();
        when(item.getStartDate()).thenReturn(startDate);

        Date endDate = new GregorianCalendar(2021, Calendar.AUGUST, 5).getTime();
        when(item.getEndDate()).thenReturn(endDate);

        String detail = "detail";
        when(item.getDetail()).thenReturn(detail);

        String statut = "statut";
        when(item.getStatut()).thenReturn(statut);

        MigrationDetailsConfig migrationDetailsConfig = new MigrationDetailsConfig(ImmutableList.of(item));

        assertThat(migrationDetailsConfig.getDataCells(session, item))
            .hasSize(migrationDetailsConfig.getSheetName().getHeadersSize())
            .containsExactly(
                detail,
                SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.format(startDate),
                SolonDateConverter.DATETIME_SLASH_MINUTE_COLON.format(endDate),
                statut
            );
    }
}
