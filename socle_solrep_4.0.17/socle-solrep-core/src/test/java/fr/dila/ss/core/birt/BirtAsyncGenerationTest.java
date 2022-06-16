package fr.dila.ss.core.birt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import fr.dila.solon.birt.common.BirtOutputFormat;
import fr.dila.solon.birt.common.SolonBirtParameters;
import fr.dila.ss.api.birt.BirtGenerationService;
import fr.dila.ss.api.constant.SSBirtConstants;
import fr.dila.st.api.service.ConfigService;
import fr.dila.st.core.service.STServiceLocator;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.launcher.config.ConfigurationGenerator;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ Framework.class, STServiceLocator.class })
@PowerMockIgnore("javax.management.*")
@Category(fr.dila.ss.core.PowerMockitoTests.class)
public class BirtAsyncGenerationTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    WorkManager mockWorkManager;

    @Mock
    ConfigService mockConfigService;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Framework.class);

        Mockito.when(Framework.getService(WorkManager.class)).thenReturn(mockWorkManager);

        Mockito
            .doAnswer(
                new Answer<Void>() {

                    @Override
                    public Void answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        testBirtWork = (BirtWork) args[0];
                        return null;
                    }
                }
            )
            .when(mockWorkManager)
            .schedule(Mockito.any(BirtWork.class), Mockito.eq(true));

        PowerMockito.mockStatic(STServiceLocator.class);
        Mockito.when(STServiceLocator.getConfigService()).thenReturn(mockConfigService);
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
    }

    private static final String REPORT_MODEL_NAME = "stat01";
    private static final String REPORT_MODEL_FILE = "MIN11_txGlobal.rptdesign";

    private Map<String, Serializable> scalarParams;

    private BirtWork testBirtWork = null;

    private void initAsyncGenerationTest() {
        scalarParams = new HashMap<>();
        scalarParams.put("key1", "value1");
        scalarParams.put("key2", "value2");
    }

    @Test
    public void testAsyncGeneration() {
        initAsyncGenerationTest();

        BirtGenerationService birtGenerationService = new BirtGenerationServiceImpl();
        birtGenerationService.generateAsync(REPORT_MODEL_NAME, null, BirtOutputFormat.XLSX, scalarParams, null, false);

        assertNotNull(testBirtWork);

        SolonBirtParameters resultParam = testBirtWork.getBirtParameters();
        assertEquals(ConfigurationGenerator.PARAM_DB_DRIVER, resultParam.getJdbcDriver());
        assertEquals(ConfigurationGenerator.PARAM_DB_JDBC_URL, resultParam.getJdbcUrl());
        assertEquals(ConfigurationGenerator.PARAM_DB_USER, resultParam.getJdbcUser());
        assertEquals(ConfigurationGenerator.PARAM_DB_PWD, resultParam.getJdbcPassword());
        assertEquals(BirtOutputFormat.XLSX, resultParam.getOutputFormat());
        assertEquals(REPORT_MODEL_FILE, resultParam.getReportModelName());
        assertEquals(scalarParams, resultParam.getScalarParameters());
    }
}
