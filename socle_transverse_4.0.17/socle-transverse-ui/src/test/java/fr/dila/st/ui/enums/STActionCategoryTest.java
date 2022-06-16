package fr.dila.st.ui.enums;

import static fr.dila.st.ui.enums.STActionCategory.PASTE_STEP_ACTIONS_LIST;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class STActionCategoryTest extends AbstractTestSortableEnum<STActionCategory> {

    @Override
    protected Class<STActionCategory> getEnumClass() {
        return STActionCategory.class;
    }

    @Test
    public void getName() {
        assertThat(PASTE_STEP_ACTIONS_LIST.getName()).isEqualTo("PASTE_STEP_ACTIONS_LIST");
    }
}
