package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.actualites.ActualiteAdminRechercheForm;
import fr.dila.ss.ui.bean.actualites.ActualiteCreationDTO;
import fr.dila.ss.ui.bean.actualites.ActualitesList;
import fr.dila.ss.ui.enums.SSActionCategory;
import fr.dila.ss.ui.jaxrs.webobject.AbstractActualiteController;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxActualites")
public class SSActualitesAjax extends AbstractActualiteController {

    public SSActualitesAjax() {
        super();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/admin/actualites/result-actualites", getMyContext());
    }

    private ActualiteUIService getActualiteUIService() {
        return SSUIServiceLocator.getActualiteUIService();
    }

    /**
     * Filtrer les actualités
     *
     * @param actualiteForm
     * 		  formulaire "Filtrer sur"
     * @return le tableau de résultats
     */
    @POST
    @Path("resultats")
    public ThTemplate getResults(@SwBeanParam ActualiteAdminRechercheForm actualiteRechercheForm) {
        context.setNavigationContextTitle(
            new Breadcrumb(
                "Gestion des actualités",
                "/admin/actualites",
                Breadcrumb.TITLE_ORDER,
                template.getContext().getWebcontext().getRequest()
            )
        );
        template.setContext(context);
        template.setName("fragments/admin/actualites/result-actualites");

        putActualiteRechercheFormInContext(actualiteRechercheForm, context, ActualiteAdminRechercheForm::new);

        ActualitesList actualitesList = getActualiteUIService().getActualitesList(context);

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, actualitesList);
        map.put(STTemplateConstants.LST_COLONNES, actualitesList.getListeColonnes());
        map.put(STTemplateConstants.RESULT_FORM, actualiteRechercheForm);
        map.put(STTemplateConstants.DATA_URL, "/admin/actualites");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/admin/actualites/resultats");
        map.put("removalActions", context.getActions(SSActionCategory.MANAGE_NEWS_REMOVAL));

        template.setData(map);

        return template;
    }

    @Path("sauvegarde")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response saveActualite(@SwBeanParam ActualiteCreationDTO actualiteCreationDTO) {
        DocumentModel actualiteDoc = getActualiteUIService().toDocumentModel(context, actualiteCreationDTO);
        SSServiceLocator.getActualiteService().createActualite(context.getSession(), actualiteDoc);
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString("actualites.creation.succes"));

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("read")
    @Produces(MediaType.APPLICATION_JSON)
    public Response setNewsRead(@FormParam("actualiteId") String actualiteId) {
        context.putInContextData(STContextDataKey.ID, actualiteId);
        SSUIServiceLocator.getActualiteUIService().setActualiteLue(context);
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @POST
    @Path("supprimer")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeActualites(@FormParam("idActualites[]") List<String> idActualites) {
        context.putInContextData(STContextDataKey.IDS, idActualites);
        SSUIServiceLocator.getActualiteUIService().removeActualites(context);
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString("actualites.gestion.action.delete.succes"));
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
