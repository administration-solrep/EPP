package fr.dila.st.core.component;

import fr.dila.st.api.service.NotificationsSuiviBatchsService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.NotificationsSuiviBatchsServiceImpl;

public class NotificationsSuiviBatchsComponent
    extends ServiceEncapsulateComponent<NotificationsSuiviBatchsService, NotificationsSuiviBatchsServiceImpl> {

    public NotificationsSuiviBatchsComponent() {
        super(NotificationsSuiviBatchsService.class, new NotificationsSuiviBatchsServiceImpl());
    }
}
