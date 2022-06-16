package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSFdrUIService;
import fr.dila.ss.ui.services.impl.SSFdrUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSFdrUIComponent extends ServiceEncapsulateComponent<SSFdrUIService, SSFdrUIServiceImpl> {

    public SSFdrUIComponent() {
        super(SSFdrUIService.class, new SSFdrUIServiceImpl());
    }
}
