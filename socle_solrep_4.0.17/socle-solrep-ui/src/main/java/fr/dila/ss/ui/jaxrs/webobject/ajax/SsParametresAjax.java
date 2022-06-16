package fr.dila.ss.ui.jaxrs.webobject.ajax;

import fr.dila.ss.ui.bean.parametres.ParametreList;
import fr.dila.ss.ui.enums.SSActionEnum;
import fr.dila.ss.ui.services.SSParametreUIService;
import fr.dila.ss.ui.services.SSUIServiceLocator;
import fr.dila.ss.ui.th.bean.ParametreForm;
import fr.dila.ss.ui.th.constants.SSTemplateConstants;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.Breadcrumb;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.bean.PaginationForm;
import fr.dila.st.ui.th.constants.STTemplateConstants;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ParametresAjax")
public class SsParametresAjax extends SolonWebObject {
    private static final String DATA_AJAX_URL = "/ajax/admin/param";
    private static final String DATA_URL = "/admin/param/technique";
    private static final String TITLE = "Paramètres techniques";

    public SsParametresAjax() {
        super();
    }

    @GET
    public ThTemplate doRecherche(@SwBeanParam PaginationForm listForm) {
        Map<String, Object> mapData = new HashMap<>();

        template.setContext(context);

        context.setNavigationContextTitle(
            new Breadcrumb(TITLE, DATA_URL, Breadcrumb.TITLE_ORDER, context.getWebcontext().getRequest())
        );

        if (listForm == null) {
            listForm = new PaginationForm();
        }

        context.putInContextData("listForm", listForm);

        SSParametreUIService paramService = SSUIServiceLocator.getParametreUIService();

        ParametreList listResult = paramService.getParametres(context);

        listResult.buildColonnes();

        mapData.put(STTemplateConstants.PAGE, listForm.getPage());
        mapData.put(STTemplateConstants.TITLE, TITLE);
        mapData.put(SSTemplateConstants.PARAM_CONTEXT, "technique");
        mapData.put(STTemplateConstants.DATA_AJAX_URL, DATA_AJAX_URL);
        mapData.put(STTemplateConstants.DATA_URL, DATA_URL);
        mapData.put(STTemplateConstants.RESULT_FORM, listForm);
        mapData.put(STTemplateConstants.RESULT_LIST, listResult);

        template.setContext(context);
        template.setData(mapData);

        return template;
    }

    @POST
    @Path("modifier")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifierParametre(@SwBeanParam ParametreForm parametreForm) {
        context.putInContextData("form", parametreForm);
        context.setCurrentDocument(parametreForm.getId());

        verifyAction(
            SSActionEnum.ADMIN_PARAM_TECHNIQUE,
            String.format("/admin/param/technique/%s/editer", context.getCurrentDocument().getTitle())
        );

        SSUIServiceLocator.getParametreUIService().updateParametre(context);

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @Override
    protected ThTemplate getMyTemplate() {
        return new AjaxLayoutThTemplate("fragments/table/tableParametres", context);
    }
}
