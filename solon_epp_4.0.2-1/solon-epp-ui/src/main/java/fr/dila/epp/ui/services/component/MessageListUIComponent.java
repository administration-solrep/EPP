package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.MessageListUIService;
import fr.dila.epp.ui.services.impl.MessageListUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class MessageListUIComponent
    extends ServiceEncapsulateComponent<MessageListUIService, MessageListUIServiceImpl> {

    /*
     * Default constructor
     */
    public MessageListUIComponent() {
        super(MessageListUIService.class, new MessageListUIServiceImpl());
    }
}
