package fr.dila.st.ui.services.actions.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.actions.BaseFunctionSelectionActionService;
import fr.dila.st.ui.services.actions.impl.BaseFunctionSelectionActionServiceImpl;

public class BaseFunctionSelectionActionComponent
    extends ServiceEncapsulateComponent<BaseFunctionSelectionActionService, BaseFunctionSelectionActionServiceImpl> {

    /**
     * Default constructor
     */
    public BaseFunctionSelectionActionComponent() {
        super(BaseFunctionSelectionActionService.class, new BaseFunctionSelectionActionServiceImpl());
    }
}
