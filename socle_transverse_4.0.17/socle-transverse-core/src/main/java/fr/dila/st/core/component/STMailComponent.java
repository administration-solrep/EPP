package fr.dila.st.core.component;

import fr.dila.st.api.service.STMailService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STMailServiceImpl;

public class STMailComponent extends ServiceEncapsulateComponent<STMailService, STMailServiceImpl> {

    public STMailComponent() {
        super(STMailService.class, new STMailServiceImpl());
    }
}
