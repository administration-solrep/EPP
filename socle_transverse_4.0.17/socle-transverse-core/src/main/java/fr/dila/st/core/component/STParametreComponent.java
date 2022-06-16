package fr.dila.st.core.component;

import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.core.service.STParametreServiceImpl;

public class STParametreComponent extends ServiceEncapsulateComponent<STParametreService, STParametreServiceImpl> {

    public STParametreComponent() {
        super(STParametreService.class, new STParametreServiceImpl());
    }
}
