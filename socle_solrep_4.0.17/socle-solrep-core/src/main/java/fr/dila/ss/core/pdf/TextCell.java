package fr.dila.ss.core.pdf;

import fr.dila.ss.api.pdf.Cell;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class TextCell implements Cell {
    private final String value;
    private final boolean bold;

    public TextCell(String value, boolean bold) {
        this.value = value;
        this.bold = bold;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the bold
     */
    public boolean isBold() {
        return bold;
    }

    @Override
    public void display(XWPFParagraph paragraph, XWPFRun run) {
        paragraph.setSpacingAfter(AbstractPdfDossierService.SIZE_SPACING);
        paragraph.setSpacingBefore(AbstractPdfDossierService.SIZE_SPACING);
        run.setBold(bold);
        run.setFontSize(AbstractPdfDossierService.SIZE_CONTENU);
        run.setText(value);
    }
}
