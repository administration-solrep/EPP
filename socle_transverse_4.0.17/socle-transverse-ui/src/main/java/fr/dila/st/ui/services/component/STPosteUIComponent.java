package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STPosteUIService;
import fr.dila.st.ui.services.impl.STPosteUIServiceImpl;

public class STPosteUIComponent extends ServiceEncapsulateComponent<STPosteUIService, STPosteUIServiceImpl> {

    /**
     * Default constructor
     */
    public STPosteUIComponent() {
        super(STPosteUIService.class, new STPosteUIServiceImpl());
    }
}
