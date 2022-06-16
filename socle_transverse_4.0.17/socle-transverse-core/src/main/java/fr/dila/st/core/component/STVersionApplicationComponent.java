package fr.dila.st.core.component;

import fr.dila.st.api.service.STVersionApplicationService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STVersionApplicationServiceImpl;

public class STVersionApplicationComponent
    extends ServiceEncapsulateComponent<STVersionApplicationService, STVersionApplicationServiceImpl> {

    public STVersionApplicationComponent() {
        super(STVersionApplicationService.class, new STVersionApplicationServiceImpl());
    }
}
