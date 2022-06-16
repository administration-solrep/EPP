package fr.dila.st.ui.services.mail;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class MailUIComponent extends ServiceEncapsulateComponent<MailUIService, MailUIServiceImpl> {

    public MailUIComponent() {
        super(MailUIService.class, new MailUIServiceImpl());
    }
}
