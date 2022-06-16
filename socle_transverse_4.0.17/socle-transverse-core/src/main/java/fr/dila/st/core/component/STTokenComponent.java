package fr.dila.st.core.component;

import fr.dila.st.api.service.STTokenService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STTokenServiceImpl;

public class STTokenComponent extends ServiceEncapsulateComponent<STTokenService, STTokenServiceImpl> {

    public STTokenComponent() {
        super(STTokenService.class, new STTokenServiceImpl());
    }
}
