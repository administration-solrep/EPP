package fr.dila.epp.ui.th.bean;

import com.google.common.collect.ImmutableMap;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.st.ui.bean.FormSort;
import fr.dila.st.ui.enums.SortOrder;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class MessageListFormTest {
    private static final String XPATH_FORMAT = "%s.%s:%s";

    private static final String EVENEMENT_ALIAS = "e";
    private static final String VERSION_ALIAS = "v";

    @Test
    public void testGetSortFormMapOk() {
        MessageListForm form = new MessageListForm();
        form.initSort();

        form.setCommunication(SortOrder.ASC);
        form.setCopie(SortOrder.DESC);
        form.setDate(null);
        form.setDestinataire(SortOrder.ASC);
        form.setEmetteur(SortOrder.DESC);
        form.setIdDossier(null);
        form.setObjetDossier(SortOrder.ASC);

        Map<String, FormSort> actual = form.getSortForm();
        Assertions
            .assertThat(actual)
            .containsExactlyInAnyOrderEntriesOf(
                ImmutableMap.of(
                    String.format(
                        XPATH_FORMAT,
                        EVENEMENT_ALIAS,
                        SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                        SolonEppSchemaConstant.EVENEMENT_TYPE_EVENEMENT_PROPERTY
                    ),
                    new FormSort(SortOrder.ASC),
                    String.format(
                        XPATH_FORMAT,
                        EVENEMENT_ALIAS,
                        SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                        SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_COPIE_CONCAT_PROPERTY
                    ),
                    new FormSort(SortOrder.DESC),
                    String.format(
                        XPATH_FORMAT,
                        EVENEMENT_ALIAS,
                        SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                        SolonEppSchemaConstant.EVENEMENT_DESTINATAIRE_PROPERTY
                    ),
                    new FormSort(SortOrder.ASC),
                    String.format(
                        XPATH_FORMAT,
                        EVENEMENT_ALIAS,
                        SolonEppSchemaConstant.EVENEMENT_SCHEMA_PREFIX,
                        SolonEppSchemaConstant.EVENEMENT_EMETTEUR_PROPERTY
                    ),
                    new FormSort(SortOrder.DESC),
                    String.format(
                        XPATH_FORMAT,
                        VERSION_ALIAS,
                        SolonEppSchemaConstant.VERSION_SCHEMA_PREFIX,
                        SolonEppSchemaConstant.VERSION_OBJET_PROPERTY
                    ),
                    new FormSort(SortOrder.ASC)
                )
            );
    }
}
