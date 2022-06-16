package fr.dila.st.core.component;

import fr.dila.st.api.service.SecurityService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.SecurityServiceImpl;

public class SecurityComponent extends ServiceEncapsulateComponent<SecurityService, SecurityServiceImpl> {

    public SecurityComponent() {
        super(SecurityService.class, new SecurityServiceImpl());
    }
}
