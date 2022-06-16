package fr.dila.ss.core.birt;

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.dila.solon.birt.common.SerializationUtils;
import fr.dila.solon.birt.common.SolonBirtParameters;
import fr.dila.ss.api.birt.BirtException;
import fr.dila.ss.api.constant.SSBirtConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import java.util.List;
import org.nuxeo.ecm.platform.commandline.executor.api.CmdParameters;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandAvailability;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandNotAvailable;
import org.nuxeo.ecm.platform.commandline.executor.api.ExecResult;
import org.nuxeo.runtime.api.Framework;

public class BirtAppCommand {
    private static final STLogger LOGGER = STLogFactory.getLog(BirtAppCommand.class);

    public static final String BIRTAPP_COMMAND = "birtapp";

    public static final String BIRTAPP_PARAM_LOGCONF = "logConfigFile";
    public static final String BIRTAPP_PARAM_CONFIG_FILE = "configFile";
    public static final String BIRTAPP_PARAM_SOLON_BIRT_PARAM = "solonBirtParam";
    public static final String BIRTAPP_PARAM_MAIN_CLASS = "mainClass";
    public static final String BIRTAPP_PARAM_CLASS_PATH = "classPath";

    private final String logConfigFile;
    private final String classPath;
    private final String mainClass;
    private final String configFile;

    public BirtAppCommand() {
        ConfigService configService = STServiceLocator.getConfigService();

        logConfigFile = configService.getValue(SSBirtConstants.BIRT_APP_LOG_CONFIG_FILE_PROP);
        classPath = configService.getValue(SSBirtConstants.BIRT_APP_CLASSPATH_PROP);
        mainClass = configService.getValue(SSBirtConstants.BIRT_APP_MAIN_CLASS_PROP);
        configFile = configService.getValue(SSBirtConstants.BIRT_APP_CONFIG_FILE_PROP);
    }

    public String call(SolonBirtParameters solonBirtParameters) {
        String serializedParam;
        try {
            serializedParam = SerializationUtils.serialize(solonBirtParameters);
        } catch (JsonProcessingException e1) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e1);
            throw new BirtException(e1);
        }

        CommandLineExecutorService cles = Framework.getService(CommandLineExecutorService.class);

        CommandAvailability ca = cles.getCommandAvailability(BIRTAPP_COMMAND);
        if (!ca.isAvailable()) {
            String message = "Command " + BIRTAPP_COMMAND + " not available";
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, message);
            throw new BirtException(message);
        }

        CmdParameters params = new CmdParameters();
        params.addNamedParameter(BIRTAPP_PARAM_LOGCONF, logConfigFile);
        params.addNamedParameter(BIRTAPP_PARAM_CLASS_PATH, classPath);
        params.addNamedParameter(BIRTAPP_PARAM_MAIN_CLASS, mainClass);
        params.addNamedParameter(BIRTAPP_PARAM_SOLON_BIRT_PARAM, serializedParam);
        params.addNamedParameter(BIRTAPP_PARAM_CONFIG_FILE, configFile);

        ExecResult result;
        try {
            LOGGER.info(
                STLogEnumImpl.LOG_INFO_TEC,
                "Launching command with parameter : " + solonBirtParameters.toString()
            );
            result = cles.execCommand(BIRTAPP_COMMAND, params);
        } catch (CommandNotAvailable e) {
            LOGGER.error(STLogEnumImpl.LOG_EXCEPTION_TEC, e);
            throw new BirtException(e);
        }

        List<String> lines = result.getOutput();
        String line = lines.get(0);
        LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Output from birt app : " + line);
        return line;
    }
}
