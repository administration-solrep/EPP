package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSFeuilleRouteUIService;
import fr.dila.ss.ui.services.impl.SSFeuilleRouteUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSFeuilleRouteUIComponent
    extends ServiceEncapsulateComponent<SSFeuilleRouteUIService, SSFeuilleRouteUIServiceImpl> {

    public SSFeuilleRouteUIComponent() {
        super(SSFeuilleRouteUIService.class, new SSFeuilleRouteUIServiceImpl());
    }
}
