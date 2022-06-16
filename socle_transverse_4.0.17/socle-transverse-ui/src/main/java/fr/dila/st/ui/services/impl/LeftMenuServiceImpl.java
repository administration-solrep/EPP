package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.services.LeftMenuService;

public abstract class LeftMenuServiceImpl extends AbstractMenuServiceImpl implements LeftMenuService {
    public static final String MENU_KEY = "leftMenuList";

    protected LeftMenuServiceImpl(String category) {
        super(MENU_KEY, category);
    }
}
