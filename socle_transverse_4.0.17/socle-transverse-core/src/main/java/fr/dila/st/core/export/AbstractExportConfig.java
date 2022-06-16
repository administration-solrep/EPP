package fr.dila.st.core.export;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.STExcelUtil;
import fr.dila.st.core.util.SolonDateConverter;
import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.text.AttributedString;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.activation.DataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.nuxeo.ecm.core.api.CoreSession;

public abstract class AbstractExportConfig<T> {
    private static final STLogger LOGGER = STLogFactory.getLog(AbstractExportConfig.class);

    private static final int LANDSCAPE_MAX_WIDTH = 31000;
    private static final int PORTRAIT_MAX_WIDTH = 22500;

    private List<T> items;
    private String[] headers;
    protected boolean landscape;
    private String sheetNameLabel;
    private final boolean isPdf;

    /**
     * Un tableau contenant les tailles de colonnes en pourcentage de largeur de page
     * (doit donc avoir une taille égale à la taille de *headers*).
     * <p>
     * Si une ou plusieurs valeures sont null les colonnes s'étendront pour le contenu.
     * Si le tableau est null, alors un tableau contenant des valeures null sera créé à l'instanciation.
     */
    private Double[] columnDynamicWidth;

    /**
     * Si `a` représente l'unité de mesure de la largeur d'une cellule excel
     * et `b` l'unité de mesure utilisé par java.awt.font.LineBreakMeasurer
     * alors `a` et `b` sont proportionnels d'un facteur approximatif à EXCEL_TO_JAVA_WIDTH_UNIT
     * <p>
     * Cette valeure est un peu plus grande que nécessaire pour prévoir des cas limites;
     * ce qui entaine des hauteurs un peu plus grandes que nécessaire dans certains cas.
     * <p>
     * (Calculé par "brute force" en testant plusierus valeures.)
     */
    private static final int EXCEL_TO_JAVA_WIDTH_UNIT = 45;
    /**
     * Excel (ou poi) limite la taille du nom d'un feuillet à 31 charactères.
     */
    private static final int MAX_SHEET_NAME_LENGTH = 31;

    protected AbstractExportConfig(
        List<T> items,
        String sheetName,
        String[] headers,
        Double[] columnDynamicWidth,
        boolean landscape,
        boolean isPdf
    ) {
        this.items = items;
        this.sheetNameLabel = sheetName.substring(0, Math.min(MAX_SHEET_NAME_LENGTH, sheetName.length()));
        this.headers = Arrays.stream(headers).map(ResourceHelper::getString).toArray(String[]::new);
        if (columnDynamicWidth != null) {
            this.columnDynamicWidth = columnDynamicWidth;
        } else {
            this.columnDynamicWidth = defaultColumnWidth(headers);
        }
        this.landscape = landscape;
        this.isPdf = isPdf;
    }

    private Double[] defaultColumnWidth(String[] headers) {
        return new Double[headers.length];
    }

    public DataSource getDataSource(CoreSession session) {
        DataSource fichierExcelResultat = null;
        try {
            HSSFWorkbook workbook = createExcelFile(session);

            fichierExcelResultat = STExcelUtil.convertExcelToDataSource(workbook);
        } catch (IOException exc) {
            LOGGER.error(session, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, exc);
        }
        return fichierExcelResultat;
    }

    private HSSFWorkbook createExcelFile(CoreSession session) {
        HSSFWorkbook workbook = initExcelFile();

        HSSFSheet sheet = workbook.getSheet(sheetNameLabel);
        int numRow = 1;

        for (T item : items) {
            addRowItem(session, sheet, item, numRow++);
        }

        formatWorkbookStyle(workbook);

        return workbook;
    }

