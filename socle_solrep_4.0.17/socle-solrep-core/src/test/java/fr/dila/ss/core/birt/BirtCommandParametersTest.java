package fr.dila.ss.core.birt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.solon.birt.common.SerializationUtils;
import fr.dila.solon.birt.common.SolonBirtParameters;
import fr.dila.ss.api.constant.SSBirtConstants;
import fr.dila.st.api.service.ConfigService;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.nuxeo.ecm.platform.commandline.executor.api.CmdParameters;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandAvailability;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandLineExecutorService;
import org.nuxeo.ecm.platform.commandline.executor.api.CommandNotAvailable;
import org.nuxeo.ecm.platform.commandline.executor.api.ExecResult;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RuntimeFeature;

@RunWith(FeaturesRunner.class)
@Features({ RuntimeFeature.class, BirtAppFeature.class })
@Deploy("fr.dila.st.api")
@Deploy("fr.dila.st.core")
@Deploy("fr.dila.ss.api")
@Deploy("fr.dila.ss.core")
@Deploy("org.nuxeo.ecm.platform.commandline.executor")
public class BirtCommandParametersTest {
    @Mock
    @RuntimeService
    protected CommandLineExecutorService mockCommandLineExecutorService;

    @Mock
    @RuntimeService
    protected ConfigService mockConfigService;

    @Inject
    private BirtAppFeature birtAppMockitoFeature;

    @Before
    public void setUp() throws CommandNotAvailable {
        when(mockConfigService.getValue(any(String.class)))
            .thenAnswer(
                new Answer<String>() {

                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        return (String) invocation.getArguments()[0];
                    }
                }
            );

        when(mockCommandLineExecutorService.getCommandAvailability(any(String.class)))
            .thenReturn(new CommandAvailability());

