package fr.dila.ss.ui.jaxrs.webobject.page.suivi;

import fr.dila.ss.ui.bean.AlerteForm;
import fr.dila.ss.ui.services.actions.SSActionsServiceLocator;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "SuiviAlerte")
public class SSSuiviAlerte extends SolonWebObject {
    protected static final String NAVIGATION_TITLE = "Sauvegarde d'une alerte";

    public SSSuiviAlerte() {
        super();
    }

    @GET
    @Path("{id}")
    public ThTemplate getAlerte(@PathParam("id") String id) {
        context.setCurrentDocument(id);
        AlerteForm form = SSActionsServiceLocator.getAlertActionService().getCurrentAlerteForm(context);

        context.removeNavigationContextTitle();
        context.setNavigationContextTitle(
            new Breadcrumb(NAVIGATION_TITLE, "/suivi/alertes/" + id, Breadcrumb.TITLE_ORDER + 1)
        );

        return buildTemplate(id, form);
    }

    @GET
    @Path("creer")
    public ThTemplate getCreationAlerte(@QueryParam("idRequete") String idRequete) {
        context.setCurrentDocument(idRequete);
        AlerteForm form = SSActionsServiceLocator.getAlertActionService().getNewAlertFromRequeteExperte(context);

        context.removeNavigationContextTitle();

        context.setNavigationContextTitle(
            new Breadcrumb(NAVIGATION_TITLE, "/suivi/alertes/creer?idRequete=" + idRequete, Breadcrumb.TITLE_ORDER + 1)
        );

        return buildTemplate(form.getId(), form);
    }

    protected ThTemplate buildTemplate(String id, AlerteForm form) {
        Map<String, Object> map = new HashMap<>();

        if (context.getNavigationContext().size() > 1) {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        } else {
            map.put(STTemplateConstants.URL_PREVIOUS_PAGE, "");
        }

        map.put("id", id);
        map.put("form", form);

        template.setContext(context);
        template.setData(map);
        return template;
    }
}
