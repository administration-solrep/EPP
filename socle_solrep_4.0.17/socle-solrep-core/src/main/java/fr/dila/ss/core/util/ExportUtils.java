package fr.dila.ss.core.util;

import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.constant.MediaType;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import java.io.File;
import java.io.IOException;
import javax.activation.DataSource;
import org.apache.commons.io.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;

public final class ExportUtils {
    private static final STLogger LOGGER = STLogFactory.getLog(ExportUtils.class);

    private ExportUtils() {
        // Default constructor
    }

    /**
     * Crée un fichier excel ou PDF à partir d'une DataSource
     *
     * @param data en excel
     * @param isPDF pour convertir ou non le fichier en format PDF
     * @return
     */
    public static File createXlsOrPdfFromDataSource(DataSource data, boolean isPDF) {
        File file = new File("export");
        try {
            FileUtils.copyInputStreamToFile(data.getInputStream(), file);
            if (isPDF) {
                Blob pdfBlob = SSServiceLocator
                    .getSSPdfService()
                    .convertToPdf(new FileBlob(file, MediaType.APPLICATION_MS_EXCEL.mime()));
                FileUtils.copyInputStreamToFile(pdfBlob.getStream(), file);
            }
        } catch (IOException e) {
            LOGGER.error(null, STLogEnumImpl.FAIL_CREATE_EXCEL_TEC, e);
        }
        return file;
    }
}
