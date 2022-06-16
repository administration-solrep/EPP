package fr.dila.ss.ui.services.component;

import fr.dila.ss.ui.services.ProfilUIService;
import fr.dila.ss.ui.services.impl.ProfilUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class ProfilUIServiceComponent extends ServiceEncapsulateComponent<ProfilUIService, ProfilUIServiceImpl> {

    /**
     * Default constructor
     */
    public ProfilUIServiceComponent() {
        super(ProfilUIService.class, new ProfilUIServiceImpl());
    }
}
