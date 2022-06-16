package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.services.HeaderService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.nuxeo.ecm.core.api.CoreSession;

public class HeaderServiceImpl extends FragmentServiceImpl implements HeaderService {
    private static final String USER_KEY = "user";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> map = new HashedMap<>();
        CoreSession session = context.getSession();
        map.put(USER_KEY, session.getPrincipal());
        return map;
    }
}
