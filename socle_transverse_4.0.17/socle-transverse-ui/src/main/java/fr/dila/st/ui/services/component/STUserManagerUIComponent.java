package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STUserManagerUIService;
import fr.dila.st.ui.services.impl.STUserManagerUIServiceImpl;

public class STUserManagerUIComponent
    extends ServiceEncapsulateComponent<STUserManagerUIService, STUserManagerUIServiceImpl> {

    /**
     * Default constructor
     */
    public STUserManagerUIComponent() {
        super(STUserManagerUIService.class, new STUserManagerUIServiceImpl());
    }
}
