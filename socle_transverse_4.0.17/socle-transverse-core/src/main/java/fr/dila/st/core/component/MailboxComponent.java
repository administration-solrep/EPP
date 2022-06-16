package fr.dila.st.core.component;

import fr.dila.st.api.service.MailboxService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.MailboxServiceImpl;

public class MailboxComponent extends ServiceEncapsulateComponent<MailboxService, MailboxServiceImpl> {

    public MailboxComponent() {
        super(MailboxService.class, new MailboxServiceImpl());
    }
}
