package fr.dila.st.core.component;

import fr.dila.cm.service.MailboxCreator;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STDefaultMailboxCreator;

public class MailboxCreatorComponent extends ServiceEncapsulateComponent<MailboxCreator, STDefaultMailboxCreator> {

    public MailboxCreatorComponent() {
        super(MailboxCreator.class, new STDefaultMailboxCreator());
    }
}
