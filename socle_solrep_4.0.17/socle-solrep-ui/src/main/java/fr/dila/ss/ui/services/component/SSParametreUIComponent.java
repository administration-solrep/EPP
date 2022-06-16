package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSParametreUIService;
import fr.dila.ss.ui.services.impl.SSParametreUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSParametreUIComponent
    extends ServiceEncapsulateComponent<SSParametreUIService, SSParametreUIServiceImpl> {

    public SSParametreUIComponent() {
        super(SSParametreUIService.class, new SSParametreUIServiceImpl());
    }
}
