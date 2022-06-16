package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.FeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.impl.FeuilleRouteActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class FeuilleRouteActionComponent
    extends ServiceEncapsulateComponent<FeuilleRouteActionService, FeuilleRouteActionServiceImpl> {

    public FeuilleRouteActionComponent() {
        super(FeuilleRouteActionService.class, new FeuilleRouteActionServiceImpl());
    }
}
