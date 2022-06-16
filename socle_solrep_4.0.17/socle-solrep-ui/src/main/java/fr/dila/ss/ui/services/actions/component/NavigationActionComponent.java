package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.NavigationActionService;
import fr.dila.ss.ui.services.actions.impl.NavigationActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class NavigationActionComponent
    extends ServiceEncapsulateComponent<NavigationActionService, NavigationActionServiceImpl> {

    public NavigationActionComponent() {
        super(NavigationActionService.class, new NavigationActionServiceImpl());
    }
}
