package fr.dila.ss.core.util;

import static fr.dila.ss.api.constant.SSFondDeDossierConstants.FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE;

import fr.dila.ss.api.pdf.Cell;
import fr.dila.ss.core.pdf.ImageCell;
import fr.dila.ss.core.pdf.TextCell;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.core.util.SolonDateConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTable.XWPFBorderType;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.jsoup.Jsoup;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class SSPdfUtil {
    public static final String FOND_BLEU = "5c75a2";
    public static final String FOND_BLANC = "ffffff";
    public static final String FOND_HEADER = "cccccc";

    public static final int SIZE_TITRE1 = 18;
    public static final int SIZE_TITRE2 = 16;
    public static final int SIZE_TITRE3 = 14;
    public static final int SIZE_CONTENU = 12;

    private static final int FORMAT_A4_WIDTH_PAYSAGE = 842;
    private static final int FORMAT_A4_HEIGHT_PAYSAGE = 595;
    private static final int FORMAT_A4_MARGIN = 36;
    private static final int CONSTANCE_POINT = 20;

    public static final int FORMAT_A4_WIDTH_PAYSAGE_SIZE = FORMAT_A4_WIDTH_PAYSAGE * CONSTANCE_POINT;
    public static final int FORMAT_A4_HEIGHT_PAYSAGE_SIZE = FORMAT_A4_HEIGHT_PAYSAGE * CONSTANCE_POINT;
    public static final int FORMAT_A4_MARGIN_SIZE = FORMAT_A4_MARGIN * CONSTANCE_POINT;

    protected static void addTableTitle(XWPFDocument document, String title) {
        createTitleRun(document, SIZE_TITRE3, title);
    }

    public static void addTitle(XWPFDocument document, String title, int size) {
        createTitleRun(document, size, title);
    }

    protected static void addText(XWPFDocument document, String text, int size) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);

        createRun(paragraph, false, size, text);
    }

    public static void addTabTitle(XWPFDocument document, String title) {
        XWPFRun run = createTitleRun(document, SIZE_TITRE2, title);
        run.addBreak();
    }

    private static XWPFRun createTitleRun(XWPFDocument document, int fontSize, String title) {
        XWPFParagraph paragraph = document.createParagraph();
        return createRun(paragraph, true, fontSize, title);
    }

    private static XWPFRun createRun(XWPFParagraph paragraph, boolean isBold, int fontSize, String title) {
        XWPFRun run = paragraph.createRun();
        run.setBold(isBold);
        run.setFontSize(fontSize);
        run.setText(title);
        return run;
    }

    protected static XWPFTable createTable(XWPFDocument document) {
        XWPFTable table = document.createTable();

        CTTblPr tblpro = table.getCTTbl().getTblPr();

        CTTblBorders borders = tblpro.addNewTblBorders();
        borders.addNewLeft().setVal(STBorder.NONE);
        borders.addNewRight().setVal(STBorder.NONE);

        table.setInsideVBorder(XWPFBorderType.NONE, 1, 1, "");

        return table;
    }

    public static XWPFTable createInvisibleTable(XWPFDocument document) {
        XWPFTable table = createTable(document);

        CTTblPr tblpro = table.getCTTbl().getTblPr();

        CTTblBorders borders = tblpro.addNewTblBorders();
        borders.addNewBottom().setVal(STBorder.NONE);
        borders.addNewTop().setVal(STBorder.NONE);

        table.setInsideHBorder(XWPFBorderType.NONE, 1, 1, "");

        return table;
    }

    public static XWPFTable addLeftIndentation(XWPFTable table) {
        CTTblWidth tableIndentation = table.getCTTbl().getTblPr().addNewTblInd();
        tableIndentation.setW(BigInteger.valueOf(FORMAT_A4_MARGIN_SIZE));
        tableIndentation.setType(STTblWidth.DXA);

        return table;
    }

    public static void addBreakPage(XWPFDocument document) {
        document.createParagraph().createRun().addBreak(BreakType.PAGE);
    }

    public static void addTableRow(XWPFTable table, boolean first, String color, Cell... cells) {
        XWPFTableCell cell;
        XWPFTableRow tableRow = first ? table.getRow(0) : table.createRow();
        tableRow.setCantSplitRow(true);
        int cellIndex = 0;
        for (Cell c : cells) {
            if (cellIndex == 0) {
                cell = tableRow.getCell(cellIndex);
            } else {
                if (first) {
                    cell = tableRow.addNewTableCell();
                } else {
                    cell = tableRow.getCell(cellIndex);
                }
            }

            XWPFParagraph paragraph = CollectionUtils.isEmpty(cell.getParagraphs())
                ? cell.addParagraph()
                : cell.getParagraphs().get(0);
            XWPFRun run = paragraph.createRun();
            if (color != null) {
                cell.setColor(color);
                if (color.equals(FOND_BLEU)) {
                    run.setColor(FOND_BLANC);
                }
            }

            c.display(paragraph, run);

            cellIndex++;
        }
    }

    public static void addTableRow(XWPFTable table, boolean first, Cell... cells) {
        addTableRow(table, first, null, cells);
    }

    protected static void hideTableInnerBorders(XWPFTable table) {
        table
            .getRows()
            .forEach(
                row ->
                    row
                        .getTableCells()
                        .forEach(
                            cell -> {
                                CTTcBorders tblBorders = cell.getCTTc().getTcPr().addNewTcBorders();

                                tblBorders.addNewRight().setVal(STBorder.NIL);

                                tblBorders.addNewLeft().setVal(STBorder.NIL);
                            }
                        )
            );
    }

    public static TextCell ajoutTexteList(List<String> values) {
        String val = "";
        if (CollectionUtils.isNotEmpty(values)) {
            val = StringUtils.join(values, ",");
        }
        return new TextCell(val, false);
    }

    protected static String listToString(List<String> values) {
        return ObjectHelper.requireNonNullElse(StringUtils.join(values, ","), "");
    }

    public static TextCell ajoutBooleen(Boolean value) {
        String textValue = BooleanUtils.isTrue(value)
            ? ResourceHelper.getString("pdf.label.oui")
            : ResourceHelper.getString("pdf.label.non");
        return new TextCell(textValue, false);
    }

    public static void addBreak(XWPFDocument document) {
        document.createParagraph().createRun().addBreak();
    }

    protected static List<File> convertDocumentModelsBlobsToPdf(List<DocumentModel> files) {
        return files
            .stream()
            .filter(d -> FOND_DE_DOSSIER_FICHIER_DOCUMENT_TYPE.equals(d.getType()))
            .map(d -> d.getAdapter(fr.dila.st.api.domain.file.File.class))
            .map(fr.dila.st.api.domain.file.File::getContent)
            .filter(Objects::nonNull)
            .map(SSServiceLocator.getSSPdfService()::convertToPdf)
            .map(Blob::getFile)
            .collect(Collectors.toList());
    }

    public static File mergePdfs(String destinationPath, List<File> pdfsToMerge) {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        pdfMerger.setDestinationFileName(destinationPath);
        pdfsToMerge.forEach(pdfMerger::addSource);

        try {
            pdfMerger.mergeDocuments();
        } catch (COSVisitorException | IOException e) {
            throw new NuxeoException(e);
        }
        return new File(destinationPath);
    }

    public static Consumer<OutputStream> getPdfsMerger(List<ByteArrayOutputStream> pdfs) {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        List<InputStream> pdfStreams = pdfs
            .stream()
            .map(pdf -> new ByteArrayInputStream(pdf.toByteArray()))
            .collect(Collectors.toList());
        pdfMerger.addSources(pdfStreams);
        ByteArrayOutputStream pdfMergee = new ByteArrayOutputStream();
        pdfMerger.setDestinationStream(pdfMergee);

        return (OutputStream out) -> mergePdfToOutputStream(pdfMerger, out);
    }

    private static void mergePdfToOutputStream(PDFMergerUtility pdfMerger, OutputStream outputStream) {
        pdfMerger.setDestinationStream(outputStream);
        try {
            pdfMerger.mergeDocuments();
        } catch (COSVisitorException | IOException e) {
            throw new NuxeoException(e);
        }
    }

    public static TextCell createTextCell(String text, boolean bold) {
        return new TextCell(text, bold);
    }

    public static TextCell createHeaderCell(String text) {
        return new TextCell(text, true);
    }

    public static TextCell createTextCell(String text) {
        return new TextCell(text, false);
    }

    public static TextCell createDateCell(Calendar date) {
        return new TextCell(SolonDateConverter.DATE_SLASH.format(date), false);
    }

    public static TextCell createDateCell(Date date) {
        return new TextCell(SolonDateConverter.DATE_SLASH.format(date), false);
    }

    public static ImageCell createImageCell(String path) {
        return new ImageCell(path);
    }

    public static void ajoutParagrapheTexte(XWPFDocument document, String titre, String contenu) {
        addTitle(document, titre, SIZE_TITRE1);
        addText(document, contenu, SIZE_CONTENU);
    }

    public static void mergeCellVertically(XWPFTable table, int col, int fromRow, int toRow) {
        for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {
            XWPFTableCell cell = table.getRow(rowIndex).getCell(col);
            CTVMerge vmerge = CTVMerge.Factory.newInstance();
            if (rowIndex == fromRow) {
                vmerge.setVal(STMerge.RESTART);
            } else {
                vmerge.setVal(STMerge.CONTINUE);
                for (int i = cell.getParagraphs().size() - 1; i >= 0; i--) {
                    cell.removeParagraph(i);
                }
                cell.addParagraph();
            }
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr != null) {
                tcPr.setVMerge(vmerge);
            } else {
                tcPr = CTTcPr.Factory.newInstance();
                tcPr.setVMerge(vmerge);
                cell.getCTTc().setTcPr(tcPr);
            }
        }
    }

    protected void mergeCellHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        for (int colIndex = fromCol; colIndex <= toCol; colIndex++) {
            XWPFTableCell cell = table.getRow(row).getCell(colIndex);
            CTHMerge hmerge = CTHMerge.Factory.newInstance();
            if (colIndex == fromCol) {
                hmerge.setVal(STMerge.RESTART);
            } else {
                hmerge.setVal(STMerge.CONTINUE);
                for (int i = cell.getParagraphs().size() - 1; i >= 0; i--) {
                    cell.removeParagraph(i);
                }
                cell.addParagraph();
            }
            CTTcPr tcPr = cell.getCTTc().getTcPr();
            if (tcPr != null) {
                tcPr.setHMerge(hmerge);
            } else {
                tcPr = CTTcPr.Factory.newInstance();
                tcPr.setHMerge(hmerge);
                cell.getCTTc().setTcPr(tcPr);
            }
        }
    }

    public static String htmlToText(String html) {
        return Jsoup.parse(html).body().text();
    }
}
