package fr.dila.ss.core.export.enums;

import com.google.common.collect.ImmutableList;
import fr.dila.st.core.export.enums.ExcelHeader;
import fr.dila.st.core.export.enums.ExcelSheetName;
import fr.dila.st.core.export.enums.STExcelHeader;
import fr.dila.st.core.util.ResourceHelper;
import java.util.List;
import java.util.Optional;

public enum SSExcelSheetName implements ExcelSheetName {
    JOURNAL_TECHNIQUE(
        "journal.technique",
        ImmutableList.of(
            SSExcelHeader.DATE,
            STExcelHeader.UTILISATEUR,
            STExcelHeader.POSTE,
            SSExcelHeader.CATEGORIE,
            SSExcelHeader.COMMENTAIRE,
            SSExcelHeader.DOSSIER
        )
    ),
    MIGRATION(
        ImmutableList.of(SSExcelHeader.MESSAGE, STExcelHeader.DATE_DEBUT, SSExcelHeader.DATE_FIN, SSExcelHeader.STATUT)
    ),
    SUPERVISION(
        ImmutableList.of(
            STExcelHeader.UTILISATEUR,
            STExcelHeader.PRENOM,
            STExcelHeader.NOM,
            SSExcelHeader.DATE_CONNEXION
        ),
        new Double[] { null, null, .3 },
        true
    ),
    USER_ADMIN(
        "user",
        ImmutableList.of(
            STExcelHeader.UTILISATEUR,
            STExcelHeader.NOM,
            STExcelHeader.PRENOM,
            STExcelHeader.MEL,
            STExcelHeader.TELEPHONE,
            STExcelHeader.DATE_DEBUT,
            SSExcelHeader.MINISTERE,
            SSExcelHeader.DIRECTION,
            STExcelHeader.POSTE,
            SSExcelHeader.DATE_DERNIERE_CONNEXION
        )
    ),
    USER_SIMPLE(
        "user",
        ImmutableList.of(
            STExcelHeader.UTILISATEUR,
            STExcelHeader.NOM,
            STExcelHeader.PRENOM,
            STExcelHeader.MEL,
            STExcelHeader.TELEPHONE,
            STExcelHeader.DATE_DEBUT,
            SSExcelHeader.MINISTERE,
            SSExcelHeader.DIRECTION,
            STExcelHeader.POSTE
        )
    );

    private final String labelKey;
    private final List<ExcelHeader> headers;
    private final Double[] columnWidths;
    private final boolean landscape;

    SSExcelSheetName(List<ExcelHeader> headers) {
        this(null, headers);
    }

    SSExcelSheetName(List<ExcelHeader> headers, Double[] columnWidths, boolean landscape) {
        this(null, headers, columnWidths, landscape);
    }

    SSExcelSheetName(String labelKey, List<ExcelHeader> headers) {
        this(labelKey, headers, null, false);
    }

    SSExcelSheetName(String labelKey, List<ExcelHeader> headers, Double[] columnWidths, boolean landscape) {
        this.labelKey = labelKey;
        this.headers = headers;
        this.columnWidths = columnWidths;
        this.landscape = landscape;
    }

    @Override
    public String getLabel() {
        return ResourceHelper.getString(
            "export." + Optional.ofNullable(labelKey).orElse(name().toLowerCase()) + ".sheet.name"
        );
    }

    @Override
    public List<ExcelHeader> getHeaders() {
        return headers;
    }

    @Override
    public Double[] getPdfDynamicColumnWidths() {
        return this.columnWidths;
    }

    @Override
    public boolean isPdfLandscape() {
        return landscape;
    }
}
