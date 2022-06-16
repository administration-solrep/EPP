package fr.dila.epp.ui.enumeration;

import static fr.dila.epp.ui.enumeration.EppActionCategory.MAIN_COMMUNICATION_EDIT;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class EppActionCategoryTest extends AbstractTestSortableEnum<EppActionCategory> {

    @Override
    protected Class<EppActionCategory> getEnumClass() {
        return EppActionCategory.class;
    }

    @Test
    public void getName() {
        assertThat(MAIN_COMMUNICATION_EDIT.getName()).isEqualTo("MAIN_COMMUNICATION_EDIT");
    }
}
