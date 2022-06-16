package fr.dila.ss.core.export.enums;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class SSExcelHeaderTest extends AbstractTestSortableEnum<SSExcelHeader> {

    @Override
    protected Class<SSExcelHeader> getEnumClass() {
        return SSExcelHeader.class;
    }

    @Test
    public void getLabelKey() {
        assertThat(SSExcelHeader.DOSSIER.getLabel()).isEqualTo("export.header.dossier");
        assertThat(SSExcelHeader.DATE_CONNEXION.getLabel()).isEqualTo("export.header.date.connexion");
    }
}
