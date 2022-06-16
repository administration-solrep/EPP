package fr.dila.epp.ui.jaxrs.webobject.ajax;

import fr.dila.epp.ui.services.SolonEppUIServiceLocator;
import fr.dila.st.ui.jaxrs.webobject.ajax.AbstractNotificationAjax;
import fr.dila.st.ui.services.NotificationUIService;
import org.nuxeo.ecm.webengine.model.WebObject;

@WebObject(type = "NotificationAjax")
public class EppNotificationAjax extends AbstractNotificationAjax {

    public EppNotificationAjax() {
        super();
    }

    @Override
    protected NotificationUIService getNotificationUIService() {
        return SolonEppUIServiceLocator.getNotificationUIService();
    }
}
