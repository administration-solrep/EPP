package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.services.FooterService;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;

public class FooterServiceImpl extends FragmentServiceImpl implements FooterService {
    private static final String CONFIG_KEY = "footerConfig";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> map = new HashedMap<>();
        map.put(CONFIG_KEY, STUIServiceLocator.getConfigUIService().getConfig());
        return map;
    }
}
