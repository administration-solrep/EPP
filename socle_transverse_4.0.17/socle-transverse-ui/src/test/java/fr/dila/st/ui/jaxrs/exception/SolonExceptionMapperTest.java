package fr.dila.st.ui.jaxrs.exception;

import static org.junit.Assert.assertEquals;

import fr.dila.st.ui.services.ConfigUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.ThEngineService;
import fr.dila.st.ui.th.model.ThTemplate;
import fr.dila.st.ui.utils.URLUtils;
import java.io.File;
import java.io.OutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ File.class, URLUtils.class, WebEngine.class, STUIServiceLocator.class })
@PowerMockIgnore("javax.management.*")
public class SolonExceptionMapperTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private HttpServletRequest request;

    @Mock
    private ThEngineService engine;

    @Mock
    private ServletContext servlet;

    @Mock
    private File fileExist;

    @Mock
    private File fileUnknow;

    private SolonExceptionMapper exceptionMapper;

    @Mock
    private WebContext webContext;

    @Mock
    private ConfigUIService configService;

    @Before
    public void before() throws NoSuchFieldException, SecurityException, Exception {
        request = Mockito.mock(HttpServletRequest.class);
        servlet = Mockito.mock(ServletContext.class);
        engine = Mockito.mock(ThEngineService.class);
        fileExist = Mockito.mock(File.class);

        //Mock de la construction du path de la servlet
        Mockito
            .when(servlet.getRealPath(Mockito.anyString()))
            .thenAnswer(
                new Answer<String>() {

                    @Override
                    public String answer(InvocationOnMock invocation) throws Throwable {
                        Object[] args = invocation.getArguments();
                        return "mon/path/" + (String) args[0];
                    }
                }
            );

        //Mock de l'appel de la génération des templates thymeleaf
        Mockito
            .doAnswer(
                invocation -> {
                    Object[] args = invocation.getArguments();
                    ThTemplate template = (ThTemplate) args[0];
                    OutputStream outputStream = (OutputStream) args[1];
                    if ("error/error".equals(template.getName())) {
                        outputStream.write("ceci est la page d'erreur".getBytes());
                    } else {
                        outputStream.write("Une autre page".getBytes());
                    }
                    return null;
                }
            )
            .when(engine)
            .render(Mockito.any(), Mockito.any());

        //Renvoi du mock servlet
        Mockito.when(request.getServletContext()).thenReturn(servlet);

        //Creation de notre mapper
        exceptionMapper = new SolonExceptionMapper(request, engine);

        Mockito.when(fileExist.exists()).thenReturn(true);
        Mockito.when(fileUnknow.exists()).thenReturn(false);

        PowerMockito.mockStatic(WebEngine.class, URLUtils.class, STUIServiceLocator.class);
        Mockito.when(STUIServiceLocator.getConfigUIService()).thenReturn(configService);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
    }

    @Test
    public void testExceptionNonGeree() throws Exception {
        //On initialise que les fichiers n'existent pas
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(fileUnknow);

        //Appel du mapper avec une exception non gérée
        Response response = exceptionMapper.toResponse(new NoSuchFieldException());
        assertEquals("ceci est la page d'erreur", response.getEntity());
    }
}
