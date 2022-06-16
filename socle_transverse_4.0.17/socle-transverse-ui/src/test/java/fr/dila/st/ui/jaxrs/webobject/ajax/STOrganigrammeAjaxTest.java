package fr.dila.st.ui.jaxrs.webobject.ajax;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import fr.dila.st.api.organigramme.EntiteNode;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.OrganigrammeElementDTO;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.STOrganigrammeManagerService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.impl.OrganigrammeTreeUIServiceImpl;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.impl.SolonAlertManager;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ STUIServiceLocator.class, STServiceLocator.class, WebEngine.class })
@PowerMockIgnore("javax.management.*")
public class STOrganigrammeAjaxTest {
    private static final String ORGANIGRAMME_ID = "1234";
    private static final String MIN_PARENT_ID = "entite-1234";

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private STOrganigrammeAjax page = new STOrganigrammeAjax();

    @Mock
    private OrganigrammeTreeUIService organigrammeTreeUIService;

    @Mock
    private WebContext webcontext;

    @Mock
    private SpecificContext mockContext;

    @Mock
    SolonAlertManager msgQueue;

    @Mock
    private UserSession userSession;

    @Mock
    private STOrganigrammeManagerService organigrammeManager;

    @Mock
    private OrganigrammeService organigrammeService;

    @Mock
    private OrganigrammeNode mockNode;

    @Mock
    private EntiteNode mockEntiteNode;

    @Captor
    private ArgumentCaptor<OrganigrammeElementDTO> organigrammeElementCaptor;

    @Before
    public void setUp() {
        PowerMockito.mockStatic(STUIServiceLocator.class);
        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(STServiceLocator.class);

        Mockito.when(STUIServiceLocator.getOrganigrammeTreeService()).thenReturn(organigrammeTreeUIService);
        Mockito.when(WebEngine.getActiveContext()).thenReturn(webcontext);
        Mockito.when(mockContext.getWebcontext()).thenReturn(webcontext);

        Mockito.when(webcontext.getUserSession()).thenReturn(userSession);
        Mockito.when(STUIServiceLocator.getSTOrganigrammeManagerService()).thenReturn(organigrammeManager);
        Mockito.when(STServiceLocator.getOrganigrammeService()).thenReturn(organigrammeService);
        Mockito.when(organigrammeService.getParentList(Mockito.any())).thenReturn(Lists.newArrayList(mockEntiteNode));

        Mockito
            .when(
                organigrammeService.getOrganigrammeNodeById(
                    Matchers.eq(ORGANIGRAMME_ID),
                    Matchers.any(OrganigrammeType.class)
                )
            )
            .thenReturn(mockNode);
        Mockito.when(mockEntiteNode.getId()).thenReturn(MIN_PARENT_ID);
        Mockito.when(mockEntiteNode.getType()).thenReturn(OrganigrammeType.MINISTERE);
        Mockito.when(mockNode.getType()).thenReturn(OrganigrammeType.POSTE);

        Mockito.when(mockContext.getMessageQueue()).thenReturn(msgQueue);

        Whitebox.setInternalState(page, "context", mockContext);
    }

