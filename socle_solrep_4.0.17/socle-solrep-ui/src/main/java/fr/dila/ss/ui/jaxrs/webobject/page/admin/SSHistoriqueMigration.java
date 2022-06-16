package fr.dila.ss.ui.jaxrs.webobject.page.admin;

import fr.dila.ss.ui.bean.SSHistoriqueMigrationDetailDTO;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.organigramme.SSMigrationGouvernementUIService;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "HistoriqueMigration")
public class SSHistoriqueMigration extends SolonWebObject {

    @Path("migrations")
    public Object getHistorique() {
        if (context.getAction(SSActionEnum.ADMIN_MIGRATION_HISTORIQUE) == null) {
            throw new STAuthorizationException("/admin/historique/migrations");
        }
        ThTemplate template = getMyTemplate(context);

        template.setName("pages/admin/migration/historique");
        template.setContext(context);
        context.setNavigationContextTitle(
            new Breadcrumb(
                "historique.migration.title",
                "/admin/historique/migrations",
                Breadcrumb.TITLE_ORDER,
                template.getContext().getWebcontext().getRequest()
            )
        );

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.DATA_URL, "/admin/historique/migrations");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/historique");

        template.setData(map);

        return newObject("HistoriqueMigrationAjax", context, template);
    }

    @GET
    @Path("consulter")
    public ThTemplate getMigrationDetail(@QueryParam("id") String id) {
        ThTemplate template = getMyTemplate(context);

        template.setName("pages/admin/migration/historique-detail");
        template.setContext(context);
        Map<String, Object> map = new HashMap<>();

        SSHistoriqueMigrationDetailDTO detail = getMigrationGouvernementUIService()
            .getHistoriqueMigrationDetailDTO(Long.parseLong(id));

        context.setNavigationContextTitle(
            new Breadcrumb(
                String.format("Consultation de la migration %s", detail.getLabel()),
                "/admin/historique/migrations/consulter&id=" + id,
                Breadcrumb.TITLE_ORDER + 1,
                context.getWebcontext().getRequest()
            )
        );

        map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        map.put(SSTemplateConstants.MIGRATION, detail);

        template.setData(map);

        return template;
    }

    protected SSMigrationGouvernementUIService getMigrationGouvernementUIService() {
        throw new UnsupportedOperationException("Implemented in app-specific classes");
    }

    protected ThTemplate getMyTemplate(SpecificContext context) {
        return new ThTemplate();
    }
}
