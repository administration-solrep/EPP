package fr.dila.ss.ui.enums;

import static fr.dila.ss.ui.enums.SSActionEnum.ADMIN_MENU_MODELE_RECHERCHE;
import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class SSActionEnumTest extends AbstractTestSortableEnum<SSActionEnum> {

    @Override
    protected Class<SSActionEnum> getEnumClass() {
        return SSActionEnum.class;
    }

    @Test
    public void getName() {
        assertThat(ADMIN_MENU_MODELE_RECHERCHE.getName()).isEqualTo("ADMIN_MENU_MODELE_RECHERCHE");
    }
}
