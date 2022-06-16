package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.DossierLockActionService;
import fr.dila.st.ui.services.actions.impl.DossierLockActionServiceImpl;

public class DossierLockActionComponent
    extends ServiceEncapsulateComponent<DossierLockActionService, DossierLockActionServiceImpl> {

    /**
     * Default constructor
     */
    public DossierLockActionComponent() {
        super(DossierLockActionService.class, new DossierLockActionServiceImpl());
    }
}
