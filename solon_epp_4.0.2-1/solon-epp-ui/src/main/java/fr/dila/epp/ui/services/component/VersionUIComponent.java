package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.VersionUIService;
import fr.dila.epp.ui.services.impl.VersionUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class VersionUIComponent extends ServiceEncapsulateComponent<VersionUIService, VersionUIServiceImpl> {

    /**
     * Default constructor
     */
    public VersionUIComponent() {
        super(VersionUIService.class, new VersionUIServiceImpl());
    }
}
