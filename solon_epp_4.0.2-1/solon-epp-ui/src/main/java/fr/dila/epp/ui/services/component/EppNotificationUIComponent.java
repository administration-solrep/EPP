package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.impl.EppNotificationUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.NotificationUIService;

public class EppNotificationUIComponent
    extends ServiceEncapsulateComponent<NotificationUIService, EppNotificationUIServiceImpl> {

    /**
     * Default constructor
     */
    public EppNotificationUIComponent() {
        super(NotificationUIService.class, new EppNotificationUIServiceImpl());
    }
}
