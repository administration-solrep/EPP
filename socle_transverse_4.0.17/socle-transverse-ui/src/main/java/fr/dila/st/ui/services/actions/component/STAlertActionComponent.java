package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.STAlertActionService;
import fr.dila.st.ui.services.actions.impl.STAlertActionServiceImpl;

public class STAlertActionComponent
    extends ServiceEncapsulateComponent<STAlertActionService, STAlertActionServiceImpl> {

    /**
     * Default constructor
     */
    public STAlertActionComponent() {
        super(STAlertActionService.class, new STAlertActionServiceImpl());
    }
}
