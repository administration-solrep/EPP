package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSModeleFdrListUIService;
import fr.dila.ss.ui.services.impl.SSModeleFdrListUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSModeleFdrListUIComponent
    extends ServiceEncapsulateComponent<SSModeleFdrListUIService, SSModeleFdrListUIServiceImpl> {

    public SSModeleFdrListUIComponent() {
        super(SSModeleFdrListUIService.class, new SSModeleFdrListUIServiceImpl());
    }
}
