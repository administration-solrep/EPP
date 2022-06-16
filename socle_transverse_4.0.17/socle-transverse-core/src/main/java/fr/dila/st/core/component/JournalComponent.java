package fr.dila.st.core.component;

import fr.dila.st.api.service.JournalService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.JournalServiceImpl;

public class JournalComponent extends ServiceEncapsulateComponent<JournalService, JournalServiceImpl> {

    public JournalComponent() {
        super(JournalService.class, new JournalServiceImpl());
    }
}
