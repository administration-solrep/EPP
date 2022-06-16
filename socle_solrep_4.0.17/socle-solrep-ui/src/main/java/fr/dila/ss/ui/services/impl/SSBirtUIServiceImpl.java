package fr.dila.ss.ui.services.impl;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.service.SSBirtService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSBirtUIService;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.core.api.Blob;

public class SSBirtUIServiceImpl implements SSBirtUIService {

    @Override
    public Response generateDoc(SpecificContext context) {
        Blob blob = this.generateBirtReport(context, BirtOutputFormat.DOC);

        return FileDownloadUtils.getAttachmentDoc(blob.getFile(), blob.getFilename());
    }

    private Blob generateBirtReport(SpecificContext context, BirtOutputFormat outputFormat) {
        String birtReportId = Objects.requireNonNull(
            context.getFromContextData(SSContextDataKey.BIRT_REPORT_ID),
            "Un id de rapport birt est attendu"
        );

        BirtGenerationService birtGenerationService = SSServiceLocator.getBirtGenerationService();
        BirtReport birtReport = birtGenerationService.getReport(birtReportId);

        String fileNameWithoutExtension = ObjectHelper.requireNonNullElseGet(
            context.getFromContextData(SSContextDataKey.BIRT_OUTPUT_FILENAME),
            () -> this.generateFileName(birtReport, outputFormat)
        );
        String fileName = FileUtils.sanitizePathTraversal(fileNameWithoutExtension + "." + outputFormat.getExtension());

        HashMap<String, Serializable> reportValues = ObjectHelper.requireNonNullElseGet(
            context.getFromContextData(SSContextDataKey.BIRT_REPORT_VALUES),
            HashMap::new
        );

        SSBirtService birtService = SSServiceLocator.getSSBirtService();
        return birtService.generateReportResults(birtReportId, fileName, reportValues, outputFormat);
    }

    private String generateFileName(BirtReport birtReport, BirtOutputFormat outputFormat) {
        String reportName = birtReport.getFile();
        reportName = reportName.substring(0, reportName.length() - ("rptdesign".length() + 1));
        return FileUtils.sanitizePathTraversal(reportName + "." + outputFormat.getExtension());
    }
}
