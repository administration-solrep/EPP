package fr.dila.ss.ui.jaxrs.webobject.page;

import fr.dila.ss.api.service.ActualiteService;
import fr.dila.ss.core.service.SSServiceLocator;
import fr.dila.ss.ui.bean.actualites.ActualiteConsultationDTO;
import fr.dila.ss.ui.bean.actualites.ActualiteUserRechercheForm;
import fr.dila.ss.ui.bean.actualites.ActualitesList;
import fr.dila.ss.ui.jaxrs.webobject.AbstractActualiteController;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.ss.ui.th.model.SSHistoriqueActualitesTemplate;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentNotFoundException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AppliHistoriqueActualites")
public class SsHistoriqueActualites extends AbstractActualiteController {
    private static final String ACTUALITE_VIEW = "pages/admin/actualites/actualiteView";
    private static final String HISTORIQUE_ACTUALITE_URL = "/historiqueActualites";
    private static final String ACTUALITE_PARAM = "id";

    protected ActualiteUIService getActualiteUIService() {
        return SSUIServiceLocator.getActualiteUIService();
    }

    protected ActualiteService getActualiteService() {
        return SSServiceLocator.getActualiteService();
    }

    /**
     * Historique des actualités
     *
     * @return template Historique des actualités
     */
    @GET
    public ThTemplate getHistoriqueActualites(@SwBeanParam ActualiteUserRechercheForm actualiteRechercheForm) {
        template.setName("pages/historiqueActualites");

        Map<String, Object> mapContext = new HashMap<>();
        context.setContextData(mapContext);
        context.clearNavigationContext();
        context.setNavigationContextTitle(
            new Breadcrumb("Actualités", HISTORIQUE_ACTUALITE_URL, Breadcrumb.TITLE_ORDER)
        );
        template.setContext(context);

        putActualiteRechercheFormInContext(actualiteRechercheForm, context, ActualiteUserRechercheForm::new);

        ActualitesList actualitesList = getActualiteUIService().getActualitesList(context);

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, actualitesList);
        map.put(STTemplateConstants.LST_COLONNES, actualitesList.getListeColonnes());
        map.put(STTemplateConstants.RESULT_FORM, actualiteRechercheForm);
        map.put(STTemplateConstants.DATA_URL, HISTORIQUE_ACTUALITE_URL);
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/historiqueActualites");
        template.setData(map);
        return template;
    }

    @GET
    @Path("/consultation/{id}")
    public Object viewActualite(@PathParam(ACTUALITE_PARAM) String actualiteId) {
        template.setName(ACTUALITE_VIEW);
        DocumentModel actualiteDoc = null;
        try {
            actualiteDoc = context.getSession().getDocument(new IdRef(actualiteId));
        } catch (DocumentNotFoundException e) {
            context.getMessageQueue().addErrorToQueue(ResourceHelper.getString("actualite.not.existe"));
            UserSessionHelper.putUserSessionParameter(
                context,
                SpecificContext.MESSAGE_QUEUE,
                context.getMessageQueue()
            );
            return redirect(HISTORIQUE_ACTUALITE_URL);
        }
        ActualiteConsultationDTO dto = getActualiteUIService().toActualiteForm(actualiteDoc);

        context.setNavigationContextTitle(
            new Breadcrumb(
                dto.getObjet(),
                "/historiqueActualites/consultation/" + dto.getId(),
                Breadcrumb.TITLE_ORDER + 10
            )
        );
        template.setContext(context);
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());
        template.getData().put(SSTemplateConstants.ACTUALITE_DTO, dto);
        return template;
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new SSHistoriqueActualitesTemplate();
    }
}
