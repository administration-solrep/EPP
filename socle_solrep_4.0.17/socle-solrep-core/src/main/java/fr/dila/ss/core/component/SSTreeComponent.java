package fr.dila.ss.core.component;

import fr.dila.ss.api.service.SSTreeService;
import fr.dila.ss.core.service.SSTreeServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSTreeComponent extends ServiceEncapsulateComponent<SSTreeService, SSTreeServiceImpl> {

    public SSTreeComponent() {
        super(SSTreeService.class, new SSTreeServiceImpl());
    }
}
