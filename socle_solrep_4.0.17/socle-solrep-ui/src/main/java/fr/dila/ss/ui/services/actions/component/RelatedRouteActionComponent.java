package fr.dila.ss.ui.services.actions.component;

import fr.dila.ss.ui.services.actions.RelatedRouteActionService;
import fr.dila.ss.ui.services.actions.impl.RelatedRouteActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RelatedRouteActionComponent
    extends ServiceEncapsulateComponent<RelatedRouteActionService, RelatedRouteActionServiceImpl> {

    public RelatedRouteActionComponent() {
        super(RelatedRouteActionService.class, new RelatedRouteActionServiceImpl());
    }
}
