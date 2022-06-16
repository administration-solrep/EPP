package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "MultipleSortAjax")
public class MultipleSortAjax extends SolonWebObject {
    public static final String MAP_KEY_LST_SORTABLE_COLUMN = "lstSortableColumn";
    public static final String MAP_KEY_INDEX = "index";
    public static final String MAP_KEY_SORT_VALUE = "sortValue";

    /**
     * On instaure un maximum arbitraire à ne pas dépasser pour le nombre de colonnes à trier
     * afin d'éviter une faille de type CWE-606: Unchecked Input for Loop Condition
     */
    protected static final int MAX_SORTABLE_COLUMNS = 100;

    public MultipleSortAjax() {
        super();
    }

    @POST
    @Path("colonne/ajouter")
    public ThTemplate addColumn(
        @FormParam("sortableColumnsId[]") List<String> sortableColumnsId,
        @FormParam("sortableColumnsLabel[]") List<String> sortableColumnsLabel,
        @FormParam("index") int index
    ) {
        List<SelectValueDTO> lstSortableColumn = new ArrayList<>();

        if (
            CollectionUtils.isNotEmpty(sortableColumnsId) &&
            CollectionUtils.isNotEmpty(sortableColumnsLabel) &&
            sortableColumnsId.size() == sortableColumnsLabel.size()
        ) {
            lstSortableColumn =
                IntStream
                    .range(0, sortableColumnsId.size())
                    .limit(MAX_SORTABLE_COLUMNS)
                    .mapToObj(i -> new SelectValueDTO(sortableColumnsId.get(i), sortableColumnsLabel.get(i)))
                    .collect(Collectors.toList());
        }

        ThTemplate template = new AjaxLayoutThTemplate();
        template.setName("fragments/table/multipleSortColumn");

        Map<String, Object> map = new HashMap<>();
        map.put(MAP_KEY_LST_SORTABLE_COLUMN, lstSortableColumn);
        map.put(MAP_KEY_INDEX, index);
        map.put(MAP_KEY_SORT_VALUE, SortOrder.ASC.getValue());
        template.setData(map);

        template.setContext(context);

        return template;
    }
}
