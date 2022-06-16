package fr.dila.ss.ui.jaxrs.webobject.page.admin.modele;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import fr.dila.ss.api.service.FeuilleRouteModelService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.contentview.ModeleFDRPageProvider;
import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.ss.ui.services.impl.SSModeleFdrListUIServiceImpl;
import fr.dila.ss.ui.th.bean.ModeleFDRListForm;
import fr.dila.st.core.util.STPageProviderHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.HashMap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.mock.SerializableMode;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.core.CoreQueryPageProviderDescriptor;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        SSServiceLocator.class,
        SSServiceLocator.class,
        Framework.class,
        ServiceUtil.class,
        STPageProviderHelper.class,
        SSActionsServiceLocator.class
    }
)
@PowerMockIgnore("javax.management.*")
public class ReponsesModeleFdrListUIServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private SSModeleFdrListUIServiceImpl service;

    @Mock
    private SpecificContext context;

    private CoreSession session;

    @Mock
    private PageProviderService pageProviderService;

    @Mock
    private CoreQueryPageProviderDescriptor descriptor;

    @Mock
    private FeuilleRouteModelService feuilleRouteModelService;

    @Mock
    private ModeleFeuilleRouteActionService modeleFeuilleRouteActionService;

    @Before
    public void before() {
        service = new SSModeleFdrListUIServiceImpl();

        PowerMockito.mockStatic(SSServiceLocator.class);
        PowerMockito.mockStatic(Framework.class);
        PowerMockito.mockStatic(ServiceUtil.class);
        PowerMockito.mockStatic(SSServiceLocator.class);
        PowerMockito.mockStatic(STPageProviderHelper.class);
        PowerMockito.mockStatic(SSActionsServiceLocator.class);

        when(SSServiceLocator.getFeuilleRouteModelService()).thenReturn(feuilleRouteModelService);

        session = mock(CoreSession.class, withSettings().serializable(SerializableMode.ACROSS_CLASSLOADERS));
        when(context.getSession()).thenReturn(session);

        //ModeleFDRPageProvider provider = PowerMockito.spy(new ModeleFDRPageProvider());
        ModeleFDRPageProvider provider = Mockito.spy(new ModeleFDRPageProvider());
        provider.setProperties(new HashMap<>());
        provider.setDefinition(descriptor);
        when(
                STPageProviderHelper.getPageProvider(
                    Mockito.eq("modeleFDRPageProvider"),
                    Mockito.eq(session),
                    Mockito.anyVararg()
                )
            )
            .thenReturn(provider);

        when(SSServiceLocator.getFeuilleRouteModelService()).thenReturn(feuilleRouteModelService);
        when(feuilleRouteModelService.getFeuilleRouteModelFolderId(session)).thenReturn("feuilleRouteModelFolderId");
        when(Framework.getService(PageProviderService.class)).thenReturn(pageProviderService);
        when(pageProviderService.getPageProviderDefinition("modeleFDRPageProvider")).thenReturn(descriptor);
        when(SSActionsServiceLocator.getModeleFeuilleRouteActionService()).thenReturn(modeleFeuilleRouteActionService);
        when(ServiceUtil.getRequiredService(ModeleFeuilleRouteActionService.class))
            .thenReturn(modeleFeuilleRouteActionService);
        when(modeleFeuilleRouteActionService.getContentViewCriteriaSubstitution(any()))
            .thenReturn("contentViewCriteria");
    }

    @Test
    public void testBuildModeleFDRSubstitutionProvider() {
        ModeleFDRListForm form = new ModeleFDRListForm();
        form.noPagination();
        ModeleFDRPageProvider provider = service.buildModeleFDRSubstitutionProvider(
            "modeleFDRPageProvider",
            context,
            form
        );
        assertEquals(descriptor, provider.getDefinition());
        assertNull(form.getIntitule());
        assertNotNull(provider.getSortInfos());
        assertEquals(0, provider.getSortInfos().size());
        assertEquals(0, provider.getPageSize());
        assertEquals(0, provider.getCurrentPageIndex());
        assertEquals(0, provider.getMaxPageSize());
        assertNotNull(provider.getParameters());
        assertEquals(2, provider.getParameters().length);
        assertEquals("feuilleRouteModelFolderId", provider.getParameters()[0]);
        assertEquals("contentViewCriteria", provider.getParameters()[1]);
    }
}
