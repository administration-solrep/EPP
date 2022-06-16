package fr.dila.st.ui.jaxrs.webobject.ajax;

import fr.dila.st.core.util.DateUtil;
import fr.dila.st.ui.bean.JsonResponse;
import fr.dila.st.ui.bean.NotificationDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.STUserSessionKey;
import fr.dila.st.ui.enums.SolonStatus;
import fr.dila.st.ui.helper.UserSessionHelper;
import fr.dila.st.ui.jaxrs.webobject.SolonWebObject;
import fr.dila.st.ui.services.NotificationUIService;
import fr.dila.st.ui.validators.annot.SwRequired;
import java.util.Date;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

public abstract class AbstractNotificationAjax extends SolonWebObject {

    protected abstract NotificationUIService getNotificationUIService();

    @POST
    @Path("init")
    @Produces(MediaType.APPLICATION_JSON)
    public Object initLastUpdate(@SwRequired @FormParam("firstLoad") Date firstLoad) {
        UserSessionHelper.putUserSessionParameter(
            context,
            STUserSessionKey.LAST_USER_NOTIFICATION,
            DateUtil.toCalendarFromNotNullDate(firstLoad)
        );
        return new JsonResponse(SolonStatus.OK, context.getMessageQueue()).build();
    }

    @GET
    @Path("delai")
    public String getDelay() {
        NotificationUIService service = getNotificationUIService();
        return Long.toString(service.getNotificationDelai(context));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object getNotifications(
        @QueryParam("idCorbeille") String idCorbeille,
        @QueryParam("idEvenement") String idEvenement
    ) {
        NotificationUIService service = getNotificationUIService();

        context.putInContextData(STContextDataKey.CORBEILLE_ID, idCorbeille);
        context.putInContextData(STContextDataKey.EVENEMENT_ID, idEvenement);
        NotificationDTO dto = service.getNotificationDTO(context);

        return new JsonResponse(SolonStatus.OK, context.getMessageQueue(), dto).build();
    }

    @POST
    @Path("rechargerCacheTDR")
    public void reloadCacheTdrEppIfNecessary() {
        NotificationUIService service = getNotificationUIService();
        service.reloadCacheTdrEppIfNecessary(context);
    }
}
