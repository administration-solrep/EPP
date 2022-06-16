package fr.dila.ss.ui.jaxrs.webobject.page.admin.actualite;

import avro.shaded.com.google.common.collect.ImmutableMap;
import fr.dila.ss.ui.bean.actualites.ActualiteAdminRechercheForm;
import fr.dila.ss.ui.bean.actualites.ActualitesList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.jaxrs.webobject.AbstractActualiteController;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "Actualites")
public class SSActualites extends AbstractActualiteController {
    private static final ImmutableMap<String, String> STATUT_OPTIONS = ImmutableMap.of(
        "null",
        "actualites.statut.actualite.toutes",
        "true",
        "actualites.statut.actualite.archivee",
        "false",
        "actualites.statut.actualite.non.archivee"
    );

    private static final ImmutableMap<String, String> PJ_OPTIONS = ImmutableMap.of(
        "null",
        "actualites.gestion.actualite.pj.toutes",
        "true",
        "actualites.gestion.actualite.pj.oui",
        "false",
        "actualites.gestion.actualite.pj.non"
    );

    private ActualiteUIService getActualiteUIService() {
        return SSUIServiceLocator.getActualiteUIService();
    }

    /**
     * Gestion des actualités
     *
     * @return template Gestion des actualités
     */
    @GET
    public ThTemplate getActualites(@SwBeanParam ActualiteAdminRechercheForm actualiteRechercheForm) {
        template.setName("pages/admin/actualites/gestionActualites");

        Map<String, Object> mapContext = new HashMap<>();
        context.setContextData(mapContext);
        context.setNavigationContextTitle(
            new Breadcrumb("Gestion des actualités", "/admin/actualites", Breadcrumb.TITLE_ORDER)
        );
        template.setContext(context);

        if (context.getAction(SSActionEnum.ADMIN_USER_ACTUALITES) == null) {
            throw new STAuthorizationException("/admin/actualites");
        }

        ActualiteAdminRechercheForm nonNullRechercheForm = putActualiteRechercheFormInContext(
            actualiteRechercheForm,
            context,
            ActualiteAdminRechercheForm::new
        );

        ActualitesList actualitesList = getActualiteUIService().getActualitesList(context);

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, actualitesList);
        map.put(STTemplateConstants.LST_COLONNES, actualitesList.getListeColonnes());
        map.put(STTemplateConstants.RESULT_FORM, nonNullRechercheForm);
        map.put(STTemplateConstants.DATA_URL, "/admin/actualites");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/admin/actualites/resultats");
        map.put("createAction", context.getAction(SSActionEnum.CREATE_NEWS));
        map.put("removalActions", context.getActions(SSActionCategory.MANAGE_NEWS_REMOVAL));
        map.put("statutOption", STATUT_OPTIONS);
        map.put("pjOption", PJ_OPTIONS);
        template.setData(map);
        return template;
    }

    /**
     * Consultation d'une actualité
     *
     * @return WebObject "ActualitesConsultation"
     */
    @Path("/consultation")
    public Object consultationActualite() {
        return newObject("ActualitesConsultation", context, template);
    }

    /**
     * Création d'une actualité
     *
     * @returnWebObject "ActualitesCreation"
     */
    @Path("/creation")
    public Object creationActualite() {
        return newObject("ActualitesCreation", context, template);
    }
}
