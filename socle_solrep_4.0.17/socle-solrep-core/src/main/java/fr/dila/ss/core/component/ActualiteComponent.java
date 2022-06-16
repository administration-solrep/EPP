package fr.dila.ss.core.component;

import fr.dila.ss.api.service.ActualiteService;
import fr.dila.ss.core.service.ActualiteServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ActualiteComponent extends ServiceEncapsulateComponent<ActualiteService, ActualiteServiceImpl> {

    public ActualiteComponent() {
        super(ActualiteService.class, new ActualiteServiceImpl());
    }
}
