package fr.dila.ss.core.component;

import fr.dila.ss.core.service.SSLockServiceImpl;
import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSLockServiceComponent extends ServiceEncapsulateComponent<STLockService, SSLockServiceImpl> {

    public SSLockServiceComponent() {
        super(STLockService.class, new SSLockServiceImpl());
    }
}
