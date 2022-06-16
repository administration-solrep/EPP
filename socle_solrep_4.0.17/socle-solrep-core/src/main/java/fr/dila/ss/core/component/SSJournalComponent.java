package fr.dila.ss.core.component;

import fr.dila.ss.api.service.SSJournalService;
import fr.dila.ss.core.service.SSJournalServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSJournalComponent extends ServiceEncapsulateComponent<SSJournalService, SSJournalServiceImpl> {

    public SSJournalComponent() {
        super(SSJournalService.class, new SSJournalServiceImpl());
    }
}
