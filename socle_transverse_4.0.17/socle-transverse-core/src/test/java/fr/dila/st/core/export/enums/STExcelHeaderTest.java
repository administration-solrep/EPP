package fr.dila.st.core.export.enums;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class STExcelHeaderTest extends AbstractTestSortableEnum<STExcelHeader> {

    @Override
    protected Class<STExcelHeader> getEnumClass() {
        return STExcelHeader.class;
    }

    @Test
    public void getLabelKey() {
        assertThat(STExcelHeader.POSTE.getLabel()).isEqualTo("export.header.poste");
        assertThat(STExcelHeader.DATE_DEBUT.getLabel()).isEqualTo("export.header.date.debut");
    }
}
