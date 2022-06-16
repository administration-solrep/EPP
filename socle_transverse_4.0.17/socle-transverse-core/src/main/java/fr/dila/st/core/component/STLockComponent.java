package fr.dila.st.core.component;

import fr.dila.st.api.service.STLockService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STLockServiceImpl;

public class STLockComponent extends ServiceEncapsulateComponent<STLockService, STLockServiceImpl> {

    public STLockComponent() {
        super(STLockService.class, new STLockServiceImpl());
    }
}
