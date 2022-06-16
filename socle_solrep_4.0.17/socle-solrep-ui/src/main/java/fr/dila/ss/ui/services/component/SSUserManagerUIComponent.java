package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.impl.SSUserManagerUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.STUserManagerUIService;

public class SSUserManagerUIComponent
    extends ServiceEncapsulateComponent<STUserManagerUIService, SSUserManagerUIServiceImpl> {

    public SSUserManagerUIComponent() {
        super(STUserManagerUIService.class, new SSUserManagerUIServiceImpl());
    }
}
