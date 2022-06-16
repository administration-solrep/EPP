package fr.dila.ss.core.birt.datasource;

import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nuxeo.common.Environment;
import org.nuxeo.launcher.config.ConfigurationGenerator;

/**
 * Classe utilitaire pour extraire les paramètres JDBC/BDD à partir de la
 * configuration Nuxeo.
 *
 * Exemples de types d'URLs traités (tirés du socle nuxeo 10):
 * <ul>
 * <li>jdbc:db2://${nuxeo.db.host}:${nuxeo.db.port}/${nuxeo.db.name}</li>
 * <li>jdbc:h2:$${nuxeo.data.dir}/h2/${nuxeo.db.name};DB_CLOSE_ON_EXIT=false</li>
 * <li>jdbc:mariadb://${nuxeo.db.host}:${nuxeo.db.port}/${nuxeo.db.name}</li>
 * <li>jdbc:sqlserver://${nuxeo.db.host}:${nuxeo.db.port};database=${nuxeo.db.name};selectMethod=cursor</li>
 * <li>jdbc:mysql://${nuxeo.db.host}:${nuxeo.db.port}/${nuxeo.db.name}?relaxAutoCommit=true</li>
 * <li>jdbc:oracle:thin:${nuxeo.db.user}/${nuxeo.db.password}@${nuxeo.db.host}:${nuxeo.db.port}:${nuxeo.db.name}</li>
 * <li>jdbc:postgresql://${nuxeo.db.host}:${nuxeo.db.port}/${nuxeo.db.name}</li>
 * </ul>
 */
public class DSHelper {
    private static final Logger LOGGER = LogManager.getLogger(DSHelper.class);

    protected static NuxeoDSConfig detectedDS;

    private static void putConfigPropertyIfExists(
        Map<String, String> properties,
        String param,
        ConfigService configService
    ) {
        try {
            properties.put(param, configService.getValue(param));
        } catch (RuntimeException e) {
            // Non défini mais pas utile
            LOGGER.info("Param {} not found in configuration", param);
        }
    }

    /**
     * Récupération des informations complètes de la datasource à partir du driver et de la configuration.
     */
    public static NuxeoDSConfig getDSConfig() {
        if (detectedDS == null) {
            ConfigService configService = STServiceLocator.getConfigService();

            String driverClass = configService.getValue(ConfigurationGenerator.PARAM_DB_DRIVER);
            String user = configService.getValue(ConfigurationGenerator.PARAM_DB_USER);

            // java.lang.RuntimeException
            Map<String, String> properties = new HashMap<>();

            properties.put(ConfigurationGenerator.PARAM_DB_DRIVER, driverClass);
            properties.put(ConfigurationGenerator.PARAM_DB_USER, user);
            putConfigPropertyIfExists(properties, ConfigurationGenerator.PARAM_DB_HOST, configService);
            putConfigPropertyIfExists(properties, ConfigurationGenerator.PARAM_DB_PORT, configService);
            putConfigPropertyIfExists(properties, ConfigurationGenerator.PARAM_DB_NAME, configService);
            putConfigPropertyIfExists(properties, Environment.NUXEO_DATA_DIR, configService);

            StringSubstitutor substitutor = new StringSubstitutor(properties);

            String jdbcUrl = configService.getValue(ConfigurationGenerator.PARAM_DB_JDBC_URL);
            jdbcUrl = jdbcUrl.replace("$${", "${"); // gestion du cas h2 où on a $${nuxeo.data.dir}

            jdbcUrl = substitutor.replace(jdbcUrl); // remplacement des autres template litterals

            detectedDS = new NuxeoDSConfig(driverClass, jdbcUrl, user);
        }
        return detectedDS;
    }
}
