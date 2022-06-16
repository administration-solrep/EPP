package fr.dila.ss.core.component;

import fr.dila.ss.api.service.SSBirtService;
import fr.dila.ss.core.service.SSBirtServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSBirtComponent extends ServiceEncapsulateComponent<SSBirtService, SSBirtServiceImpl> {

    public SSBirtComponent() {
        super(SSBirtService.class, new SSBirtServiceImpl());
    }
}
