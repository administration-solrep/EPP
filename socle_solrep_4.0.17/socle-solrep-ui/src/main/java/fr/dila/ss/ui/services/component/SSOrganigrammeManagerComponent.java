package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSOrganigrammeManagerService;
import fr.dila.ss.ui.services.impl.SSOrganigrammeManagerServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSOrganigrammeManagerComponent
    extends ServiceEncapsulateComponent<SSOrganigrammeManagerService, SSOrganigrammeManagerServiceImpl> {

    public SSOrganigrammeManagerComponent() {
        super(SSOrganigrammeManagerService.class, new SSOrganigrammeManagerServiceImpl());
    }
}
