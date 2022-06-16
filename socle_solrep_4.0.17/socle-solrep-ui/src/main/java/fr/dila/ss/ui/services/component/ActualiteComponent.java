package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.ActualiteUIService;
import fr.dila.ss.ui.services.impl.ActualiteUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ActualiteComponent extends ServiceEncapsulateComponent<ActualiteUIService, ActualiteUIServiceImpl> {

    public ActualiteComponent() {
        super(ActualiteUIService.class, new ActualiteUIServiceImpl());
    }
}
