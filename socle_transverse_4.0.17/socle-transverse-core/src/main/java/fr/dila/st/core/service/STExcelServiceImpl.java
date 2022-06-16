package fr.dila.st.core.service;

import fr.dila.st.api.service.STExcelService;
import fr.dila.st.core.util.ObjectHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class STExcelServiceImpl implements STExcelService {

    @Override
    public HSSFWorkbook initExcelFile(String sheetName, String... header) {
        // création du fichier Excel
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, sheetName);
        // création des colonnes header
        HSSFRow currentRow = sheet.createRow(0);
        addCellToRow(currentRow, 0, header);

        return workbook;
    }

    @Override
    public void addCellToRow(HSSFRow row, int numColonneStart, String... dataCells) {
        dataCells = ObjectHelper.requireNonNullElseGet(dataCells, () -> new String[0]);

        int numColonne = numColonneStart;
        for (String dataCell : dataCells) {
            row.createCell(numColonne++).setCellValue(dataCell);
        }
    }
}
