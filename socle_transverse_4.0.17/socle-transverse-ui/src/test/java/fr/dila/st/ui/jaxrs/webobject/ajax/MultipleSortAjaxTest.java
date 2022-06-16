package fr.dila.st.ui.jaxrs.webobject.ajax;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class MultipleSortAjaxTest {

    /**
     * Cas standard
     */
    @Test
    public void test_addColumn_ok() {
        MultipleSortAjax multipleSortAjax = new MultipleSortAjax();

        List<String> sortableColumnsId = ImmutableList.of("A", "B", "C", "D", "E");
        List<String> sortableColumnsLabel = ImmutableList.of("a", "b", "c", "d", "e");

        ThTemplate template = multipleSortAjax.addColumn(sortableColumnsId, sortableColumnsLabel, 1);

        assertThat(template.getName()).isNotBlank();

        Map<String, Object> map = template.getData();
        assertThat(map.get(MultipleSortAjax.MAP_KEY_LST_SORTABLE_COLUMN)).asList().hasSize(sortableColumnsId.size());
        assertThat(map.get(MultipleSortAjax.MAP_KEY_SORT_VALUE)).isEqualTo(SortOrder.ASC.getValue());
    }

    /**
     * Vérification de la protection contre la faille de sécurité CWE-606: Unchecked
     * Input for Loop Condition
     */
    @Test
    public void test_addColumn_cwe606() {
        MultipleSortAjax multipleSortAjax = new MultipleSortAjax();

        List<String> sortableColumnsId = new ArrayList<>();
        List<String> sortableColumnsLabel = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            sortableColumnsId.add("id_" + i);
            sortableColumnsLabel.add("label_" + i);
        }

        ThTemplate template = multipleSortAjax.addColumn(sortableColumnsId, sortableColumnsLabel, 1);

        assertThat(template.getName()).isNotBlank();
        Map<String, Object> map = template.getData();
        assertThat(map.get(MultipleSortAjax.MAP_KEY_LST_SORTABLE_COLUMN))
            .asList()
            .hasSize(MultipleSortAjax.MAX_SORTABLE_COLUMNS);
        assertThat(map.get(MultipleSortAjax.MAP_KEY_SORT_VALUE)).isEqualTo(SortOrder.ASC.getValue());
    }
}
