package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSBirtUIService;
import fr.dila.ss.ui.services.impl.SSBirtUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSBirtUIServiceComponent extends ServiceEncapsulateComponent<SSBirtUIService, SSBirtUIServiceImpl> {

    /**
     * Default constructor
     */
    public SSBirtUIServiceComponent() {
        super(SSBirtUIService.class, new SSBirtUIServiceImpl());
    }
}
