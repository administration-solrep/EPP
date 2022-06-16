package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.impl.SSDossierDistributionUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSDossierDistributionUIComponent
    extends ServiceEncapsulateComponent<fr.dila.ss.ui.services.SSDossierDistributionUIService, SSDossierDistributionUIServiceImpl> {

    public SSDossierDistributionUIComponent() {
        super(fr.dila.ss.ui.services.SSDossierDistributionUIService.class, new SSDossierDistributionUIServiceImpl());
    }
}
