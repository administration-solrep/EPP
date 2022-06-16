package fr.dila.st.core.export.enums;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import org.junit.Test;

public class STExcelSheetNameTest extends AbstractTestSortableEnum<STExcelSheetName> {

    @Override
    protected Class<STExcelSheetName> getEnumClass() {
        return STExcelSheetName.class;
    }

    @Test
    public void getLabel() {
        assertThat(STExcelSheetName.IDENTIFIANT.getLabel()).isEqualTo("export.identifiant.sheet.name");
    }

    @Test
    public void getHeaders() {
        assertThat(STExcelSheetName.IDENTIFIANT.getHeaders())
            .containsExactly(
                STExcelHeader.UTILISATEUR,
                STExcelHeader.TITRE,
                STExcelHeader.PRENOM,
                STExcelHeader.NOM,
                STExcelHeader.MEL
            );
        assertThat(STExcelSheetName.USER.getHeaders())
            .containsExactly(
                STExcelHeader.UTILISATEUR,
                STExcelHeader.NOM,
                STExcelHeader.PRENOM,
                STExcelHeader.MEL,
                STExcelHeader.TELEPHONE,
                STExcelHeader.DATE_DEBUT,
                STExcelHeader.POSTE
            );
    }

    @Test
    public void getHeadersSize() {
        assertThat(STExcelSheetName.IDENTIFIANT.getHeadersSize()).isEqualTo(5);
        assertThat(STExcelSheetName.USER.getHeadersSize()).isEqualTo(7);
    }

    @Test
    public void getHeadersLabels() {
        assertThat(STExcelSheetName.IDENTIFIANT.getHeadersLabels())
            .containsExactly(
                "export.header.utilisateur",
                "export.header.titre",
                "export.header.prenom",
                "export.header.nom",
                "export.header.mel"
            );
    }
}
