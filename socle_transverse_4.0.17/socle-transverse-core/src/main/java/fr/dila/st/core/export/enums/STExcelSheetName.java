package fr.dila.st.core.export.enums;

import com.google.common.collect.ImmutableList;
import fr.dila.st.core.util.ResourceHelper;
import java.util.List;

public enum STExcelSheetName implements ExcelSheetName {
    IDENTIFIANT(
        ImmutableList.of(
            STExcelHeader.UTILISATEUR,
            STExcelHeader.TITRE,
            STExcelHeader.PRENOM,
            STExcelHeader.NOM,
            STExcelHeader.MEL
        )
    ),
    USER(
        ImmutableList.of(
            STExcelHeader.UTILISATEUR,
            STExcelHeader.NOM,
            STExcelHeader.PRENOM,
            STExcelHeader.MEL,
            STExcelHeader.TELEPHONE,
            STExcelHeader.DATE_DEBUT,
            STExcelHeader.POSTE
        )
    );

    private final List<ExcelHeader> headers;

    STExcelSheetName(List<ExcelHeader> headers) {
        this.headers = headers;
    }

    @Override
    public String getLabel() {
        return ResourceHelper.getString("export." + name().toLowerCase() + ".sheet.name");
    }

    @Override
    public List<ExcelHeader> getHeaders() {
        return headers;
    }
}
