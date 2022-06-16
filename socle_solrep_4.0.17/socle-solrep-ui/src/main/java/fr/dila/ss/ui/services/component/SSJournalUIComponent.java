package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.SSJournalUIService;
import fr.dila.ss.ui.services.impl.SSJournalUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

@SuppressWarnings("rawtypes")
public class SSJournalUIComponent extends ServiceEncapsulateComponent<SSJournalUIService, SSJournalUIServiceImpl> {

    public SSJournalUIComponent() {
        super(SSJournalUIService.class, new SSJournalUIServiceImpl());
    }
}
