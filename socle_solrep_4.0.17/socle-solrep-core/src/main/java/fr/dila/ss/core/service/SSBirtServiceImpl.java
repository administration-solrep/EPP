package fr.dila.ss.core.service;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.logging.enumerationCodes.SSLogEnumImpl;
import fr.dila.ss.api.service.SSBirtService;
import fr.dila.st.api.constant.MediaType;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.core.factory.STLogFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;

public class SSBirtServiceImpl implements SSBirtService {
    private static final long serialVersionUID = 1L;

    private static final STLogger LOGGER = STLogFactory.getLog(SSBirtServiceImpl.class);

    /**
     * default constructor
     */
    public SSBirtServiceImpl() {
        // do nothing
    }

    protected Blob transformFileToBlob(final File file, final String reportName, final BirtOutputFormat format)
        throws FileNotFoundException {
        Blob blob = new FileBlob(file);
        blob.setFilename(reportName + "." + format.getExtension());
        if (BirtOutputFormat.XLS.equals(format)) {
            blob.setMimeType(MediaType.APPLICATION_MS_EXCEL.mime());
        }
        return blob;
    }

    @Override
    public Blob generateReportResults(
        final File fileResult,
        final File imagesDir,
        final String reportName,
        final String reportFile,
        final Map<String, ? extends Serializable> inputValues,
        final BirtOutputFormat outputFormat
    ) {
        return generateReportResults(
                fileResult,
                imagesDir,
                reportName,
                reportFile,
                inputValues,
                Collections.singletonList(outputFormat)
            )
            .get(outputFormat);
    }

    @Override
    public Blob generateReportResults(
        final String reportName,
        final String reportFile,
        final Map<String, ? extends Serializable> inputValues,
        final BirtOutputFormat outputFormat
    ) {
        return generateReportResults(
                null,
                null,
                reportName,
                reportFile,
                inputValues,
                Collections.singletonList(outputFormat)
            )
            .get(outputFormat);
    }

    @Override
    public Map<BirtOutputFormat, Blob> generateReportResults(
        final File fileResult,
        final File imagesDir,
        final String reportName,
        final String reportFile,
        final Map<String, ? extends Serializable> inputValues,
        final List<BirtOutputFormat> outputFormats
    ) {
        Map<BirtOutputFormat, Blob> reportResults = new HashMap<>();

        for (BirtOutputFormat format : outputFormats) {
            // Si aucun fichier n'a été passé en paramètre, on en a créé un temporaire pour générer le rapport
            // on le supprime donc après GC
            String path = fileResult != null ? fileResult.getAbsolutePath() : null;
            if (path != null && !path.contains(".")) {
                path += "." + format.getExtension();
            }
            File result = SSServiceLocator
                .getBirtGenerationService()
                .generate(reportName, reportFile, format, inputValues, path, fileResult == null);
            try {
                reportResults.put(format, transformFileToBlob(result, reportName, format));
            } catch (FileNotFoundException e) {
                LOGGER.error(null, SSLogEnumImpl.FAIL_CREATE_BIRT_TEC, e);
            }
        }

        return reportResults;
    }

    @Override
    public Map<BirtOutputFormat, File> generateReportFileResults(
        String reportName,
        String reportFile,
        Map<String, ? extends Serializable> inputValues,
        List<BirtOutputFormat> outputFormats
    ) {
        Map<BirtOutputFormat, File> reportResultsFile = new HashMap<>();

        BirtGenerationService birtGenerationService = SSServiceLocator.getBirtGenerationService();

        for (BirtOutputFormat format : outputFormats) {
            File result = birtGenerationService.generate(reportName, reportFile, format, inputValues, null, true);
            reportResultsFile.put(format, result);
        }
        return reportResultsFile;
    }

    @Override
    public Map<BirtOutputFormat, Blob> generateReportResults(
        final String reportName,
        final String reportFile,
        final Map<String, ? extends Serializable> inputValues,
        final List<BirtOutputFormat> outputFormats
    ) {
        return generateReportResults(null, null, reportName, reportFile, inputValues, outputFormats);
    }
}
