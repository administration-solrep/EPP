package fr.dila.ss.core.component;

import fr.dila.ss.api.service.MailboxPosteService;
import fr.dila.ss.core.service.MailboxPosteServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class MailboxPosteComponent extends ServiceEncapsulateComponent<MailboxPosteService, MailboxPosteServiceImpl> {

    public MailboxPosteComponent() {
        super(MailboxPosteService.class, new MailboxPosteServiceImpl());
    }
}
