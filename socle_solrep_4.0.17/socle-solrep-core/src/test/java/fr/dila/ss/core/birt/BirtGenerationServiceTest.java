package fr.dila.ss.core.birt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.solon.birt.common.SerializationUtils;
import fr.dila.solon.birt.common.SolonBirtParameters;
import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.constant.SSBirtConstants;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.st.api.service.ConfigService;
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
import org.nuxeo.launcher.config.ConfigurationGenerator;
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
public class BirtGenerationServiceTest {
    @Mock
    @RuntimeService
    protected CommandLineExecutorService mockCommandLineExecutorService;

    @Mock
    @RuntimeService
    protected ConfigService mockConfigService;

    @Inject
    private BirtAppFeature birtAppMockitoFeature;

    private static final String REPORT_MODEL_NAME = "stat01";
    private static final String REPORT_MODEL_FILE = "MIN11_txGlobal.rptdesign";
    private Map<String, Serializable> scalarParams;

    @Before
    public void setUp() {
        when(mockConfigService.getValue(any(String.class)))
            .thenAnswer(
                new Answer<String>() {

                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        String arg = (String) invocation.getArguments()[0];

                        if (SSBirtConstants.BIRT_REPORTS_LIST_FILE_PROP.equals(arg)) {
                            return "src/test/resources/birtReports/list_reports.xml";
                        }

                        return arg;
                    }
                }
            );

        when(mockCommandLineExecutorService.getCommandAvailability(any(String.class)))
            .thenReturn(new CommandAvailability());

        when(mockCommandLineExecutorService.getDefaultCmdParameters()).thenReturn(new CmdParameters());
    }

    @Test
    public void testGeneratePdf() throws CommandNotAvailable, IOException {
        initFormatGenerationTest();

        BirtGenerationService birtGenerationService = SSServiceLocator.getBirtGenerationService();
        String result = birtAppMockitoFeature.readFirstLine(
            birtGenerationService.generate(REPORT_MODEL_NAME, null, BirtOutputFormat.PDF, scalarParams, null, true)
        );
        makeFormatGenerationAsserts(result, BirtOutputFormat.PDF);
    }

    @Test
    public void testGenerateHtml() throws CommandNotAvailable, IOException {
        initFormatGenerationTest();

        BirtGenerationService birtGenerationService = SSServiceLocator.getBirtGenerationService();
        String result = birtAppMockitoFeature.readFirstLine(
            birtGenerationService.generate(REPORT_MODEL_NAME, null, BirtOutputFormat.HTML, scalarParams, null, true)
        );

        makeFormatGenerationAsserts(result, BirtOutputFormat.HTML);
    }

    @Test
    public void testGenerateExcel() throws CommandNotAvailable, IOException {
        initFormatGenerationTest();

        BirtGenerationService birtGenerationService = SSServiceLocator.getBirtGenerationService();
        String result = birtAppMockitoFeature.readFirstLine(
            birtGenerationService.generate(REPORT_MODEL_NAME, null, BirtOutputFormat.XLSX, scalarParams, null, true)
        );

        makeFormatGenerationAsserts(result, BirtOutputFormat.XLSX);
    }

    private void initFormatGenerationTest() throws CommandNotAvailable {
        scalarParams = new HashMap<>();
        scalarParams.put("key1", "value1");
        scalarParams.put("key2", "value2");

        // Le mock renvoie le contenu du paramètre solonBirtParam
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
    }

    private void makeFormatGenerationAsserts(String result, BirtOutputFormat format)
        throws JsonMappingException, JsonProcessingException {
        assertNotNull(result);

        SolonBirtParameters resultParam = SerializationUtils.deserialize(result);
        assertEquals(ConfigurationGenerator.PARAM_DB_DRIVER, resultParam.getJdbcDriver());
        assertEquals(ConfigurationGenerator.PARAM_DB_JDBC_URL, resultParam.getJdbcUrl());
        assertEquals(ConfigurationGenerator.PARAM_DB_USER, resultParam.getJdbcUser());
        assertEquals(ConfigurationGenerator.PARAM_DB_PWD, resultParam.getJdbcPassword());
        assertEquals(format, resultParam.getOutputFormat());
        assertEquals(REPORT_MODEL_FILE, resultParam.getReportModelName());
        assertEquals(scalarParams, resultParam.getScalarParameters());
    }
}
