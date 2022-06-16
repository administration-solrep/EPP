package fr.dila.ss.ui.jaxrs.webobject.page.admin;

import com.google.common.collect.ImmutableList;
import fr.dila.ss.ui.services.SSOrganigrammeManagerService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.services.impl.SSOrganigrammeManagerServiceImpl;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.pages.admin.STOrganigramme;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliOrganigramme")
public class SSOrganigramme extends STOrganigramme {

    @Override
    @Path("consult")
    public Object getUserOrganigramme() throws IllegalAccessException, InstantiationException {
        ThTemplate template = getMyTemplate(context);

        template.setName("pages/admin/user/organigramme");
        template.setContext(context);
        context.setNavigationContextTitle(
            new Breadcrumb("Gestion de l'organigramme", ORGANIGRAMME_URL, ORGANIGRAMME_ORDER)
        );

        if (template.getData() == null) {
            Map<String, Object> map = new HashMap<>();

            template.setData(map);
        }

        SSOrganigrammeManagerService service = SSUIServiceLocator.getSSOrganigrammeManagerService();
        template
            .getData()
            .put(
                SSOrganigrammeManagerServiceImpl.ORGANIGRAMME_BASE_ACTIONS_KEY,
                service.loadBaseAdminOrganigrammeActions(context.getSession())
            );
        template
            .getData()
            .put(
                SSOrganigrammeManagerServiceImpl.ORGANIGRAMME_MAIN_ACTIONS_KEY,
                service.loadMainAdminOrganigrammeActions(context.getSession())
            );

        template.getData().put("acceptedTypes", ImmutableList.of("xls", "xlsx"));

        return newObject("OrganigrammeAjax", context, template);
    }
}
