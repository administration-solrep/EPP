package fr.dila.ss.core.component;

import fr.dila.ss.api.service.SSUserService;
import fr.dila.ss.core.service.SSUserServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSUserComponent extends ServiceEncapsulateComponent<SSUserService, SSUserServiceImpl> {

    public SSUserComponent() {
        super(SSUserService.class, new SSUserServiceImpl());
    }
}
