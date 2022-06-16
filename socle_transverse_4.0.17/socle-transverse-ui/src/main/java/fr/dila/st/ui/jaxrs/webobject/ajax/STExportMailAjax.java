package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.ExportMailType;
import fr.dila.st.ui.enums.STActionEnum;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "ExportMailAjax")
public class STExportMailAjax extends SolonWebObject {

    public STExportMailAjax() {
        super();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response doExportMail(@FormParam("ids[]") List<String> ids, @FormParam("type") ExportMailType type) {
        if (ExportMailType.USER == type) {
            verifyAction(STActionEnum.ADMIN_USER_RECHERCHE, "Export utilisateurs");
            List<DocumentModel> users = STServiceLocator.getSTUserSearchService().getUsersFromIds(ids);
            context.putInContextData(STContextDataKey.USERS, users);
            STUIServiceLocator.getRechercheUtilisateursUIService().createExportExcel(context);
            context.getMessageQueue().addToastSuccess(ResourceHelper.getString("export.mail.succes.toast"));
        }
        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
