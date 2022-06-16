package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.MailSuggestionActionService;
import fr.dila.st.ui.services.actions.impl.MailSuggestionActionServiceImpl;

public class MailSuggestionActionComponent
    extends ServiceEncapsulateComponent<MailSuggestionActionService, MailSuggestionActionServiceImpl> {

    /**
     * Default constructor
     */
    public MailSuggestionActionComponent() {
        super(MailSuggestionActionService.class, new MailSuggestionActionServiceImpl());
    }
}