    @Test
    public void testSelectArbre() {
        List<OrganigrammeElementDTO> dtos = new ArrayList<>();
        Mockito.when(organigrammeTreeUIService.getOrganigramme(Mockito.any())).thenReturn(dtos);
        ThTemplate template = page.getSelectArbre(null, null, false, null, false, null, null);
        assertNotNull(template);
        assertEquals("fragments/components/organigrammeSelectArbre", template.getName());
        assertEquals("ajaxLayout", template.getLayout());
        assertThat(template.getContext()).isEqualTo(mockContext);
        Mockito.verify(mockContext).putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, false);
        Mockito
            .verify(mockContext)
            .putInContextData(
                OrganigrammeTreeUIServiceImpl.OPEN_NODES_ID_KEY,
                OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_OPEN_NODES_KEY
            );
        Map<String, Object> datas = template.getData();
        assertNotNull(datas);
        assertEquals(10, datas.size());
        assertEquals(dtos, datas.get(STTemplateConstants.TREE_LIST));
        assertEquals(1, datas.get(STTemplateConstants.LEVEL));
        assertNull(datas.get(STTemplateConstants.CURRENT_ID));
        assertEquals(true, datas.get(STTemplateConstants.IS_OPEN));
        assertNull(datas.get(STTemplateConstants.SELECT_ID));
        assertNull(datas.get(STTemplateConstants.TYPE_SELECTION));
        assertEquals(false, datas.get(STTemplateConstants.ACTIVATE_POSTE_FILTER));
    }

    @Test
    public void testSelectArbreWithSelection() {
        List<OrganigrammeElementDTO> dtos = new ArrayList<>();
        Mockito.when(organigrammeTreeUIService.getOrganigramme(Mockito.any())).thenReturn(dtos);
        ThTemplate template = page.getSelectArbre(Arrays.asList("TYPE"), "SELECTID", true, null, false, null, null);
        assertNotNull(template);
        assertEquals("fragments/components/organigrammeSelectArbre", template.getName());
        assertEquals("ajaxLayout", template.getLayout());
        assertThat(template.getContext()).isEqualTo(mockContext);
        Mockito.verify(mockContext).putInContextData(OrganigrammeTreeUIServiceImpl.ACTIVATE_POSTE_FILTER_KEY, true);
        Mockito
            .verify(mockContext)
            .putInContextData(
                OrganigrammeTreeUIServiceImpl.OPEN_NODES_ID_KEY,
                OrganigrammeTreeUIServiceImpl.ORGANIGRAMME_TREE_OPEN_NODES_KEY
            );
        Map<String, Object> datas = template.getData();
        assertNotNull(datas);
        assertEquals(10, datas.size());
        assertEquals(dtos, datas.get(STTemplateConstants.TREE_LIST));
        assertEquals(1, datas.get(STTemplateConstants.LEVEL));
        assertEquals("SELECTID", datas.get(STTemplateConstants.CURRENT_ID));
        assertEquals(true, datas.get(STTemplateConstants.IS_OPEN));
        assertEquals("SELECTID", datas.get(STTemplateConstants.SELECT_ID));
        assertEquals("TYPE", datas.get(STTemplateConstants.TYPE_SELECTION));
        assertEquals(true, datas.get(STTemplateConstants.ACTIVATE_POSTE_FILTER));
    }

    @Test
    public void testDeleteNodeForMin() throws JSONException {
        Mockito
            .when(organigrammeTreeUIService.deleteNode(OrganigrammeType.MINISTERE, ORGANIGRAMME_ID, mockContext))
            .thenReturn(null);
        Mockito.when(mockContext.getAction(STActionEnum.REMOVE_MINISTERE)).thenReturn(new Action());
        JsonResponse result = (JsonResponse) page.deleteNode(ORGANIGRAMME_ID, "MIN", MIN_PARENT_ID).getEntity();
        assertEquals(SolonStatus.OK, result.getStatut());

        Mockito
            .verify(mockContext)
            .putInContextData(Mockito.eq(STContextDataKey.ORGANIGRAMME_NODE), organigrammeElementCaptor.capture());
        OrganigrammeElementDTO dto = organigrammeElementCaptor.getValue();
        assertThat(dto.getOrganigrammeNode()).isEqualTo(mockNode);
        assertThat(dto.getMinistereId()).isEqualTo(MIN_PARENT_ID);
        assertThat(dto.getParent().getOrganigrammeNode()).isEqualTo(mockEntiteNode);

        Mockito.verify(organigrammeManager).computeOrganigrammeActions(mockContext);
        Mockito.verify(organigrammeTreeUIService).deleteNode(OrganigrammeType.MINISTERE, ORGANIGRAMME_ID, mockContext);
    }

    @Test
    public void testDeleteNodeForPoste() throws JSONException {
        Mockito
            .when(organigrammeTreeUIService.deleteNode(OrganigrammeType.POSTE, ORGANIGRAMME_ID, mockContext))
            .thenReturn(null);
        Mockito.when(mockContext.getAction(STActionEnum.REMOVE_NODE)).thenReturn(new Action());
        JsonResponse result = (JsonResponse) page.deleteNode(ORGANIGRAMME_ID, "PST", MIN_PARENT_ID).getEntity();
        assertEquals(SolonStatus.OK, result.getStatut());

        Mockito
            .verify(mockContext)
            .putInContextData(Mockito.eq(STContextDataKey.ORGANIGRAMME_NODE), organigrammeElementCaptor.capture());
        OrganigrammeElementDTO dto = organigrammeElementCaptor.getValue();
        assertThat(dto.getOrganigrammeNode()).isEqualTo(mockNode);
        assertThat(dto.getMinistereId()).isEqualTo(MIN_PARENT_ID);
        assertThat(dto.getParent().getOrganigrammeNode()).isEqualTo(mockEntiteNode);
        Mockito.verify(organigrammeManager).computeOrganigrammeActions(mockContext);
        Mockito.verify(organigrammeTreeUIService).deleteNode(OrganigrammeType.POSTE, ORGANIGRAMME_ID, mockContext);
    }

    @Test
    public void testDeleteNodeNoAuth() throws JSONException {
        Mockito
            .when(organigrammeTreeUIService.deleteNode(OrganigrammeType.MINISTERE, ORGANIGRAMME_ID, mockContext))
            .thenReturn(null);
        Mockito.when(mockContext.getAction(STActionEnum.REMOVE_NODE)).thenReturn(new Action());
        Throwable throwable = catchThrowable(() -> page.deleteNode(ORGANIGRAMME_ID, "MIN", MIN_PARENT_ID));
        assertThat(throwable)
            .isExactlyInstanceOf(STAuthorizationException.class)
            .hasMessage("Accès à la ressource non autorisé : /admin/organigramme/deleteNode");

        Mockito.verifyZeroInteractions(organigrammeTreeUIService);
    }

    @Test
    public void testDeleteNode_problems() throws JSONException {
        String testFile = "/tmp/test/file.xls";
        Mockito.when(mockContext.getAction(STActionEnum.REMOVE_MINISTERE)).thenReturn(new Action());
        Mockito
            .when(organigrammeTreeUIService.deleteNode(OrganigrammeType.MINISTERE, ORGANIGRAMME_ID, mockContext))
            .thenReturn(testFile);
        JsonResponse result = (JsonResponse) page.deleteNode(ORGANIGRAMME_ID, "MIN", null).getEntity();
        assertEquals(SolonStatus.FUNCTIONAL_ERROR, result.getStatut());
        assertNotNull(result.getData());

        String fileVal = (String) result.getData();
        assertEquals(testFile, fileVal);
    }

    @Test
    public void testSuggestions() throws JsonProcessingException {
        String result = page.getSuggestions(null, null, false);
        assertEquals("[]", result);

        List<SuggestionDTO> dtos = new ArrayList<>();
        dtos.add(new SuggestionDTO("p-1", "Poste 1"));
        dtos.add(new SuggestionDTO("p-2", "Poste 2"));
        Mockito.when(organigrammeManager.getSuggestions(Mockito.any())).thenReturn(dtos);

        List<String> typesSelection = new ArrayList<>();
        typesSelection.add("PST");

        result = page.getSuggestions(typesSelection, "Pos", true);
        assertEquals("[{\"key\":\"p-1\",\"label\":\"Poste 1\"},{\"key\":\"p-2\",\"label\":\"Poste 2\"}]", result);
    }
}
