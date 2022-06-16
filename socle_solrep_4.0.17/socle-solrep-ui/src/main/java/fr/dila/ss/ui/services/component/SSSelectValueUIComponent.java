package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSSelectValueUIService;
import fr.dila.ss.ui.services.impl.SSSelectValueUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSSelectValueUIComponent
    extends ServiceEncapsulateComponent<SSSelectValueUIService, SSSelectValueUIServiceImpl> {

    public SSSelectValueUIComponent() {
        super(SSSelectValueUIService.class, new SSSelectValueUIServiceImpl());
    }
}
