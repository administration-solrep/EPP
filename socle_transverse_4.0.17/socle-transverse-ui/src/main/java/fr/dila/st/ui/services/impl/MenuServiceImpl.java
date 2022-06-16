package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.enums.STActionCategory;
import fr.dila.st.ui.services.MenuService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;

public class MenuServiceImpl extends AbstractMenuServiceImpl implements MenuService {
    private static final String MENU_KEY = "menuList";
    private static final String CONFIG_KEY = "config";

    public MenuServiceImpl() {
        super(MENU_KEY, STActionCategory.MAIN_MENU.getName());
    }

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> returnMap = super.getData(context);
        returnMap.put(CONFIG_KEY, STUIServiceLocator.getConfigUIService().getConfig());
        return returnMap;
    }
}
