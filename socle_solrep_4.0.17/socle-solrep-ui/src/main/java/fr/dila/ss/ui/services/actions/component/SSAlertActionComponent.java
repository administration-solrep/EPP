package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.SSAlertActionService;
import fr.dila.ss.ui.services.actions.impl.SSAlertActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSAlertActionComponent
    extends ServiceEncapsulateComponent<SSAlertActionService, SSAlertActionServiceImpl> {

    /**
     * Default constructor
     */
    public SSAlertActionComponent() {
        super(SSAlertActionService.class, new SSAlertActionServiceImpl());
    }
}
