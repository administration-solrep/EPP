package fr.dila.st.ui.utils;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.web.common.vh.VirtualHostHelper;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ VirtualHostHelper.class })
@PowerMockIgnore("javax.management.*")
public class URLUtilsTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    HttpServletRequest request;

    private static final String X_FORWARDED_HOST = "x-forwarded-host";

    @Before
    public void before() {
        request = Mockito.mock(HttpServletRequest.class);
        PowerMockito.mockStatic(VirtualHostHelper.class);
    }

    @Test
    public void testExternalResourceEtSlash() {
        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn("header");

        //Test avec double slash
        String contextGenerated = URLUtils.generateContextPath("/reponses/", "/css/test.css", request);
        assertEquals("/reponses", contextGenerated);

        //Test sans slash à la ressource mais avec slash au path
        contextGenerated = URLUtils.generateContextPath("/reponses/", "css/test.css", request);
        assertEquals("/reponses/", contextGenerated);

        //Test sans slash au context mais avec slash à la resoources
        contextGenerated = URLUtils.generateContextPath("/reponses", "/css/test.css", request);
        assertEquals("/reponses", contextGenerated);

        //Test sans slash
        contextGenerated = URLUtils.generateContextPath("/reponses", "css/test.css", request);
        assertEquals("/reponses/", contextGenerated);

        //On enlève le header Apache et on vérifie qu'on a les même résultats
        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn(null);

        //Test avec double slash
        contextGenerated = URLUtils.generateContextPath("/reponses/", "/css/test.css", request);
        assertEquals("/reponses", contextGenerated);

        //Test sans slash à la ressource mais avec slash au path
        contextGenerated = URLUtils.generateContextPath("/reponses/", "css/test.css", request);
        assertEquals("/reponses/", contextGenerated);

        //Test sans slash au context mais avec slash à la resoources
        contextGenerated = URLUtils.generateContextPath("/reponses", "/css/test.css", request);
        assertEquals("/reponses", contextGenerated);

        //Test sans slash
        contextGenerated = URLUtils.generateContextPath("/reponses", "css/test.css", request);
        assertEquals("/reponses/", contextGenerated);
    }

    @Test
    public void testContextPathPage() {
        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn("header");

        //Test d'une page avec slash via Apache
        String contextGenerated = URLUtils.generateContextPath("/reponses/", "/login", request);
        assertEquals("/reponses", contextGenerated);

        //Test d'une page sans slash via Apache
        contextGenerated = URLUtils.generateContextPath("/reponses/", "login", request);
        assertEquals("/reponses/", contextGenerated);

        //On enlève le header Apache et on vérifie qu'on a le même résultat
        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn(null);

        //Test d'une page avec slash sans Apache
        contextGenerated = URLUtils.generateContextPath("/reponses", "/login", request);
        assertEquals("/reponses/site/app-ui", contextGenerated);

        //Test d'une page sans slash sans Apache
        contextGenerated = URLUtils.generateContextPath("/reponses", "login", request);
        assertEquals("/reponses/site/app-ui/", contextGenerated);
    }

    @Test
    public void testContextPathWithoutResource() {
        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn("header");
        //Test d'uneressoures vide
        String contextGenerated = URLUtils.generateContextPath("/reponses/", "", request);
        assertEquals("/reponses/", contextGenerated);
    }

    @Test
    public void testRedirect() {
        Mockito
            .when(VirtualHostHelper.getBaseURL(Mockito.any(HttpServletRequest.class)))
            .thenReturn("http://reponse/serveur");

        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn("header");

        //Test d'une redirection avec slash avec Apache
        String contextGenerated = URLUtils.generateRedirectPath("/login", request);
        assertEquals("http://reponse/serveur/login", contextGenerated);

        //Test d'une redirection sans slash avec Apache
        contextGenerated = URLUtils.generateRedirectPath("login", request);
        assertEquals("http://reponse/serveur/login", contextGenerated);

        //Suppresiion du header Apache
        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn(null);

        //Test d'une redirection avec slash sans Apache
        contextGenerated = URLUtils.generateRedirectPath("/login", request);
        assertEquals("http://reponse/serveur/site/app-ui/login", contextGenerated);

        //Test d'une redirection sans slash sans Apache
        contextGenerated = URLUtils.generateRedirectPath("login", request);
        assertEquals("http://reponse/serveur/site/app-ui/login", contextGenerated);
    }

    @Test
    public void testconstructPagePath() {
        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn("header");

        //Test d'une construction de path d'une page avec slash avec Apache (on renvoi tel quel)
        String contextGenerated = URLUtils.constructPagePath("/login", request);
        assertEquals("/login", contextGenerated);
        contextGenerated = URLUtils.constructPagePath("login", request);
        assertEquals("login", contextGenerated);

        Mockito.when(request.getHeader(X_FORWARDED_HOST)).thenReturn(null);

        //Test d'une construction de path d'une page avec slash sans Apache (Slash demandé on met un slash au début)
        contextGenerated = URLUtils.constructPagePath("/login", request);
        assertEquals("/site/app-ui/login", contextGenerated);

        //Test d'une construction de path d'une page sans slash sans Apache (Pas de slash demandé on ne met pas de slash au début)
        contextGenerated = URLUtils.constructPagePath("login", request);
        assertEquals("site/app-ui/login", contextGenerated);

        //Test d'une construction de path d'une ressources css => on renvoit tel quel
        contextGenerated = URLUtils.constructPagePath("/css/moncss.css", request);
        assertEquals("/css/moncss.css", contextGenerated);

        //Test avec un path vide
        contextGenerated = URLUtils.constructPagePath(" ", request);
        assertEquals(" ", contextGenerated);
    }
}
