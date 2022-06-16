package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.EPPOrganigrammeManagerService;
import fr.dila.epp.ui.services.impl.EPPOrganigrammeManagerServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class EPPOrganigrammeManagerComponent
    extends ServiceEncapsulateComponent<EPPOrganigrammeManagerService, EPPOrganigrammeManagerServiceImpl> {

    /**
     * Default constructor
     */
    public EPPOrganigrammeManagerComponent() {
        super(EPPOrganigrammeManagerService.class, new EPPOrganigrammeManagerServiceImpl());
    }
}
