package fr.dila.ss.core.export;

import fr.dila.ss.core.util.SSExcelUtil;
import fr.dila.st.core.export.AbstractEnumExportConfig;
import fr.dila.st.core.export.enums.ExcelSheetName;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

public abstract class AbstractHeaderWithBorderExportConfig<T> extends AbstractEnumExportConfig<T> {

    protected AbstractHeaderWithBorderExportConfig(List<T> items, ExcelSheetName sheetName) {
        super(items, sheetName);
    }

    protected AbstractHeaderWithBorderExportConfig(List<T> items, ExcelSheetName sheetName, boolean isPdf) {
        super(items, sheetName, isPdf);
    }

    @Override
    protected void formatWorkbookStyle(HSSFWorkbook workbook) {
        super.formatWorkbookStyle(workbook);

        setCellBorderStyle(workbook.getSheet(getSheetNameLabel()).getRow(0));
    }

    @Override
    protected void formatRowStyle(HSSFSheet sheet, HSSFRow row) {
        super.formatRowStyle(sheet, row);
        setCellBorderStyle(row);
    }

    private static void setCellBorderStyle(HSSFRow row) {
        row.forEach(cell -> SSExcelUtil.setBorder(cell, BorderStyle.THIN, IndexedColors.BLACK.getIndex()));
    }
}
