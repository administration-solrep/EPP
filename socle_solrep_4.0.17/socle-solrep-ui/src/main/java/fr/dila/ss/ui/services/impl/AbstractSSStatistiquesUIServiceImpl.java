package fr.dila.ss.ui.services.impl;

import static fr.dila.ss.core.service.SSServiceLocator.getBirtGenerationService;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.birt.BirtException;
import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.ss.api.security.principal.SSPrincipal;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.BirtReportList;
import fr.dila.ss.ui.services.SSStatistiquesUIService;
import fr.dila.ss.ui.th.bean.BirtReportListForm;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.SolonDateConverter;
import fr.dila.st.ui.enums.SortOrder;
import fr.dila.st.ui.th.model.SpecificContext;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;

public abstract class AbstractSSStatistiquesUIServiceImpl implements SSStatistiquesUIService {

    protected Pattern getBirtReportIdPattern() {
        return Pattern.compile("stat\\d{2}");
    }

    protected String getBirtPatternSample() {
        return "stat22";
    }

    protected abstract String getGeneratedReportDirectory();

    protected String getGeneratedReportDirectoryValue() {
        ConfigService configService = STServiceLocator.getConfigService();
        return configService.getValue(getGeneratedReportDirectory());
    }

    @Override
    public BirtReport getBirtReport(String id) {
        validateBirtReportId(id);
        return getBirtGenerationService().getReport(id);
    }

    protected void validateBirtReportId(String id) {
        if (!getBirtReportIdPattern().matcher(id).matches()) {
            throw new BirtException(
                String.format(
                    "L'id [%s] du rapport birt doit correspondre à la regex %s. Par exemple : %s",
                    id,
                    getBirtReportIdPattern(),
                    getBirtPatternSample()
                )
            );
        }
    }

    protected String generateReportFile(BirtReport birtReport, Map<String, ? extends Serializable> scalarValues) {
        Blob blob = generateReportFile(birtReport, scalarValues, BirtOutputFormat.HTML);

        File file = blob.getFile();
        return generateReportFileURL(file);
    }

    protected String generateReportFileURL(File file) {
        return generateReportFileURL(file.getParent());
    }

    protected String generateReportFileURL(String filename) {
        return "report?reportDirectoryName=" + filename;
    }

    protected Blob generateReportFile(
        BirtReport birtReport,
        Map<String, ? extends Serializable> scalarValues,
        BirtOutputFormat outputFormat
    ) {
        String generatedReportDirectory = getGeneratedReportDirectoryValue();
        // Compute name of the report file if it was not given
        String reportName = fr.dila.st.core.util.FileUtils.sanitizePathTraversal(birtReport.getFile());
        reportName = reportName.substring(0, reportName.length() - ("rptdesign".length() + 1));
        File reportDir = new File(
            generatedReportDirectory,
            reportName + "_" + SolonDateConverter.DATETIME_UNDER_REVERSE_SECOND_UNDER.format(new Date())
        );
        if (reportDir.exists()) {
            try {
                FileUtils.deleteDirectory(reportDir);
            } catch (IOException e) {
                throw new NuxeoException(e);
            }
        }
        reportDir.mkdir();

        File imagesDir = new File(reportDir.getPath() + "/imgs");
        if (imagesDir.exists()) {
            try {
                FileUtils.deleteDirectory(imagesDir);
            } catch (IOException e) {
                throw new NuxeoException(e);
            }
        }
        imagesDir.mkdir();
        String fileName = fr.dila.st.core.util.FileUtils.sanitizePathTraversal(
            reportName + "." + outputFormat.getExtension()
        );
        File generatedReport = new File(String.format("%s/%s", reportDir.getPath(), fileName));
        return SSServiceLocator
            .getSSBirtService()
            .generateReportResults(
                generatedReport,
                imagesDir,
                birtReport.getId(),
                generatedReport.getName(),
                scalarValues,
                outputFormat
            );
    }

    @Override
    public String getHtmlReportContent(SpecificContext context, String reportDirectoryName) {
        String generatedReportDirectory = getGeneratedReportDirectoryValue();

        File reportDir = new File(
            generatedReportDirectory,
            fr.dila.st.core.util.FileUtils.sanitizePathTraversal(reportDirectoryName)
        );
        if (reportDir.exists()) {
            try (Stream<Path> reportPaths = Files.list(reportDir.toPath())) {
                return reportPaths
                    .filter(path -> path.toFile().isFile())
                    .findFirst()
                    .map(AbstractSSStatistiquesUIServiceImpl::getFileContent)
                    .orElse(null);
            } catch (IOException e) {
                throw new NuxeoException(e);
            }
        }

        return null;
    }

    protected static String getFileContent(Path path) {
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException exception) {
            throw new NuxeoException(exception);
        }
    }

    @Override
    public Collection<ReportProperty> getScalarProperties(BirtReport birtReport) {
        List<ReportProperty> scalarProperties = new ArrayList<>();
        for (ReportProperty reportProperty : birtReport.getProperties().values()) {
            if (reportProperty.isScalar()) {
                scalarProperties.add(reportProperty);
            }
        }
        return scalarProperties;
    }

    protected List<BirtReport> getStatistiqueReports(SSPrincipal ssPrincipal) {
        BirtGenerationService birtGenerationService = SSServiceLocator.getBirtGenerationService();
        Map<String, BirtReport> allReports = birtGenerationService.getReports().getBirtReportMap();

        return allReports
            .entrySet()
            .stream()
            .filter(entry -> entry.getKey().startsWith("stat"))
            .map(Entry::getValue)
            .sorted(Comparator.comparing(BirtReport::getTitle))
            .collect(Collectors.toList());
    }

    @Override
    public BirtReportList getStatistiqueList(SSPrincipal ssPrincipal, BirtReportListForm form) {
        List<BirtReport> allStats = getStatistiqueReports(ssPrincipal);

        if (SortOrder.DESC == form.getTitre()) {
            Collections.reverse(allStats);
        }

        BirtReportList statList = new BirtReportList();
        int startElement = form.getStartElement();
        int endElement = startElement + form.getSize();
        statList.setListe(allStats.subList(startElement, endElement < allStats.size() ? endElement : allStats.size()));
        statList.setNbTotal(allStats.size());
        statList.buildColonnes(form);

        return statList;
    }

    @Override
    public String getReportUrl(BirtReport birtReport, Map<String, String> params) {
        String reportUrl = birtReport.getId();

        if (MapUtils.isNotEmpty(params)) {
            reportUrl +=
                params
                    .entrySet()
                    .stream()
                    .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                    .map(entry -> entry.getKey() + '=' + entry.getValue())
                    .collect(Collectors.joining("&", "?", ""));
        }
        return reportUrl;
    }
}
