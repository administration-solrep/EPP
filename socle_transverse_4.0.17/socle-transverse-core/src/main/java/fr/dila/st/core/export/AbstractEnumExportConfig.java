package fr.dila.st.core.export;

import fr.dila.st.core.export.enums.ExcelSheetName;
import java.util.List;

public abstract class AbstractEnumExportConfig<T> extends AbstractExportConfig<T> {
    private final ExcelSheetName sheetName;

    protected AbstractEnumExportConfig(List<T> items, ExcelSheetName sheetName) {
        this(items, sheetName, false);
    }

    protected AbstractEnumExportConfig(List<T> items, ExcelSheetName sheetName, boolean isPdf) {
        super(
            items,
            sheetName.getLabel(),
            sheetName.getHeadersLabelKeys(),
            sheetName.getPdfDynamicColumnWidths(),
            sheetName.isPdfLandscape(),
            isPdf
        );
        this.sheetName = sheetName;
    }

    public ExcelSheetName getSheetName() {
        return this.sheetName;
    }
}
