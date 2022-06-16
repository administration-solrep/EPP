package fr.dila.ss.api.pdf;

import java.io.File;
import java.io.IOException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.nuxeo.ecm.core.api.Blob;

import fr.dila.st.api.io.STByteArrayOutputStream;

public interface SSPdfService {
    File convertDocxToPdf(File docxFile, String pdfFilename) throws IOException;

    Blob convertToPdf(Blob blob);

    File generatePdf(String filename, XWPFDocument document);

    STByteArrayOutputStream generatePdf(XWPFDocument document);
}
