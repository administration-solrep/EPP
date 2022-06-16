package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.BatchListe;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.BatchSearchForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "BatchSuiviAjax")
public class StBatchSuiviAjax extends SolonWebObject {
    private static final String NAVIGATION_TILE = "Suivi des batchs";
    private static final String DATA_URL = "/admin/batch/suivi";
    private static final String DATA_AJAX_URL = "/ajax/admin/batch/recherche";
    public static final String SEARCH_FORMS_KEY = "searchBatchForms";

    @GET
    public Object doHome() {
        template.setContext(context);

        BatchSearchForm batchSearchForm = null;

        @SuppressWarnings("unchecked")
        Map<String, Object> map = UserSessionHelper.getUserSessionParameter(context, SEARCH_FORMS_KEY, Map.class);

        if (map != null) {
            batchSearchForm = (BatchSearchForm) map.get("results");
            return rechercherBatchs(batchSearchForm);
        } else {
            template.getData().put(STTemplateConstants.DISPLAY_TABLE, false);
        }

        initTemplate(batchSearchForm);

        return template;
    }

    @POST
    public ThTemplate rechercherBatchs(@SwBeanParam BatchSearchForm batchSearchForm) {
        template.setContext(context);

        Map<String, Object> mapContext = new HashMap<>();

        BatchListe batchListe = STUIServiceLocator.getSuiviBatchUIService().getBatchListe(batchSearchForm);

        initTemplate(batchSearchForm);

        template.getData().put(STTemplateConstants.RESULT_LIST, batchListe);

        mapContext.put("results", batchSearchForm);

        template
            .getData()
            .put(STTemplateConstants.DISPLAY_TABLE, CollectionUtils.isNotEmpty(batchListe.getListeColonnes()));
        UserSessionHelper.putUserSessionParameter(context, SEARCH_FORMS_KEY, mapContext);
        context.setContextData(mapContext);

        return template;
    }

    private void initTemplate(BatchSearchForm batchSearchForm) {
        if (batchSearchForm == null) {
            batchSearchForm = new BatchSearchForm();
        }

        Map<String, Object> mapData = template.getData();

        context.setNavigationContextTitle(
            new Breadcrumb(NAVIGATION_TILE, DATA_URL, Breadcrumb.TITLE_ORDER, context.getWebcontext().getRequest())
        );
        mapData.put(STTemplateConstants.RESULT_FORM, batchSearchForm);
        mapData.put(STTemplateConstants.DATA_AJAX_URL, DATA_AJAX_URL);
        mapData.put(STTemplateConstants.DATA_URL, DATA_URL);
    }

    @POST
    @Path("/reinit")
    public void reinitSearch() {
        UserSessionHelper.putUserSessionParameter(getMyContext(), SEARCH_FORMS_KEY, null);
    }

    @Override
    public ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/components/result-list-batch", context);
    }
}
