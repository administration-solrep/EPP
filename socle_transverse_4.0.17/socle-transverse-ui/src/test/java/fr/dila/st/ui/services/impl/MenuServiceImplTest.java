package fr.dila.st.ui.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import avro.shaded.com.google.common.collect.Lists;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.ConfigDTO;
import fr.dila.st.ui.bean.Menu;
import fr.dila.st.ui.services.ConfigUIService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;
import org.nuxeo.ecm.webengine.model.WebContext;
import org.nuxeo.ecm.webengine.session.UserSession;
import org.nuxeo.runtime.api.Framework;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.agent.PowerMockAgent;
import org.powermock.modules.junit4.rule.PowerMockRule;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest({ ServiceUtil.class, STServiceLocator.class, Framework.class })
@PowerMockIgnore("javax.management.*")
public class MenuServiceImplTest {

    static {
        PowerMockAgent.initializeIfNeeded();
    }

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Mock
    private SpecificContext context;

    @Mock
    private ActionManager actionService;

    @Mock
    private ConfigUIService configUIService;

    @Mock
    private CoreSession session;

    @Mock
    private WebContext nuxeoContext;

    @Mock
    private UserSession userSession;

    private MenuServiceImpl menuService;
    private ConfigDTO config;

    @Mock
    private Action mockAction;

    @Before
    public void before() {
        PowerMockito.mockStatic(ServiceUtil.class);
        when(ServiceUtil.getRequiredService(ActionManager.class)).thenReturn(actionService);

        PowerMockito.mockStatic(STUIServiceLocator.class);
        when(STUIServiceLocator.getConfigUIService()).thenReturn(configUIService);

        config = new ConfigDTO("plateformeName", "plateformeLibelle", "color", "backgroundColor", "version");
        when(configUIService.getConfig()).thenReturn(config);

        when(session.getPrincipal()).thenReturn(null);
        when(context.getSession()).thenReturn(session);
        when(context.getWebcontext()).thenReturn(nuxeoContext);
        when(nuxeoContext.getUserSession()).thenReturn(userSession);

        PowerMockito.mockStatic(Framework.class);
        when(Framework.expandVars(Matchers.anyString()))
            .thenAnswer(invocation -> invocation.getArgumentAt(0, String.class));

        when(actionService.getAction(Matchers.anyString(), Matchers.any(), Matchers.anyBoolean()))
            .thenReturn(mockAction);

        menuService = new MenuServiceImpl();
    }

    @Test
    public void testGetDataMenuEmpty() {
        List<Action> actionList = new ArrayList<>();
        when(actionService.getActions(Matchers.any(), Matchers.any())).thenReturn(actionList);
        Map<String, Object> returnMap = menuService.getData(context);
        assertNotNull(returnMap.get("menuList"));
        assertEquals(2, returnMap.size());
        assertEquals(0, ((Menu[]) returnMap.get("menuList")).length);
        assertEquals(config, returnMap.get("config"));
    }

    @Test
    public void testGetDataSimpleMenu() {
        List<Action> actionList = new ArrayList<>();
        Action action1 = new Action();
        action1.setLabel("monAction");
        action1.setLink("monActionLink");
        actionList.add(action1);

        Action action2 = new Action();
        action2.setLabel("monAction2");
        action2.setLink("monActionLink2");
        actionList.add(action2);

        Action action3 = new Action();
        action3.setLabel("monAction3");
        action3.setLink("monActionLink3");
        actionList.add(action3);

        when(actionService.getActions(Matchers.any(), Matchers.any())).thenReturn(actionList);

        Map<String, Object> returnMap = menuService.getData(context);
        assertNotNull(returnMap.get("menuList"));
        Menu[] menus = ((Menu[]) returnMap.get("menuList"));
        assertEquals(3, menus.length);
        assertEquals(false, menus[2].getIsCurrent());
        assertEquals("monAction3", menus[2].getTitleKey());
        assertNull(menus[2].getChilds());
        assertEquals("monActionLink3", menus[2].getUrl());
    }

    @Test
    public void testGetDataChildMenu() {
        List<Action> actionList = new ArrayList<>();

        Action action1 = new Action();
        action1.setLabel("monAction");
        action1.setLink("monActionLink");
        action1.setType("menuSection");
        Map<String, Serializable> properties = new HashMap<>();
        properties.put("submenu", "monSousMenu");
        action1.setProperties(properties);
        actionList.add(action1);

        Action action2 = new Action();
        action2.setLabel("monAction2");
        action2.setLink("monActionLink2");
        actionList.add(action2);

        List<Action> actionChild = new ArrayList<>();
        Action action3 = new Action();
        action3.setLabel("monActionChild1");
        action3.setLink("monActionChildLink1");
        actionChild.add(action3);

        when(actionService.getActions(Matchers.matches("MAIN_MENU"), Matchers.any())).thenReturn(actionList);
        when(actionService.getActions(Matchers.matches("monSousMenu"), Matchers.any())).thenReturn(actionChild);

        Map<String, Object> returnMap = menuService.getData(context);
        assertNotNull(returnMap.get("menuList"));
        Menu[] menus = ((Menu[]) returnMap.get("menuList"));
        assertEquals(2, menus.length);
        assertEquals(false, menus[0].getIsCurrent());
        assertEquals("monAction", menus[0].getTitleKey());
        assertNull(menus[0].getUrl());
        assertNotNull(menus[0].getChilds());
        Menu[] childs = menus[0].getChilds();
        assertEquals(1, childs.length);
        assertEquals(false, childs[0].getIsCurrent());
        assertEquals("monActionChild1", childs[0].getTitleKey());
        assertNull(childs[0].getChilds());
        assertEquals("monActionChildLink1", childs[0].getUrl());
    }

