package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.SSRechercheModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.impl.SSRechercheModeleFeuilleRouteActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSRechercheModeleFeuilleRouteActionComponent
    extends ServiceEncapsulateComponent<SSRechercheModeleFeuilleRouteActionService, SSRechercheModeleFeuilleRouteActionServiceImpl> {

    public SSRechercheModeleFeuilleRouteActionComponent() {
        super(SSRechercheModeleFeuilleRouteActionService.class, new SSRechercheModeleFeuilleRouteActionServiceImpl());
    }
}
