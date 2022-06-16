package fr.dila.st.ui.services.component;

import fr.dila.st.core.proxy.ServiceEncapsulateComponent;
import fr.dila.st.ui.services.OrganigrammeTreeUIService;
import fr.dila.st.ui.services.impl.OrganigrammeTreeUIServiceImpl;

public class OrganigrammeTreeUIComponent
    extends ServiceEncapsulateComponent<OrganigrammeTreeUIService, OrganigrammeTreeUIServiceImpl> {

    /**
     * Default constructor
     */
    public OrganigrammeTreeUIComponent() {
        super(OrganigrammeTreeUIService.class, new OrganigrammeTreeUIServiceImpl());
    }
}
