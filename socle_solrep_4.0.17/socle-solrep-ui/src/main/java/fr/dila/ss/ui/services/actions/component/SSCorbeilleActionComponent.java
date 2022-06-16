package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.SSCorbeilleActionService;
import fr.dila.ss.ui.services.actions.impl.SSCorbeilleActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSCorbeilleActionComponent
    extends ServiceEncapsulateComponent<SSCorbeilleActionService, SSCorbeilleActionServiceImpl> {

    /**
     * Default constructor
     */
    public SSCorbeilleActionComponent() {
        super(SSCorbeilleActionService.class, new SSCorbeilleActionServiceImpl());
    }
}
