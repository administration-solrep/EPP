package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.ParametreActionService;
import fr.dila.st.ui.services.actions.impl.ParametreActionServiceImpl;

public class ParametreActionComponent
    extends ServiceEncapsulateComponent<ParametreActionService, ParametreActionServiceImpl> {

    /**
     * Default constructor
     */
    public ParametreActionComponent() {
        super(ParametreActionService.class, new ParametreActionServiceImpl());
    }
}
