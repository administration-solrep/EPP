package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.EppProfilUIService;
import fr.dila.epp.ui.services.impl.EppProfilUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class EppProfilUIComponent extends ServiceEncapsulateComponent<EppProfilUIService, EppProfilUIServiceImpl> {

    /**
     * Default constructor
     */
    public EppProfilUIComponent() {
        super(EppProfilUIService.class, new EppProfilUIServiceImpl());
    }
}
