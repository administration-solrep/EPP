package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.Menu;
import fr.dila.st.ui.th.annot.ActionMenu;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.map.HashedMap;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.actions.ActionContext;
import org.nuxeo.ecm.platform.actions.ELActionContext;
import org.nuxeo.ecm.platform.actions.ejb.ActionManager;

public abstract class AbstractMenuServiceImpl extends FragmentServiceImpl {
    public static final String CURRENT_KEY = "currentMenu";

    private static final String MAIN_KEY = "MAIN_MENU";
    private final String menuKey;
    private final String category;
    private static final int SECOND_MENU_START_INDEX = 50;

    protected AbstractMenuServiceImpl(String menuKey, String category) {
        this.menuKey = menuKey;
        this.category = category;
    }

    protected Menu[] getMenu(SpecificContext context) {
        ActionContext actionContext = initActionContext(context);

        ActionMenu[] curMenu = null;
        String current = null;

        if (context.getContextData().get(CURRENT_KEY) != null) {
            curMenu = (ActionMenu[]) context.getContextData().get(CURRENT_KEY);
        }

        if (curMenu != null) {
            for (ActionMenu menu : curMenu) {
                if (category.equals(menu.category())) {
                    Action curAction = getAction(menu.id(), actionContext);
                    current = curAction.getLabel();
                }
            }

            addItemToNavigationContext(curMenu, context, actionContext);
        }

        return convertActionsToMenu(getActions(category, actionContext), actionContext, context, current);
    }

    private List<Breadcrumb> buildBreadcrumbFromMenu(SpecificContext context, List<Menu> pathToCurrentMenu, int start) {
        return IntStream
            .range(0, pathToCurrentMenu.size())
            .mapToObj(
                idx -> {
                    Breadcrumb crumb = context.getMenuIfExist(pathToCurrentMenu.get(idx).getTitleKey());
                    if (crumb == null) {
                        crumb =
                            new Breadcrumb(
                                pathToCurrentMenu.get(idx).getTitleKey(),
                                pathToCurrentMenu.get(idx).getUrl(),
                                idx + start
                            );
                    }
                    return crumb;
                }
            )
            .collect(Collectors.toList());
    }

    private void addItemToNavigationContext(
        ActionMenu[] curMenu,
        SpecificContext context,
        ActionContext actionContext
    ) {
        List<Breadcrumb> newNavigationContext = new ArrayList<>();

        if (context.getNavigationContextTitle() != null && !context.getNavigationContextTitle().isEmpty()) {
            newNavigationContext.addAll(context.getNavigationContextTitle());
        }

        for (ActionMenu annotMenu : curMenu) {
            final int start = MAIN_KEY.equals(annotMenu.category()) ? 0 : SECOND_MENU_START_INDEX;

            Action curAction = getAction(annotMenu.id(), actionContext);
            Menu[] menuArray = convertActionsToMenu(
                getActions(annotMenu.category(), actionContext),
                actionContext,
                context,
                curAction.getLabel()
            );

            for (Menu menu : menuArray) {
                List<Menu> pathToCurrentMenu = new ArrayList<>();
                menu.getPathToCurrent(pathToCurrentMenu);

                if (!pathToCurrentMenu.isEmpty()) {
                    newNavigationContext.addAll(buildBreadcrumbFromMenu(context, pathToCurrentMenu, start));
                }
            }
        }

        newNavigationContext.sort((Breadcrumb n1, Breadcrumb n2) -> n1.getOrder().compareTo(n2.getOrder()));

        context.getWebcontext().getUserSession().put(Breadcrumb.USER_SESSION_KEY, newNavigationContext);
    }

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> map = new HashedMap<>();
        map.put(getMenuKey(), getMenu(context));
        return map;
    }

    public String getMenuKey() {
        return menuKey;
    }

    public String getCategory() {
        return category;
    }

    protected List<Action> getActions(String category, SpecificContext context) {
        return getActions(category, initActionContext(context));
    }

    protected ActionContext initActionContext(SpecificContext context) {
        ELActionContext ac = new ELActionContext();
        ac.setCurrentPrincipal(context.getSession().getPrincipal());
        ac.setDocumentManager(context.getSession());
        ac.putAllLocalVariables(context.getContextData());
        return ac;
    }

    protected List<Action> getActions(String category, ActionContext context) {
        final ActionManager actionService = ServiceUtil.getRequiredService(ActionManager.class);
        return actionService.getActions(category, context);
    }

    protected Action getAction(String actionId, ActionContext context) {
        final ActionManager actionService = ServiceUtil.getRequiredService(ActionManager.class);
        return actionService.getAction(actionId, context, false);
    }

    protected Menu convertActionToMenu(
        Action action,
        ActionContext actionContext,
        SpecificContext context,
        String currentLabel
    ) {
        Menu returnMenu = null;
        if ("menuSection".equals(action.getType())) {
            String subcategory = (String) action.getProperties().get("submenu");
            Menu[] children = convertActionsToMenu(
                getActions(subcategory, actionContext),
                actionContext,
                context,
                currentLabel
            );
            returnMenu = new Menu(null, action.getLabel(), children);
        } else {
            returnMenu = new Menu(action.getLink(), action.getLabel());
        }

        if (returnMenu.getTitleKey().equals(currentLabel) || isCurrentChildren(returnMenu.getChilds())) {
            returnMenu.setIsCurrent(true);
        }

        return returnMenu;
    }

    protected Menu[] convertActionsToMenu(
        List<Action> actions,
        ActionContext actionContext,
        SpecificContext context,
        String currentLabel
    ) {
        Menu[] menus = new Menu[actions.size()];
        int i = 0;

        for (Action action : actions) {
            menus[i] = convertActionToMenu(action, actionContext, context, currentLabel);
            ++i;
        }
        return menus;
    }

    private Boolean isCurrentChildren(Menu[] children) {
        return (
            children != null &&
            !Arrays.stream(children).filter(i -> i.getIsCurrent()).collect(Collectors.toList()).isEmpty()
        );
    }
}
