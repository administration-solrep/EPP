package fr.dila.ss.core.service.vocabulary;

import fr.dila.ss.api.service.vocabulary.RoutingTaskTypeService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class RoutingTaskTypeComponent
    extends ServiceEncapsulateComponent<RoutingTaskTypeService, RoutingTaskTypeServiceImpl> {

    public RoutingTaskTypeComponent() {
        super(RoutingTaskTypeService.class, new RoutingTaskTypeServiceImpl());
    }
}
