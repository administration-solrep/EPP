package fr.dila.st.ui.th.model;

import static fr.dila.st.ui.enums.STActionCategory.USER_MENU_USER_EDIT;
import static fr.dila.st.ui.enums.STContextDataKey.BREADCRUMB_BASE_LEVEL;
import static fr.dila.st.ui.enums.STContextDataKey.UNITE_STRUCTURELLE_FORM;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import fr.dila.st.ui.bean.AlertContainer;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.th.bean.UniteStructurelleForm;
import fr.dila.st.ui.th.enums.AlertType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.el.ValueExpression;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ELActionContext;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ WebEngine.class, UserSession.class, SpecificContext.class, Framework.class, ELActionContext.class })
@PowerMockIgnore("javax.management.*")
public class SpecificContextTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    private SpecificContext context;

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private WebContext webContext;

    @Mock
    private NuxeoPrincipal nuxeoPrincipal;

    @Mock
    private CoreSession session;

    @Mock
    private UserSession userSession;

    @Mock
    private DocumentModel document;

    @Mock
    private ActionManager actionService;

    @Mock
    private ValueExpression valueExpression;

    @Mock
    private ELActionContext elActionContext;

    @Before
    public void before() {
        context = new SpecificContext();

        PowerMockito.mockStatic(WebEngine.class);
        PowerMockito.mockStatic(Framework.class);

        when(webContext.getUserSession()).thenReturn(userSession);
        when(userSession.get(Breadcrumb.USER_SESSION_KEY)).thenReturn(new ArrayList<>());
        when(userSession.containsKey(Breadcrumb.USER_SESSION_KEY)).thenReturn(true);

        when(Framework.getService(ActionManager.class)).thenReturn(actionService);
    }

    @Test
    public void testSimpleConstructor() {
        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(nuxeoPrincipal);
        when(webContext.getCoreSession()).thenReturn(session);

        SpecificContext specificContext = new SpecificContext();

        assertNotNull(specificContext);
        assertNotNull(specificContext.getWebcontext());
        assertEquals(webContext, specificContext.getWebcontext());
        assertNotNull(specificContext.getSession());
        assertEquals(session, specificContext.getSession());
        assertNotNull(specificContext.getContextData());
        assertNotNull(specificContext.getMessageQueue());
        assertEquals(false, specificContext.getCopyDataToResponse());

        when(WebEngine.getActiveContext()).thenReturn(null);
        specificContext = new SpecificContext();

        assertNotNull(specificContext);
        assertNull(specificContext.getWebcontext());
        assertNull(specificContext.getSession());
        assertNull(specificContext.getCurrentDocument());
    }

    @Test
    public void testSetters() {
        assertNotNull(context);
        context.setWebcontext(null);
        context.setSession(null);
        context.setContextData(null);
        assertNull(context.getWebcontext());
        assertNull(context.getSession());
        assertNull(context.getContextData());
        assertNotNull(context.getMessageQueue());

        assertEquals(false, context.getCopyDataToResponse());
        assertNull(context.getCurrentDocument());

        context.setWebcontext(webContext);
        assertNotNull(context.getWebcontext());
        assertEquals(webContext, context.getWebcontext());

        context.setSession(session);
        assertNotNull(context.getSession());
        assertEquals(session, context.getSession());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("testKey", "testValue");
        context.setContextData(map);
        Map<String, Object> contextData = context.getContextData();
        assertNotNull(contextData);
        assertEquals(1, contextData.size());
        assertNotNull(contextData.get("testKey"));
        assertEquals("testValue", contextData.get("testKey"));

        context.setCopyDataToResponse(true);
        assertEquals(true, context.getCopyDataToResponse());

        when(document.getType()).thenReturn("dossier");
        context.setCurrentDocument(document);
        assertEquals("dossier", context.getCurrentDocument().getType());
    }

    @Test
    public void testNavigationContext() {
        context.setWebcontext(webContext);

        List<Breadcrumb> lstBreadCrumbs = context.getNavigationContext();

        assertTrue(CollectionUtils.isEmpty(lstBreadCrumbs));
        assertTrue(CollectionUtils.isEmpty(context.getNavigationContextTitle()));

        context.setNavigationContextTitle("Test");

        verify(userSession, Mockito.times(2)).put(Breadcrumb.USER_SESSION_KEY, Lists.newArrayList());
        verify(userSession).put(Breadcrumb.USER_SESSION_KEY, Lists.newArrayList(new Breadcrumb("Test", null, 1000)));

        context.setNavigationContextTitle(new Breadcrumb("Test 2", null, Breadcrumb.SUBTITLE_ORDER));

        verify(userSession, Mockito.times(2)).put(Breadcrumb.USER_SESSION_KEY, Lists.newArrayList());
        verify(userSession, Mockito.times(2))
            .put(
                Breadcrumb.USER_SESSION_KEY,
                Lists.newArrayList(new Breadcrumb("Test", null, 1000), new Breadcrumb("Test 2", null, 10000))
            );

        context.removeNavigationContextTitle(Breadcrumb.SUBTITLE_ORDER);
        verify(userSession, Mockito.times(2))
            .put(Breadcrumb.USER_SESSION_KEY, Lists.newArrayList(new Breadcrumb("Test", null, 1000)));

        context.removeNavigationContextTitle();
        verify(userSession, Mockito.times(3)).put(Breadcrumb.USER_SESSION_KEY, Lists.newArrayList());

        context.setNavigationContextTitle(new Breadcrumb("Test 1-1", null, Breadcrumb.TITLE_ORDER + 1));
        context.removeNavigationContextTitle(Breadcrumb.TITLE_ORDER);
        verify(userSession, Mockito.times(4)).put(Breadcrumb.USER_SESSION_KEY, Lists.newArrayList());
    }

    @Test
    public void testMessageQueue() {
        assertNotNull(context.getMessageQueue());
        assertEquals(Lists.newArrayList(), context.getMessageQueue().getInfoQueue());

        context.getMessageQueue().addInfoToQueue("Test");
        assertNotNull(context.getMessageQueue());
        assertEquals(
            Lists.newArrayList(new AlertContainer(Lists.newArrayList("Test"), null, AlertType.ALERT_INFO)),
            context.getMessageQueue().getInfoQueue()
        );
    }

    @Test
    public void testContextData() {
        when(WebEngine.getActiveContext()).thenReturn(webContext);
        when(webContext.getPrincipal()).thenReturn(nuxeoPrincipal);
        when(webContext.getCoreSession()).thenReturn(session);

        SpecificContext specificContext = new SpecificContext();

        assertNotNull(specificContext.getWebcontext());

        final String booleanKey = "booleanKey";
        specificContext.setProperty(booleanKey, TRUE);
        verify(webContext).setProperty(booleanKey, TRUE);

        final String stringKey = "stringKey";
        final String testString = "testString";
        specificContext.setProperty(stringKey, testString);
        verify(webContext).setProperty(stringKey, testString);

        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        final String mapKey = "mapKey";
        specificContext.setProperty(mapKey, map);
        verify(webContext).setProperty(mapKey, map);

        List<Map<String, Object>> list = new ArrayList<>();
        list.add(map);
        final String listKey = "listKey";
        specificContext.setProperty(listKey, list);
        verify(webContext).setProperty(listKey, list);
    }

    @Test
    public void testGetAction() throws Exception {
        STActionCategory category = USER_MENU_USER_EDIT;
        String valueToEval = "id=##{value}";
        String valueString = "value";
        String interpretedValue = "interpretedValue";
        String expectedNewProp = "id=interpretedValue";
        Map<String, Serializable> props = new HashMap<>();
        props.put("toEval", valueToEval);
        props.put("String", valueString);
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setProperties(props);
        actions.add(action);

        PowerMockito.whenNew(ELActionContext.class).withNoArguments().thenReturn(elActionContext);
        when(session.getPrincipal()).thenReturn(nuxeoPrincipal);
        when(actionService.getActions(category.getName(), elActionContext)).thenReturn(actions);
        when(elActionContext.evalExpression("#{value}", String.class)).thenReturn(interpretedValue);

        execAndAssert_getAction(valueString, expectedNewProp, action, category);
    }

    /**
     * Plusieurs expressions à évaluer dans valueToEval
     */
    @Test
    public void testGetAction_multipleEval() throws Exception {
        STActionCategory category = USER_MENU_USER_EDIT;
        String valueToEval = "id=##{value} || id=##{value2}";

        String valueString = "value";
        String interpretedValue = "interpretedValue";
        String interpretedValue2 = "interpretedValue2";

        String expectedNewProp = "id=interpretedValue || id=interpretedValue2";

        Map<String, Serializable> props = new HashMap<>();
        props.put("toEval", valueToEval);
        props.put("String", valueString);

        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setProperties(props);
        actions.add(action);

        PowerMockito.whenNew(ELActionContext.class).withNoArguments().thenReturn(elActionContext);
        when(session.getPrincipal()).thenReturn(nuxeoPrincipal);
        when(actionService.getActions(category.getName(), elActionContext)).thenReturn(actions);
        when(elActionContext.evalExpression("#{value}", String.class)).thenReturn(interpretedValue);
        when(elActionContext.evalExpression("#{value2}", String.class)).thenReturn(interpretedValue2);

        execAndAssert_getAction(valueString, expectedNewProp, action, category);
    }

    /**
     * Aucune expression à évaluer dans valueToEval
     */
    @Test
    public void testGetAction_noEval() throws Exception {
        STActionCategory category = USER_MENU_USER_EDIT;
        String valueToEval = "id=1";

        String valueString = "value";
        String interpretedValue = "interpretedValue";
        String interpretedValue2 = "interpretedValue2";

        String expectedNewProp = "id=1";

        Map<String, Serializable> props = new HashMap<>();
        props.put("toEval", valueToEval);
        props.put("String", valueString);

        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setProperties(props);
        actions.add(action);

        PowerMockito.whenNew(ELActionContext.class).withNoArguments().thenReturn(elActionContext);
        when(session.getPrincipal()).thenReturn(nuxeoPrincipal);
        when(actionService.getActions(category.getName(), elActionContext)).thenReturn(actions);
        when(elActionContext.evalExpression("#{value}", String.class)).thenReturn(interpretedValue);
        when(elActionContext.evalExpression("#{value2}", String.class)).thenReturn(interpretedValue2);

        execAndAssert_getAction(valueString, expectedNewProp, action, category);
    }

    /**
     * Cas hypothétique où l'expression est évaluée en une autre expression interprétable comme expression à évaluer !
     * => On veut surtout s'assurer que ça ne plante pas (boucle infinie)
     */
    @Test
    public void testGetAction_recursiveEval() throws Exception {
        STActionCategory category = USER_MENU_USER_EDIT;
        String valueToEval = "id=##{value}";
        String valueString = "value";
        String interpretedValue = "##{value}";
        String expectedNewProp = "id=##{value}";
        Map<String, Serializable> props = new HashMap<>();
        props.put("toEval", valueToEval);
        props.put("String", valueString);
        List<Action> actions = new ArrayList<>();
        Action action = new Action();
        action.setProperties(props);
        actions.add(action);

        PowerMockito.whenNew(ELActionContext.class).withNoArguments().thenReturn(elActionContext);
        when(session.getPrincipal()).thenReturn(nuxeoPrincipal);
        when(actionService.getActions(category.getName(), elActionContext)).thenReturn(actions);
        when(elActionContext.evalExpression("#{value}", String.class)).thenReturn(interpretedValue);

        execAndAssert_getAction(valueString, expectedNewProp, action, category);
    }

    private void execAndAssert_getAction(
        String valueString,
        String expectedNewProp,
        Action action,
        STActionCategory category
    ) {
        context.setCurrentDocument(document);
        context.setSession(session);
        List<Action> evaledActions = context.getActions(category);

        assertNotNull(evaledActions);
        assertEquals(1, evaledActions.size());
        assertEquals(action, evaledActions.get(0));
        Map<String, Serializable> resProps = evaledActions.get(0).getProperties();
        assertEquals(2, resProps.size());
        assertEquals(expectedNewProp, resProps.get("toEval"));
        assertEquals(valueString, resProps.get("String"));
        verify(elActionContext).setCurrentDocument(any(DocumentModel.class));
        verify(elActionContext).putAllLocalVariables(anyMap());
        verify(elActionContext).setDocumentManager(any(CoreSession.class));
        verify(elActionContext).setCurrentPrincipal(nuxeoPrincipal);
    }

    @Test
    public void testPutInContextData() {
        int value = 4;
        STContextDataKey key = BREADCRUMB_BASE_LEVEL;

        context.putInContextData(key, value);

        assertThat(context.getContextData().get(key.name())).isEqualTo(value);
    }

    @Test
    public void testPutInContextDataWithNullValue() {
        STContextDataKey key = BREADCRUMB_BASE_LEVEL;

        context.putInContextData(key, null);

        assertThat(context.getContextData()).containsEntry(key.name(), null);
    }

    @Test
    public void testPutInContextDataWithBadValue() {
        String badValue = "BadValue";
        STContextDataKey key = BREADCRUMB_BASE_LEVEL;

        Throwable throwable = catchThrowable(() -> context.putInContextData(key, badValue));
        assertThat(throwable)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage(
                "Le type de la donnée %s ne correspond pas à celui de la clé %s : %s",
                badValue,
                key,
                key.getValueType().getSimpleName()
            );
    }

    @Test
    public void testGetFromContextDataWithInteger() {
        int breadcrumbLevel = 4;
        STContextDataKey key = BREADCRUMB_BASE_LEVEL;

        context.getContextData().put(key.name(), breadcrumbLevel);

        Integer value = context.getFromContextData(key);

        assertThat(value).isEqualTo(breadcrumbLevel);
    }

    @Test
    public void testGetFromContextDataWithUniteStructurelleForm() {
        UniteStructurelleForm uniteStructurelleForm = new UniteStructurelleForm();
        String libelle = "test libelle";
        uniteStructurelleForm.setLibelle(libelle);
        String identifiant = "identifiant";
        uniteStructurelleForm.setIdentifiant(identifiant);
        String type = "type";
        uniteStructurelleForm.setType(type);

        STContextDataKey key = UNITE_STRUCTURELLE_FORM;

        context.getContextData().put(key.name(), uniteStructurelleForm);

        UniteStructurelleForm value = context.getFromContextData(key);

        assertThat(value).isEqualTo(uniteStructurelleForm);
    }

    @Test
    public void testGetFromContextDataWithNullValue() {
        STContextDataKey key = BREADCRUMB_BASE_LEVEL;

        context.getContextData().put(key.name(), null);

        Integer value = context.getFromContextData(key);

        assertThat(value).isNull();
    }

    @Test
    public void testContainsKeyInContextData() {
        STContextDataKey key = BREADCRUMB_BASE_LEVEL;

        context.getContextData().put(key.name(), null);

        assertThat(context.containsKeyInContextData(key)).isTrue();
    }

    @Test
    public void testContainsKeyInContextDataWithUnknownKey() {
        assertThat(context.containsKeyInContextData(BREADCRUMB_BASE_LEVEL)).isFalse();
    }

    @Test
    public void setCurrentDocumentWithIdDocument() {
        String idDocument = "a6715757-ede1-4740-8f55-641f957cb651";

        context.setSession(session);
        when(session.getDocument(new IdRef(idDocument))).thenReturn(document);

        context.setCurrentDocument(idDocument);

        assertThat(context.getCurrentDocument()).isEqualTo(document);
    }

    @Test
    public void setCurrentDocumentWithInvalidIdDocument() {
        context.setCurrentDocument(" ");

        assertThat(context.getCurrentDocument()).isNull();
    }
}
