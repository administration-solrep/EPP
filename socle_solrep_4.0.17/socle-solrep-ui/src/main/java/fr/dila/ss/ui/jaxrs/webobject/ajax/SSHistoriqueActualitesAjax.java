package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.bean.actualites.ActualiteUserRechercheForm;
import fr.dila.ss.ui.bean.actualites.ActualitesList;
import fr.dila.ss.ui.jaxrs.webobject.AbstractActualiteController;
import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.POST;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "AjaxHistoriqueActualites")
public class SSHistoriqueActualitesAjax extends AbstractActualiteController {

    public SSHistoriqueActualitesAjax() {
        super();
    }

    protected ActualiteUIService getActualiteUIService() {
        return SSUIServiceLocator.getActualiteUIService();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/actualites/result-historiqueActualites", getMyContext());
    }

    /**
     * Filtrer les actualités
     *
     * @param actualiteForm
     * 		  formulaire "Filtrer sur"
     * @return le tableau de résultats
     */
    @POST
    public ThTemplate getResults(@SwBeanParam ActualiteUserRechercheForm actualiteRechercheForm) {
        context.setNavigationContextTitle(
            new Breadcrumb(
                "Actualités",
                "/historiqueActualites",
                Breadcrumb.TITLE_ORDER,
                template.getContext().getWebcontext().getRequest()
            )
        );
        template.setContext(context);
        template.setName("fragments/actualites/result-historiqueActualites");

        putActualiteRechercheFormInContext(actualiteRechercheForm, context, ActualiteUserRechercheForm::new);

        ActualitesList actualitesList = getActualiteUIService().getActualitesList(context);

        Map<String, Object> map = new HashMap<>();
        map.put(STTemplateConstants.RESULT_LIST, actualitesList);
        map.put(STTemplateConstants.LST_COLONNES, actualitesList.getListeColonnes());
        map.put(STTemplateConstants.RESULT_FORM, actualiteRechercheForm);
        map.put(STTemplateConstants.DATA_URL, "/historiqueActualites");
        map.put(STTemplateConstants.DATA_AJAX_URL, "/ajax/historiqueActualites");
        template.setData(map);

        return template;
    }
}