    @Test
    public void testGetDataWithCurrentSimple() {
        List<Action> actionList = new ArrayList<>();
        Action action1 = new Action();
        action1.setLabel("monAction");
        action1.setLink("monActionLink");
        actionList.add(action1);

        Action action2 = new Action();
        action2.setLabel("monAction2");
        action2.setLink("monActionLink2");
        actionList.add(action2);

        Action action3 = new Action();
        action3.setLabel("monAction3");
        action3.setLink("monActionLink3");
        actionList.add(action3);

        when(actionService.getActions(Matchers.any(), Matchers.any())).thenReturn(actionList);

        Map<String, Object> contextMap = new HashMap<>();
        ActionMenu[] curMenu = new ActionMenu[1];
        curMenu[0] =
            new ActionMenu() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String id() {
                    return "monAction2";
                }

                @Override
                public String category() {
                    return "MAIN_MENU";
                }
            };
        contextMap.put(AbstractMenuServiceImpl.CURRENT_KEY, curMenu);

        when(context.getContextData()).thenReturn(contextMap);
        when(mockAction.getLabel()).thenReturn("monAction2");

        Map<String, Object> returnMap = menuService.getData(context);
        assertNotNull(returnMap.get("menuList"));
        Menu[] menus = ((Menu[]) returnMap.get("menuList"));
        assertEquals(3, menus.length);
        assertEquals(false, menus[2].getIsCurrent());
        assertEquals("monAction3", menus[2].getTitleKey());
        assertNull(menus[2].getChilds());
        assertEquals("monActionLink3", menus[2].getUrl());
        assertEquals(true, menus[1].getIsCurrent());
    }

    @Test
    public void testGetDataChildMenuIsCurrent() {
        List<Action> actionList = new ArrayList<>();

        Action action1 = new Action();
        action1.setLabel("monAction");
        action1.setLink("monActionLink");
        action1.setType("menuSection");
        Map<String, Serializable> properties = new HashMap<>();
        properties.put("submenu", "monSousMenu");
        action1.setProperties(properties);
        actionList.add(action1);

        Action action2 = new Action();
        action2.setLabel("monAction2");
        action2.setLink("monActionLink2");
        actionList.add(action2);

        List<Action> actionChild = new ArrayList<>();
        Action action3 = new Action();
        action3.setLabel("monActionChild1");
        action3.setLink("monActionChildLink1");
        actionChild.add(action3);

        when(actionService.getActions(Matchers.matches("MAIN_MENU"), Matchers.any())).thenReturn(actionList);
        when(actionService.getActions(Matchers.matches("monSousMenu"), Matchers.any())).thenReturn(actionChild);
        when(context.getNavigationContextTitle())
            .thenReturn(Lists.newArrayList(new Breadcrumb("Test de consultation", null, 1000)));

        Map<String, Object> contextMap = new HashMap<>();
        ActionMenu[] curMenu = new ActionMenu[2];
        curMenu[0] =
            new ActionMenu() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String id() {
                    return "monActionChild1";
                }

                @Override
                public String category() {
                    return "MAIN_MENU";
                }
            };
        curMenu[1] =
            new ActionMenu() {

                @Override
                public Class<? extends Annotation> annotationType() {
                    return null;
                }

                @Override
                public String id() {
                    return "autreLien";
                }

                @Override
                public String category() {
                    return "ANOTHER_MENU";
                }
            };
        contextMap.put(AbstractMenuServiceImpl.CURRENT_KEY, curMenu);
        when(context.getContextData()).thenReturn(contextMap);
        when(mockAction.getLabel()).thenReturn("monActionChild1");

        Map<String, Object> returnMap = menuService.getData(context);
        assertNotNull(returnMap.get("menuList"));
        Menu[] menus = ((Menu[]) returnMap.get("menuList"));
        assertEquals(2, menus.length);
        assertEquals(true, menus[0].getIsCurrent());
        assertEquals("monAction", menus[0].getTitleKey());
        assertNull(menus[0].getUrl());
        assertNotNull(menus[0].getChilds());
        Menu[] childs = menus[0].getChilds();
        assertEquals(1, childs.length);
        assertEquals(true, childs[0].getIsCurrent());
        assertEquals("monActionChild1", childs[0].getTitleKey());
        assertNull(childs[0].getChilds());
        assertEquals("monActionChildLink1", childs[0].getUrl());
    }
}
