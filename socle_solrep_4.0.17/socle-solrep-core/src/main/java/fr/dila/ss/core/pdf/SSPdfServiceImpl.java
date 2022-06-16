package fr.dila.ss.core.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.beanutils.ConversionException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.convert.api.ConversionService;

import fr.dila.ss.api.pdf.SSPdfService;
import fr.dila.st.api.io.STByteArrayOutputStream;
import fr.dila.st.core.io.STByteArrayOutputStreamImpl;
import fr.dila.st.core.util.FileUtils;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

public class SSPdfServiceImpl implements SSPdfService {
    private static final String APPLICATION_MSWORD_MIMETYPE = "application/msword";
    private static final String APPLICATION_PDF_MIMETYPE = "application/pdf";

    @Override
    public File convertDocxToPdf(File docxFile, String pdfFilename) throws IOException {
        File pdfFile = FileUtils.getAppTmpFile(FileUtils.generateCompletePdfFilename(pdfFilename));
        convertToPdf(new FileBlob(docxFile, APPLICATION_MSWORD_MIMETYPE)).transferTo(pdfFile);
        return pdfFile;
    }

    private STByteArrayOutputStream convertDocxToPdf(ByteArrayOutputStream outputStream) throws IOException {
    	STByteArrayOutputStreamImpl baos = new STByteArrayOutputStreamImpl();
        convertToPdf(new ByteArrayBlob(outputStream.toByteArray(), APPLICATION_MSWORD_MIMETYPE)).transferTo(baos);
        return baos;
    }

    @Override
    public Blob convertToPdf(Blob blob) {
        if (APPLICATION_PDF_MIMETYPE.equals(blob.getMimeType())) {
            return blob;
        }

        ConversionService conversionService = ServiceUtil.getRequiredService(ConversionService.class);
        String converterName = conversionService.getConverterName(blob.getMimeType(), APPLICATION_PDF_MIMETYPE);

        BlobHolder blobHolder = new SimpleBlobHolder(blob);
        BlobHolder result = conversionService.convert(
            Objects.requireNonNull(converterName, "Pas de converter vers pdf"),
            blobHolder,
            null
        );
        try {
            return result.getBlob();
        } catch (ConversionException exc) {
            throw new NuxeoException(exc);
        }
    }

    @Override
    public File generatePdf(String filename, XWPFDocument document) {
        File docxFile = FileUtils.getAppTmpFile(FileUtils.generateCompleteDocxFilename(filename));

        try (FileOutputStream fos = new FileOutputStream(docxFile)) {
            document.write(fos);
            return convertDocxToPdf(docxFile, filename);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    @Override
    public STByteArrayOutputStream generatePdf(XWPFDocument document) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            document.write(baos);
            return convertDocxToPdf(baos);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }
}
