package fr.dila.st.ui.jaxrs.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import com.sun.jersey.api.container.ContainerException;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.exception.STValidationException;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.th.ThEngineService;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ WebEngine.class, STLogFactory.class })
@PowerMockIgnore("javax.management.*")
public class ContainerExceptionMapperTest {
    private static final String ERROR_400_FILE_PATH = "error400.html";
    private static final String REQUEST_HEADER = "X-Requested-With";
    private static final String CONTAINER_EXCEPTION_MESSAGE = "Exception obtaining parameters";
    private static final String BAD_REQUEST_ERROR_KEY = "request.bad.param";
    private static final String BAD_REQUEST_ERROR_MESSAGE = "Requête invalide";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private ContainerExceptionMapper mapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ThEngineService engineService;

    @Mock
    private ServletContext servletContext;

    @Mock
    private WebContext webContext;

    @Mock
    private NuxeoPrincipal principal;

    @Mock
    private CoreSession session;

    @Mock
    private UserSession userSession;

    @Mock
    private STLogger loggerContainerExceptionMapper;

    @Mock
    private STLogger loggerResourceHelper;

    @Before
    public void setUp() {
        mapper = new ContainerExceptionMapper(request, engineService);

        PowerMockito.mockStatic(WebEngine.class);
        when(WebEngine.getActiveContext()).thenReturn(webContext);

        PowerMockito.mockStatic(STLogFactory.class);
        when(STLogFactory.getLog(ContainerExceptionMapper.class)).thenReturn(loggerContainerExceptionMapper);
        when(STLogFactory.getLog(ResourceHelper.class)).thenReturn(loggerResourceHelper);

        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getRealPath(anyString())).thenReturn(ERROR_400_FILE_PATH);
        when(webContext.getPrincipal()).thenReturn(principal);
        when(webContext.getCoreSession()).thenReturn(session);
        when(webContext.getUserSession()).thenReturn(userSession);
        when(userSession.get(SpecificContext.MESSAGE_QUEUE)).thenReturn(new SolonAlertManager());
    }

    @Test
    public void testBuildResponse() {
        ContainerException exception = new ContainerException(CONTAINER_EXCEPTION_MESSAGE);

        when(request.getHeader(REQUEST_HEADER)).thenReturn(null);

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        verifyStatic();
        loggerContainerExceptionMapper.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, exception);
    }

    @Test
    public void testBuildResponseWithValidationException() {
        NuxeoException cause = new STValidationException(
            BAD_REQUEST_ERROR_KEY,
            new InvocationTargetException(new NullPointerException())
        );

        ContainerException exception = new ContainerException(CONTAINER_EXCEPTION_MESSAGE, cause);

        when(request.getHeader(REQUEST_HEADER)).thenReturn(null);

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());

        verifyStatic();
        loggerContainerExceptionMapper.error(session, STLogEnumImpl.FAIL_LOG_TEC, exception);
    }

    @Test
    public void testBuildAjaxResponse() {
        ContainerException exception = new ContainerException(CONTAINER_EXCEPTION_MESSAGE);

        when(request.getHeader(REQUEST_HEADER)).thenReturn("XMLHttpRequest");

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        assertThat(response.getEntity()).isEqualTo(BAD_REQUEST_ERROR_MESSAGE);

        verifyStatic();
        loggerContainerExceptionMapper.error(session, STLogEnumImpl.LOG_EXCEPTION_TEC, exception);
    }

    @Test
    public void testBuildAjaxResponseWithValidationException() {
        NuxeoException cause = new STValidationException(
            BAD_REQUEST_ERROR_KEY,
            new InvocationTargetException(new NullPointerException())
        );

        ContainerException exception = new ContainerException(CONTAINER_EXCEPTION_MESSAGE, cause);

        when(request.getHeader(REQUEST_HEADER)).thenReturn("XMLHttpRequest");

        Response response = mapper.toResponse(exception);

        assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
        assertThat(response.getEntity()).isEqualTo(BAD_REQUEST_ERROR_MESSAGE);

        verifyStatic();
        loggerContainerExceptionMapper.error(session, STLogEnumImpl.FAIL_LOG_TEC, exception);
    }
}
