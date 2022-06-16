package fr.dila.ss.core.export.enums;

import static org.assertj.core.api.Assertions.assertThat;

import fr.dila.st.core.AbstractTestSortableEnum;
import fr.dila.st.core.export.enums.STExcelHeader;
import org.junit.Test;

public class SSExcelSheetNameTest extends AbstractTestSortableEnum<SSExcelSheetName> {

    @Override
    protected Class<SSExcelSheetName> getEnumClass() {
        return SSExcelSheetName.class;
    }

    @Test
    public void getLabel() {
        assertThat(SSExcelSheetName.JOURNAL_TECHNIQUE.getLabel()).isEqualTo("export.journal.technique.sheet.name");
    }

    @Test
    public void getHeaders() {
        assertThat(SSExcelSheetName.MIGRATION.getHeaders())
            .containsExactly(
                SSExcelHeader.MESSAGE,
                STExcelHeader.DATE_DEBUT,
                SSExcelHeader.DATE_FIN,
                SSExcelHeader.STATUT
            );
        assertThat(SSExcelSheetName.JOURNAL_TECHNIQUE.getHeaders())
            .containsExactly(
                SSExcelHeader.DATE,
                STExcelHeader.UTILISATEUR,
                STExcelHeader.POSTE,
                SSExcelHeader.CATEGORIE,
                SSExcelHeader.COMMENTAIRE,
                SSExcelHeader.DOSSIER
            );
    }

    @Test
    public void getHeadersSize() {
        assertThat(SSExcelSheetName.MIGRATION.getHeadersSize()).isEqualTo(4);
        assertThat(SSExcelSheetName.JOURNAL_TECHNIQUE.getHeadersSize()).isEqualTo(6);
    }

    @Test
    public void getHeadersLabels() {
        assertThat(SSExcelSheetName.MIGRATION.getHeadersLabels())
            .containsExactly(
                "export.header.message",
                "export.header.date.debut",
                "export.header.date.fin",
                "export.header.statut"
            );
    }
}
