package fr.dila.ss.core.pdf;

import fr.dila.ss.api.exception.SSException;
import fr.dila.ss.api.pdf.Cell;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class ImageCell implements Cell {
    private final String imagePath;

    public ImageCell(String imagePath) {
        this.imagePath = imagePath;
    }

    /**
     * @return the imagePath
     */
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void display(XWPFParagraph paragraph, XWPFRun run) {
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(imagePath)) {
            run.addPicture(
                is,
                XWPFDocument.PICTURE_TYPE_PNG,
                imagePath,
                Units.toEMU(AbstractPdfDossierService.SIZE_TITRE2),
                Units.toEMU(AbstractPdfDossierService.SIZE_TITRE2)
            );
        } catch (IOException | InvalidFormatException e) {
            throw new SSException(e);
        }
    }
}
