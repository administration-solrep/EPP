package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.SSDocumentRoutingActionService;
import fr.dila.ss.ui.services.actions.impl.SSDocumentRoutingActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSDocumentRoutingActionComponent
    extends ServiceEncapsulateComponent<SSDocumentRoutingActionService, SSDocumentRoutingActionServiceImpl> {

    public SSDocumentRoutingActionComponent() {
        super(SSDocumentRoutingActionService.class, new SSDocumentRoutingActionServiceImpl());
    }
}
