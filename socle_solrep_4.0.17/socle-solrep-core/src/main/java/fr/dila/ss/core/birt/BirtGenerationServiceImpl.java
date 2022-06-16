package fr.dila.ss.core.birt;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.solon.birt.common.SolonBirtParameters;
import fr.dila.ss.api.birt.BirtException;
import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.birt.BirtReport;
import fr.dila.ss.api.birt.BirtReports;
import fr.dila.ss.api.birt.MapAdapter;
import fr.dila.ss.api.constant.SSBirtConstants;
import fr.dila.ss.core.birt.datasource.DSHelper;
import fr.dila.ss.core.birt.datasource.NuxeoDSConfig;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Optional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.io.FilenameUtils;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.launcher.config.ConfigurationGenerator;
import org.nuxeo.runtime.api.Framework;

public class BirtGenerationServiceImpl implements BirtGenerationService {
    private static final STLogger LOGGER = STLogFactory.getLog(BirtGenerationServiceImpl.class);

    private static final String BIRT_APP_MODE_PROPERTY = "solon.birt.app.mode";
    private static final String BIRT_APP_MODE_DEFAULT_VALUE = BirtMode.APP.getValue();

    private enum BirtMode {
        APP,
        SERVER;

        public String getValue() {
            return name().toLowerCase();
        }
    }

    /**
     * Appel de l'api /report du serveur Birt
     *
     * @param birtParameters param SolonBirtParameters
     * @return chemin complet du fichier généré.
     */
    private File callBirtServerCommand(SolonBirtParameters birtParameters) {
        initJdbcParameters(birtParameters);

        return new File(new BirtServerCommand().call(birtParameters));
    }

    /**
     * Appel de la commande Birt avec les paramètres Birt au complet.
     *
     * @param birtParameters param SolonBirtParameters
     * @return chemin complet du fichier généré.
     */
    private File callBirtAppCommand(SolonBirtParameters birtParameters) {
        initJdbcParameters(birtParameters);

        return new File(new BirtAppCommand().call(birtParameters));
    }

    private void scheduleBirtWork(SolonBirtParameters birtParameters) {
        initJdbcParameters(birtParameters);

        BirtWork birtWork = new BirtWork(birtParameters);

        WorkManager workManager = Framework.getService(WorkManager.class);
        workManager.schedule(birtWork, true);
    }

    void initJdbcParameters(SolonBirtParameters birtParameters) {
        NuxeoDSConfig dsConfig = DSHelper.getDSConfig();

        birtParameters.setJdbcUrl(dsConfig.getUrl());
        birtParameters.setJdbcUser(dsConfig.getUserName());
        birtParameters.setJdbcDriver(dsConfig.getDriverClass());

        setJdbcPassword(birtParameters);
    }

    /**
     * Set sans stockage dans var intermédiaire.
     */
    private void setJdbcPassword(final SolonBirtParameters params) {
        ConfigService configService = STServiceLocator.getConfigService();
        params.setJdbcPassword(configService.getValue(ConfigurationGenerator.PARAM_DB_PWD));
    }

    @Override
    public File generate(
        String reportName,
        String reportFile,
        BirtOutputFormat format,
        Map<String, ? extends Serializable> scalarParameters,
        String resultPathname,
        boolean track
    ) {
        SolonBirtParameters birtParameters = prepareCommandParameters(
            reportName,
            reportFile,
            format,
            scalarParameters,
            resultPathname,
            track
        );

        // Mode standalone ou mode serveur (api/rest)
        String birtAppMode = Framework.getProperty(BIRT_APP_MODE_PROPERTY, BIRT_APP_MODE_DEFAULT_VALUE);

        if (BirtMode.APP.getValue().equals(birtAppMode)) {
            return callBirtAppCommand(birtParameters);
        } else {
            return callBirtServerCommand(birtParameters);
        }
    }

    @Override
    public void generateAsync(
        String reportName,
        String reportFile,
        BirtOutputFormat format,
        Map<String, ? extends Serializable> scalarParameters,
        String resultPathname,
        boolean track
    ) {
        SolonBirtParameters birtParameters = prepareCommandParameters(
            reportName,
            reportFile,
            format,
            scalarParameters,
            resultPathname,
            track
        );

        scheduleBirtWork(birtParameters);
    }

    private SolonBirtParameters prepareCommandParameters(
        String reportName,
        String reportFile,
        BirtOutputFormat format,
        Map<String, ? extends Serializable> scalarParameters,
        String resultPathname,
        boolean track
    ) {
        if (!FilenameUtils.isExtension(reportFile, "rptdesign")) {
            // Si report file est null alors le nom devrait être celui par défaut (pas de suffix) et on peut retrouver le rptdesign
            BirtReport birtReport = getReport(reportName);
            reportFile = birtReport.getFile();
        }
        SolonBirtParameters birtParameters = new SolonBirtParameters();

        birtParameters.setReportModelName(reportFile);
        birtParameters.setOutputFormat(format);
        if (resultPathname != null) {
            birtParameters.setResultPathName(resultPathname);
        }

        birtParameters.setScalarParameters(scalarParameters);
        birtParameters.setTrack(track);

        return birtParameters;
    }

    private static BirtReports birtReports;

    @Override
    public BirtReports getReports() {
        if (birtReports == null) {
            File file = getBirtReportsFile();

            if (!file.exists()) {
                throw new BirtException("Le fichier de configuration xml des rapports Birt n'existe pas : " + file);
            }

            try {
                JAXBContext context = JAXBContext.newInstance(BirtReports.class);
                MapAdapter mapAdapter = new MapAdapter();
                Unmarshaller unmarshaller = context.createUnmarshaller();
                unmarshaller.setAdapter(mapAdapter);

                Object obj = unmarshaller.unmarshal(file);

                birtReports = (BirtReports) obj;
            } catch (JAXBException e) {
                LOGGER.error(STLogEnumImpl.FAIL_LOG_TEC, e);
                throw new BirtException(e);
            }
        }

        return birtReports;
    }

    @Override
    public File getBirtReportsFile() {
        ConfigService configService = STServiceLocator.getConfigService();
        String filename = configService.getValue(SSBirtConstants.BIRT_REPORTS_LIST_FILE_PROP);
        return new File(filename);
    }

    @Override
    public BirtReport getReport(String reportId) {
        return Optional
            .ofNullable(getReports().getBirtReportMap().get(reportId))
            .orElseThrow(() -> new BirtException("Rapport Birt non trouvé : " + reportId));
    }
}
