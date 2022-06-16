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
import javax.ws.rs.WebApplicationException;
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
@PrepareForTest(
    {
        AbstractExceptionMapper.class,
        File.class,
        URLUtils.class,
        WebEngine.class,
        WebContext.class,
        STUIServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class WebApplicationExceptionMapperTest {

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

    @Mock
    private WebContext webContext;

    private WebApplicationExceptionMapper exceptionMapper;

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
                    } else if ("error/error404".equals(template.getName())) {
                        outputStream.write("Erreur 404".getBytes());
                    } else if ("error/error400".equals(template.getName())) {
                        outputStream.write("Page parent type erreur 400".getBytes());
                    } else if ("error/error500".equals(template.getName())) {
                        outputStream.write("Erreur 500".getBytes());
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
        exceptionMapper = new WebApplicationExceptionMapper(request, engine);

        Mockito.when(fileExist.exists()).thenReturn(true);
        Mockito.when(fileUnknow.exists()).thenReturn(false);

        PowerMockito.mockStatic(URLUtils.class, WebEngine.class, STUIServiceLocator.class);

        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(STUIServiceLocator.getConfigUIService()).thenReturn(configService);
    }

    @Test
    public void testFileExist() throws Exception {
        //On initialise que les fichiers n'existent pas
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(fileUnknow);

        //On indique que le fichier 404 existe
        PowerMockito
            .whenNew(File.class)
            .withArguments("mon/path/th-templates/error/error404.html")
            .thenReturn(fileExist);
        Response response = exceptionMapper.toResponse(new WebApplicationException(404));
        assertEquals("Erreur 404", response.getEntity());

        //On indique que le fichier 500 existe
        PowerMockito
            .whenNew(File.class)
            .withArguments("mon/path/th-templates/error/error500.html")
            .thenReturn(fileExist);
        response = exceptionMapper.toResponse(new WebApplicationException(500));
        assertEquals("Erreur 500", response.getEntity());

        //On indique que n'importe quel fichier existe
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(fileExist);
        response = exceptionMapper.toResponse(new WebApplicationException(302));
        assertEquals("Une autre page", response.getEntity());
    }

    @Test
    public void testFileDoesntExist() throws Exception {
        //On initialise que les fichiers n'existent pas
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(fileUnknow);

        //On vérifie qu'on a l'erreur par defaut
        Response response = exceptionMapper.toResponse(new WebApplicationException(202));
        assertEquals("ceci est la page d'erreur", response.getEntity());
    }

    @Test
    public void testFileDoesntExistButParentExist() throws Exception {
        //On initialise que les fichiers n'existent pas
        PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(fileUnknow);

        //On indique que le fichier 404 n'existe pas mais que le fichier parent 400 existe
        PowerMockito
            .whenNew(File.class)
            .withArguments("mon/path/th-templates/error/error404.html")
            .thenReturn(fileUnknow);
        PowerMockito
            .whenNew(File.class)
            .withArguments("mon/path/th-templates/error/error400.html")
            .thenReturn(fileExist);
        Response response = exceptionMapper.toResponse(new WebApplicationException(404));
        assertEquals("Page parent type erreur 400", response.getEntity());

        //On indique que le fichier 500 existe
        PowerMockito
            .whenNew(File.class)
            .withArguments("mon/path/th-templates/error/error500.html")
            .thenReturn(fileExist);
        response = exceptionMapper.toResponse(new WebApplicationException(503));
        assertEquals("Erreur 500", response.getEntity());
    }
}
