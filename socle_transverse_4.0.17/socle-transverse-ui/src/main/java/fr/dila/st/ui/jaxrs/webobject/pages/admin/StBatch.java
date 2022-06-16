package fr.dila.st.ui.jaxrs.webobject.pages.admin;

import com.google.common.collect.ImmutableMap;
import fr.dila.st.core.exception.STAuthorizationException;
import fr.dila.st.ui.bean.BatchDetailListe;
import fr.dila.st.ui.bean.BatchPlanificationListe;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.nuxeo.ecm.webengine.model.WebObject;

@Produces("text/html;charset=UTF-8")
@WebObject(type = "Batch")
public class StBatch extends SolonWebObject {
    private static final String DATA_CONSULT = "/admin/batch/suivi/";
    private static final String DATA_PLANIF = "/admin/batch/planif/";
    private static final String DATA_NOTIF = "/admin/batch/notif/";
    public static final Map<String, String> NOTIF_CHOICES = ImmutableMap.of(
        "true",
        "batch.notif.radio.activees.label",
        "false",
        "batch.notif.radio.desactivees.label"
    );
    public static final String SEARCH_FORMS_KEY = "searchBatchForms";

    @Path("/suivi")
    public Object doSuivi() {
        if (context.getAction(STActionEnum.ADMIN_BATCH) == null) {
            throw new STAuthorizationException(DATA_CONSULT);
        }
        template.setName("pages/admin/batch/batchSuivi");
        return newObject("BatchSuiviAjax", context, template);
    }

    @GET
    @Path("/planif")
    public ThTemplate doPlanification() {
        verifyAction(STActionEnum.ADMIN_BATCH, DATA_PLANIF);
        template.setContext(context);
        template.setName("pages/admin/batch/batchPlanification");
        Map<String, Object> map = new HashMap<>();

        BatchPlanificationListe batchPlanificationListe = STUIServiceLocator
            .getSuiviBatchUIService()
            .getBatchPlanificationListe(getContext().getCoreSession());
        batchPlanificationListe.buildColonnes();

        context.setNavigationContextTitle(
            new Breadcrumb(
                "Planification des batchs",
                DATA_PLANIF,
                Breadcrumb.TITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        map.put("planification", batchPlanificationListe);

        template.setData(map);

        return template;
    }

    @GET
    @Path("/suivi/{id}")
    public ThTemplate consulterBatch(@PathParam("id") String id) {
        verifyAction(STActionEnum.ADMIN_BATCH, DATA_CONSULT + id);

        Map<String, Object> mapData = new HashMap<>();

        BatchDetailListe batchDetailListe = STUIServiceLocator.getSuiviBatchUIService().getBatchDetailListe(id);
        batchDetailListe.buildColonnes();

        context.setNavigationContextTitle(
            new Breadcrumb(
                "Détail " + batchDetailListe.getNom(),
                DATA_CONSULT + id,
                Breadcrumb.SUBTITLE_ORDER,
                context.getWebcontext().getRequest()
            )
        );

        mapData.put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        mapData.put(STTemplateConstants.LST_COLONNES, batchDetailListe.getListeColonnes());
        mapData.put("batch", batchDetailListe);

        template.setName("pages/admin/batch/batchDetail");
        template.setData(mapData);
        template.setContext(context);

        return template;
    }

    @GET
    @Path("/notif")
    public ThTemplate doNotif() {
        verifyAction(STActionEnum.ADMIN_BATCH, DATA_NOTIF);
        template.setContext(context);
        template.setName("pages/admin/batch/batchNotif");

        context.setNavigationContextTitle(
            new Breadcrumb("Notifications", DATA_NOTIF, Breadcrumb.TITLE_ORDER, context.getWebcontext().getRequest())
        );

        template
            .getData()
            .put("batchNotif", STUIServiceLocator.getSuiviBatchUIService().initBatchNotifForm(context.getSession()));
        template.getData().put("options", NOTIF_CHOICES);
        template
            .getData()
            .put(STTemplateConstants.EDIT_ACTIONS, context.getAction(STActionEnum.ADMIN_BATCH_SAVE_NOTIFICATION));
        template.getData().put(STTemplateConstants.URL_PREVIOUS_PAGE, context.getUrlPreviousPage());

        return template;
    }
}
