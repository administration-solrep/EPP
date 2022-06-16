package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSSupervisionUIService;
import fr.dila.ss.ui.services.impl.SSSupervisionUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSSupervisionUIComponent
    extends ServiceEncapsulateComponent<SSSupervisionUIService, SSSupervisionUIServiceImpl> {

    public SSSupervisionUIComponent() {
        super(SSSupervisionUIService.class, new SSSupervisionUIServiceImpl());
    }
}
