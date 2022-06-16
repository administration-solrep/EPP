package fr.dila.ss.ui.services.organigramme;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class SSOrganigrammeManagerUIComponent
    extends ServiceEncapsulateComponent<SSOrganigrammeManagerUIService, SSOrganigrammeManagerUIServiceImpl> {

    /**
     * Default constructor
     */
    public SSOrganigrammeManagerUIComponent() {
        super(SSOrganigrammeManagerUIService.class, new SSOrganigrammeManagerUIServiceImpl());
    }
}
