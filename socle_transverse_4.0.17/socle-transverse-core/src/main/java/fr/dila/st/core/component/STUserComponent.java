package fr.dila.st.core.component;

import fr.dila.st.api.service.STUserService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STUserServiceImpl;

public class STUserComponent extends ServiceEncapsulateComponent<STUserService, STUserServiceImpl> {

    public STUserComponent() {
        super(STUserService.class, new STUserServiceImpl());
    }
}
