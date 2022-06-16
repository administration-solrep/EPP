package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STMinistereUIService;
import fr.dila.st.ui.services.impl.STMinistereUIServiceImpl;

public class STMinistereUIComponent
    extends ServiceEncapsulateComponent<STMinistereUIService, STMinistereUIServiceImpl> {

    /**
     * Default constructor
     */
    public STMinistereUIComponent() {
        super(STMinistereUIService.class, new STMinistereUIServiceImpl());
    }
}
