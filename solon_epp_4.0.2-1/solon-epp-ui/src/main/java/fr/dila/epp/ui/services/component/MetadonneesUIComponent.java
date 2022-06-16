package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.MetadonneesUIService;
import fr.dila.epp.ui.services.impl.MetadonneesUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class MetadonneesUIComponent
    extends ServiceEncapsulateComponent<MetadonneesUIService, MetadonneesUIServiceImpl> {

    /*
     * Default constructor
     */
    public MetadonneesUIComponent() {
        super(MetadonneesUIService.class, new MetadonneesUIServiceImpl());
    }
}
