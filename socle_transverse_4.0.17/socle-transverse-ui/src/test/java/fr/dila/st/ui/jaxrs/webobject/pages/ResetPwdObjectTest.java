package fr.dila.st.ui.jaxrs.webobject.pages;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.ConfigDTO;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.ConfigUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PowerMockIgnore("javax.management.*")
@PrepareForTest({ STUIServiceLocator.class, SolonWebObject.class, STServiceLocator.class })
public class ResetPwdObjectTest {
    private static final String URL = "url";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private SpecificContext mockSpecificContext;

    @Mock
    private WebContext mockWebContext;

    @Mock
    private HttpServletRequest mockHttpServletRequest;

    @Mock
    private STParametreService sTParametreService;

    @Mock
    private ConfigUIService mockConfigUIService;

    private ConfigDTO configDTO = new ConfigDTO("name", "environmentName", "color", "backgroundColor", "version");

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(STServiceLocator.class);
        when(STUIServiceLocator.getConfigUIService()).thenReturn(mockConfigUIService);
        when(mockConfigUIService.getConfig()).thenReturn(configDTO);

        PowerMockito.whenNew(SpecificContext.class).withNoArguments().thenReturn(mockSpecificContext);

        when(STServiceLocator.getSTParametreService()).thenReturn(sTParametreService);
        when(sTParametreService.getParametreWithoutSession(Mockito.anyString())).thenReturn(URL);
    }

    @Test
    public void testGetResetPwd() {
        when(mockSpecificContext.getWebcontext()).thenReturn(mockWebContext);
        when(mockWebContext.getRequest()).thenReturn(mockHttpServletRequest);
        String expectedUser = "myuser";

        ThTemplate template = new ResetPwdObject().getResetPwd(expectedUser);

        assertThat(template.getData().get("username")).isEqualTo(expectedUser);
        assertThat(template.getData().get("config")).isEqualTo(configDTO);
        assertThat(template.getName()).isEqualTo("askResetPassword");
        assertThat(template.getData().get(STTemplateConstants.CONDITIONS_ACCESS_URL)).isEqualTo(URL);
    }
}
