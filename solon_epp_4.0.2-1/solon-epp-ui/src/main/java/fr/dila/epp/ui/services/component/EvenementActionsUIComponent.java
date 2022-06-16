package fr.dila.epp.ui.services.component;

import fr.dila.epp.ui.services.EvenementActionsUIService;
import fr.dila.epp.ui.services.impl.EvenementActionsUIServiceImpl;
import fr.dila.st.core.proxy.ServiceEncapsulateComponent;

public class EvenementActionsUIComponent
    extends ServiceEncapsulateComponent<EvenementActionsUIService, EvenementActionsUIServiceImpl> {

    /**
     * Default constructor
     */
    public EvenementActionsUIComponent() {
        super(EvenementActionsUIService.class, new EvenementActionsUIServiceImpl());
    }
}
