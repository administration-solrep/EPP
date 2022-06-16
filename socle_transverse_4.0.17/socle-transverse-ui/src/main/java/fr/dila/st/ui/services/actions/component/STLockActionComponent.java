package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.STLockActionService;
import fr.dila.st.ui.services.actions.impl.STLockActionServiceImpl;

public class STLockActionComponent extends ServiceEncapsulateComponent<STLockActionService, STLockActionServiceImpl> {

    /**
     * Default constructor
     */
    public STLockActionComponent() {
        super(STLockActionService.class, new STLockActionServiceImpl());
    }
}
