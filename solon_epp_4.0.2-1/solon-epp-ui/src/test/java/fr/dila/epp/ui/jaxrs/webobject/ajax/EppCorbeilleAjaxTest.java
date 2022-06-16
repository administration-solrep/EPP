package fr.dila.epp.ui.jaxrs.webobject.ajax;

import static fr.dila.epp.ui.services.impl.EppCorbeilleMenuServiceImpl.ACTIVE_KEY;
import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import fr.dila.epp.ui.bean.EppMessageDTO;
import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.services.MessageListUIService;
import fr.dila.epp.ui.services.SelectValueUIService;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.services.actions.EvenementTypeActionService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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
        SpecificContext.class,
        SolonWebObject.class,
        SolonEppUIServiceLocator.class,
        SolonEppActionsServiceLocator.class,
        UserSessionHelper.class,
        WebEngine.class
    }
)
@PowerMockIgnore("javax.management.*")
public class EppCorbeilleAjaxTest {
    EppCorbeilleAjax controlleur;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    SpecificContext context;

    @Mock
    private WebContext webcontext;

    @Mock
    private HttpServletRequest request;

    @Mock
    ThTemplate template;

    @Mock
    MessageListUIService messageListUIService;

    @Mock
    EvenementTypeActionService evenementTypeActionService;

    @Mock
    SelectValueUIService selectValueUIService;

    @Before
    public void before() throws Exception {
        PowerMockito.mockStatic(UserSessionHelper.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(SolonEppUIServiceLocator.class);
        PowerMockito.mockStatic(SolonEppActionsServiceLocator.class);

        when(SolonEppUIServiceLocator.getMessageListUIService()).thenReturn(messageListUIService);
        when(SolonEppActionsServiceLocator.getEvenementTypeActionService()).thenReturn(evenementTypeActionService);
        when(SolonEppUIServiceLocator.getSelectValueUIService()).thenReturn(selectValueUIService);
        PowerMockito.whenNew(SpecificContext.class).withAnyArguments().thenReturn(context);
        when(context.getWebcontext()).thenReturn(webcontext);
        when(webcontext.getRequest()).thenReturn(request);
        controlleur = new EppCorbeilleAjax();
    }

    @Test
    public void testDoHome() {
        MessageList msgList = new MessageList();
        List<EppMessageDTO> liste = new ArrayList<>();
        liste.add(
            new EppMessageDTO("1", "objet", "", "emetteur", "destinataire", "", "communication", "1.0", "25/07/2020")
        );
        msgList.setListe(liste);
        when(messageListUIService.getMessageListForCorbeille(context)).thenReturn(msgList);

        ThTemplate template = controlleur.doHome("1");

        verify(context, times(1)).putInContextData(ID, "1");
        //verify(context, times(1)).putInContextData(MESSAGE_LIST_FORM, msgForm);
        PowerMockito.verifyStatic(times(1));
        UserSessionHelper.putUserSessionParameter(context, ACTIVE_KEY, "1");

        assertNotNull(template);
        assertNotNull(template.getContext());

        assertEquals("1", template.getData().get(STTemplateConstants.CORBEILLE));
        assertEquals(1, msgList.getListe().size());
        assertTrue(msgList.getListe().get(0) instanceof EppMessageDTO);
        assertEquals("25/07/2020", msgList.getListe().get(0).getDate());

        assertEquals(10, msgList.getListeColones().size());
        assertEquals("corbeille.communication.table.header.iddossier", msgList.getListeColones().get(1).getLabel());

        assertEquals("/corbeille/consulter", template.getData().get(STTemplateConstants.DATA_URL));
        assertEquals("/ajax/corbeille/search", template.getData().get(STTemplateConstants.DATA_AJAX_URL));
    }
}
