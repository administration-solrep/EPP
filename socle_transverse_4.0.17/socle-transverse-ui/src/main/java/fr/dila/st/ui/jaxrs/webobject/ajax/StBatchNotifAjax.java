package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.annot.SwBeanParam;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.STUIServiceLocator;
import fr.dila.st.ui.th.bean.BatchNotifForm;
import fr.dila.st.ui.th.model.SpecificContext;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "BatchNotifAjax")
public class StBatchNotifAjax extends SolonWebObject {

    @POST
    @Path("modifier")
    @Produces(MediaType.APPLICATION_JSON)
    public Response doPatch(@SwBeanParam BatchNotifForm batchNotifForm) {
        STUIServiceLocator.getSuiviBatchUIService().updateNotification(batchNotifForm, context.getSession());
        context.getMessageQueue().addToastSuccess(ResourceHelper.getString("batch.notif.save.success"));

        UserSessionHelper.putUserSessionParameter(context, SpecificContext.MESSAGE_QUEUE, context.getMessageQueue());

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }
}
