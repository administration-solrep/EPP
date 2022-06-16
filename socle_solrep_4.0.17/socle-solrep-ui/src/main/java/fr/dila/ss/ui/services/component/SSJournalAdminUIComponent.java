package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSJournalAdminUIService;
import fr.dila.ss.ui.services.impl.SSJournalAdminUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSJournalAdminUIComponent
    extends ServiceEncapsulateComponent<SSJournalAdminUIService, SSJournalAdminUIServiceImpl> {

    public SSJournalAdminUIComponent() {
        super(SSJournalAdminUIService.class, new SSJournalAdminUIServiceImpl());
    }
}
