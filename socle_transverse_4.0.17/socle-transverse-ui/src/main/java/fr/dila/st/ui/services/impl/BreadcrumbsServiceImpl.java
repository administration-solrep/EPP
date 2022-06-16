package fr.dila.st.ui.services.impl;

import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.services.BreadcrumbsService;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;

public class BreadcrumbsServiceImpl extends FragmentServiceImpl implements BreadcrumbsService {
    private static final String ESPACE_TRAVAIL = "menu.travail.title";
    private static final String HOME_KEY = "isHome";

    @Override
    public Map<String, Object> getData(SpecificContext context) {
        Map<String, Object> map = new HashedMap<>();
        List<Breadcrumb> lstBreadcrumbs = context.getNavigationContext();
        int fullLength = 0;

        for (Breadcrumb crumb : lstBreadcrumbs) {
            fullLength += crumb.getTitleLength();
        }

        map.put("breadcrumbsLength", fullLength);
        map.put("lstBreadcrumbs", lstBreadcrumbs);
        map.put(HOME_KEY, lstBreadcrumbs.stream().allMatch(breadcrumb -> breadcrumb.isThisMenu(ESPACE_TRAVAIL)));

        return map;
    }
}
