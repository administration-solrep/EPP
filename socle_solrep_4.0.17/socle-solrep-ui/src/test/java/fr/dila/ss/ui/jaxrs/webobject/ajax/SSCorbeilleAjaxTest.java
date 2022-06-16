package fr.dila.ss.ui.jaxrs.webobject.ajax;

import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.ACTIVE_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.MASQUER_CORBEILLES_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.MODE_TRI_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.POSTE_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.SELECTED_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.SELECTION_VALIDEE_KEY;
import static fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl.USER_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import fr.dila.ss.api.enums.TypeRegroupement;
import fr.dila.ss.ui.bean.MailboxListDTO;
import fr.dila.ss.ui.services.SSMailboxListComponentService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ SSUIServiceLocator.class, WebEngine.class, Framework.class })
@PowerMockIgnore("javax.management.*")
public class SSCorbeilleAjaxTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    SSCorbeilleAjax page = new SSCorbeilleAjax();

    @Mock
    SSMailboxListComponentService mailboxService;

    @Mock
    WebContext webContext;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ActionManager actionService;

    @Before
    public void before() {
        PowerMockito.mockStatic(SSUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(Framework.class);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webContext);
        Mockito.when(Framework.getService(ActionManager.class)).thenReturn(actionService);
        Mockito.when(webContext.getRequest()).thenReturn(request);
        Mockito.when(request.getQueryString()).thenReturn("");
    }

    @Test
    public void testMailbox() {
        Mockito.when(SSUIServiceLocator.getSSMailboxListComponentService()).thenReturn(mailboxService);
        Map<String, Object> map = new HashMap<>();
        MailboxListDTO dto = new MailboxListDTO();
        map.put("mailboxListMap", dto);
        map.put(ACTIVE_KEY, "test");
        Mockito.when(mailboxService.getData(Mockito.any())).thenReturn(map);

        ThTemplate template = page.getMailbox(
            TypeRegroupement.PAR_MINISTERE,
            "key",
            "selectionPost",
            "selectionUser",
            false,
            false
        );
        assertNotNull(template);
        assertEquals("fragments/components/arbre", template.getName());
        assertEquals("ajaxLayout", template.getLayout());

        SpecificContext context = template.getContext();
        assertNotNull(context);
        assertNull(context.getSession());
        assertEquals(6, context.getContextData().size());
        assertEquals(TypeRegroupement.PAR_MINISTERE, context.getFromContextData(MODE_TRI_KEY));
        assertEquals("key", context.getFromContextData(SELECTED_KEY));
        assertEquals("selectionPost", context.getFromContextData(POSTE_KEY));
        assertEquals("selectionUser", context.getFromContextData(USER_KEY));
        assertEquals(false, context.getFromContextData(SELECTION_VALIDEE_KEY));
        assertEquals(false, context.getFromContextData(MASQUER_CORBEILLES_KEY));

        Map<String, Object> datas = template.getData();
        assertNotNull(datas);
        assertEquals(7, datas.size());
        assertEquals(dto.getChilds(), datas.get(STTemplateConstants.TREE_LIST));
        assertEquals(1, datas.get(STTemplateConstants.LEVEL));
        assertEquals("", datas.get(SSTemplateConstants.MY_ID));
        assertEquals("", datas.get(SSTemplateConstants.TOGGLER_ID));
        assertEquals(true, datas.get(STTemplateConstants.IS_OPEN));
        assertEquals("test", datas.get(ACTIVE_KEY));
        assertEquals("corbeilles", datas.get(STTemplateConstants.TITLE));

        template =
            page.getMailbox(
                TypeRegroupement.PAR_POSTE,
                "keyTest",
                "selectionPosteTest",
                "selectionUserTest",
                true,
                true
            );

        context = template.getContext();
        assertNotNull(context.getContextData());
        assertEquals(6, context.getContextData().size());
        assertEquals(TypeRegroupement.PAR_POSTE, context.getFromContextData(MODE_TRI_KEY));
        assertEquals("keyTest", context.getFromContextData(SELECTED_KEY));
        assertEquals("selectionPosteTest", context.getFromContextData(POSTE_KEY));
        assertEquals("selectionUserTest", context.getFromContextData(USER_KEY));
        assertEquals(true, context.getFromContextData(SELECTION_VALIDEE_KEY));
        assertEquals(true, context.getFromContextData(MASQUER_CORBEILLES_KEY));
    }
}
