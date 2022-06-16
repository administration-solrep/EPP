package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSMailboxListComponentService;
import fr.dila.ss.ui.services.impl.SSMailboxListComponentServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSMailboxListComponentComponent
    extends ServiceEncapsulateComponent<SSMailboxListComponentService, SSMailboxListComponentServiceImpl> {

    public SSMailboxListComponentComponent() {
        super(SSMailboxListComponentService.class, new SSMailboxListComponentServiceImpl());
    }
}
