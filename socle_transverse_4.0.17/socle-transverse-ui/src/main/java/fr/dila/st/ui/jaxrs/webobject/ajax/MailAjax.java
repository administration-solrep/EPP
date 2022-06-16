package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.services.impl.STRechercheUtilisateursUIServiceImpl;
import fr.dila.st.ui.th.bean.MailForm;
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
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "MailAjax")
public class MailAjax extends SolonWebObject {

    public MailAjax() {
        super();
    }

    @POST
    @Path("contenu")
    public ThTemplate loadContent(@FormParam("destinataires[]") List<String> destinataires) {
        UserSessionHelper.putUserSessionParameter(
            context,
            STRechercheUtilisateursUIServiceImpl.SESSION_PARAM_RECIPIENT_IDS,
            destinataires
        );

        ThTemplate template = new AjaxLayoutThTemplate("fragments/components/mailModalContent", context);

        Map<String, Object> map = new HashMap<>();
        map.put(
            STTemplateConstants.LST_SENDER,
            STUIServiceLocator.getMailUIService().retrieveAdresseEmissionValues(context)
        );
        template.setData(map);

        return template;
    }

    @Path("envoyer")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendMail(@SwBeanParam MailForm mailForm) {
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        context.putInContextData(STContextDataKey.MAIL_FORM, mailForm); // add mail form

        STUIServiceLocator.getRechercheUtilisateursUIService().envoyerMail(context);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
