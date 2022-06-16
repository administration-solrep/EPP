package fr.dila.st.ui.enums;

public enum STActionCategory implements ActionCategory {
    ADMIN_MENU_USER_EDIT,
    FILTERED_ACTIONS,
    LEFT_SEARCH_MENU,
    MAIN_MENU,
    ORGANIGRAMME_ACTIONS,
    PASTE_STEP_ACTIONS_LIST,
    PASTE_STEP_IN_FORK_ACTIONS_LIST,
    USER_ACTION_LIST_LEFT,
    USER_ACTION_LIST_RIGHT,
    USER_MENU_USER_EDIT,
    VIEW_ACTION_LIST;

    @Override
    public String getName() {
        return name();
    }
}
