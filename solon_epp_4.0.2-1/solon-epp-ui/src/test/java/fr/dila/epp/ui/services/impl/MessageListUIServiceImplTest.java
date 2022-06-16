package fr.dila.epp.ui.services.impl;

import static fr.dila.st.ui.enums.STContextDataKey.ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import fr.dila.epp.ui.bean.EppMessageDTO;
import fr.dila.epp.ui.bean.MessageList;
import fr.dila.epp.ui.contentview.CorbeillePageProvider;
import fr.dila.epp.ui.helper.CorbeilleProviderHelper;
import fr.dila.epp.ui.services.MessageListUIService;
import fr.dila.epp.ui.services.actions.CorbeilleActionService;
import fr.dila.epp.ui.services.actions.SolonEppActionsServiceLocator;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.CorbeilleTypeService;
import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.STPageProviderHelper;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest(
    {
        CorbeilleProviderHelper.class,
        SolonEppActionsServiceLocator.class,
        SolonEppServiceLocator.class,
        STPageProviderHelper.class
    }
)
@PowerMockIgnore("javax.management.*")
public class MessageListUIServiceImplTest {
    MessageListUIService service;

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    CorbeillePageProvider provider;

    @Mock
    CorbeilleActionService corbeilleActionService;

    @Mock
    PageProviderService pageProviderService;

    @Mock
    PageProviderDefinition ppDefinition;

    @Mock
    SpecificContext context;

    @Mock
    CoreSession session;

    @Mock
    EppPrincipal principal;

    @Mock
    CorbeilleTypeService corbeilleService;

    @Before
    public void before() {
        PowerMockito.mockStatic(SolonEppActionsServiceLocator.class);
        PowerMockito.mockStatic(SolonEppServiceLocator.class);
        when(SolonEppActionsServiceLocator.getCorbeilleActionService()).thenReturn(corbeilleActionService);
        when(SolonEppServiceLocator.getCorbeilleTypeService()).thenReturn(corbeilleService);
        PowerMockito.mockStatic(CorbeilleProviderHelper.class);
        PowerMockito.mockStatic(STPageProviderHelper.class);
        when(STPageProviderHelper.getPageProvider(Mockito.anyString(), Mockito.any(), Mockito.anyVararg()))
            .thenReturn(provider);

        service = new MessageListUIServiceImpl();
        when(context.getSession()).thenReturn(session);
        when(session.getPrincipal()).thenReturn(principal);
        when(corbeilleService.getCorbeilleInstitutionTree(anyString())).thenReturn(buildFakeNode());
    }

    private List<CorbeilleNode> buildFakeNode() {
        List<CorbeilleNode> lstCorbeilles = new ArrayList<>();

        CorbeilleNode corbeille1 = new CorbeilleNode();
        CorbeilleNode corbeille2 = new CorbeilleNode();
        CorbeilleNode corbeille3 = new CorbeilleNode();

        corbeille1.setName("1");
        corbeille1.setLabel("Première corbeille");
        lstCorbeilles.add(corbeille1);

        corbeille2.setName("2");
        corbeille2.setLabel("Deuxième corbeille");
        corbeille3.setName("3");
        corbeille3.setLabel("Enfant");
        corbeille2.setCorbeilleNodeList(Lists.newArrayList(corbeille3));

        lstCorbeilles.add(corbeille2);

        return lstCorbeilles;
    }

    @Test
    public void testGetMessageListForCorbeille() {
        EppMessageDTO msgDTO = new EppMessageDTO(
            "idDossier",
            "objetDossier",
            "lecture",
            "emetteur",
            "destinataire",
            "copie",
            "communication",
            "version",
            "date"
        );
        when(context.getFromContextData(ID)).thenReturn("3");
        when(provider.getCurrentPage()).thenReturn(Collections.singletonList(msgDTO));
        when(provider.getResultsCount()).thenReturn(1L);
        when(provider.getDefinition()).thenReturn(ppDefinition);

        MessageList msgList = service.getMessageListForCorbeille(context);
        assertNotNull(msgList);
        assertEquals(1, msgList.getNbTotal().intValue());
        assertNotNull(msgList.getListe());
        assertEquals(1, msgList.getListe().size());
        assertEquals(msgDTO, msgList.getListe().get(0));
        assertEquals("Deuxième corbeille : Enfant", msgList.getTitre());
    }
}
