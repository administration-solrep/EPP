package fr.dila.st.ui.enums;

import static fr.dila.st.ui.enums.STActionEnum.ADMIN_USER_ACCES;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class STActionEnumTest extends AbstractTestSortableEnum<STActionEnum> {

    @Override
    protected Class<STActionEnum> getEnumClass() {
        return STActionEnum.class;
    }

    @Test
    public void getName() {
        assertThat(ADMIN_USER_ACCES.getName()).isEqualTo("ADMIN_USER_ACCES");
    }
}
