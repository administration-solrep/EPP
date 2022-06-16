package fr.dila.epp.ui.services.actions.component;

import fr.dila.epp.ui.services.actions.MetadonneesActionService;
import fr.dila.epp.ui.services.actions.impl.MetadonneesActionServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class MetadonneesActionComponent
    extends ServiceEncapsulateComponent<MetadonneesActionService, MetadonneesActionServiceImpl> {

    /**
     * Default constructor
     */
    public MetadonneesActionComponent() {
        super(MetadonneesActionService.class, new MetadonneesActionServiceImpl());
    }
}
