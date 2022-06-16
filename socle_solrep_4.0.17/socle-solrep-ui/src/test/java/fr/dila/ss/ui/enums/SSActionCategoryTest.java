package fr.dila.ss.ui.enums;

import static fr.dila.ss.ui.enums.SSActionCategory.MODELE_FDR_STEP_ACTIONS;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class SSActionCategoryTest extends AbstractTestSortableEnum<SSActionCategory> {

    @Override
    protected Class<SSActionCategory> getEnumClass() {
        return SSActionCategory.class;
    }

    @Test
    public void getName() {
        assertThat(MODELE_FDR_STEP_ACTIONS.getName()).isEqualTo("MODELE_FDR_STEP_ACTIONS");
    }
}
