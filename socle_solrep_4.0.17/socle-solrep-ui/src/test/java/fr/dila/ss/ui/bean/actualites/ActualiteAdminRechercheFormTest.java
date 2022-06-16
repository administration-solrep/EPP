package fr.dila.ss.ui.bean.actualites;

import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_DANS_HISTORIQUE;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_DATE_EMISSION;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_DATE_VALIDITE;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_HASPJ;
import static fr.dila.ss.api.constant.ActualiteConstant.ACTUALITE_PREFIX_XPATH_OBJET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import java.util.Map;
import java.util.Map.Entry;
import org.junit.Before;
import org.junit.Test;

public class ActualiteAdminRechercheFormTest {
    private ActualiteAdminRechercheForm dto;

    @Before
    public void setUp() throws Exception {
        dto = new ActualiteAdminRechercheForm();
    }

    @Test
    public void testConstructor() {
        assertThat(dto.getDateFinValiditeDebut()).isNull();
        assertThat(dto.getDateFinValiditeFin()).isNull();
        assertThat(dto.getDateFinValiditeSort()).isNull();
        assertThat(dto.getStatusSort()).isNull();
        assertThat(dto.getPage()).isEqualTo(1);
        assertThat(dto.getSize()).isEqualTo(20);
        assertThat(dto.getDateEmissionDebut()).isNull();
        assertThat(dto.getDateEmissionFin()).isNull();
        assertThat(dto.getObjet()).isNull();
        assertThat(dto.getArchivee()).isNull();
        assertThat(dto.getHasPj()).isNull();
        assertThat(dto.getDateEmissionSort()).isNull();
        assertThat(dto.getObjetSort()).isNull();
    }

    @Test
    public void testGetSortForm() {
        SortOrder dateEmissionSort = SortOrder.ASC;
        dto.setDateEmissionSort(dateEmissionSort);

        SortOrder dateFinValiditeSort = SortOrder.DESC;
        dto.setDateFinValiditeSort(dateFinValiditeSort);

        SortOrder statusSort = SortOrder.ASC;
        dto.setStatusSort(statusSort);

        Map<String, FormSort> sortForm = dto.getSortForm();

        assertThat(sortForm)
            .extractingFromEntries(Entry::getKey, entry -> entry.getValue().getSortOrder())
            .containsExactlyInAnyOrder(
                tuple(ACTUALITE_PREFIX_XPATH_DATE_EMISSION, dateEmissionSort),
                tuple(ACTUALITE_PREFIX_XPATH_DATE_VALIDITE, dateFinValiditeSort),
                tuple(ACTUALITE_PREFIX_XPATH_OBJET, null),
                tuple(ACTUALITE_PREFIX_XPATH_HASPJ, null),
                tuple(ACTUALITE_PREFIX_XPATH_DANS_HISTORIQUE, statusSort)
            );
    }

    @Test
    public void testSetDefaultSort() {
        dto.setDefaultSort();

        assertThat(dto.getDateEmissionSort()).isEqualTo(SortOrder.DESC);
    }
}