    private HSSFWorkbook initExcelFile() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, sheetNameLabel);
        HSSFRow currentRow = sheet.createRow(0);
        STExcelUtil.addCellToRow(currentRow, 0, this.headers);

        return workbook;
    }

    private void addRowItem(CoreSession session, HSSFSheet sheet, T item, Integer numRow) {
        if (item != null) {
            HSSFRow currentRow = sheet.createRow(numRow);

            STExcelUtil.addCellToRow(currentRow, 0, getDataCells(session, item));

            formatRowStyle(sheet, currentRow);
        }
    }

    protected void formatWorkbookStyle(HSSFWorkbook workbook) {
        STExcelUtil.formatStyle(workbook, sheetNameLabel, headers.length, getPdfAbsoluteColumnWidths(), landscape);
    }

    protected void formatRowStyle(HSSFSheet sheet, HSSFRow row) {
        Integer[] widths = getPdfAbsoluteColumnWidths();

        // Uniquement dans le cas d'un export pdf pour un export avec une largeur de colonne fixe
        if (isPdf && ArrayUtils.isNotEmpty(widths)) {
            int rowMinHeight = -1;
            for (int i = 0; i < widths.length; i++) {
                HSSFCell cell = row.getCell(i);
                Integer width = widths[i];
                String cellValue = cell.getStringCellValue();

                if (width != null) { // Si la colonne à une taille fixe
                    // 1. On wrap son contenu
                    HSSFCellStyle style = sheet.getWorkbook().createCellStyle();
                    style.setWrapText(true);
                    style.setVerticalAlignment(VerticalAlignment.TOP);
                    cell.setCellStyle(style);

                    // 2. On calcul le nombre de ligne pour afficher le contenu de cette cellule selon sa largeur
                    if (StringUtils.isNotEmpty(cellValue)) {
                        // Doc: https://mail-archives.apache.org/mod_mbox/poi-user/200906.mbox/%3C24216153.post@talk.nabble.com%3E
                        // Create Font object with Font attribute (e.g. Font family, Font size, etc) for calculation
                        HSSFFont font = cell.getCellStyle().getFont(sheet.getWorkbook());
                        Font currFont = new Font(font.getFontName(), Font.PLAIN, font.getFontHeightInPoints());
                        AttributedString attrStr = new AttributedString(cellValue);
                        attrStr.addAttribute(TextAttribute.FONT, currFont);

                        // Use LineBreakMeasurer to count number of lines needed for the text
                        FontRenderContext frc = new FontRenderContext(null, true, true);
                        LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);
                        int nextPos;
                        int lineCnt = 0;
                        while (measurer.getPosition() < cellValue.length()) {
                            lineCnt++;
                            int wrappingWidth = width / EXCEL_TO_JAVA_WIDTH_UNIT;
                            nextPos = measurer.nextOffset(wrappingWidth);
                            measurer.setPosition(nextPos);
                        }
                        int cellheight = row.getHeight() * lineCnt;

                        // 3. On sauvegarde la hauteur minimal que doit avoir le row si supèrieur à la valeure actuelle
                        rowMinHeight = Math.max(rowMinHeight, cellheight);
                    }
                }
            }

            if (rowMinHeight > -1) {
                row.setHeight((short) rowMinHeight);
            }
        }
    }

    protected abstract String[] getDataCells(CoreSession session, T item);

    public String getSheetNameLabel() {
        return sheetNameLabel;
    }

    private static String formatCalendar(Calendar cal, Function<Date, String> formatDateFunction) {
        return Optional.ofNullable(cal).map(Calendar::getTime).map(formatDateFunction).orElse(StringUtils.EMPTY);
    }

    protected static String formatCalendar(Calendar cal, SimpleDateFormat sdf) {
        return formatCalendar(cal, sdf::format);
    }

    protected static String formatCalendarWithHour(Calendar cal) {
        return formatCalendar(cal, SolonDateConverter.DATETIME_SLASH_MINUTE_COLON::format);
    }

    private static String formatDate(Date date, Function<Date, String> formatDateFunction) {
        return Optional.ofNullable(date).map(formatDateFunction).orElse(StringUtils.EMPTY);
    }

    protected static String formatDateWithHour(Date date) {
        return formatDate(date, SolonDateConverter.DATETIME_SLASH_MINUTE_COLON::format);
    }

    private int getPdfPageWidth() {
        return landscape ? LANDSCAPE_MAX_WIDTH : PORTRAIT_MAX_WIDTH;
    }

    private Integer[] getPdfAbsoluteColumnWidths() {
        if (columnDynamicWidth == null) {
            return new Integer[0];
        }
        return Arrays
            .stream(columnDynamicWidth)
            .map(
                relativeValue -> {
                    if (relativeValue != null) {
                        return (int) (relativeValue * getPdfPageWidth());
                    } else {
                        return null;
                    }
                }
            )
            .toArray(Integer[]::new);
    }
}
