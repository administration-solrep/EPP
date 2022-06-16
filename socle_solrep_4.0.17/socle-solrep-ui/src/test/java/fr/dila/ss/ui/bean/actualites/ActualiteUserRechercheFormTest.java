package fr.dila.ss.ui.bean.actualites;

import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_DATE_EMISSION;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_HASPJ;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_OBJET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Before;
import org.junit.Test;

public class ActualiteUserRechercheFormTest {
    private ActualiteUserRechercheForm dto;

    @Before
    public void setUp() throws Exception {
        dto = new ActualiteUserRechercheForm();
    }

    @Test
    public void testConstructor() {
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getSize()).isEqualTo(10);
        assertThat(dto.getDateEmissionDebut()).isNull();
        assertThat(dto.getDateEmissionFin()).isNull();
        assertThat(dto.getObjet()).isNull();
        assertThat(dto.getArchivee()).isTrue();
        assertThat(dto.getHasPj()).isNull();
        assertThat(dto.getDateEmissionSort()).isNull();
        assertThat(dto.getObjetSort()).isNull();
    }

    @Test
    public void testGetSortForm() {
        SortOrder dateEmissionSort = SortOrder.ASC;
        dto.setDateEmissionSort(dateEmissionSort);

        Map<String, FormSort> sortForm = dto.getSortForm();

        assertThat(sortForm)
            .extractingFromEntries(Entry::getKey, entry -> entry.getValue().getSortOrder())
            .containsExactlyInAnyOrder(
                tuple(ACTUALITE_PREFIX_XPATH_DATE_EMISSION, dateEmissionSort),
                tuple(ACTUALITE_PREFIX_XPATH_OBJET, null),
                tuple(ACTUALITE_PREFIX_XPATH_HASPJ, null)
            );
    }

    @Test
    public void testSetDateEmissionFin() {
        Calendar dateEmissionFin = new GregorianCalendar(2021, 1, 20);

        dto.setDateEmissionFin(dateEmissionFin);

        assertThat(dto.getDateEmissionFin()).isEqualTo(dateEmissionFin);
        assertThat(dto.getDateEmissionFinEffective()).isEqualTo(dateEmissionFin);
    }

    @Test
    public void testSetDateEmissionFinWithNullDate() {
        dto.setDateEmissionFin(null);

        assertThat(dto.getDateEmissionFin()).isNull();
        assertThat(SolonDateConverter.DATE_DASH.format(dto.getDateEmissionFinEffective()))
            .isEqualTo(SolonDateConverter.DATE_DASH.formatNow());
    }

    @Test
    public void testSetDefaultSort() {
        dto.setDefaultSort();

        assertThat(dto.getDateEmissionSort()).isEqualTo(SortOrder.DESC);
    }
}
