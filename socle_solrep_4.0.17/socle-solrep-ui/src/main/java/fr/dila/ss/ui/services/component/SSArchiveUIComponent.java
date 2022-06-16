package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSArchiveUIService;
import fr.dila.ss.ui.services.impl.SSArchiveUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSArchiveUIComponent extends ServiceEncapsulateComponent<SSArchiveUIService, SSArchiveUIServiceImpl> {

    public SSArchiveUIComponent() {
        super(SSArchiveUIService.class, new SSArchiveUIServiceImpl());
    }
}