        when(mockCommandLineExecutorService.getDefaultCmdParameters()).thenReturn(new CmdParameters());
    }

    /**
     * Vérifie que le paramètre SolonBirtParameters est bien transféré à la commande
     * Birt App.
     */
    @Test
    public void testBirtAppCommandSolonParam() throws CommandNotAvailable, IOException {
        String jdbcDriver = "jdbcDriver";
        String jdbcPassword = "jdbcPassword";
        String jdbcUrl = "jdbcUrl";
        String jdbcUser = "jdbcUser";
        BirtOutputFormat outputFormat = BirtOutputFormat.HTML;
        String reportModelName = "reportModelName";
        String resultPathName = "resultPathName";
        Map<String, Serializable> scalarParams = new HashMap<>();
        scalarParams.put("key1", "value1");
        scalarParams.put("key2", "value2");

        SolonBirtParameters params = new SolonBirtParameters();
        params.setJdbcDriver(jdbcDriver);
        params.setJdbcPassword(jdbcPassword);
        params.setJdbcUrl(jdbcUrl);
        params.setJdbcUser(jdbcUser);
        params.setOutputFormat(outputFormat);
        params.setReportModelName(reportModelName);
        params.setResultPathName(resultPathName);
        params.setScalarParameters(scalarParams);

        // Le mock renvoie le chemin vers le fichier temporaire
        when(mockCommandLineExecutorService.execCommand(eq(BirtAppCommand.BIRTAPP_COMMAND), any(CmdParameters.class)))
            .thenAnswer(
                new Answer<ExecResult>() {

                    @Override
                    public ExecResult answer(InvocationOnMock invocation) throws Throwable {
                        return birtAppMockitoFeature.answerExecResult(
                            invocation,
                            BirtAppCommand.BIRTAPP_PARAM_SOLON_BIRT_PARAM
                        );
                    }
                }
            );

        String result = birtAppMockitoFeature.readFirstLine(new File(new BirtAppCommand().call(params)));
        assertNotNull(result);

        SolonBirtParameters resultParam = SerializationUtils.deserialize(result);
        assertEquals(jdbcDriver, resultParam.getJdbcDriver());
        assertEquals(jdbcUrl, resultParam.getJdbcUrl());
        assertEquals(jdbcUser, resultParam.getJdbcUser());
        assertEquals(jdbcPassword, resultParam.getJdbcPassword());
        assertEquals(outputFormat, resultParam.getOutputFormat());
        assertEquals(reportModelName, resultParam.getReportModelName());
        assertEquals(resultPathName, resultParam.getResultPathName());
        assertEquals(scalarParams, resultParam.getScalarParameters());
    }

    /**
     * Vérifie que le paramètre indiquant le chemin du fichier log4j.xml est bien
     * transféré à la commande Birt App.
     */
    @Test
    public void testBirtAppCommandLogConfigFile() throws CommandNotAvailable, IOException {
        SolonBirtParameters params = new SolonBirtParameters();

        // Le mock renvoie un fichier contenant le paramètre logConfigFile
        when(mockCommandLineExecutorService.execCommand(eq(BirtAppCommand.BIRTAPP_COMMAND), any(CmdParameters.class)))
            .thenAnswer(
                new Answer<ExecResult>() {

                    @Override
                    public ExecResult answer(InvocationOnMock invocation) throws Throwable {
                        return birtAppMockitoFeature.answerExecResult(invocation, BirtAppCommand.BIRTAPP_PARAM_LOGCONF);
                    }
                }
            );

        String result = birtAppMockitoFeature.readFirstLine(new File(new BirtAppCommand().call(params)));
        assertNotNull(result);
        assertEquals(SSBirtConstants.BIRT_APP_LOG_CONFIG_FILE_PROP, result);
    }

    /**
     * Vérifie que le paramètre indiquant le classpath est bien transféré à la
     * commande Birt App.
     */
    @Test
    public void testBirtAppCommandClassPath() throws CommandNotAvailable, IOException {
        SolonBirtParameters params = new SolonBirtParameters();

        // Le mock renvoie un fichier contenant le classpath
        when(mockCommandLineExecutorService.execCommand(eq(BirtAppCommand.BIRTAPP_COMMAND), any(CmdParameters.class)))
            .thenAnswer(
                new Answer<ExecResult>() {

                    @Override
                    public ExecResult answer(InvocationOnMock invocation) throws Throwable {
                        return birtAppMockitoFeature.answerExecResult(
                            invocation,
                            BirtAppCommand.BIRTAPP_PARAM_CLASS_PATH
                        );
                    }
                }
            );

        String result = birtAppMockitoFeature.readFirstLine(new File(new BirtAppCommand().call(params)));
        assertNotNull(result);
        assertEquals(SSBirtConstants.BIRT_APP_CLASSPATH_PROP, result);
    }

    /**
     * Vérifie que le paramètre indiquant la classe principale est bien transféré à
     * la commande Birt App.
     *
     * @throws IOException
     */
    @Test
    public void testBirtAppCommandMainClass() throws CommandNotAvailable, IOException {
        SolonBirtParameters params = new SolonBirtParameters();

        // Le mock renvoie un fichier contenant le nom de la classe principale
        when(mockCommandLineExecutorService.execCommand(eq(BirtAppCommand.BIRTAPP_COMMAND), any(CmdParameters.class)))
            .thenAnswer(
                new Answer<ExecResult>() {

                    @Override
                    public ExecResult answer(InvocationOnMock invocation) throws Throwable {
                        return birtAppMockitoFeature.answerExecResult(
                            invocation,
                            BirtAppCommand.BIRTAPP_PARAM_MAIN_CLASS
                        );
                    }
                }
            );

        String result = birtAppMockitoFeature.readFirstLine(new File(new BirtAppCommand().call(params)));
        assertNotNull(result);
        assertEquals(SSBirtConstants.BIRT_APP_MAIN_CLASS_PROP, result);
    }

    /**
     * Vérifie que le paramètre indiquant le chemin vers le fichier de config est
     * bien transféré à la commande Birt App.
     */
    @Test
    public void testBirtAppCommandConfigFile() throws CommandNotAvailable, IOException {
        SolonBirtParameters params = new SolonBirtParameters();

        // Le mock renvoie un fichier contenant le paramètre config file
        when(mockCommandLineExecutorService.execCommand(eq(BirtAppCommand.BIRTAPP_COMMAND), any(CmdParameters.class)))
            .thenAnswer(
                new Answer<ExecResult>() {

                    @Override
                    public ExecResult answer(InvocationOnMock invocation) throws Throwable {
                        return birtAppMockitoFeature.answerExecResult(
                            invocation,
                            BirtAppCommand.BIRTAPP_PARAM_CONFIG_FILE
                        );
                    }
                }
            );

        String result = birtAppMockitoFeature.readFirstLine(new File(new BirtAppCommand().call(params)));
        assertNotNull(result);
        assertEquals(SSBirtConstants.BIRT_APP_CONFIG_FILE_PROP, result);
    }
}
