package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.ModeleFeuilleRouteActionService;
import fr.dila.ss.ui.services.actions.impl.ModeleFeuilleRouteActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSModeleFeuilleRouteActionComponent
    extends ServiceEncapsulateComponent<ModeleFeuilleRouteActionService, ModeleFeuilleRouteActionServiceImpl> {

    /**
     * Default constructor
     */
    public SSModeleFeuilleRouteActionComponent() {
        super(ModeleFeuilleRouteActionService.class, new ModeleFeuilleRouteActionServiceImpl());
    }
}
