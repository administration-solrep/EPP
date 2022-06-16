package fr.dila.ss.ui.jaxrs.webobject.page;

import com.google.common.collect.ImmutableMap;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.ss.api.birt.ReportProperty;
import fr.dila.ss.ui.enums.SSContextDataKey;
import fr.dila.ss.ui.services.SSStatistiquesUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.FileDownloadUtils;
import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.Blob;

public abstract class AbstractSSStatistiques extends SolonWebObject {
    public static final String MAP_VALUE_DATAAJAXURL = "/ajax/stats/liste";
    public static final String MAP_VALUE_DATAURL = "/stats";
    public static final String ZIP_DOWNLOAD_URL = "/stats/telecharger/zip";
    public static final String ZIP_FILENAME = "zipFilename";
    public static final String ZIP_LINK = "zipLink";
    public static final String HAS_PARAMS = "hasParams";
    public static final String KEY_DISPLAY_ORG_SELECT_MIN = "displayOrganigrammeSelectMin";
    public static final String KEY_DISPLAY_ORG_SELECT_DIR = "displayOrganigrammeSelectDir";
    public static final String KEY_DISPLAY_ORG_SELECT_POSTE = "displayOrganigrammeSelectPoste";
    public static final String KEY_LIST_MINISTERES = "listMinisteres";

    public static final String KEY_PARAMS = "params";
    /** URL d'accès direct à cette stat (et ses paramètres) */
    public static final String KEY_REPORT_URL = "reportUrl";

    private static final Map<BirtOutputFormat, BiFunction<File, String, Response>> ATTACHMENT_FUNCTION = ImmutableMap.of(
        BirtOutputFormat.PDF,
        FileDownloadUtils::getAttachmentPdf,
        BirtOutputFormat.XLS,
        FileDownloadUtils::getAttachmentXls
    );

    public AbstractSSStatistiques() {
        super();
    }

    protected boolean hasScalarTypeProperty(String scalarName, Collection<ReportProperty> scalarProperties) {
        if (StringUtils.isEmpty(scalarName) || scalarProperties == null) {
            return false;
        }
        return scalarProperties.stream().anyMatch(p -> scalarName.equals(p.getType()));
    }

    /**
     * Récupération du rapport généré
     *
     * @param reportDirectoryName
     * @return
     */
    @GET
    @Path("report")
    public Object getBirtReport(
        @QueryParam("reportDirectoryName") String reportDirectoryName,
        @QueryParam("idContextuel") String idContextuel
    ) {
        // Retrieve HTML
        String out = getStatService().getHtmlReportContent(context, reportDirectoryName);

        if (out != null) {
            // File found, content is returned
            return out;
        }

        // 404
        return null;
    }

    protected Response telechargerFichier(SpecificContext context, File file) {
        BirtOutputFormat format = context.getFromContextData(SSContextDataKey.BIRT_OUTPUT_FORMAT);
        String fileName = modifyFileName(file, format);

        return ATTACHMENT_FUNCTION.get(format).apply(file, fileName);
    }

    protected Response telechargerFichier(SpecificContext context, Blob blob) {
        return telechargerFichier(context, blob.getFile());
    }

    protected String modifyFileName(File file, BirtOutputFormat format) {
        return file
            .getName()
            .substring(0, file.getName().indexOf("_"))
            .concat(".")
            .concat(FilenameUtils.getExtension(file.getName()));
    }

    protected SSStatistiquesUIService getStatService() {
        return SSUIServiceLocator.getSSStatistiquesUIService();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new ThTemplate();
    }
}
