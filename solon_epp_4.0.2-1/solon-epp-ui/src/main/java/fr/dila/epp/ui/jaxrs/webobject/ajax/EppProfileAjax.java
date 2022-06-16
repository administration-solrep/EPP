package fr.dila.epp.ui.jaxrs.webobject.ajax;

import fr.dila.epp.ui.enumeration.EppContextDataKey;
import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.epp.ui.th.bean.ProfilForm;
import fr.dila.epp.ui.th.constants.EppTemplateConstants;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.th.model.AjaxLayoutThTemplate;
import fr.dila.st.ui.th.model.SpecificContext;
import fr.dila.st.ui.th.model.ThTemplate;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ProfileAjax")
public class EppProfileAjax extends SolonWebObject {

    @GET
    @Path("metadatas")
    public ThTemplate getMetadataValues() {
        ThTemplate template = new AjaxLayoutThTemplate(
            "fragments/components/profil/profilUtilisateurModalContent",
            context
        );

        template.getData().put("dto", SolonEppUIServiceLocator.getProfilService().getProfil(context));
        template.getData().put(EppTemplateConstants.USER_NAME, context.getSession().getPrincipal().getActingUser());

        return template;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("parametres")
    public Response saveParameters(@SwBeanParam ProfilForm form) {
        context.putInContextData(EppContextDataKey.PROFIL_FORM, form);
        SolonStatus status = SolonStatus.OK;

        SolonEppUIServiceLocator.getProfilService().saveProfil(context);
        if (CollectionUtils.isNotEmpty(context.getMessageQueue().getErrorQueue())) {
            status = SolonStatus.FUNCTIONAL_ERROR;
        }

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());
        return new JsonResponse(status, context.getMessageQueue()).build();
    }
}
